package com.gloryzyf.kbmvc.db;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 数据库连接池配置
 * @author gloryzyf<bupt_zhuyufei@163.com>
 */
public class PoolConfig {

    public final static String DEFAULT_CONFIG_RESOUCE_PATH = "/kbmvc_db.properties";

    public final static String DRIVER = "db.driver";
    public final static String URL = "db.url";
    public final static String INITIAL_POOL_SIZE = "db.initialPoolSize";
    public final static String MIN_POOL_SIZE = "db.minPoolSize";
    public final static String MAX_POOL_SIZE = "db.maxPoolSize";
    public final static String MAX_IDLE_TIME = "db.maxIdleTime";
    public final static String USERNAME = "db.username";
    public final static String PASSWORD = "db.password";
    public final static String AUTO_COMMIT = "db.autoCommit";

    private static String driver = null;
    private static String url = null;
    private static int initialPoolSize = 3;
    private static int minPoolSize = 1;
    private static int maxPoolSize = 10;
    private static long maxIdleTime = 60 * 1000L;
    private static String username = null;
    private static String password = null;
    private static boolean autoCommit = true;

    static {
        Properties props = new Properties();
        String path = PoolConfig.class.getResource("/").getPath();
        InputStream is = null;
        try {
            is = new FileInputStream(path);
            if (is != null) {
                props.load(is);
                driver = (String) props.get(DRIVER);
                url = (String) props.get(URL);
                initialPoolSize = Integer.parseInt((String) props.get(INITIAL_POOL_SIZE));
                minPoolSize = Integer.parseInt((String) props.get(MIN_POOL_SIZE));
                maxPoolSize = Integer.parseInt((String) props.get(MAX_POOL_SIZE));
                username = (String) props.get(USERNAME);
                password = (String) props.get(PASSWORD);
                autoCommit = Boolean.parseBoolean((String) props.get(AUTO_COMMIT));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getDriver() {
        return driver;
    }

    public static String getUrl() {
        return url;
    }

    public static int getInitialPoolSize() {
        return initialPoolSize;
    }

    public static int getMinPoolSize() {
        return minPoolSize;
    }

    public static int getMaxPoolSize() {
        return maxPoolSize;
    }

    public static long getMaxIdleTime() {
        return maxIdleTime;
    }

    public static String getUsername() {
        return username;
    }

    public static String getPassword() {
        return password;
    }

    public static boolean getAutoCommit() {
        return autoCommit;
    }

}
