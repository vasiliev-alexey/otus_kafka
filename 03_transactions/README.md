# Разработка приложения с транзакциями

## Цель: Научиться самостоятельно разрабатывать и запускать приложения с транзакциями

## Описание/Пошаговая инструкция выполнения домашнего задания:

Самостоятельно запустить Kafka по предложенному алгоритму:

* Запустить Kafka
* Создать два топика: topic1 и topic2
* Разработать приложение, которое:
* открывает транзакцию
* отправляет по 5 сообщений в каждый топик
* подтверждает транзакцию
* открывает другую транзакцию
* отправляет по 2 сообщения в каждый топик
* отменяет транзакцию
* Разработать приложение, которое будет читать сообщения из топиков topic1 и topic2 так, чтобы сообщения из
  подтверждённой транзакции были выведены, а из неподтверждённой - нет

---

### Решение

1. Реализуем [ProducerApp](./src/main/java/com/av/ProducerApp.java)
2. Реализуем [ConsumerApp](./src/main/java/com/av/ConsumerApp.java)
3. Стартуем kafka d Docker Compose

    ```shell
    docker-compose up -d
    ```

4. Запускаем приложение ConsumerApp
5. Запускаем приложение ProducerApp
6. Проверяем результат

 <details>
    <summary>Результат ProducerApp</summary>
    
