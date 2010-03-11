package com.amee.base;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:*applicationContext.xml"})
public abstract class BaseTest {

    @Before
    public void before() {
        // Do nothing.
    }

    @After
    public void after() {
        // Do nothing.
    }
}