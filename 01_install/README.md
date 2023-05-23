# Запуск Kafka

## Цель: Научиться самостоятельно запускать Kafka (быстрый старт)

## Описание/Пошаговая инструкция выполнения домашнего задания:


Самостоятельно запустить Kafka по предложенному алгоритму:

*    Установить Java JDK
*    Скачать Kafka с сайта kafka.apache.org и развернуть на локальном диске
*    Запустить Zookeeper
*    Запустить Kafka Broker
*    Создать топик test
*    Записать несколько сообщений в топик
*    Прочитать сообщения из топика


---
### Решение



1. Создаем инфраструктуру [в виде виртуальной  машины Virtual Box и Vagrant](./Vagrantfile).
2. Пишем [Ansible playbook](./provisioners/kafka_install.yml) 
<details> 
<summary>3. Поднимаем инфраструктуру и накатываем плейбук с установкой JRE + Kafka + Zookeeper Service + Patch JVM Config</summary>
<pre>
➜  hw1 git:(master) ✗ vagrant up

Bringing machine 'otus_kafka' up with 'virtualbox' provider...
==> otus_kafka: Importing base box 'ubuntu/jammy64'...
==> otus_kafka: Matching MAC address for NAT networking...
==> otus_kafka: Setting the name of the VM: hw1_otus_kafka_1684859617972_30660
==> otus_kafka: Clearing any previously set network interfaces...
==> otus_kafka: Preparing network interfaces based on configuration...
    otus_kafka: Adapter 1: nat
    otus_kafka: Adapter 2: hostonly
==> otus_kafka: Forwarding ports...
    otus_kafka: 22 (guest) => 2222 (host) (adapter 1)
==> otus_kafka: Running 'pre-boot' VM customizations...
==> otus_kafka: Booting VM...
==> otus_kafka: Waiting for machine to boot. This may take a few minutes...
    otus_kafka: SSH address: 127.0.0.1:2222
    otus_kafka: SSH username: vagrant
    otus_kafka: SSH auth method: private key
    otus_kafka: 
    otus_kafka: Vagrant insecure key detected. Vagrant will automatically replace
    otus_kafka: this with a newly generated keypair for better security.
    otus_kafka: 
    otus_kafka: Inserting generated public key within guest...
    otus_kafka: Removing insecure key from the guest if it's present...
    otus_kafka: Key inserted! Disconnecting and reconnecting using new SSH key...
==> otus_kafka: Machine booted and ready!
==> otus_kafka: Checking for guest additions in VM...
    otus_kafka: The guest additions on this VM do not match the installed version of
    otus_kafka: VirtualBox! In most cases this is fine, but in rare cases it can
    otus_kafka: prevent things such as shared folders from working properly. If you see
    otus_kafka: shared folder errors, please make sure the guest additions within the
    otus_kafka: virtual machine match the version of VirtualBox you have installed on
    otus_kafka: your host and reload your VM.
    otus_kafka: 
    otus_kafka: Guest Additions Version: 6.0.0 r127566
    otus_kafka: VirtualBox Version: 7.0
==> otus_kafka: Setting hostname...
==> otus_kafka: Configuring and enabling network interfaces...
==> otus_kafka: Rsyncing folder: /lab/otus_kafka/hw1/ => /vagrant
==> otus_kafka: Running provisioner: ansible_local...
    otus_kafka: Installing Ansible...
    otus_kafka: Running ansible-playbook...

PLAY [Otus kafka install] ******************************************************

TASK [Gathering Facts] *********************************************************
ok: [otus_kafka]

TASK [Include vars] ************************************************************
ok: [otus_kafka]

TASK [Install JRE after apt update] ********************************************
changed: [otus_kafka]

TASK [Create a group] **********************************************************
changed: [otus_kafka]

TASK [Create an user] **********************************************************
changed: [otus_kafka]

TASK [Create a Installation Directory] *****************************************
changed: [otus_kafka]

