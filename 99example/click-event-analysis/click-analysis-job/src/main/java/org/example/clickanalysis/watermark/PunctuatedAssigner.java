package org.example.clickanalysis.watermark;

import org.apache.flink.api.common.eventtime.Watermark;
import org.apache.flink.api.common.eventtime.WatermarkGenerator;
import org.apache.flink.api.common.eventtime.WatermarkOutput;
import org.example.clickanalysis.records.ClickEvent;

/**
 * onEvent 를 구현해서 들어온 이벤트를 보고 워터마크를 생성
 * 이벤트 내 watermark 를 생성할 조건이 된다면 이벤트 조건으로 워터마크를 생성한다
 *
 * */
public class PunctuatedAssigner implements WatermarkGenerator<ClickEvent> {

    @Override
    public void onEvent(ClickEvent event, long eventTimestamp, WatermarkOutput output) {
        if (event.hasWatermarkMarker()) {
            output.emitWatermark(new Watermark(event.getWatermarkTimestamp()));
        }
    }

    @Override
    public void onPeriodicEmit(WatermarkOutput output) {
    }

}
