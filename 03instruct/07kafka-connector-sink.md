
# [Kafka Connector Sink](https://nightlies.apache.org/flink/flink-docs-release-1.20/docs/connectors/datastream/kafka/#kafka-sink)

```
DataStream<String> stream = ...;
        
KafkaSink<String> sink = KafkaSink.<String>builder()
        .setBootstrapServers(brokers)
        .setRecordSerializer(KafkaRecordSerializationSchema.builder()
            .setTopic("topic-name")
            .setValueSerializationSchema(new SimpleStringSchema())
            .build()
        )
        .setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
        .build();
        
stream.sinkTo(sink);
```

The following properties are required to build a KafkaSink:
- Bootstrap servers, setBootstrapServers(String)
- Record serializer, setRecordSerializer(KafkaRecordSerializationSchema)
- If you configure the delivery guarantee with DeliveryGuarantee.EXACTLY_ONCE you also have use setTransactionalIdPrefix(String)

## [Fault Tolerance](https://nightlies.apache.org/flink/flink-docs-release-1.20/docs/connectors/datastream/kafka/#fault-tolerance)

- 장애 발생 시에도, 장애로부터 프로그램을 복구하고 지속 실행할 수 있는 능력
- Stream processing에서는 장애 발생 시, 데이터 유실이나 중복이 발생할 수 있음
- Flink는 state snapshot을 통해 fault tolerance를 보장함
  - Snapshot: Flink job state의 전역 이미지를 가리키는 범용적인 용어. 각 데이터 소스에 대한 포인터 (ex. Kafka offset), 각 operator에서 이제까지 처리한 결과 등이 저장됨.

> Overall the KafkaSink supports three different DeliveryGuarantees. For DeliveryGuarantee.AT_LEAST_ONCE and DeliveryGuarantee.EXACTLY_ONCE Flink’s checkpointing must be enabled. By default the KafkaSink uses DeliveryGuarantee.NONE. Below you can find an explanation of the different guarantees.

전반적으로 카프카싱크는 세 가지 DeliveryGuarantee를 지원합니다. DeliveryGuarantee.AT_LEAST_ONCE와 DeliveryGuarantee.EXACTLY_ONCE의 경우 Flink의 체크포인트가 활성화되어 있어야 합니다. 기본적으로 카프카싱크는 DeliveryGuarantee.NONE을 사용합니다. 아래에서 다양한 보증에 대한 설명을 확인할 수 있습니다.

- DeliveryGuarantee.NONE does not provide any guarantees: messages may be lost in case of issues on the Kafka broker and messages may be duplicated in case of a Flink failure.
  - Kafka 브로커에 문제가 발생할 경우 메시지가 손실될 수 있으며, Flink 장애가 발생할 경우 메시지가 중복될 수 있습니다.
- DeliveryGuarantee.AT_LEAST_ONCE: The sink will wait for all outstanding records in the Kafka buffers to be acknowledged by the Kafka producer on a checkpoint. No messages will be lost in case of any issue with the Kafka brokers but messages may be duplicated when Flink restarts because Flink reprocesses old input records.
  - DeliveryGuarantee.AT_LEAST_ONCE: 싱크는 체크포인트에서 카프카 프로듀서가 카프카 버퍼의 모든 미해결 레코드가 승인될 때까지 기다립니다. Kafka 브로커에 문제가 발생하더라도 메시지는 손실되지 않지만, Flink가 이전 입력 레코드를 재처리하기 때문에 Flink가 다시 시작될 때 메시지가 중복될 수 있습니다.
- DeliveryGuarantee.EXACTLY_ONCE: In this mode, the KafkaSink will write all messages in a Kafka transaction that will be committed to Kafka on a checkpoint. Thus, if the consumer reads only committed data (see Kafka consumer config isolation.level), no duplicates will be seen in case of a Flink restart. However, this delays record visibility effectively until a checkpoint is written, so adjust the checkpoint duration accordingly. Please ensure that you use unique transactionalIdPrefix across your applications running on the same Kafka cluster such that multiple running jobs do not interfere in their transactions! Additionally, it is highly recommended to tweak Kafka transaction timeout (see Kafka producer transaction.timeout.ms)» maximum checkpoint duration + maximum restart duration or data loss may happen when Kafka expires an uncommitted transaction.
  - DeliveryGuarantee.EXACTLY_ONCE: 이 모드에서는 KafkaSink가 체크포인트에서 Kafka에 커밋될 모든 메시지를 Kafka 트랜잭션에 씁니다. 따라서 소비자가 커밋된 데이터만 읽는 경우(Kafka 소비자 구성 격리.수준 참조), Flink 재시작 시 중복이 표시되지 않습니다. 그러나 이렇게 하면 체크포인트가 기록될 때까지 레코드 가시성이 효과적으로 지연되므로 체크포인트 기간을 적절히 조정하세요. 실행 중인 여러 작업이 트랜잭션을 방해하지 않도록 동일한 Kafka 클러스터에서 실행 중인 애플리케이션 전체에서 고유한 트랜잭션IdPrefix를 사용해야 합니다! 또한, 커밋되지 않은 트랜잭션이 만료될 때 데이터 손실이 발생할 수 있으므로 Kafka 트랜잭션 타임아웃(Kafka 프로듀서 transaction.timeout.ms 참조)"의 최대 체크포인트 기간 + 최대 재시작 기간을 조정할 것을 적극 권장합니다.

