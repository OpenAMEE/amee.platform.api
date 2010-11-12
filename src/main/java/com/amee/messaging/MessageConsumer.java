package com.amee.messaging;

import com.amee.messaging.config.ExchangeConfig;
import com.amee.messaging.config.MessagingConfig;
import com.amee.messaging.config.QueueConfig;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ShutdownSignalException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.SmartLifecycle;

import java.io.IOException;

public abstract class MessageConsumer implements Runnable, SmartLifecycle, ApplicationContextAware {

    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    protected MessageService messageService;

    @Autowired
    protected MessagingConfig messagingConfig;

    protected Channel channel;
    protected Thread thread;
    protected boolean stopping = false;
    protected ApplicationContext applicationContext;

    @Override
    public synchronized void start() {
        log.info("start()");
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public synchronized void stop() {
        log.info("stop()");
        stopping = true;
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }

    @Override
    public boolean isRunning() {
        return (thread != null) && (thread.isAlive());
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable callback) {
        stop();
        callback.run();
    }

    @Override
    public int getPhase() {
        // Start as late as possible.
        return Integer.MAX_VALUE;
    }

    @Override
    public void run() {
        log.info("run()");
        while (!Thread.currentThread().isInterrupted()) {
            try {
                log.debug("run() Waiting.");
                // Wait before first-run and subsequent retries.
                Thread.sleep(messagingConfig.getRunSleep());
                log.debug("run() Starting.");
                // Start the Consumer and handle the deliveries.
                configureChannel();
                consume();
                // We got here if there is no channel or this was stopped.
                log.debug("run() No channel or stopped.");
            } catch (IOException e) {
                log.warn("run() Caught IOException. We'll try restarting the consumer. Message was: " +
                        ((e.getCause() != null) ? e.getCause().getMessage() : e.getMessage()));
            } catch (ShutdownSignalException e) {
                log.warn("run() Caught ShutdownSignalException. We'll try restarting the consumer. Message was: " +
                        e.getMessage());
            } catch (InterruptedException e) {
                log.info("run() Interrupted.");
                closeAndClear();
                return;
            } catch (Exception e) {
                log.error("run() Caught Exception: " + e.getMessage(), e);
            } catch (Throwable t) {
                log.error("run() Caught Throwable: " + t.getMessage(), t);
            }
        }
    }

    protected void configureChannel() throws IOException {
        // Ensure the channel is closed & consumer is cleared before starting.
        closeAndClear();
        // Try to get a channel for our configuration.
        channel = messageService.getChannelAndBind(
                getExchangeConfig(),
                getQueueConfig(),
                getBindingKey());
    }

    protected abstract void consume() throws IOException, InterruptedException;

    protected synchronized void closeAndClear() {
        log.debug("closeAndClear()");
        if (channel != null) {
            try {
                channel.close();
            } catch (IOException e) {
                // Swallow.
            } catch (ShutdownSignalException e) {
                // Swallow.
            }
            channel = null;
        }
    }

    public abstract ExchangeConfig getExchangeConfig();

    public abstract QueueConfig getQueueConfig();

    public abstract String getBindingKey();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
