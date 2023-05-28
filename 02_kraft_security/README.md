# kraft и kafka. настройка безопасности.

## Цель: 
Научиться разворачивать kafka с помощью kraft и самостоятельно настраивать безопасность 

## Описание/Пошаговая инструкция выполнения домашнего задания:

Развернуть Kafka с KRaft и настроить безопасность:

1.    Запустить Kafka с Kraft:

       *   Сгенерировать UUID кластера;
       *   Отформатировать папки для журналов
       *   Запустить брокер

2.   Настроить аутентификацию
3.   Настроить авторизацию



---
### Решение

1. Генерируем Kafka cluster ID
```sh
cat /proc/sys/kernel/random/uuid | tr -d '-' | base64 | cut -b 1-22   
```
Сохраняем занечение в переменные для установки в  
./provisioners/vars/main

./kafka-server-start.sh  ../config/server.properties

./kafka-cluster.sh cluster-id --bootstrap-server kafka_server_1:9092,kafka_server_2:9092,kafka_server_3:9092

./kafka-topics.sh --create --topic test --bootstrap-server kafka_server_1:9092,kafka_server_2:9092,kafka_server_3:9092

sudo systemctl start kafka


--
 openssl req -new -x509 -keyout ca-key -out ca-cert -days 365  -passout pass:foobar  -config openssl.cnf 

/opt/kafka/bin//kafka-console-producer.sh --topic test  t --bootstrap-server kafka_server_1:9092,kafka_server_2:9092,kafka_server_3:9092
 /opt/kafka/bin/kafka-console-consumer.sh --topic test_kraft --from-beginning --bootstrap-server kafka_server_1:9092,kafka_server_2:9092,kafka_server_3:9092

1.   

---
https://adityasridhar.com/posts/how-to-easily-install-kafka-without-zookeeper  
https://awesome-it.de/2023/05/27/kafka-security-mtls-acl-authorization/
client.truststore.jks
https://habr.com/ru/companies/dbtc/articles/487318/


https://docs.vmware.com/en/VMware-Smart-Assurance/10.1.9/deployment-scenarios/GUID-118261F7-4B70-4C87-BBE7-1D00AF4CA0C0.html

https://www.vertica.com/docs/9.3.x/HTML/Content/Authoring/KafkaIntegrationGuide/TLS-SSL/KafkaTLS-SSLExamplePart3ConfigureKafka.htm
https://www.ibm.com/docs/en/cloud-paks/cp-biz-automation/20.0.x?topic=emitter-preparing-ssl-certificates-kafka