
# [Watermark](https://nightlies.apache.org/flink/flink-docs-release-1.20/docs/concepts/time/#event-time-and-watermarks)

- Stream Processing에서 Event Time의 흐름을 측정하기 위한 메커니즘
- Timestamp(t)를 가지고 Data Stream 안에서 Event 들과 같이 흐름
- 해당 Stream의 Event Time이 t에 이르렀음을 알려주는 역할
  - = 더 이상 t 이전에 발생한 이벤트는 들어오지 않음을 의미

> The mechanism in Flink to measure progress in event time is watermarks. Watermarks flow as part of the data stream and carry a timestamp t. A Watermark(t) declares that event time has reached time t in that stream, meaning that there should be no more elements from the stream with a timestamp t’ <= t (i.e. events with timestamps older or equal to the watermark).

이벤트 시간의 진행 상황을 측정하는 Flink의 메커니즘은 워터마크입니다. 워터마크는 데이터 스트림의 일부로 흐르며 타임스탬프 t를 가지고 있습니다. Watermark(t)는 이벤트 시간이 해당 스트림의 시간 t에 도달했음을 선언하며, 이는 스트림에서 타임스탬프 t' <= t를 가진 요소(즉, 워터마크보다 오래되거나 동일한 타임스탬프를 가진 이벤트)가 더 이상 없어야 함을 의미합니다.

> The figure below shows a stream of events with (logical) timestamps, and watermarks flowing inline. In this example the events are in order (with respect to their timestamps), meaning that the watermarks are simply periodic markers in the stream.

아래 그림은 (논리적) 타임스탬프가 있는 이벤트 스트림과 인라인으로 흐르는 워터마크를 보여줍니다. 이 예시에서 이벤트는 (타임스탬프를 기준으로) 순서대로 정렬되어 있으며, 워터마크는 스트림에서 주기적으로 표시되는 마커에 불과합니다.

![watermark in order](img/watermark-in-order.png)

> Watermarks are crucial for out-of-order streams, as illustrated below, where the events are not ordered by their timestamps. In general a watermark is a declaration that by that point in the stream, all events up to a certain timestamp should have arrived. Once a watermark reaches an operator, the operator can advance its internal event time clock to the value of the watermark.

워터마크는 아래 그림과 같이 이벤트가 타임스탬프에 따라 정렬되지 않은 비정상적인 스트림에서 매우 중요합니다. 일반적으로 워터마크는 스트림의 해당 시점까지 특정 타임스탬프까지의 모든 이벤트가 도착했음을 선언하는 것입니다. 워터마크가 operator에 도달하면 operator는 내부 이벤트 시간 시계를 워터마크의 값으로 앞당길 수 있습니다.

![watermark out of order](img/watermark-out-of-order.png)

W(11) 을 만났다는 의미는 10까지의 이벤트들을 모아 윈도우를 닫아야 한다는 의미

> Note that event time is inherited by a freshly created stream element (or elements) from either the event that produced them or from watermark that triggered creation of those elements.

Event time은 새로 생성된 스트림 요소(또는 요소들)를 생성한 이벤트 또는 해당 요소의 생성을 트리거한 워터마크로부터 상속된다는 점에 유의하세요.


## Watermarks in Parallel Streams

- 일반적으로 Watermark는 Source에서 생성
  - 굳이 Source에서 만들지 못하는 상황에서 특정 operator 에서 Watermark를 생성되게 코드를 구현 할 수는 있으나 Source 에서 Watermark를 생성하도록 권장됨
- Kafka Source 의 경우 카프카 파티션 별로 Watermark를 생성
  - offset이 파티션 별로 선정되기 때문에 Watermark도 파티션 별로 생성
- 하나의 operator에 두개의 워터마크가 들어온다면 그 중에 작은 시간을 기준으로 선정하게 된다

> Watermarks are generated at, or directly after, source functions. Each parallel subtask of a source function usually generates its watermarks independently. These watermarks define the event time at that particular parallel source.

워터마크는 소스 함수에서 또는 소스 함수 바로 뒤에 생성됩니다. 소스 함수의 각 병렬 하위 작업은 일반적으로 워터마크를 독립적으로 생성합니다. 이러한 워터마크는 특정 병렬 소스의 이벤트 시간을 정의합니다.

> As the watermarks flow through the streaming program, they advance the event time at the operators where they arrive. Whenever an operator advances its event time, it generates a new watermark downstream for its successor operators.

워터마크가 스트리밍 프로그램을 통해 흐르면서 도착하는 operator의 이벤트 시간을 앞당깁니다. operator가 이벤트 시간을 앞당길 때마다 후속 operator를 위해 새로운 워터마크를 다운스트림에 생성합니다.

> Some operators consume multiple input streams; a union, for example, or operators following a keyBy(…) or partition(…) function. Such an operator’s current event time is the minimum of its input streams’ event times. As its input streams update their event times, so does the operator.

