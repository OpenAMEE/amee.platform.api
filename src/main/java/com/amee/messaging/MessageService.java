package com.amee.messaging;

import com.amee.messaging.config.ConnectionConfig;
import com.amee.messaging.config.ExchangeConfig;
import com.amee.messaging.config.PublishConfig;
import com.amee.messaging.config.QueueConfig;
import com.rabbitmq.client.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.io.IOException;

/**
 * A Spring service bean to manage RabbitMQ message sending, channels, exchanges, queues and connections.
 */
@Service
public class MessageService {

    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    private ConnectionConfig connectionConfig;

    @Autowired
    private ConnectionParameters connectionParameters;

    private Connection connection;

    /**
     * A callback method called when this bean is in the process of being removed by the Spring container.
     */
    @PreDestroy
    public synchronized void stop() {
        log.info("stop()");
        if (connection != null) {
            try {
                connection.close();
            } catch (IOException e) {
                // Swallow.
            } catch (ShutdownSignalException e) {
                // Swallow.
            }
            connection = null;
        }
    }

    /**
     * Publish a message using the supplied {@link ExchangeConfig}, {@link PublishConfig} and routing key.
     *
     * @param exchangeConfig configuration for the exchange publish message to
     * @param publishConfig  general configuration for RabbitMQ messing publishing
     * @param routingKey     the RabbitMQ routing key
     * @param message        the message to publish
     */
    public void publish(
            ExchangeConfig exchangeConfig,
            PublishConfig publishConfig,
            String routingKey,
            Message message) {
        log.debug("publish() " + routingKey);
        try {
            // Try to get a channel.
            Channel channel = getChannel();
            if (channel != null) {
                // Ensure exchange is declared.
                exchangeDeclare(channel, exchangeConfig);
                // Publish.
                channel.basicPublish(
                        exchangeConfig.getName(),
                        routingKey,
                        publishConfig.isMandatory(),
                        publishConfig.isImmediate(),
                        publishConfig.getProperties(),
                        message.getMessage().getBytes());
                // We're done with the channel.
                channel.close();
            } else {
                log.warn("publish() Unable to get a channel.");
            }
        } catch (IOException e) {
            log.warn("publish() Caught IOException: " + e.getMessage());
        } catch (ShutdownSignalException e) {
            log.warn("publish() Caught ShutdownSignalException: " + e.getMessage());
        } catch (MessagingException e) {
            log.warn("publish() Caught MessagingException: " + e.getMessage());
        }
    }

    /**
     * Get a new RabbitMQ {@link Channel} for the supplied {@link ExchangeConfig}.
     *
     * @param exchangeConfig to base Channel on
     * @return a new Channel
     * @throws IOException thrown by RabbitMQ
     */
    public Channel getChannel(ExchangeConfig exchangeConfig) throws IOException {
        log.debug("getChannel()");
        // Try to get a channel.
        Channel channel = getChannel();
        if (channel != null) {
            // Ensure exchange is declared.
            exchangeDeclare(channel, exchangeConfig);
        }
        return channel;
    }

    /**
     * Get a new RabbitMQ {@link Channel} for the supplied {@link ExchangeConfig} and {@link QueueConfig} and bind
     * to a queue with the binding key. Will ensure an exchange and queue are declared.
     *
     * @param exchangeConfig to base Channel on
     * @param queueConfig    to base Channel on
     * @param bindingKey     the binding key
     * @return a new Channel
     * @throws IOException thrown by RabbitMQ
     */
    public Channel getChannelAndBind(
            ExchangeConfig exchangeConfig,
            QueueConfig queueConfig,
            String bindingKey) throws IOException {
        log.debug("getChannelAndBind() " + bindingKey);
        // Try to get a channel.
        Channel channel = getChannel();
        if (channel != null) {
            // Ensure exchange & queue are declared and queue is bound.
            exchangeDeclare(channel, exchangeConfig);
            queueDeclare(channel, queueConfig);
            queueBind(channel, queueConfig, exchangeConfig, bindingKey);
        }
        return channel;
    }

    /**
     * Declare a new RabbitMQ exchange based on the supplied {@link Channel} and {@link ExchangeConfig}.
     *
     * @param channel        to base exchange on
     * @param exchangeConfig to base exchange on
     * @throws IOException thrown by RabbitMQ
     */
    public void exchangeDeclare(Channel channel, ExchangeConfig exchangeConfig) throws IOException {
        channel.exchangeDeclare(
                exchangeConfig.getName(),
                exchangeConfig.getType(),
                exchangeConfig.isPassive(),
                exchangeConfig.isDurable(),
                exchangeConfig.isAutoDelete(),
                exchangeConfig.getArguments());
    }

    /**
     * Declare a new RabbitMQ queue based on the supplied {@link Channel} and {@link QueueConfig}.
     *
     * @param channel     to base queue on
     * @param queueConfig to base queue on
     * @throws IOException thrown by RabbitMQ
     */
    public void queueDeclare(Channel channel, QueueConfig queueConfig) throws IOException {
        channel.queueDeclare(
                queueConfig.getName(),
                queueConfig.isPassive(),
                queueConfig.isDurable(),
                queueConfig.isExclusive(),
                queueConfig.isAutoDelete(),
                queueConfig.getArguments());
    }

    /**
     * Bind a queue to a channel based on the supplied {@link Channel} and {@link QueueConfig},
     * {@link ExchangeConfig} and binding key.
     *
     * @param channel        to base queue on
     * @param queueConfig    to base queue on
     * @param exchangeConfig to base queue on
     * @param bindingKey     binding key to use
     * @throws IOException thrown by RabbitMQ
     */
    public void queueBind(Channel channel, QueueConfig queueConfig, ExchangeConfig exchangeConfig, String bindingKey) throws IOException {
        channel.queueBind(
                queueConfig.getName(),
                exchangeConfig.getName(),
                bindingKey,
                null);
    }

    /**
     * Get a new {@link Channel} for the current {@link Connection}.
     *
     * @return a new {@link Channel}
     * @throws IOException thrown by RabbitMQ
     */
    public Channel getChannel() throws IOException {
        try {
            return getConnection().createChannel();
        } catch (ShutdownSignalException e) {
            log.warn("getChannel() Caught ShutdownSignalException. We'll try to ignore once. Message was: " + e.getMessage());
            connection = null;
            return getConnection().createChannel();
        }
    }

    /**
     * Get an existing or new RabbitMQ {@link Connection}. The addresses configured in the
     * current {@link ConnectionConfig} instance are used.
     *
     * @return the existing or a new {@link Connection}
     * @throws IOException thrown by RabbitMQ
     */
    public Connection getConnection() throws IOException {
        if (connection == null) {
            synchronized (this) {
                if (connection == null) {
                    connection = getConnectionFactory().newConnection(connectionConfig.getAddresses());
                }
            }
        }
        return connection;
    }

    /**
     * Return a new {@link ConnectionFactory} based on the corrent {@link ConnectionParameters}.
     *
     * @return a new {@link ConnectionFactory}
     */
    public ConnectionFactory getConnectionFactory() {
        return new ConnectionFactory(connectionParameters);
    }
}
