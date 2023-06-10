# kraft и kafka. настройка безопасности.

## Цель:

Научиться разворачивать kafka с помощью kraft и самостоятельно настраивать безопасность

## Описание/Пошаговая инструкция выполнения домашнего задания:

Развернуть Kafka с KRaft и настроить безопасность:

1. Запустить Kafka с Kraft:

* Сгенерировать UUID кластера
* Отформатировать папки для журналов
* Запустить брокер

2. Настроить аутентификацию SASL/PLAIN. Создать трёх пользователей с произвольными именами.
3. Настроить авторизацию. Создать топик. Первому пользователю выдать права на запись в этот топик. Второму пользователю
   выдать права на чтение этого топика. Третьему пользователю не выдавать никаких прав на этот топик.
4. От имени каждого пользователя выполнить команды:

* Получить список топиков
* Записать сообщения в топик
* Прочитать сообщения из топика
* В качестве результата ДЗ прислать документ с описанием настроек кластера, выполненных команд и снимков экрана с
  результатами команд.

---

### Решение

1. Генерируем Kafka cluster ID

 ```sh
   cat /proc/sys/kernel/random/uuid | tr -d '-' | base64 | cut -b 1-22   
```

Сохраняем занечение в переменные для установки в ./provisioners/vars/main

2. Создаем кластер из 3 виртуальных машин и распаковываем дистрибутив kafka

```sh
   vagrant up  --provision-with  infra_setup    
```

  <details>
  <summary>3. Конфигурируем кластер  </summary>

* [sasl в server.properties](./provisioners/templates/server.properties.j2)

```js

############################# Server Basics #############################

# The id of the broker. This must be set to a unique integer for each broker.
#broker.id=0

process.roles=broker,controller

# The node id associated with this instance's roles
node.id={{node_id}}

# The connect string for the controller quorum
controller.quorum.voters=1@kafka_server_1:9093,2@kafka_server_2:9093,3@kafka_server_2:9093


############################# Socket Server Settings #############################

# The address the socket server listens on. If not configured, the host name will be equal to the value of
# java.net.InetAddress.getCanonicalHostName(), with PLAINTEXT listener name, and port 9092.
#   FORMAT:
#     listeners = listener_name://host_name:port
#   EXAMPLE:
#     listeners = PLAINTEXT://your.host.name:9092
listeners=BROKER://:9092,CONTROLLER://:9093
listener.security.protocol.map=BROKER:SASL_PLAINTEXT,CONTROLLER:SASL_PLAINTEXT


# Listener, host name, and port for the controller to advertise to the brokers. If
# this server is a controller, this listener must be configured.

inter.broker.listener.name=BROKER
controller.listener.names=CONTROLLER

# Listener name, hostname and port the broker will advertise to clients.
# If not set, it uses the value for "listeners".
#advertised.listeners=PLAINTEXT://your.host.name:9092
advertised.listeners=BROKER://:9092



## SASL config
security.protocol=PLAIN #SASL_PLAINTEXT
sasl.enabled.mechanisms=PLAIN
sasl.mechanism.controller.protocol=PLAIN
sasl.mechanism.inter.broker.protocol=PLAIN
listener.name.broker.plain.sasl.jaas.config=org.apache.kafka.common.security.plain.PlainLoginModule required   username="admin"     password="secret"  user_admin="secret"  user_alice="secret_alice"  user_bob="secret_bob"  user_charlie="secret_charlie";  
listener.name.controller.plain.sasl.jaas.config=org.apache.kafka.common.security.plain.PlainLoginModule required     username="admin"    password="secret"  user_admin="secret" user_charlie="secret_charlie";

## ACL
authorizer.class.name=org.apache.kafka.metadata.authorizer.StandardAuthorizer
allow.everyone.if.no.acl.found=false
super.users=User:admin
```

* Создавая [3 мандата доступа для alice, bob , charlie](./provisioners/templates/sasl.config.properties.j2)

```sh
sasl.jaas.config=org.apache.kafka.common.security.plain.PlainLoginModule required username="{{item}}" password="secret_{{item}}";
security.protocol=SASL_PLAINTEXT
sasl.mechanism=PLAIN
```

* выдаем правва

