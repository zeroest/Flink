package org.example.clickanalysis.records;

public interface WatermarkMarkerEvent {

    boolean hasWatermarkMarker();

    long getWatermarkTimestamp();

}
