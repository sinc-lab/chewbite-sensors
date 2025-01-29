package com.android.chewbiteSensors.data_sensors;

public class SensorEventData {
    private final long timestamp;
    private float x, y, z;
    private double lat, lon, alt;
    private int count;

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

    public SensorEventData(long timestamp, int count) {
        this.timestamp = timestamp;
        this.count = count;
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

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public double getAlt() {
        return (float) alt;
    }

    public int getCount() {
        return count;
    }
}
