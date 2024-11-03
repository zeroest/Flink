package org.example.clickanalysis.watermark;

import org.apache.flink.api.common.eventtime.Watermark;
import org.apache.flink.api.common.eventtime.WatermarkGenerator;
import org.apache.flink.api.common.eventtime.WatermarkOutput;
import org.example.clickanalysis.records.ClickEvent;

/**
 * onPeriodicEmit 을 구현하여 주기적으로 워터마크를 생성
 * 특정 시간(Lag)을 가지고 뒤따라가는 워터마크
 * */
public class TimeLagWatermarkGenerator implements WatermarkGenerator<ClickEvent> {

    private static final long maxTimeLag = 5000;

    @Override
    public void onEvent(ClickEvent event, long eventTimestamp, WatermarkOutput output) {
    }

    @Override
    public void onPeriodicEmit(WatermarkOutput output) {
        output.emitWatermark(new Watermark(System.currentTimeMillis() - maxTimeLag));
    }

}
