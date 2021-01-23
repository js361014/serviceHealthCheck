package service.health.check.updater;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Address;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;

import service.health.check.messages.Config;

public class App {

    public App() {
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        Channel channel = createRabbitMqChannel();
        channel.queueDeclare(Config.CHECKED_ADDRESSES_QUEUE, true, false, false, null);
        Consumer consumer = new UpdaterConsumer(channel);
        channel.basicConsume(Config.CHECKED_ADDRESSES_QUEUE, true, consumer);
    }

    private static Channel createRabbitMqChannel() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        Connection connection = factory.newConnection(Address.parseAddresses(Config.RABBITMQ_CONNECTION_ADDRESS));
        return connection.createChannel();
    }
}
