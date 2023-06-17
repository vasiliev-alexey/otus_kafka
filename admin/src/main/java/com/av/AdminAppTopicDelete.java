package com.av;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;


public class AdminAppTopicDelete {

    static final String[] topics = {"topic1", "topic2"};

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Logger logger = LoggerFactory.getLogger(AdminAppTopicDelete.class.getName());

        Properties properties = Utils.loadProperties();


        var adminClient = AdminClient.create(properties);


        Map<TopicPartition, OffsetSpec> requestLatestOffsets = new HashMap<>();


        Map<TopicPartition, OffsetAndMetadata> offsets =
                adminClient.listConsumerGroupOffsets("first")
                        .partitionsToOffsetAndMetadata().get();

        for (TopicPartition tp : offsets.keySet()) {
            requestLatestOffsets.put(tp, OffsetSpec.latest());
        }

        Map<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo> olderOffsets =
                adminClient.listOffsets(requestLatestOffsets).all().get();
        Map<TopicPartition, RecordsToDelete> recordsToDelete = new HashMap<>();
        for (Map.Entry<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo> e :
                olderOffsets.entrySet())
            recordsToDelete.put(e.getKey(),
                    RecordsToDelete.beforeOffset(e.getValue().offset()-1000));
        adminClient.deleteRecords(recordsToDelete).all().get();

        adminClient.close(Duration.ofSeconds(30));


    }


}
