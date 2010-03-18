package com.amee.base.transaction;

public interface TransactionController {

    public void begin(boolean withTransaction);
    
    public void end();

    public void beforeHandle(boolean withTransaction);

    public void afterHandle(boolean success);

    public void afterCommit();

    public void setRollbackOnly();
}
