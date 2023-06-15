package com.av;

import com.github.javafaker.Faker;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Properties;

import static java.lang.String.format;


public class ProducerApp {
    private static final String[] topics = {"topic1", "topic2"};

    private static final Faker faker = new Faker();

    public static void main(String[] args) throws InterruptedException {
        Logger logger = LoggerFactory.getLogger(ProducerApp.class.getName());


        Properties properties = Utils.loadProperties();
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "app-transactions");


        try (KafkaProducer<String, String> producer = new KafkaProducer<>(properties)) {

            producer.initTransactions();
            for (int i = 0; i < 7; i++) {
                logger.info("Try message id {} ", i);
                if (i == 0 || i == 5) {
                    logger.info("start new transaction by id {}", i);
                    producer.beginTransaction();
                }

                for (String topic : topics) {
                    var message = generateMessage(topic, i);
                    var record = new ProducerRecord<String, String>(topic, message.toString());
                    logger.info("Record created: " + record);
                    producer.send(record, (metadata, exception) -> {
                        if (exception == null) {
                            logger.info("Sent successfully. Metadata: " + metadata.toString());
                        } else {
                            exception.printStackTrace();
                        }
                    });
                }
                if (i == 4) {
                    logger.info("commit transaction by id {}", i);
                    producer.commitTransaction();
                } else if (i == 6) {
                    logger.info("abort transaction by id {}", i);
                    producer.abortTransaction();
                }
                Thread.sleep(1000);
            }
        }
    }

    private static JSONObject generateMessage(String topic, int i) {
        JSONObject message = new JSONObject();
        message.put("address", format("topic: %s msgId: %d", topic, i));
        message.put("fact", faker.chuckNorris().fact());
        return message;
    }

}
