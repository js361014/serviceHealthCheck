package service.health.check.updater;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

import javax.mail.MessagingException;
import javax.mail.Session;

import com.rabbitmq.client.Address;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;

import service.health.check.messages.Config;

public class App {

    public App() {
    }

    private static void sendEmail() throws UnsupportedEncodingException, MessagingException {
        String smtpHostServer = "mail";
        String email = "reszrlfz@sharklasers.com";

        Properties props = System.getProperties();

        props.put("mail.smtp.host", smtpHostServer);

        Session session = Session.getInstance(props, null);

        EmailUtil.sendEmail(session, email, "Testing Subject", "Testing Body");
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
