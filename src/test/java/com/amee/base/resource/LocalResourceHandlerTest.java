package com.amee.base.resource;

import com.amee.base.BaseTest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import static junit.framework.Assert.*;

/**
 * Tests usage of LocalResourceHandler.
 */
public class LocalResourceHandlerTest extends BaseTest {

    private final Log log = LogFactory.getLog(getClass());

    boolean slowResourceHandlerCompleted = false;

    /**
     * Test that a slow running ResourceHandler can be stopped with a timeout.
     */
    @Test
    public void willTrapSlowResourceHandler() throws InterruptedException {
        String result = null;
        // Create a LocalResourceHandler that will cancel the ResourceHandler after 1 second.
        LocalResourceHandler lrh = new LocalResourceHandler();
        lrh.setTimeout(1);
        try {
            // Invoke a ResourceHandler which takes 2 seconds to complete.
            result = (String) lrh.handleWithTimeout(new RequestWrapper(), new ResourceHandler() {
                @Override
                public Object handle(RequestWrapper requestWrapper) {
                    // Loop for at least 2 seconds to simulate a long-running task, like a database call.
                    for (int i = 0; i < 200; i++) {
                        try {
                            // Sleep for 10 milliseconds.
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            log.debug("Caught InterruptedException: " + e.getMessage());
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                    if (!Thread.currentThread().isInterrupted()) {
                        log.debug("Not interrupted, returning result.");
                        slowResourceHandlerCompleted = true;
                        return "Completed.";
                    } else {
                        log.debug("Interrupted, returning null.");
                        return null;
                    }
                }
            });
            fail("Should have thrown a TimedOutException.");
        } catch (TimedOutException e) {
            // Let two seconds pass before testing (to allow ResourceHandler to complete).
            Thread.sleep(2 * 1000);
            assertNull("Result should be null.", result);
            assertFalse("Should not have completed.", slowResourceHandlerCompleted);
        }
    }

    /**
     * Test that a ResourceException can be caught from a ResourceHandler.
     */
    @Test
    public void willCatchResourceExceptionFromResourceHandler() {
        // Create a LocalResourceHandler that will cancel the ResourceHandler after 1 second.
        LocalResourceHandler lrh = new LocalResourceHandler();
        lrh.setTimeout(1);
        try {
            // Invoke a ResourceHandler which always throws a NotFoundException.
            lrh.handleWithTimeout(new RequestWrapper(), new ResourceHandler() {
                @Override
                public Object handle(RequestWrapper requestWrapper) {
                    // Pretend something was not found.
                    log.debug("Throwing a NotFoundException...");
                    throw new NotFoundException();
                }
            });
            fail("Should have thrown a NotFoundException.");
        } catch (NotFoundException e) {
            // Should get here.
        }
    }

    /**
     * Test that the LocalResourceHandler will correctly handle an uncaught exception.
     */
    @Test
    public void willHandleAnExceptionFromResourceHandler() {
        // Create a LocalResourceHandler that will cancel the ResourceHandler after 1 second.
        LocalResourceHandler lrh = new LocalResourceHandler();
        lrh.setTimeout(1);
        try {
            // Invoke a ResourceHandler which always throws a RuntimeException.
            lrh.handleWithTimeout(new RequestWrapper(), new ResourceHandler() {
                @Override
                public Object handle(RequestWrapper requestWrapper) {
                    // Pretend something went wrong.
                    log.debug("Throwing a RuntimeException...");
                    throw new RuntimeException(
                            "This RuntimeException is expected. The tests should still pass and the build should be OK.");
                }
            });
            fail("Should have thrown an InternalErrorException.");
        } catch (InternalErrorException e) {
            // Should get here.
        }
    }

    /**
     * Test that the LocalResourceHandler will correctly handle a null result
     */
    @Test
    public void willHandleANullResultFromResourceHandler() {
        // Create a LocalResourceHandler that will cancel the ResourceHandler after 1 second.
        LocalResourceHandler lrh = new LocalResourceHandler();
        lrh.setTimeout(1);
        try {
            // Invoke a ResourceHandler which always returns null.
            lrh.handleWithTimeout(new RequestWrapper(), new ResourceHandler() {
                @Override
                public Object handle(RequestWrapper requestWrapper) {
                    // Simulate a null response.
                    return null;
                }
            });
            fail("Should have thrown an InternalErrorException.");
        } catch (InternalErrorException e) {
            // Should get here.
        }
    }
}