```sh    
    19:00:05.675 [main] INFO  com.av.ProducerApp - Try message id 0 
    19:00:05.677 [main] INFO  com.av.ProducerApp - start new transaction by id 0
    19:00:05.703 [main] INFO  com.av.ProducerApp - Record created: ProducerRecord(topic=topic1, partition=null, headers=RecordHeaders(headers = [], isReadOnly = false), key=null, value={"address":"topic: topic1 msgId: 0","fact":"Chuck Norris doesn't need garbage collection because he doesn't call .Dispose(), he calls .DropKick()."}, timestamp=null)
    19:00:05.714 [main] INFO  com.av.ProducerApp - Record created: ProducerRecord(topic=topic2, partition=null, headers=RecordHeaders(headers = [], isReadOnly = false), key=null, value={"address":"topic: topic2 msgId: 0","fact":"Chuck Norris doesn't delete files, he blows them away."}, timestamp=null)
    19:00:05.725 [kafka-producer-network-thread | producer-app-transactions] INFO  com.av.ProducerApp - Sent successfully. Metadata: topic1-0@87
    19:00:05.725 [kafka-producer-network-thread | producer-app-transactions] INFO  com.av.ProducerApp - Sent successfully. Metadata: topic2-0@87
    19:00:06.716 [main] INFO  com.av.ProducerApp - Try message id 1 
    19:00:06.717 [main] INFO  com.av.ProducerApp - Record created: ProducerRecord(topic=topic1, partition=null, headers=RecordHeaders(headers = [], isReadOnly = false), key=null, value={"address":"topic: topic1 msgId: 1","fact":"Whiteboards are white because Chuck Norris scared them that way."}, timestamp=null)
    19:00:06.717 [main] INFO  com.av.ProducerApp - Record created: ProducerRecord(topic=topic2, partition=null, headers=RecordHeaders(headers = [], isReadOnly = false), key=null, value={"address":"topic: topic2 msgId: 1","fact":"Chuck Norris breaks RSA 128-bit encrypted codes in milliseconds."}, timestamp=null)
    19:00:06.719 [kafka-producer-network-thread | producer-app-transactions] INFO  com.av.ProducerApp - Sent successfully. Metadata: topic1-0@88
    19:00:06.719 [kafka-producer-network-thread | producer-app-transactions] INFO  com.av.ProducerApp - Sent successfully. Metadata: topic2-0@88
    19:00:07.717 [main] INFO  com.av.ProducerApp - Try message id 2 
    19:00:07.718 [main] INFO  com.av.ProducerApp - Record created: ProducerRecord(topic=topic1, partition=null, headers=RecordHeaders(headers = [], isReadOnly = false), key=null, value={"address":"topic: topic1 msgId: 2","fact":"Chuck Norris doesn't get compiler errors, the language changes itself to accommodate Chuck Norris."}, timestamp=null)
    19:00:07.718 [main] INFO  com.av.ProducerApp - Record created: ProducerRecord(topic=topic2, partition=null, headers=RecordHeaders(headers = [], isReadOnly = false), key=null, value={"address":"topic: topic2 msgId: 2","fact":"Chuck Norris doesn't believe in floating point numbers because they can't be typed on his binary keyboard."}, timestamp=null)
    19:00:07.721 [kafka-producer-network-thread | producer-app-transactions] INFO  com.av.ProducerApp - Sent successfully. Metadata: topic1-0@89
    19:00:07.721 [kafka-producer-network-thread | producer-app-transactions] INFO  com.av.ProducerApp - Sent successfully. Metadata: topic2-0@89
    19:00:08.718 [main] INFO  com.av.ProducerApp - Try message id 3 
    19:00:08.719 [main] INFO  com.av.ProducerApp - Record created: ProducerRecord(topic=topic1, partition=null, headers=RecordHeaders(headers = [], isReadOnly = false), key=null, value={"address":"topic: topic1 msgId: 3","fact":"Chuck Norris can solve the Towers of Hanoi in one move."}, timestamp=null)
    19:00:08.719 [main] INFO  com.av.ProducerApp - Record created: ProducerRecord(topic=topic2, partition=null, headers=RecordHeaders(headers = [], isReadOnly = false), key=null, value={"address":"topic: topic2 msgId: 3","fact":"Chuck Norris doesn't get compiler errors, the language changes itself to accommodate Chuck Norris."}, timestamp=null)
    19:00:08.721 [kafka-producer-network-thread | producer-app-transactions] INFO  com.av.ProducerApp - Sent successfully. Metadata: topic1-0@90
    19:00:08.721 [kafka-producer-network-thread | producer-app-transactions] INFO  com.av.ProducerApp - Sent successfully. Metadata: topic2-0@90
    19:00:09.719 [main] INFO  com.av.ProducerApp - Try message id 4 
    19:00:09.720 [main] INFO  com.av.ProducerApp - Record created: ProducerRecord(topic=topic1, partition=null, headers=RecordHeaders(headers = [], isReadOnly = false), key=null, value={"address":"topic: topic1 msgId: 4","fact":"When Chuck Norris' code fails to compile the compiler apologises."}, timestamp=null)
    19:00:09.720 [main] INFO  com.av.ProducerApp - Record created: ProducerRecord(topic=topic2, partition=null, headers=RecordHeaders(headers = [], isReadOnly = false), key=null, value={"address":"topic: topic2 msgId: 4","fact":"Chuck Norris doesn't bug hunt, as that signifies a probability of failure. He goes bug killing."}, timestamp=null)
    19:00:09.720 [main] INFO  com.av.ProducerApp - commit transaction by id 4
    19:00:09.722 [kafka-producer-network-thread | producer-app-transactions] INFO  com.av.ProducerApp - Sent successfully. Metadata: topic1-0@91
    19:00:09.723 [kafka-producer-network-thread | producer-app-transactions] INFO  com.av.ProducerApp - Sent successfully. Metadata: topic2-0@91
    19:00:10.724 [main] INFO  com.av.ProducerApp - Try message id 5 
    19:00:10.724 [main] INFO  com.av.ProducerApp - start new transaction by id 5
    19:00:10.724 [main] INFO  com.av.ProducerApp - Record created: ProducerRecord(topic=topic1, partition=null, headers=RecordHeaders(headers = [], isReadOnly = false), key=null, value={"address":"topic: topic1 msgId: 5","fact":"There is no Esc key on Chuck Norris' keyboard, because no one escapes Chuck Norris."}, timestamp=null)
    19:00:10.724 [main] INFO  com.av.ProducerApp - Record created: ProducerRecord(topic=topic2, partition=null, headers=RecordHeaders(headers = [], isReadOnly = false), key=null, value={"address":"topic: topic2 msgId: 5","fact":"All arrays Chuck Norris declares are of infinite size, because Chuck Norris knows no bounds."}, timestamp=null)
    19:00:10.729 [kafka-producer-network-thread | producer-app-transactions] INFO  com.av.ProducerApp - Sent successfully. Metadata: topic1-0@93
    19:00:10.729 [kafka-producer-network-thread | producer-app-transactions] INFO  com.av.ProducerApp - Sent successfully. Metadata: topic2-0@93
    19:00:11.725 [main] INFO  com.av.ProducerApp - Try message id 6 
    19:00:11.725 [main] INFO  com.av.ProducerApp - Record created: ProducerRecord(topic=topic1, partition=null, headers=RecordHeaders(headers = [], isReadOnly = false), key=null, value={"address":"topic: topic1 msgId: 6","fact":"Chuck Norris can binary search unsorted data."}, timestamp=null)
    19:00:11.725 [main] INFO  com.av.ProducerApp - Record created: ProducerRecord(topic=topic2, partition=null, headers=RecordHeaders(headers = [], isReadOnly = false), key=null, value={"address":"topic: topic2 msgId: 6","fact":"Chuck Norris doesn't delete files, he blows them away."}, timestamp=null)
    19:00:11.725 [main] INFO  com.av.ProducerApp - abort transaction by id 6
    19:00:11.728 [kafka-producer-network-thread | producer-app-transactions] INFO  com.av.ProducerApp - Sent successfully. Metadata: topic1-0@94
    19:00:11.728 [kafka-producer-network-thread | producer-app-transactions] INFO  com.av.ProducerApp - Sent successfully. Metadata: topic2-0@94
    
    Process finished with exit code 0
```
    
