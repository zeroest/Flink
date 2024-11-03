
# Source Monitoring

Flink 에서 수집된 Metric 이 외부 시스템에 리포팅 될 수 있도록 지원

[Reporter 종류](https://nightlies.apache.org/flink/flink-docs-release-1.20/docs/deployment/metric_reporters/)

- JMX
- Graphite
- InfluxDB
- [Prometheus](https://nightlies.apache.org/flink/flink-docs-release-1.20/docs/deployment/metric_reporters/#prometheus)
  - 기본 라이브러리 제공 - plugins/metrics-prometheus/flink-metrics-prometheus-${version}.jar
- PrometheusPushGateway
- StatsD
- Datadog
- Slf4j

## [Kafka Connector Source Monitoring](https://nightlies.apache.org/flink/flink-docs-release-1.20/docs/connectors/datastream/kafka//#monitoring)

### [Scope of Metric](https://nightlies.apache.org/flink/flink-docs-release-1.20/docs/connectors/datastream/kafka//#scope-of-metric)

| Scope    | Metrics                            | User Variables   | Description                                                                                                                                                   | Type    |
| -------- | ---------------------------------- | ---------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------- |
| Operator | currentEmitEventTimeLag            | n/a              | 레코드 이벤트 타임스탬프부터 소스 커넥터¹에 의해 레코드가 전송되는 시간까지 시간 범위: `currentEmitEventTimeLag = EmitTime - EventTime.`                                                           | Gauge   |
| Operator | watermarkLag                       | n/a              | 워터마크가 벽시계 시간보다 늦게 표시되는 시간 범위입니다: `watermarkLag = CurrentTime - Watermark`                                                                                     | Gauge   |
| Operator | sourceIdleTime                     | n/a              | 소스가 레코드를 처리하지 않은 시간 범위입니다: `sourceIdleTime = CurrentTime - LastRecordProcessTime                                                                              | Gauge   |
| Operator | pendingRecords                     | n/a              | 소스에서 가져오지 않은 레코드 수(예: 카프카 파티션에서 소비자 오프셋 후 사용 가능한 레코드)입니다.                                                                                                     | Gauge   |
| Operator | KafkaSourceReader.commitsSucceeded | n/a              | 오프셋 커밋이 켜져 있고 체크포인트가 활성화된 경우 Kafka에 성공한 총 오프셋 커밋 횟수입니다.                                                                                                       | Counter |
| Operator | KafkaSourceReader.commitsFailed    | n/a              | 오프셋 커밋이 켜져 있고 체크포인트가 활성화된 경우, Kafka에 대한 총 오프셋 커밋 실패 횟수입니다. 오프셋을 다시 Kafka에 커밋하는 것은 소비자 진행 상황을 노출하기 위한 수단일 뿐이므로 커밋 실패는 Flink의 체크포인트 파티션 오프셋의 무결성에 영향을 미치지 않습니다. | Counter |
| Operator | KafkaSourceReader.committedOffsets | topic, partition | 각 파티션에 대해 마지막으로 성공적으로 커밋된 오프셋입니다. 특정 파티션의 메트릭은 토픽 이름과 파티션 ID로 지정할 수 있습니다.                                                                                     | Gauge   |
| Operator | KafkaSourceReader.currentOffsets   | topic, partition | 각 파티션에 대한 소비자의 현재 읽기 오프셋입니다. 특정 파티션의 메트릭은 주제 이름과 파티션 ID로 지정할 수 있습니다.                                                                                          | Gauge   |

## [Kafka Consumer Metrics 등록](https://nightlies.apache.org/flink/flink-docs-release-1.20/docs/connectors/datastream/kafka//#kafka-consumer-metrics)

- register.consumer.metrics 옵션 true 일 때 수집 됨 (default true)
- Kafka consumer의 모든 metric은 KafkaSourceReader.KafkaConsumer 그룹 아래 저장됨
- Kafka consumer metric 종류: [[Kafka doc] Consumer monitoring](https://kafka.apache.org/documentation/#consumer_monitoring)

> All metrics of Kafka consumer are also registered under group KafkaSourceReader.KafkaConsumer. For example, Kafka consumer metric “records-consumed-total” will be reported in metric: <some_parent_groups>.operator.KafkaSourceReader.KafkaConsumer.records-consumed-total .

Kafka 소비자에 대한 모든 메트릭도 KafkaSourceReader.KafkaConsumer 그룹에 등록됩니다. 예를 들어, Kafka 소비자 메트릭 "records-consumed-total"은 메트릭으로 보고됩니다: <일부_부모_그룹>.operator.KafkaSourceReader.KafkaConsumer.records-consumed-total .

> You can configure whether to register Kafka consumer’s metric by configuring option register.consumer.metrics. This option will be set as true by default.

register.consumer.metrics 옵션을 구성하여 Kafka 소비자의 메트릭을 등록할지 여부를 구성할 수 있습니다. 이 옵션은 기본적으로 true로 설정됩니다.

> For metrics of Kafka consumer, you can refer to Apache Kafka Documentation for more details.

Kafka 소비자의 메트릭에 대한 자세한 내용은 Apache Kafka 설명서를 참조하세요. [[Kafka doc] Consumer monitoring](https://kafka.apache.org/documentation/#consumer_monitoring)

> In case you experience a warning with a stack trace containing javax.management.InstanceAlreadyExistsException: kafka.consumer:[...], you are probably trying to register multiple KafkaConsumers with the same client.id. The warning indicates that not all available metrics are correctly forwarded to the metrics system. You must ensure that a different client.id.prefix for every KafkaSource is configured and that no other KafkaConsumer in your job uses the same client.id.

javax.management.InstanceAlreadyExistsException: kafka.consumer:[...]가 포함된 스택 추적과 함께 경고가 표시되는 경우, 동일한 client.id로 여러 KafkaConsumer를 등록하려고 하는 것일 수 있습니다. 이 경고는 사용 가능한 모든 메트릭이 메트릭 시스템으로 올바르게 전달되지 않았음을 나타냅니다. 모든 KafkaSource에 대해 서로 다른 client.id.접두사가 구성되어 있고 작업의 다른 KafkaConsumer가 동일한 client.id를 사용하지 않는지 확인해야 합니다.
