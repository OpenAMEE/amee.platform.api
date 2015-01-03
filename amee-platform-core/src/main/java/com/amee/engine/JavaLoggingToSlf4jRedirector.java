package com.amee.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

/**
 * Writes JDK log messages to SLF4J logging.
 *
 * See: http://wiki.apache.org/myfaces/Trinidad_and_Common_Logging
 *
 * TODO: Should we use jul-to-slf4j? http://www.slf4j.org/legacy.html
 */
public class JavaLoggingToSlf4jRedirector {

    static JDKLogHandler activeHandler;

    public static void activate() {
        try {
            java.util.logging.Logger rootLogger = LogManager.getLogManager().getLogger("");

            // Remove old handlers
            for (Handler handler : rootLogger.getHandlers()) {
                rootLogger.removeHandler(handler);
            }

            // Add our own
            activeHandler = new JDKLogHandler();
            activeHandler.setLevel(Level.ALL);
            rootLogger.addHandler(activeHandler);
            rootLogger.setLevel(Level.ALL);

            // Logger a message using JUL. This should be redirected to JCL.
            java.util.logging.Logger.getLogger(JavaLoggingToSlf4jRedirector.class.getName())
                .info("activate() sending JDK log messages to SLF4J.");
        } catch (Exception e) {
            LoggerFactory.getLogger(JavaLoggingToSlf4jRedirector.class).error("activate() SLF4J redirect failed.", e);
        }
    }

    public static void deactivate() {
        java.util.logging.Logger rootLogger = LogManager.getLogManager().getLogger("");
        rootLogger.removeHandler(activeHandler);
        java.util.logging.Logger.getLogger(JavaLoggingToSlf4jRedirector.class.getName())
            .info("deactivate() SLF4J redirect deactivated.");
    }

    protected static class JDKLogHandler extends Handler {
        private Map<String, Logger> cachedLogs = new ConcurrentHashMap<String, Logger>();

        /**
         * Gets a {@link Logger} instance. The Logger will be created if it is not already cached.
         *
         * @param logName the name of the Logger to get.
         * @return the Logger instance.
         */
        private Logger getLog(String logName) {
            Logger log = cachedLogs.get(logName);
            if (log == null) {
                log = LoggerFactory.getLogger(logName);
                cachedLogs.put(logName, log);
            }
            return log;
        }

        @Override
        public void publish(LogRecord record) {
            Logger log = getLog(record.getLoggerName());
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
