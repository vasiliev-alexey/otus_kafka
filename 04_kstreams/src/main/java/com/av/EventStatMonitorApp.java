package com.av;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;


public class EventStatMonitorApp {

    private static final String[] topics = {"events-stats"};

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(EventStatMonitorApp.class.getName());
        Properties properties = Utils.loadProperties();
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "EventStatMonitorApp");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        try (KafkaConsumer<String, Long> consumer = new KafkaConsumer<String, Long>(properties)) {
            consumer.subscribe(Arrays.asList(topics));
            while (true) {
                ConsumerRecords<String, Long> records =
                        consumer.poll(Duration.ofSeconds(1));
                for (ConsumerRecord<String, Long> record : records) {
                    var keys = record.key().split("@");
                    if (keys.length == 2) {
                        logger.info("TimeWindow: {}  Team: {}  Count: {}", keys[1], keys[0], record.value());
                    } else {
                        logger.info("TimeWindow: {}  Count: {}", record.key(), record.value());
                    }
                }
            }
        }


    }

}
