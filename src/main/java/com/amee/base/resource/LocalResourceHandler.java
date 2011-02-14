package com.amee.base.resource;

import com.amee.base.domain.VersionBeanFinder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;

/**
 * A ResourceHandler which proxies handling of the RequestWrapper to the Spring bean identified in the
 * target property. A target ResourceHandler will be found in the Spring context which matches the Version
 * supported for the current request. The target ResourceHandler will be invoked with a Future
 * with the timeout value (if this is greater than zero).
 */
@Service
@Scope("prototype")
public class LocalResourceHandler implements ResourceHandler {

    private final Log log = LogFactory.getLog(getClass());

    private static final ExecutorService executor = Executors.newCachedThreadPool();

    @Autowired
    private VersionBeanFinder versionBeanFinder;

    private String target = "";

    private int timeout = 0;
    private int defaultTimeout = 0;

    public Object handle(RequestWrapper requestWrapper) {
        // Lookup target bean.
        Object target = versionBeanFinder.getBeanForVersion(getTarget(), requestWrapper.getVersion());
        if (target != null) {
            // Target bean found, send request there and get result object.
            // Only ResourceHandler derived implementations are supported.
            if (ResourceHandler.class.isAssignableFrom(target.getClass())) {
                if (getTimeout() > 0) {
                    return handleWithTimeout(requestWrapper, (ResourceHandler) target);
                } else {
                    return ((ResourceHandler) target).handle(requestWrapper);
                }
            } else {
                // Target bean type not supported.
                log.warn("handle() Target bean type not supported: " + target.getClass());
                throw new NotFoundException();
            }
        } else {
            // Target bean not found.
            log.warn("handle() Target bean not found:  " + requestWrapper.getTarget());
            throw new NotFoundException();
        }
    }

    /**
     * Handle the request with a ResourceHandler that is executed via a Future with a timeout.
     *
     * @param requestWrapper RequestWrapper for this request
     * @param handler        the ResourceHandler for this request
     * @return the response object
     */
    protected Object handleWithTimeout(final RequestWrapper requestWrapper, final ResourceHandler handler) {
        Object response = null;
        // Wrap the ResourceHandler in a Callable so it can be invoked via a Future below.
        Callable<Object> task = new Callable<Object>() {
            public Object call() throws Exception {
                return handler.handle(requestWrapper);
            }
        };
        // Submit the ResourceHandler task for execution.
        log.debug("handleWithTimeout() Submitting the task.");
        Future<Object> future = executor.submit(task);
        try {
            // Get the result from the ResourceHandler. Will block until the result is available or
            // the timeout is duration is reached.
            response = future.get(getTimeout(), TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            // The task took longer than the timeout seconds to complete so we gave up.
            log.warn("handleWithTimeout() Caught TimeoutException (giving up).");
            throw new TimedOutException();
        } catch (InterruptedException e) {
            // Logic within a ResourceHandler should not allow an InterruptedException to escape.
            log.error("handleWithTimeout() Caught InterruptedException: " + e.getMessage());
            throw new InternalErrorException();
        } catch (ExecutionException e) {
            // We expect ResourceExceptions sometimes.
            if ((e.getCause() != null) && ResourceException.class.isAssignableFrom(e.getCause().getClass())) {
                throw (ResourceException) e.getCause();
            } else {
                log.error("handleWithTimeout() Caught unexpected ExecutionException: " + e.getMessage());
                throw new InternalErrorException();
            }
        } finally {
            // Ensure the task is cancelled.
            if (future.cancel(true)) {
                // This is not a good place to be. What was the task doing after
                // the future.get method call (above) completed?
                log.warn("handleWithTimeout() Task was cancelled.");
            }
        }
        // We expect to have a response.
        if (response == null) {
            log.error("handleWithTimeout() Response was null.");
            throw new InternalErrorException();
        }
        return response;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        if (target == null) {
            target = "";
        }
        this.target = target;
    }

    public int getTimeout() {
        return timeout > 0 ? timeout : defaultTimeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    @Value("#{ systemProperties['amee.resourceDefaultTimeout'] }")
    public void setDefaultTimeout(Integer defaultTimeout) {
        this.defaultTimeout = defaultTimeout;
    }
}