TASK [Download Kafka and Unzip] ************************************************
[WARNING]: Module remote_tmp /home/kafka/.ansible/tmp did not exist and was
created with a mode of 0700, this may cause issues when running as another
user. To avoid this, create the remote_tmp dir with the correct permissions
manually
changed: [otus_kafka]

TASK [Move all the files to parent Directory] **********************************
changed: [otus_kafka]

TASK [Update the log path] *****************************************************
changed: [otus_kafka]

TASK [Update the Java Heap Size for Kafka] *************************************
changed: [otus_kafka]

TASK [Create a Service file for ZooKeeper with Copy module] ********************
changed: [otus_kafka]

TASK [Create a Service file for Kafka with Copy module] ************************
changed: [otus_kafka]

TASK [Start Services] **********************************************************
changed: [otus_kafka] => (item=zookeeper)
changed: [otus_kafka] => (item=kafka)

TASK [Validating if zookeeper is up and listening on port 2181] ****************
ok: [otus_kafka]

TASK [Validating if Kafka is up and listening on port 9092] ********************
ok: [otus_kafka]

TASK [Create a Topic] **********************************************************
changed: [otus_kafka]

PLAY RECAP *********************************************************************
otus_kafka                 : ok=16   changed=12   unreachable=0    failed=0    skipped=0    rescued=0    ignored=0   
</pre>

</details>

<details> 
<summary>4. Подключаемся и создаем сообщения в созданный topic test</summary>

<pre>
vagrant  ssh    
</pre>

<pre>
Welcome to Ubuntu 22.04 LTS (GNU/Linux 5.15.0-41-generic x86_64)

 * Documentation:  https://help.ubuntu.com
 * Management:     https://landscape.canonical.com
 * Support:        https://ubuntu.com/advantage

  System information as of Tue May 23 16:39:38 UTC 2023

  System load:  0.0986328125      Processes:               112
  Usage of /:   6.4% of 38.70GB   Users logged in:         0
  Memory usage: 34%               IPv4 address for enp0s3: 10.0.2.15
  Swap usage:   0%                IPv4 address for enp0s8: 192.168.56.101


178 updates can be applied immediately.
103 of these updates are standard security updates.
To see these additional updates run: apt list --upgradable


vagrant@kafka-server:~$ cd /opt/kafka/bin/
vagrant@kafka-server:/opt/kafka/bin$  ./kafka-console-producer.sh --topic test --bootstrap-server localhost:9092
>Test on crete topic
>go  
>go 
>Task 1 complite  
>vagrant@kafka-server:/opt/kafka/bin$ 
</pre>

</details> 

<details> 
<summary>5. Подключаемся и зачитываем ранее созданные сообщения</summary>

<pre>
vagrant  ssh    
</pre>

<pre>
Welcome to Ubuntu 22.04 LTS (GNU/Linux 5.15.0-41-generic x86_64)

 * Documentation:  https://help.ubuntu.com
 * Management:     https://landscape.canonical.com
 * Support:        https://ubuntu.com/advantage

  System information as of Tue May 23 16:42:45 UTC 2023

  System load:  0.001953125       Processes:               112
  Usage of /:   6.4% of 38.70GB   Users logged in:         0
  Memory usage: 35%               IPv4 address for enp0s3: 10.0.2.15
  Swap usage:   0%                IPv4 address for enp0s8: 192.168.56.101


178 updates can be applied immediately.
103 of these updates are standard security updates.
To see these additional updates run: apt list --upgradable


Last login: Tue May 23 16:39:49 2023 from 10.0.2.2
vagrant@kafka-server:~$ cd /opt/kafka/bin/
vagrant@kafka-server:/opt/kafka/bin$ ./kafka-console-consumer.sh --topic test --from-beginning --bootstrap-server localhost:9092
Test on crete topic
go
go
Task 1 complite
^CProcessed a total of 4 messages
vagrant@kafka-server:/opt/kafka/bin$ 


</pre>

</details> 

---