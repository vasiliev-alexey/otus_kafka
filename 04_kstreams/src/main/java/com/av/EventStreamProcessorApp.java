package com.av;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Properties;

import org.apache.kafka.streams.kstream.KStream;

public class EventStreamProcessorApp {
    private static final String EVENTS = "events";
    static final String EVENTS_STATS = "events-stats";
    static final Duration EVENT_MONITOR_WINDOW = Duration.ofMinutes(5);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");


    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(EventStreamProcessorApp.class.getName());
        final Properties streamsConfiguration = Utils.loadProperties();
        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "EventStreamProcessorApp");
        streamsConfiguration.put(StreamsConfig.CLIENT_ID_CONFIG, "event-streams-app-client");
        streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        final var builder = new StreamsBuilder();
        var producedCount = Produced.with(Serdes.String(), Serdes.Long());
        KStream<String, String> events = builder.stream(EVENTS);
        events.groupByKey().windowedBy(TimeWindows.of(EVENT_MONITOR_WINDOW)).count().toStream().map((key, value) -> new KeyValue<>(key.key() + "@" + dateFormat.format(key.window().start()) + "->" + dateFormat.format(key.window().end()), value)).to(EVENTS_STATS, producedCount);
        KafkaStreams streams = new KafkaStreams(builder.build(), streamsConfiguration);

        try {
            streams.cleanUp();
            streams.start();
            logger.info("start processor");
            Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
        } catch (Exception e) {
            logger.error(e.getMessage());
        }


    }


}
