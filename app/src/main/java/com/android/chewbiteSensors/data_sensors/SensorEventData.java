package com.android.chewbiteSensors.data_sensors;

public class SensorEventData {
    private final long timestamp;
    private float x, y, z;
    private double lat, lon, alt;

    public SensorEventData(long timestamp, float x, float y, float z) {
        this.timestamp = timestamp;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public SensorEventData(long timestamp, double lat, double lon, double alt) {
        this.timestamp = timestamp;
        this.lat = lat;
        this.lon = lon;
        this.alt = alt;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public float getLat() {
        return (float) lat;
    }

    public float getLon() {
        return (float) lon;
    }

    public float getAlt() {
        return (float) alt;
    }
}
