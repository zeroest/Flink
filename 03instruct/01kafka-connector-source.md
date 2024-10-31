
# [Kafka Connector Source](https://nightlies.apache.org/flink/flink-docs-release-1.20/docs/connectors/datastream/kafka/)

## [Kafka Offset](https://nightlies.apache.org/flink/flink-docs-release-1.20/docs/connectors/datastream/kafka/#starting-offset)

```
KafkaSource.builder()
    // Start from committed offset of the consuming group, without reset strategy
    // 리셋 전략 없이 소비 그룹의 커밋된 오프셋부터 시작하기
    .setStartingOffsets(OffsetsInitializer.committedOffsets())
    // Start from committed offset, also use EARLIEST as reset strategy if committed offset doesn't exist
    // 커밋된 오프셋에서 시작하고, 커밋된 오프셋이 존재하지 않는 경우 초기화 전략으로 EARLIEST를 사용합니다
    .setStartingOffsets(OffsetsInitializer.committedOffsets(OffsetResetStrategy.EARLIEST))
    // Start from the first record whose timestamp is greater than or equals a timestamp (milliseconds)
    // 타임스탬프가 타임스탬프(밀리초)보다 크거나 같은 첫 번째 레코드부터 시작합니다.
    .setStartingOffsets(OffsetsInitializer.timestamp(1657256176000L))
    // Start from earliest offset
    // 가장 빠른 오프셋부터 시작
    .setStartingOffsets(OffsetsInitializer.earliest()) // default
    // Start from latest offset
    // 최신 오프셋부터 시작
    .setStartingOffsets(OffsetsInitializer.latest());
```

`OffsetsInitializer.committedOffsets()`: 카프카 메세지를 읽어 올때 체크포인트가 일어난 이후 부터 읽어 오도록 설정  
OffsetResetStrategy: committed offset이 없는 경우에 `EARLIEST` or `LATEST` 정책에 따르도록 설정

committed offset 과 checkpoint 관계
- 플링크의 카프카 소스는 체크포인팅이 완료된 이후에 거기까지의 오프셋을 커밋한다
- 즉 committed offset 이후부터 데이터를 읽어 온다는 것은 체크포인팅 이후 부터 데이터를 읽어 온다는 것을 의미함
- [checkpointing을 하지 않도록 플링크에 설정되어 있다면 카프카 컨슈머의 설정에 따른다](https://nightlies.apache.org/flink/flink-docs-release-1.20/docs/connectors/datastream/kafka/#consumer-offset-committing)

## [Additional Properties](https://nightlies.apache.org/flink/flink-docs-release-1.20/docs/connectors/datastream/kafka/#additional-properties)

KafkaSource has following options for configuration:

- client.id.prefix: defines the prefix to use for Kafka consumer’s client ID
  - Kafka 소비자의 클라이언트 ID에 사용할 접두사를 정의합니다.
- partition.discovery.interval.ms: defines the interval im milliseconds for Kafka source to discover new partitions. See Dynamic Partition Discovery below for more details.
  - Kafka 소스가 새 파티션을 검색할 간격(밀리초)을 정의합니다. See Dynamic Partition Discovery
- register.consumer.metrics: specifies whether to register metrics of KafkaConsumer in Flink metric group (default: true)
  - Flink 메트릭 그룹에 KafkaConsumer의 메트릭을 등록할지 여부를 지정합니다.
- commit.offsets.on.checkpoint: specifies whether to commit consuming offsets to Kafka brokers on checkpoint
  - 체크포인트에서 소비 오프셋을 Kafka 브로커에 커밋할지 여부를 지정합니다.