```sh
.
/kafka-acls.sh --bootstrap-server {{boot_srvs}} --add --allow-principal User:alice --operation Write --topic {{topicname}}  --command-config  {{installation_dir}}/users_config / admin.config.properties
/kafka-acls.sh --bootstrap-server {{boot_srvs}} --add --allow-principal User:bob --operation Read --topic  {{topicname}}  --command-config  {{installation_dir}}/users_config / admin.config.properties
 /kafka-acls.sh --bootstrap-server {{boot_srvs}} --list --command-config  {{installation_dir}}/users_config / admin.config.properties
  /kafka-acls.sh --bootstrap-server {{boot_srvs}} --list --command-config  {{installation_dir}}/users_config / admin.config.properties

```       

```sh
   vagrant provision  --provision-with  kafka_install    
```

  </details>
  <details>
  <summary>4. Проверяем права доступа на просмотр топиков  </summary>

* Alice и Bob - есть права на топики и они видят топик test_kraft,
* Charlie - нет

```sh
➜  02_kraft_security git:(kraft) ✗ vssh kafka_server_3                                                                                                                                   vagrant@kafka-server-3:/opt/kafka/bin$ ./kafka-topics.sh --list  --bootstrap-server  kafka_server_1:9092,kafka_server_2:9092,kafka_server_3:9092 --command-config /opt/kafka/users_config/alice.config.conf 
test_kraft
vagrant@kafka-server-3:/opt/kafka/bin$ ./kafka-topics.sh --list  --bootstrap-server  kafka_server_1:9092,kafka_server_2:9092,kafka_server_3:9092 --command-config /opt/kafka/users_config/bob.config.conf 
test_kraft
vagrant@kafka-server-3:/opt/kafka/bin$ ./kafka-topics.sh --list  --bootstrap-server  kafka_server_1:9092,kafka_server_2:9092,kafka_server_3:9092 --command-config /opt/kafka/users_config/charlie.config.conf 

vagrant@kafka-server-3:/opt/kafka/bin$ 
```

</details>

<details>
  <summary>5. Проверяем права на запись в топик test_kraft  </summary>

* Alice есть права на запись,
* Bob - во время отправки ошибка доступа, ClusterAuthorizationException: Cluster authorization failed.
* Charlie - ошибка Not authorized to access topics: [test_kraft]

```sh
vagrant@kafka-server-3:/opt/kafka/bin$ ./kafka-console-producer.sh --bootstrap-server    kafka_server_1:9092,kafka_server_2:9092,kafka_server_3:9092 --topic test_kraft --producer.config /opt/kafka/users_config/alice.config.conf 
>alice sent message
>vagrant@kafka-server-3:/opt/kafka/bin$ ./kafka-console-producer.sh --bootstrap-server    kafka_server_1:9092,kafka_server_2:9092,kafka_server_3:9092 --topic test_kraft --producer.config /opt/kafka/users_config/bob.config.conf 
>bob send
org.apache.kafka.common.KafkaException: Cannot execute transactional method because we are in an error state
        at org.apache.kafka.clients.producer.internals.TransactionManager.maybeFailWithError(TransactionManager.java:1010)
        at org.apache.kafka.clients.producer.internals.TransactionManager.maybeAddPartition(TransactionManager.java:328)
        at org.apache.kafka.clients.producer.KafkaProducer.doSend(KafkaProducer.java:1061)
        at org.apache.kafka.clients.producer.KafkaProducer.send(KafkaProducer.java:962)
        at kafka.tools.ConsoleProducer$.send(ConsoleProducer.scala:70)
        at kafka.tools.ConsoleProducer$.main(ConsoleProducer.scala:52)
        at kafka.tools.ConsoleProducer.main(ConsoleProducer.scala)
Caused by: org.apache.kafka.common.errors.ClusterAuthorizationException: Cluster authorization failed.
[2023-06-10 12:39:19,852] ERROR [Producer clientId=console-producer] Aborting producer batches due to fatal error (org.apache.kafka.clients.producer.internals.Sender)
org.apache.kafka.common.errors.ClusterAuthorizationException: Cluster authorization failed.
[2023-06-10 12:39:19,854] ERROR Error when sending message to topic test_kraft with key: null, value: 8 bytes with error: (org.apache.kafka.clients.producer.internals.ErrorLoggingCallback)
org.apache.kafka.common.errors.ClusterAuthorizationException: Cluster authorization failed.
vagrant@kafka-server-3:/opt/kafka/bin$ ./kafka-console-producer.sh --bootstrap-server    kafka_server_1:9092,kafka_server_2:9092,kafka_server_3:9092 --topic test_kraft --producer.config /opt/kafka/users_config/charlie.config.conf
>charlie send
[2023-06-10 12:39:42,048] WARN [Producer clientId=console-producer] Error while fetching metadata with correlation id 4 : {test_kraft=TOPIC_AUTHORIZATION_FAILED} (org.apache.kafka.clients.NetworkClient)
[2023-06-10 12:39:42,054] ERROR [Producer clientId=console-producer] Topic authorization failed for topics [test_kraft] (org.apache.kafka.clients.Metadata)
[2023-06-10 12:39:42,056] ERROR Error when sending message to topic test_kraft with key: null, value: 12 bytes with error: (org.apache.kafka.clients.producer.internals.ErrorLoggingCallback)
org.apache.kafka.common.errors.TopicAuthorizationException: Not authorized to access topics: [test_kraft]
```

