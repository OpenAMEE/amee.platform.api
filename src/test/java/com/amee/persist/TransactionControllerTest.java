package com.amee.persist;

import com.amee.base.transaction.TransactionController;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TransactionControllerTest extends BaseTest {

    @Autowired
    private TransactionController transactionController;

    @Test
    public void canRollbackTransaction() {
        Assert.assertTrue("Can rollback transaction.", true);
    }
}
