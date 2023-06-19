# Разработка приложения Kafka Streams.

## Разработать приложение Kafka Streams

## Описание/Пошаговая инструкция выполнения домашнего задания:

Разработка приложения Kafka Streams:

* Запустить Kafka
* Создать топик events
* Разработать приложение, которое подсчитывает количество событий с одинаковыми key в рамках сессии 5 минут
* Для проверки отправлять сообщения, используя console producer

---

### Решение

1. Реализуем  процессор событий [EventStreamProcessorApp](./src/main/java/com/av/EventStreamProcessorApp.java)
2. Реализуем генератор событий [EventGeneratorApp](./src/main/java/com/av/EventGeneratorApp.java)
3. Реализуем монитор статистики по событиям [EventStatMonitorApp](./src/main/java/com/av/EventStatMonitorApp.java)
4. Поднимаем [Docker-compose](./docker-compose.yml)
    ```shell
    docker-compose up -d
    ```
5. Запускем EventGeneratorApp, EventStreamProcessorApp, EventStatMonitorApp

---
### Резульат
Созданные топики

![Созданные топики](./doc/Screenshot%202023-06-18%20at%2011-59-43%20Kafdrop%20Broker%20List.png)

Монитор статистики событий

![Монитор статистики событий](./doc/Screenshot%20from%202023-06-18%2012-00-53.png)

