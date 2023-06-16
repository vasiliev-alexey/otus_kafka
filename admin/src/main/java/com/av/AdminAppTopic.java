package com.av;

import com.github.javafaker.Faker;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;


public class AdminAppTopic {

    static final String[] topics = {"topic1", "topic2"};

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Logger logger = LoggerFactory.getLogger(AdminAppTopic.class.getName());

        Properties properties = Utils.loadProperties();


        var adminClient = AdminClient.create(properties);

        dropTopics(adminClient);
         createTopics(adminClient);

        adminClient.close(Duration.ofSeconds(30));


    }


    private static void createTopics(AdminClient client) {

        var topicList = new ArrayList<NewTopic>();


        for (int i = 0; i < 10; i++) {

            var topicName = Faker.instance().cat().name();
            topicList.add(new NewTopic(topicName, 1, (short) 1));
        }

        var res = client.createTopics(topicList);

        topicList.forEach(t -> {
            try {
                System.out.println(res.topicId(t.name()).get());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
            ;
        });
    }

    private static  void  dropTopics(AdminClient client) throws ExecutionException, InterruptedException {

        var topicNames = client.listTopics().names().get();

        client.deleteTopics(topicNames).all();

    }

}
