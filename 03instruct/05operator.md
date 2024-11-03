
# Operator

Flink App 안에서 DataStream을 새로운 DataStream으로 변환해주는 역할

## Map

- 각 element 를 1:1 변환

```java
dataStream.map(new MapFunction<Integer, Integer>() {
    @Overide
    public Integer map(Integer value) throws Exception {
        return 2 * value;
    }
})
```

## FlatMap

- 각 element 를 1:N 변환

```java
dataStream.flatMap(new FlatMapFunction<String, String>() {
    @Overide
    public void flatMap(String value, Collector<String> out) throws Exception {
        for(String word : value.split(" ")) {
            out.collect(word);
        }
    }
})
```

## Filter

- 각 element 에 대해 boolean 값 return, return 이 true 인 경우에만 element 전달

```java
dataStream.filter(new FilterFunction<Integer>() {
    @Overide
    public boolean filter(Integer value) throws Exception {
        return value != 0;
    }
})
```

## Key By

- DataStream 을 파티션으로 나눠서 KeyedStream 으로 변환, hash partitioning

```java
dataStream.keyBy(value -> value.getSomeKey());
dataStream.keyBy(value -> value.f0);
```

## Reduce

- 현재 element 와 지금까지 reduce 된 값 연산

```java
dataStream.reduce(new ReduceFunction<Integer>() {
    @Overide
    public Integer reduce(Integer value1, Integer value2) throws Exception {
        return value1 + value2;
    }
})
```

## Window

- KeyedStream의 각 Key 별 element를 특정 기준에 따라 grouping 함
- KeyedStream에서만 사용 가능!
- [Window 자료 참고](./06window.md)

```java
dataStream
        .keyBy(value -> value.getSomeKey())
        // 5초 단위로 스트림에 흘러가는 element를 묶음
        .window(TumblingEventTimeWindows.of(Time.seconds(5)));
```

## Window All

- DataStream에 window 적용, 모든 element에 대해 grouping 하므로 non-parallel 작업
- KeyedStream 가 아닌 operator에서 사용 가능
- [Window 자료 참고](./06window.md)

```java
dataStream.windowAll(TumblingEventTimeWindows.of(Time.seconds(5)));
```

## Window Apply

- Window 전체에 함수 적용
- Windowing 된 스트림에 대해서 어떤 작업을 할 것인지 구현하는 과정
- [Window 자료 참고](./06window.md)

```java
windowedStream.apply(new WindowFunction<Tuple2<String, Integer>, Integer, Tuple, Window>() {
    public void apply(Tuple tuple, Window window, Iterable<Tuple2<String, Integer>> values, Collector<Integer> out) throws Exception {
        int sum = 0;
        for (value t : values) {
            sum += t.f1;
        }
        out.collect(new Integer(sum));
    }
});
```

## Window Reduce

- Window 에 reduce 함수 적용
- Windowing 된 스트림에 대해서 어떤 작업을 할 것인지 구현하는 과정
- [Window 자료 참고](./06window.md)

```java
windowedStream.reduce(new ReduceFunction<Tuple2<String, Integer>>() {
    public Tuple2<String, Integer> reduce(Tuple2<String, Integer> value1, Tuple2<String, Integer> value2) throws Exception {
        return new Tuple2<String, Integer>(value1.f0, value1.f1 + value2.f1);
    }
});
```

## Union

- 여러 개의 DataStream을 하나의 DataStream으로 합치는 작업

```java
dataStream.union(otherStream1, otherStream2, ...);
```
