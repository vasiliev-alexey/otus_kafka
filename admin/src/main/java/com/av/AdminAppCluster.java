package com.av;

import com.github.javafaker.Faker;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.ExecutionException;


public class AdminAppCluster {

    static final String[] topics = {"topic1", "topic2"};

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Logger logger = LoggerFactory.getLogger(AdminAppCluster.class.getName());

        Properties properties = Utils.loadProperties();


        var adminClient = AdminClient.create(properties);

        DescribeClusterResult cluster = adminClient.describeCluster();
        System.out.println("Connected to cluster " + cluster.clusterId().get());
        System.out.println("The brokers in the cluster are:");
        cluster.nodes().get().forEach(node -> System.out.println(" * " + node));
        System.out.println("The controller is: " + cluster.controller().get());

        adminClient.close(Duration.ofSeconds(30));


    }



}
