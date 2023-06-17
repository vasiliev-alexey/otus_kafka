package com.av;

import com.github.javafaker.Faker;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.UnknownMemberIdException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;


public class AdminAppConsumer {

    static final String[] topics = {"topic1", "topic2"};

    static String CONSUMER_GROUP = "first";

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Logger logger = LoggerFactory.getLogger(AdminAppConsumer.class.getName());

        Properties properties = Utils.loadProperties();


        var adminClient = AdminClient.create(properties);


        describeConsumerGroup(adminClient);


          resetOffset(adminClient);

        adminClient.close(Duration.ofSeconds(30));


    }


   private static void resetOffset( AdminClient adminClient) throws ExecutionException, InterruptedException {
       Map<TopicPartition, OffsetSpec> requestLatestOffsets = new HashMap<>();


       Map<TopicPartition, OffsetAndMetadata> offsets =
               adminClient.listConsumerGroupOffsets("first")
                       .partitionsToOffsetAndMetadata().get();

       for(TopicPartition tp: offsets.keySet()) {
           requestLatestOffsets.put(tp, OffsetSpec.latest());
       }
       Map<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo> earliestOffsets =
               adminClient.listOffsets(requestLatestOffsets).all().get();
       Map<TopicPartition, OffsetAndMetadata> resetOffsets = new HashMap<>();
       for (Map.Entry<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo> e:
               earliestOffsets.entrySet()) {
           resetOffsets.put(e.getKey(), new OffsetAndMetadata(e.getValue().offset()-1000000));
       }
       try {
           adminClient.alterConsumerGroupOffsets(CONSUMER_GROUP, resetOffsets).all().get();
       } catch (ExecutionException e) {
           System.out.println("Failed to update the offsets committed by group "
                   + CONSUMER_GROUP + " with error " + e.getMessage());
           if (e.getCause() instanceof UnknownMemberIdException)
               System.out.println("Check if consumer group is still active.");
       }
    }

    private static void describeConsumerGroup(AdminClient adminClient) throws ExecutionException, InterruptedException {

        adminClient.listConsumerGroups().valid().get().forEach(System.out::println);



        Map<TopicPartition, OffsetAndMetadata> offsets =
                adminClient.listConsumerGroupOffsets("first")
                        .partitionsToOffsetAndMetadata().get();
        Map<TopicPartition, OffsetSpec> requestLatestOffsets = new HashMap<>();



        for(TopicPartition tp: offsets.keySet()) {
            requestLatestOffsets.put(tp, OffsetSpec.latest());
        }
        Map<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo> latestOffsets =
                adminClient.listOffsets(requestLatestOffsets).all().get();
        for (Map.Entry<TopicPartition, OffsetAndMetadata> e: offsets.entrySet()) {
            String topic = e.getKey().topic();
            int partition = e.getKey().partition();
            long committedOffset = e.getValue().offset();
            long latestOffset = latestOffsets.get(e.getKey()).offset();
            System.out.println("Consumer group " + "first"
                    + " has committed offset " + committedOffset
                    + " to topic " + topic + " partition " + partition
                    + ". The latest offset in the partition is "
                    + latestOffset + " so consumer group is "
                    + (latestOffset - committedOffset) + " records behind");
        }




    }



}
