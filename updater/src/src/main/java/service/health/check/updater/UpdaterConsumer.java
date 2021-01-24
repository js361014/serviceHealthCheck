package service.health.check.updater;

import java.io.IOException;

import javax.persistence.EntityManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import lombok.extern.slf4j.Slf4j;
import service.health.check.messages.CheckedAddress;
import service.health.check.models.HibernateUtil;
import service.health.check.updater.managers.AddressManager;

@Slf4j
public class UpdaterConsumer extends DefaultConsumer {

    // dependencies
    private final CheckedAddressHandler checkedAddressHandler;
    private final ObjectMapper mapper;

    public UpdaterConsumer(Channel channel) {
        super(channel);
        EntityManager entityManager = HibernateUtil.getEntityManagerFactory().createEntityManager();
        AddressManager addressManager = new AddressManager(entityManager);
        this.checkedAddressHandler = new CheckedAddressHandler(addressManager);
        this.mapper = new ObjectMapper();
    }

    public void handleDelivery(String consumerTag, Envelope envelope,
                               AMQP.BasicProperties properties, byte[] body) throws IOException {
        String messageJson = new String(body);
        log.info("Updater - Message received: " + messageJson);
        CheckedAddress checkedAddress = getCheckedAddress(messageJson);
        checkedAddressHandler.handle(checkedAddress);
        log.info("Updater - Work done! " + messageJson);
    }

    private CheckedAddress getCheckedAddress(String messageJson) throws JsonProcessingException {
        return mapper.readValue(messageJson,
                                new TypeReference<CheckedAddress>() {
                                                         });
    }
}
