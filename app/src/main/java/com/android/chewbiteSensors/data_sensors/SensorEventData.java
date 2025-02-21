package com.android.chewbiteSensors.data_sensors;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class SensorEventData {
    private final String timestamp;
    private float x, y, z;
    private double lat, lon, alt;
    private int count;
    private static final String DATE_FORMAT = "yyyy-MM-dd - HH:mm:ss";

    @SuppressLint("SimpleDateFormat")
    public SensorEventData(long timestamp, float x, float y, float z) {
        this.timestamp = this.timeToString(timestamp);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @SuppressLint("SimpleDateFormat")
    public SensorEventData(long timestamp, double lat, double lon, double alt) {
        this.timestamp = new SimpleDateFormat(DATE_FORMAT).format(timestamp);
        this.lat = lat;
        this.lon = lon;
        this.alt = alt;
    }

    @SuppressLint("SimpleDateFormat")
    public SensorEventData(long timestamp, int count) {
        this.timestamp = this.timeToString(timestamp); //Long.toString(timestamp);
        this.count = count;
    }

    public String getTimestamp() {
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

    @SuppressLint("DefaultLocale")
    private String timeToString(long timestamp) {
        // Convertir el tiempo del sensor a milisegundos basado en elapsedRealtimeNanos
        long currentTimeMillis = System.currentTimeMillis();
        long elapsedRealtimeMillis = TimeUnit.NANOSECONDS.toMillis(android.os.SystemClock.elapsedRealtimeNanos());
        long eventTimeMillis = currentTimeMillis - elapsedRealtimeMillis + TimeUnit.NANOSECONDS.toMillis(timestamp);

        // Extraer milisegundos, microsegundos y nanosegundos restantes
        long millis = eventTimeMillis % 1000;
        long micros = (timestamp / 1000) % 1000;
        long nanos = timestamp % 1000;

        // Formatear la fecha y hora
        Date date = new Date(eventTimeMillis);
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        //return sdf.format(date) + String.format(":%03d:%03d:%03d", millis, micros, nanos);
        return sdf.format(date);
    }
}
