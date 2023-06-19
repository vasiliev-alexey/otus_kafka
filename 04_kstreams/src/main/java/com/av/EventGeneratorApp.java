package com.av;

import com.github.javafaker.Faker;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;


import java.util.Properties;


public class EventGeneratorApp {
    private static final String topic = "events";
    private static final Faker faker = new Faker();

    public static void main(String[] args) throws InterruptedException {
        Properties properties = Utils.loadProperties();
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(properties)) {
            while (true) {
                var record = new ProducerRecord<String, String>(topic, faker.esports().team(), faker.esports().event());
                producer.send(record);
                Thread.sleep(300);
            }
        }
    }
}
