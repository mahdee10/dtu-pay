package org.dtu.reporting.config;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitMQConnection {
    private static Connection connection;

    public static Connection getConnection() throws Exception {
        if (connection == null || !connection.isOpen()) {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            connection = factory.newConnection();
        }
        return connection;
    }

    public static void closeConnection() throws Exception {
        if (connection != null && connection.isOpen()) {
            connection.close();
        }
    }
}