</details>
    
    
<details>
          <summary>Результат ConsumerApp</summary>
          
```sh

          19:00:09.726 [main] INFO  com.av.ConsumerApp - partition 0| offset 87| {"address":"topic: topic1 msgId: 0","fact":"Chuck Norris doesn't need garbage collection because he doesn't call .Dispose(), he calls .DropKick()."}
          19:00:09.726 [main] INFO  com.av.ConsumerApp - partition 0| offset 88| {"address":"topic: topic1 msgId: 1","fact":"Whiteboards are white because Chuck Norris scared them that way."}
          19:00:09.726 [main] INFO  com.av.ConsumerApp - partition 0| offset 89| {"address":"topic: topic1 msgId: 2","fact":"Chuck Norris doesn't get compiler errors, the language changes itself to accommodate Chuck Norris."}
          19:00:09.726 [main] INFO  com.av.ConsumerApp - partition 0| offset 90| {"address":"topic: topic1 msgId: 3","fact":"Chuck Norris can solve the Towers of Hanoi in one move."}
          19:00:09.726 [main] INFO  com.av.ConsumerApp - partition 0| offset 91| {"address":"topic: topic1 msgId: 4","fact":"When Chuck Norris' code fails to compile the compiler apologises."}
          19:00:09.726 [main] INFO  com.av.ConsumerApp - partition 0| offset 87| {"address":"topic: topic2 msgId: 0","fact":"Chuck Norris doesn't delete files, he blows them away."}
          19:00:09.726 [main] INFO  com.av.ConsumerApp - partition 0| offset 88| {"address":"topic: topic2 msgId: 1","fact":"Chuck Norris breaks RSA 128-bit encrypted codes in milliseconds."}
          19:00:09.726 [main] INFO  com.av.ConsumerApp - partition 0| offset 89| {"address":"topic: topic2 msgId: 2","fact":"Chuck Norris doesn't believe in floating point numbers because they can't be typed on his binary keyboard."}
          19:00:09.726 [main] INFO  com.av.ConsumerApp - partition 0| offset 90| {"address":"topic: topic2 msgId: 3","fact":"Chuck Norris doesn't get compiler errors, the language changes itself to accommodate Chuck Norris."}
          19:00:09.726 [main] INFO  com.av.ConsumerApp - partition 0| offset 91| {"address":"topic: topic2 msgId: 4","fact":"Chuck Norris doesn't bug hunt, as that signifies a probability of failure. He goes bug killing."}
          
```
          
</details>

Видим что цель достигнута

---