</details>

<details>
  <summary>6. Проверяем права на чтение из топика test_kraft  </summary>

* Alice нет прав на чтение, но ошибка не явная - не назначен ACL для записи в топики офсетов крафта для группы
* Bob - успешно прочитано
* Charlie - ошибка Not authorized to access topics: [test_kraft]

```sh
vagrant@kafka-server-3:/opt/kafka/bin$ ./kafka-console-consumer.sh --bootstrap-server  kafka_server_1:9092,kafka_server_2:9092,kafka_server_3:9092  --topic test_kraft -from-beginning  --consumer.config   /opt/kafka/users_config/alice.config.conf
[2023-06-10 13:29:58,324] ERROR Error processing message, terminating consumer process:  (kafka.tools.ConsoleConsumer$)
org.apache.kafka.common.errors.GroupAuthorizationException: Not authorized to access group: console-consumer-19564
Processed a total of 0 messages
vagrant@kafka-server-3:/opt/kafka/bin$ ./kafka-console-consumer.sh --bootstrap-server    kafka_server_1:9092,kafka_server_2:9092,kafka_server_3:9092 --topic test_kraft --group group.bob  --consumer.config /opt/kafka/users_config/bob.config.conf 

alice sent message
^CProcessed a total of 2 messages
vagrant@kafka-server-3:/opt/kafka/bin$ ./kafka-console-consumer.sh --bootstrap-server    kafka_server_1:9092,kafka_server_2:9092,kafka_server_3:9092 --topic test_kraft --group group.bob  --consumer.config /opt/kafka/users_config/charlie.config.conf
[2023-06-10 13:30:19,731] WARN [Consumer clientId=console-consumer, groupId=group.bob] Error while fetching metadata with correlation id 2 : {test_kraft=TOPIC_AUTHORIZATION_FAILED} (org.apache.kafka.clients.NetworkClient)
[2023-06-10 13:30:19,734] ERROR [Consumer clientId=console-consumer, groupId=group.bob] Topic authorization failed for topics [test_kraft] (org.apache.kafka.clients.Metadata)
[2023-06-10 13:30:19,738] ERROR Error processing message, terminating consumer process:  (kafka.tools.ConsoleConsumer$)
org.apache.kafka.common.errors.TopicAuthorizationException: Not authorized to access topics: [test_kraft]
Processed a total of 0 messages
vagrant@kafka-server-3:/opt/kafka/bin$ 
```

</details>





---
<details>
  <summary> Заметки и статьи  </summary>

[How to easily install kafka without zookeeper](https://adityasridhar.com/posts/how-to-easily-install-kafka-without-zookeeper   )  
https://awesome-it.de/2023/05/27/kafka-security-mtls-acl-authorization/client.truststore.jks  
[Введение во взаимную аутентификацию сервисов на Java c TLS/SSL](https://habr.com/ru/companies/dbtc/articles/487318/  )
https://docs.vmware.com/en/VMware-Smart-Assurance/1.1.9/deployment-scenarios/GUID-118261F7-4B70-4C87-BBE7-1D00AF4CA0C0.html
https://www.vertica.com/docs/9.3.x/HTML/Content/Authoring/KafkaIntegrationGuide/TLS-SSL/KafkaTLS-SSLExamplePart3ConfigureKafka.htm  
https://www.ibm.com/docs/en/cloud-paks/cp-biz-automation/20.0.x?topic=emitter-preparing-ssl-certificates-kafka

[Обеспечение безопасности в Apache Kafka](https://habr.com/ru/companies/otus/articles/727552/)  
[Kafka: Часть 9 — Описание ACL прав Kafka](https://inaword.ru/security/kafka-chast-10-opisanie-acl-prav-kafka/)

</details>
