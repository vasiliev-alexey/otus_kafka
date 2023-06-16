package com.av;

import com.github.javafaker.Faker;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewPartitions;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;


public class AdminAppTopicExtend {

    static final String[] topics = {"topic1", "topic2"};

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Logger logger = LoggerFactory.getLogger(AdminAppTopicExtend.class.getName());

        Properties properties = Utils.loadProperties();


        var adminClient = AdminClient.create(properties);

        Map<String, NewPartitions> newPartitions = new HashMap<>();
        newPartitions.put("topic1", NewPartitions.increaseTo(2));
        adminClient.createPartitions(newPartitions).all().get();

        adminClient.close(Duration.ofSeconds(30));


    }


}
