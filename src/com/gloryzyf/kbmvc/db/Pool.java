package com.gloryzyf.kbmvc.db;

import java.sql.Connection;

/**
 * @author gloryzyf<bupt_zhuyufei@163.com>
 */
public interface Pool {

    Connection getConnection();

    void releaseConnection(Connection connection);
}
