package com.gloryzyf.kbmvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author gloryzyf<bupt_zhuyufei@163.com>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
    //数据库列名
    String name();
    //字段类型
    Class<?> type();
    //是否是主键
    boolean isPrimary() default false;
}
