package com.amee.persist;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * A Spring bean intended for use as a JMX bean to expose statistics about the current DataSource.
 */
@Service("dataSourceStatistics")
public class DataSourceStatistics {

    @Autowired
    private BasicDataSource dataSource;

    public int getInitialSize() {
        return dataSource.getInitialSize();
    }

    public int getMaxIdle() {
        return dataSource.getMaxIdle();
    }

    public int getMinIdle() {
        return dataSource.getMinIdle();
    }

    public int getNumIdle() {
        return dataSource.getNumIdle();
    }

    public int getMaxTotal() {
        return dataSource.getMaxTotal();
    }

    public int getNumActive() {
        return dataSource.getNumActive();
    }
}