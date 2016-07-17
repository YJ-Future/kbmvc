package com.gloryzyf.kbmvc.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
/**
 * 数据库连接池
 * @author gloryzyf<bupt_zhuyufei@163.com>
 */
public class ConnectionPool implements Pool {

    //当前数据库连接数
    private volatile int connCount = 0;

    private ReentrantLock lock = new ReentrantLock();

    //连接池
    private ConcurrentLinkedQueue<ConnectionHolder> connections = new ConcurrentLinkedQueue<>();

    public static final long DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS = 60 * 1000L;

    private volatile long timeBetweenEvictionRunsMillis = DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS;
    //关闭空闲连接的线程池
    private ScheduledExecutorService destroyScheduler;

    private ConnectionPool() {
        init();
    }

    private static ConnectionPool poolInstance = new ConnectionPool();

    public static ConnectionPool getInstance() {
        return poolInstance;
    }

    /**
     * 初始化数据库连接池
     */
    private void init() {
        try {
            Class.forName(PoolConfig.getUrl());
            Connection conn = null;
            for (int i = 0; i < PoolConfig.getInitialPoolSize(); i++) {
                conn = getPhysicalConnection();
                connections.offer(new ConnectionHolder(conn));
            }
            connCount++;
            createAndStartDestroyScheduler();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从数据库连接池中获取数据库连接
     * @return
     */
    @Override
    public Connection getConnection() {
        lock.lock();
        Connection conn = null;
        try {
            if (connections.size() <= 0 && connCount < PoolConfig.getMaxPoolSize()) {
                ConnectionHolder holder = new ConnectionHolder(getPhysicalConnection());
                connections.offer(holder);
            }
            conn = connections.poll().getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return conn;
    }

    /**
     * 真正创建数据库连接池
     * @return
     */
    public Connection getPhysicalConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(PoolConfig.getUrl(), PoolConfig.getUsername(), PoolConfig.getPassword());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    @Override
    public void releaseConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connections.offer(new ConnectionHolder(connection));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createAndStartDestroyScheduler() {
        DestroyTask destroyTask = new DestroyTask();
        long period = this.timeBetweenEvictionRunsMillis;
        destroyScheduler = Executors.newSingleThreadScheduledExecutor();
        destroyScheduler.scheduleAtFixedRate(destroyTask, period, period, TimeUnit.MILLISECONDS);
        return;
    }

    /**
     * 关闭空闲数据库连接Task
     */
    public class DestroyTask implements Runnable {

        @Override
        public void run() {
            shrink();
        }
    }

    /**
     * 销毁空闲超时连接
     */
    private void shrink() {
        Iterator<ConnectionHolder> iter = connections.iterator();
        long currentTimeMillis = System.currentTimeMillis();
        lock.lock();
        try {
            while (iter.hasNext()) {
                ConnectionHolder holder = iter.next();
                long idleTimeMillis = currentTimeMillis - holder.getLastActiveTimeMillis();
                if (connCount > PoolConfig.getMinPoolSize()) {
                    if (idleTimeMillis > PoolConfig.getMaxIdleTime()) {
                        connections.remove(holder);
                        holder.setConnection(null);
                        connCount--;
                    }
                } else
                    break;
            }
        } finally {
            lock.unlock();
        }
    }
}