일부 operator는 여러 입력 스트림을 소비합니다. 예를 들어 유니온이나 keyBy(...) 또는 partition(...) 함수를 따르는 operator 등이 있습니다. 이러한 operator의 현재 이벤트 시간은 해당 입력 스트림의 이벤트 시간 중 최소값입니다. 입력 스트림이 이벤트 시간을 업데이트하면 operator도 업데이트됩니다.

> The figure below shows an example of events and watermarks flowing through parallel streams, and operators tracking event time.

아래 그림은 병렬 스트림을 통해 흐르는 이벤트와 워터마크, 그리고 이벤트 시간을 추적하는 operator의 예를 보여줍니다.

![watermark in parallel streams](img/watermark-in-parallel-streams.png)

## [Watermark Strategies](https://nightlies.apache.org/flink/flink-docs-release-1.20/docs/dev/datastream/event-time/generating_watermarks/#introduction-to-watermark-strategies)

### Event Time 설정 하기

- Event time 기준으로 stream processing을 하기 위해 event timestamp를 Flink에 등록해줘야 함
- 이벤트의 특정 필드에서 시간을 추출해서 전달
- [TimestampAssigner 사용](../99example/click-event-analysis/click-analysis-job/src/main/java/org/example/clickanalysis/ClickEventAnalyzer.java)

```
    WatermarkStrategy<ClickEvent> watermarkStrategy = WatermarkStrategy
      .<ClickEvent>forBoundedOutOfOrderness(Duration.ofMillis(200))
      .withIdleness(Duration.ofSeconds(5))
      .withTimestampAssigner((clickEvent, l) -> clickEvent.getTimestamp().getTime());
```

### Watermark 생성하기

- Event time 등록과 함께, watermark 생성 필요
- Stream 내에서 event time의 흐름을 알려주기 위함
- WatermarkGenerator 사용
  - 일반적으로 사용되는 watermark strategy는 static method로 제공
- Built-in Watermark Generator
- auto watermark generation interval

> The Flink API expects a WatermarkStrategy that contains both a TimestampAssigner and WatermarkGenerator. A number of common strategies are available out of the box as static methods on WatermarkStrategy, but users can also build their own strategies when required.

Flink API는 타임스탬프 할당자와 워터마크 생성기를 모두 포함하는 워터마크 전략을 기대합니다. 여러 가지 일반적인 전략이 워터마크전략에서 정적 메서드로 즉시 사용 가능하지만, 필요에 따라 사용자가 직접 전략을 구축할 수도 있습니다.

```java
public interface WatermarkStrategy<T> 
    extends TimestampAssignerSupplier<T>,
            WatermarkGeneratorSupplier<T>{

    /**
     * Instantiates a {@link TimestampAssigner} for assigning timestamps according to this
     * strategy.
     */
    @Override
    TimestampAssigner<T> createTimestampAssigner(TimestampAssignerSupplier.Context context);

    /**
     * Instantiates a WatermarkGenerator that generates watermarks according to this strategy.
     */
    @Override
    WatermarkGenerator<T> createWatermarkGenerator(WatermarkGeneratorSupplier.Context context);
}
```

> As mentioned, you usually don’t implement this interface yourself but use the static helper methods on WatermarkStrategy for common watermark strategies or to bundle together a custom TimestampAssigner with a WatermarkGenerator. For example, to use bounded-out-of-orderness watermarks and a lambda function as a timestamp assigner you use this:

앞서 언급했듯이 일반적으로 이 인터페이스를 직접 구현하지 않고 일반적인 워터마크 전략을 위해 WatermarkStrategy의 정적 헬퍼 메서드를 사용하거나 사용자 정의 타임스탬프 할당자와 워터마크 생성기를 함께 번들링합니다. 예를 들어 bounded-out-of-orderness 워터마크와 람다 함수를 타임스탬프 할당자로 사용하려면 이 인터페이스를 사용합니다:

```
WatermarkStrategy
        .<Tuple2<Long, String>>forBoundedOutOfOrderness(Duration.ofSeconds(20))
        .withTimestampAssigner((event, timestamp) -> event.f0);
```

> Specifying a TimestampAssigner is optional and in most cases you don’t actually want to specify one. For example, when using Kafka or Kinesis you would get timestamps directly from the Kafka/Kinesis records.

타임스탬프 할당자를 지정하는 것은 선택 사항이며, 대부분의 경우 실제로 지정하지 않는 것이 좋습니다. 예를 들어, 카프카나 키네시스를 사용할 때는 카프카/키네시스 레코드에서 직접 타임스탬프를 가져옵니다.

### [Built-in Watermark Generators](https://nightlies.apache.org/flink/flink-docs-release-1.20/docs/dev/datastream/event-time/built_in/#builtin-watermark-generators)

#### Monotonously Increasing Timestamps

- 현재 timestamp를 watermark로 사용
- 이상적인 경우, 타임스탬프가 증가하기만 하는 경우

`WatermarkStrategy.forMonotonousTimestamps();`

#### Fixed Amount of Lateness

- Max timestamp - event의 최대 지연 시간을 워터마크로 사용
- 현재까지 들어온 이벤트 중 가장 큰 타임스탬프에서 지정한 특정 시간(Duration.ofSeconds(10))을 빼고 워터마크 지정
- 특정시간: 이벤트가 늦게 들어올거라는 가정하에 설정, 즉 최대 지연시간을 설정한다

