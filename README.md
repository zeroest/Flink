
# Apache Flink

Stateful Computations over Data Streams
Apache Flink is a framework and distributed processing engine for stateful computations over unbounded and bounded data streams. Flink has been designed to run in all common cluster environments, perform computations at in-memory speed and at any scale.

- stateful computations
- over unbounded and bounded
  - Bounded - Batch analytics
  - Unbounded - Streaming analytics
- perform computations at in-memory speed and at any scale


## 특장점

### Powerful Runtime

- 초당 수천만 이벤트 처리 throughput
- 1초 미만 latency
- Exactly-once 보장
- 수십 terabyte state 관리 및 복구

### 다양한 API 및 개발 언어 지원

- Flink SQL
- Table API
- DataStream API

### Stream과 Batch 통합 지원 

- Real-time data 와 Historical data 통합 관리

### Production-readiness

- Metric system
- 모니터링, 디버깅 Web UI 지원
- Savepoint 기반 statefully scaling, upgrade, migration 지원

---

## Stateful Stream Processing

distributed processing engine for stateful computations

### State Stream Processing 케이스

- 이벤트 패턴 매칭 시: 이벤트 시퀀스
- 분/시간/일 단위 이벤트 aggregation 시: 중간 aggregation 결과
- 머신 러닝 모델의 스트리밍 데이터 학습 시: 현재 버전의 모델 파라미터
- 과거 데이터 관리 필요 시: 과거에 발생한 이벤트

### State 관리 중요한 이유

- Fault-tolerance 보장
- Flink 애플리케이션 re-scaling

### Flink State 관리 방식

```
User code <-> Local state backend -> Persisted savepoint
```

Local state backend (local read/writes that manipulate state)
- Checkpoint: 주기적으로 state와 metadata의 snapshot을 내려 받음 (Local state backend에 저장)
- Restore: 작업 실패 시 checkpoint에서 state를 복구해서 consistency를 보장

Persisted savepoint
- Savepoint: 특정 시점에 분산 되어있는 전역 state에 대한 snapshot
  - HDFS, S3 등에 저장
  - Flink 업그레이드, 코드 변경 등의 작업 시 사용자가 직접 활용

---

[Apache Flink Series](https://medium.com/@akash.d.goel/apache-flink-series-part-2-a3330d62ce63)

