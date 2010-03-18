package com.amee.persist;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public int getMaxActive() {
        return dataSource.getMaxActive();
    }

    public int getNumActive() {
        return dataSource.getNumActive();
    }
}