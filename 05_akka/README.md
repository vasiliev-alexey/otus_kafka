# akka streams, alpakka.

## закрепить знания, полученные на занятии

## Описание/Пошаговая инструкция выполнения домашнего задания:

[в репозитории](https://github.com/ValentinShilinDe/kafka-mooc-2023-04) есть [темплэйт домашней работы](kafka-mooc-2023-04\src\main\scala\akka_akka_streams\homework\homeworktemplate.scala)

 написать граф дсл, где есть какой то входной поток(целочисленный), он должен быть разделен на 3 (broadcast):
 *   первый поток - все элементы умножаем на 10
 *   второй поток - все элементы умножаем на 2
 *   третий поток - все элементы умножаем на 3
 *   потом собираем это все в один поток (zip) в котором эти 3 подпотока должны быть сложены:
  ```sh
       1 2 3 4 5 -> 1 2 3 4 5-> 10 20 30 40 50 
-> 1 2 3 4 5-> 2 4 6 8 10 -> (10,2,3), (20,4,6),(30,6,9),(40,8,12),(50,10,15)-> 15, 30, 45, 60, 75  -> 1 2 3 4 5 -> 3 6 9 12 15
```

---                                                   
