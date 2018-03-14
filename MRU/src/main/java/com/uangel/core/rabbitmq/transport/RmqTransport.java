package com.uangel.core.rabbitmq.transport;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RmqTransport {

    private final String host;
    private final String queueName;
    private final String userName;
    private final String password;

    private Connection connection;
    private Channel channel;

    public RmqTransport(String host, String userName, String password, String queueName) {
        this.host = host;
        this.queueName = queueName;
        this.userName = userName;
        this.password = password;
    }

    protected Channel getChannel() {
        return channel;
    }

    protected String getQueueName() {
        return queueName;
    }

    public boolean connect() {

        if (makeConnection() == false) {
            return false;
        }

        if (makeChannel() == false) {
            closeConnection();
            return false;
        }

        return true;
    }

    public void close() {
        closeChannel();
        closeConnection();
    }

    private boolean makeConnection() {

        boolean result = false;

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setUsername(userName);
        factory.setPassword(password);

        try {
            connection = factory.newConnection();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private void closeConnection() {
        try {
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean makeChannel() {

        boolean result = false;

        try {
            channel = connection.createChannel();
            channel.queueDeclare(queueName, false, false, false, null);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private void closeChannel() {
        try {
            channel.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
