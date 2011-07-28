package com.amee.base.engine;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Writes JDK log messages to commons logging.
 *
 * See: http://wiki.apache.org/myfaces/Trinidad_and_Common_Logging
 */
public class JavaLoggingToCommonsLoggingRedirector {

    static JDKLogHandler activeHandler;

    public static void activate() {
        try {
            Logger rootLogger = LogManager.getLogManager().getLogger("");

            // Remove old handlers
            for (Handler handler : rootLogger.getHandlers()) {
                rootLogger.removeHandler(handler);
            }

            // Add our own
            activeHandler = new JDKLogHandler();
            activeHandler.setLevel(Level.ALL);
            rootLogger.addHandler(activeHandler);
            rootLogger.setLevel(Level.ALL);

            // Log a message using JUL. This should be redirected to JCL.
            Logger.getLogger(JavaLoggingToCommonsLoggingRedirector.class.getName())
                .info("activate() sending JDK log messages to Commons Logging.");
        } catch (Exception e) {
            LogFactory.getLog(JavaLoggingToCommonsLoggingRedirector.class).error("activate() Commons Logging redirect failed.", e);
        }
    }

    public static void deactivate() {
        Logger rootLogger = LogManager.getLogManager().getLogger("");
        rootLogger.removeHandler(activeHandler);
        Logger.getLogger(JavaLoggingToCommonsLoggingRedirector.class.getName())
            .info("deactivate() Commons Logging redirect deactivated.");
    }

    protected static class JDKLogHandler extends Handler {
        private Map<String, Log> cachedLogs = new ConcurrentHashMap<String, Log>();

        /**
         * Gets a {@link Log} instance. The Log will be created if it is not already cached.
         *
         * @param logName the name of the Log to get.
         * @return the Log instance.
         */
        private Log getLog(String logName) {
            Log log = cachedLogs.get(logName);
            if (log == null) {
                log = LogFactory.getLog(logName);
                cachedLogs.put(logName, log);
            }
            return log;
        }

        @Override
        public void publish(LogRecord record) {
            Log log = getLog(record.getLoggerName());
            String message = record.getMessage();
            Throwable exception = record.getThrown();
            Level level = record.getLevel();
            if (level == Level.SEVERE) {
                log.error(message, exception);
            } else if (level == Level.WARNING) {
                log.warn(message, exception);
            } else if (level == Level.INFO) {
                log.info(message, exception);
            } else if (level == Level.CONFIG) {
                log.debug(message, exception);
            } else {
                log.trace(message, exception);
            }
        }

        @Override
        public void flush() {
            // Nothing to do.
        }

        @Override
        public void close() throws SecurityException {
            // Nothing to do.
        }
    }

}