`WatermarkStrategy.forBoundedOutOfOrderness(Duration.ofSeconds(10));`

## [Writing WatermarkGenerators](https://nightlies.apache.org/flink/flink-docs-release-1.20/docs/dev/datastream/event-time/generating_watermarks/#writing-watermarkgenerators)

- [onEvent 구현 예제](../99example/click-event-analysis/click-analysis-job/src/main/java/org/example/clickanalysis/watermark/PunctuatedAssigner.java)
- [onPeriodicEmit 구현 예제](../99example/click-event-analysis/click-analysis-job/src/main/java/org/example/clickanalysis/watermark/TimeLagWatermarkGenerator.java)

```java
/**
 * The {@code WatermarkGenerator} generates watermarks either based on events or
 * periodically (in a fixed interval).
 * 이 워터마크 생성기는 이벤트에 따라 또는 주기적으로(고정된 간격으로) 워터마크를 생성합니다.
 *
 * <p><b>Note:</b> This WatermarkGenerator subsumes the previous distinction between the
 * {@code AssignerWithPunctuatedWatermarks} and the {@code AssignerWithPeriodicWatermarks}.
 */
@Public
public interface WatermarkGenerator<T> {

  /**
   * Called for every event, allows the watermark generator to examine 
   * and remember the event timestamps, or to emit a watermark based on
   * the event itself.
   * 모든 이벤트에 대해 호출되며, 워터마크 생성기가 이벤트 타임스탬프를 검사하고 기억하거나 이벤트 자체를 기반으로 워터마크를 생성할 수 있도록 합니다.
   */
  void onEvent(T event, long eventTimestamp, WatermarkOutput output);

  /**
   * Called periodically, and might emit a new watermark, or not.
   * 주기적으로 호출되며 새 워터마크를 생성하거나 생성하지 않을 수 있습니다.
   *
   * <p>The interval in which this method is called and Watermarks 
   * are generated depends on {@link ExecutionConfig#getAutoWatermarkInterval()}.
   * 이 메서드가 호출되고 워터마크가 생성되는 간격은 {@link ExecutionConfig#getAutoWatermarkInterval()}에 따라 달라집니다 (default 200ms)
   */
  void onPeriodicEmit(WatermarkOutput output);
}
```

## [Dealing With Idle Sources](https://nightlies.apache.org/flink/flink-docs-release-1.20/docs/dev/datastream/event-time/generating_watermarks/#dealing-with-idle-sources)

- Idle Source, 이벤트가 흐르지 않는 소스가 있다는건 다른 데이터 소스에서 많은 이벤트를 받아도 전체로 본다면 흐르지 않는것과 같다
  - 여러 개의 watermark 가 들어오는 경우, operator의 event time은 min 값으로 설정됨
  - 이때 일부 토픽에서 이벤트가 들어오지 않는다면 워터마크가 생성되지 않고 흐르지 않게 됨
  - 워터마크가 더이상 흐르지 않기 때문에 min 값의 워터마크가 고정, 즉 timestamp가 업데이트 되지 않음
  - 윈도우는 닫히지 않고 뒤에 연결되는 다운스트림에도 결과를 내보내지 않음, 즉 전체 스트림에 데이터가 흐르지 않게 된다 
- 이를 방지하기 위해 설정 
  - (withIdleness(Duration.ofSeconds(5))) - 소스에 이벤트가 흐르지 않는 시간이 5초 이상 된다면
  - 소스는 아이들 상태로 판단하여 해당 소스에서 워터마크가 오지 않더라도 무시하도록 처리

> If one of the input splits/partitions/shards does not carry events for a while this means that the WatermarkGenerator also does not get any new information on which to base a watermark. We call this an idle input or an idle source. This is a problem because it can happen that some of your partitions do still carry events. In that case, the watermark will be held back, because it is computed as the minimum over all the different parallel watermarks.

입력 분할/파티션/샤드 중 하나가 한동안 이벤트를 전달하지 않는다면 이는 워터마크 생성기가 워터마크의 기반이 될 새로운 정보를 얻지 못한다는 것을 의미합니다. 이를 유휴 입력 또는 유휴 소스라고 부릅니다. 이는 일부 파티션에 여전히 이벤트가 남아 있을 수 있기 때문에 문제가 됩니다. 이 경우 워터마크는 모든 다른 병렬 워터마크에 대해 최소값으로 계산되기 때문에 보류됩니다.

> To deal with this, you can use a WatermarkStrategy that will detect idleness and mark an input as idle. WatermarkStrategy provides a convenience helper for this:

이 문제를 해결하기 위해 유휴 상태를 감지하고 입력을 유휴 상태로 표시하는 WatermarkStrategy를 사용할 수 있습니다. 워터마크전략은 이를 위한 편리한 도우미를 제공합니다:

```
WatermarkStrategy
        .<Tuple2<Long, String>>forBoundedOutOfOrderness(Duration.ofSeconds(20))
        .withIdleness(Duration.ofMinutes(1));
```
