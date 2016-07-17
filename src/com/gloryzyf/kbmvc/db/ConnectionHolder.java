package com.gloryzyf.kbmvc.db;

import java.sql.Connection;

/**
 * @author gloryzyf<bupt_zhuyufei@163.com>
 */
public class ConnectionHolder {

    private Connection connection;
    private  long connectTimeMillis;
    private long lastActiveTimeMillis;

    public ConnectionHolder(Connection connection){
        this.connection = connection;
        connectTimeMillis = System.currentTimeMillis();
        lastActiveTimeMillis = connectTimeMillis;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public long getLastActiveTimeMillis() {
        return lastActiveTimeMillis;
    }

    public void setLastActiveTimeMillis(long lastActiveTimeMillis) {
        this.lastActiveTimeMillis = lastActiveTimeMillis;
    }
}
