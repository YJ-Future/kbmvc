package com.gloryzyf.kbmvc.sql;

import com.gloryzyf.kbmvc.annotation.Column;
import com.gloryzyf.kbmvc.annotation.Entity;
import com.mysql.jdbc.StringUtils;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author gloryzyf<bupt_zhuyufei@163.com>
 */
public class SqlGenerator {

    private static final ConcurrentHashMap<Class<?>, String> insertSqlMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, String> updateSqlMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Class<?>, String> deleteSqlMap = new ConcurrentHashMap<>();

    private SqlGenerator() {
    }

    private static SqlGenerator generator = new SqlGenerator();

    public static SqlGenerator getInstance() {
        return generator;
    }

    public String getInsertSql(Class<?> clazz) {
        if (clazz == null)
            return null;
        String sql = insertSqlMap.get(clazz);
        if (sql != null)
            return sql;
        Entity entity = clazz.getAnnotation(Entity.class);
        if (entity == null)
            return null;
        StringBuilder strBuilder = new StringBuilder("insert into ");
        strBuilder.append(entity.table()).append(" (");
        Field[] fields = clazz.getDeclaredFields();
        int fieldCount = 0;
        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);
            if (column == null)
                continue;
            strBuilder.append(column.name() + ",");
            fieldCount++;
        }
        if (fieldCount == 0)
            return null;
        strBuilder.deleteCharAt(strBuilder.length() - 1);
        strBuilder.append(") values (");
        for (int i = 0; i < fieldCount; i++) {
            strBuilder.append("?,");
        }
        strBuilder.deleteCharAt(strBuilder.length() - 1);
        strBuilder.append(")");
        sql = strBuilder.toString();
        insertSqlMap.put(clazz, sql);
        return sql;
    }

    public String getUpdateSql(Class<?> clazz, String... columns) {
        //update user set col1 = ?, col2 = ? where id = ?
        if (clazz == null || columns == null || columns.length <= 0)
            return null;
        //从map中取缓存
        String key = null;
        StringBuilder keyBuilder = new StringBuilder(clazz.toString());
        for (String col : columns) {
            keyBuilder.append(col).append(" ");
        }
        key = keyBuilder.toString();
        String sql = updateSqlMap.get(key);
        if (sql != null)
            return sql;
        Entity entity = clazz.getAnnotation(Entity.class);
        if (entity == null)
            return null;
        //生成sql
        StringBuilder strBuilder = new StringBuilder("update ");
        String tableName = entity.table();
        strBuilder.append(tableName);
        strBuilder.append(" set ");
        for (String col : columns) {
            strBuilder.append(col + " = ?,");
        }
        strBuilder.deleteCharAt(strBuilder.length() - 1);
        strBuilder.append(" where ");
        strBuilder.deleteCharAt(strBuilder.length() - 1);
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);
            if (column == null)
                continue;
            if (column.isPrimary()) {
                strBuilder.append(column.name() + " = ? ");
                sql = strBuilder.toString();
                break;
            }
        }
        //添加sql到map中缓存
        if (sql != null)
            updateSqlMap.put(key, sql);
        return sql;
    }

    public String getDeleteSql(Class<?> clazz) {
        String sql = deleteSqlMap.get(clazz);
        if (sql != null)
            return sql;
        StringBuilder strBuilder = new StringBuilder("delete from ");
        Entity entity = clazz.getAnnotation(Entity.class);
        if (entity == null)
            return null;
        strBuilder.append(entity.table()).append(" where ");
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);
            if (column == null)
                continue;
            if (column.isPrimary()) {
                strBuilder.append(column.name()).append(" = ?");
                sql = strBuilder.toString();
                break;
            }
        }
        if (sql != null)
            deleteSqlMap.put(clazz, sql);
        return sql;
    }
}
