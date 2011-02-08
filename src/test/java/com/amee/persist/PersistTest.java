package com.amee.persist;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public abstract class PersistTest {

    @Before
    public void before() {
        // Do nothing.
    }

    @After
    public void after() {
        // Do nothing.
    }
}