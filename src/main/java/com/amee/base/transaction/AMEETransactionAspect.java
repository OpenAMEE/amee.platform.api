package com.amee.base.transaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@Aspect
@Order(0)
public class AMEETransactionAspect implements ApplicationContextAware {

    private final Log log = LogFactory.getLog(getClass());

    private ApplicationContext applicationContext;

    @Before("@annotation(com.amee.base.transaction.AMEETransaction)")
    public void before() {
        log.debug("before()");
        applicationContext.publishEvent(new TransactionEvent(this, TransactionEventType.BEFORE_BEGIN));
    }

    @AfterReturning("@annotation(com.amee.base.transaction.AMEETransaction)")
    public void afterReturning() {
        log.debug("afterReturning()");
        applicationContext.publishEvent(new TransactionEvent(this, TransactionEventType.END));
    }

    @AfterThrowing("@annotation(com.amee.base.transaction.AMEETransaction)")
    public void afterThrowing() {
        log.debug("afterThrowing()");
        applicationContext.publishEvent(new TransactionEvent(this, TransactionEventType.ROLLBACK));
        applicationContext.publishEvent(new TransactionEvent(this, TransactionEventType.END));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}


