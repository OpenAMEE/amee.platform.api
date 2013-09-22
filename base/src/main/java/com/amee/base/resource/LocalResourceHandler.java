package com.amee.base.resource;

import com.amee.base.domain.VersionBeanFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;

/**
 * A ResourceHandler which proxies handling of the {@link RequestWrapper} to the Spring bean identified in the
 * target property. A target ResourceHandler will be found in the Spring context which matches the Version
 * supported for the current request. The target {@link ResourceHandler} will be invoked with a {@link Future}
 * with the timeout value (if this is greater than zero).
 * <p/>
 * The 'Local' part of the name for this class and sub-classes implies that requests will be handled by resource
 * beans in the same JVM where the request arrived. This is opposed to other {@link ResourceHandler} which
 * may send requests elsewhere to be handled.
 */
@Service
@Scope("prototype")
public class LocalResourceHandler implements ResourceHandler {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final ExecutorService executor = Executors.newCachedThreadPool();

    @Autowired
    private VersionBeanFinder versionBeanFinder;

    // The name of the target spring bean, which should be a ResourceHandler.
    private String target = "";

    // A timeout value (in seconds) that can be set per ResourceHandler.
    private int timeout = 0;

    // A global default timeout value (in seconds).
    private int defaultTimeout = 0;

    @Override
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
    public Object handleWithTimeout(final RequestWrapper requestWrapper, final ResourceHandler handler) {
        Object response = null;
        final Map values = MDC.getCopyOfContextMap();

        // Wrap the ResourceHandler in a Callable so it can be invoked via a Future below.
        Callable<Object> task = new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                if (values != null) {
                    MDC.setContextMap(values);
                }
                return handler.handle(requestWrapper);
            }
        };
        // Submit the ResourceHandler task for execution.
        log.debug("handleWithTimeout() Submitting the task.");
        Future<Object> future = executor.submit(task);
        try {
            // Get the result from the ResourceHandler. Will block until the result is available or
            // the timeout duration is reached.
            response = future.get(getTimeout(), TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            // The task took longer than the timeout seconds to complete so we gave up.
            log.warn("handleWithTimeout() Caught TimeoutException (giving up).");
            throw new TimedOutException();
        } catch (InterruptedException e) {
            // Logic within a ResourceHandler should not allow an InterruptedException to escape.
            log.error("handleWithTimeout() Caught InterruptedException: " + e.getMessage(), e);
            throw new InternalErrorException();
        } catch (ExecutionException e) {
            // We expect ResourceExceptions sometimes.
            if ((e.getCause() != null) && ResourceException.class.isAssignableFrom(e.getCause().getClass())) {
                throw (ResourceException) e.getCause();
            } else {
                log.error("handleWithTimeout() Caught unexpected ExecutionException: " + e.getMessage(), e);
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

    /**
     * Get the name of the target spring bean.
     *
     * @return target spring bean name
     */
    public String getTarget() {
        return target;
    }

    /**
     * Set the name of the target spring bean.
     *
     * @param target spring bean name
     */
    public void setTarget(String target) {
        if (target == null) {
            target = "";
        }
        this.target = target;
    }

    /**
     * Get the timeout.
     *
     * @return timeout value in seconds
     */
    public int getTimeout() {
        return timeout > 0 ? timeout : defaultTimeout;
    }

    /**
     * Set the timeout.
     *
     * @param timeout value in seconds
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * Sets the default timeout value from the amee.resourceDefaultTimeout system property.
     *
     * @param defaultTimeout timeout value in seconds
     */
    @Value("#{ systemEnvironment['RESOURCE_DEFAULT_TIMEOUT'] }")
    public void setDefaultTimeout(Integer defaultTimeout) {
        this.defaultTimeout = defaultTimeout;
    }
}
