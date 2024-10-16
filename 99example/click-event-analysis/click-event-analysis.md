
# Example Project

## Docker install

```shell
#!/bin/bash

# Add Docker's official GPG key:
sudo apt-get update
sudo apt-get install ca-certificates curl
sudo install -m 0755 -d /etc/apt/keyrings
sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
sudo chmod a+r /etc/apt/keyrings/docker.asc

# Add the repository to Apt sources:
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt-get update

# sudo snap install docker
sudo apt-get install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

# sudo addgroup --system docker
sudo adduser $USER docker
# newgrp docker
# sudo snap disable docker
# sudo snap enable docker

# Install docker-compose
sudo curl -L https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m) -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

## Project clone

```shell
git clone https://github.com/haemee/flink-training.git
```

## Project Infra

- zookeeper
- kafka
- mysql
- prometheus
- grafana

```shell
cd flink-training/operations-playground
docker-compose build
chmod 777 grafana
docker-compose up -d
```

## Set Up

### Connector 다운로드

```shell
wget https://repo.maven.apache.org/maven2/org/apache/flink/flink-connector-jdbc/1.16.3/flink-connector-jdbc-1.16.3.jar
wget https://repo.maven.apache.org/maven2/mysql/mysql-connector-java/8.0.19/mysql-connector-java-8.0.19.jar
wget https://repo1.maven.org/maven2/org/apache/flink/flink-shaded-guava/30.1.1-jre-15.0/flink-shaded-guava-30.1.1-jre-15.0.jar

mv flink-connector-jdbc-1.16.3.jar /home/flink/flink/flk/lib
mv mysql-connector-java-8.0.19.jar /home/flink/flink/flk/lib
mv flink-shaded-guava-30.1.1-jre-15.0.ja /home/flink/flink/flk/lib
```

### Analysis code build

```shell
sudo apt update
sudo apt install maven

# 빌드 전에 kafka, mysql 주소 변경
cd flink-training/click-analysis-job
mvn clean package

sftp flink@jm01:/home/flink <<< $'put /home/ubuntu/flink-training/click-analysis-job/target/click-analysis-job-1.0-SNAPSHOT.jar'

/home/flink/flink/flk/bin/flink run -d ~/click-analysis-job-1.0-SNAPSHOT.jar --bootstrap.servers inf01:9094 --checkpointing --event-time
```

### Kafka source sink 확인

```shell
wget https://downloads.apache.org/kafka/3.8.0/kafka_2.12-3.8.0.tgz
tar xvzf kafka_2.12-3.8.0.tgz
cd kafka_2.12-3.8.0/bin

# or

docker exec -it operations-playground-kafka-1 bash

./kafka-topics.sh --bootstrap inf01:9094 --list

./kafka-console-consumer.sh --bootstrap-server inf01:9094 --topic input
./kafka-console-consumer.sh --bootstrap-server inf01:9094 --topic output
```

### MySQL 데이터 확인

```shell
docker exec -it operations-playground-mysql-1 bash

mysql -u sql-demo -p demo-sql

use sql-demo;
show tables;
select * from click_event_report
```

### Grafana 대시보드 확인

- inf01:3000

```shell
sudo apt-get install sqlite3

sqlite3 ./grafana/grafana.db

update user set password = '59acf18b94d7eb0694c61e60ce44c110c7a683ac6a8f09580d626f90f4a242000746579358d77dd9e570e83fa24faa88a8a6', salt = 'F3FAxVm33R' where login = 'admin';
```
