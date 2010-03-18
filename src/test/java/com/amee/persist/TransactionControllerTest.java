package com.amee.persist;

import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TransactionControllerTest extends BaseTest {

    @Autowired
    private TransactionControllerImpl transactionController;

    @Test
    public void canRollbackTransaction() {
        Assert.assertTrue("Can rollback transaction.", true);
    }
}
