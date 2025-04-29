package com.android.chewbiteSensors.data_sensors;

import android.hardware.Sensor;

public enum SensorInfo {
    ACCELEROMETER("acelerómetro", "accelerometer", Sensor.TYPE_ACCELEROMETER, "status_switch_accelerometer_configuration"),
    GYROSCOPE("giroscopio", "gyroscope", Sensor.TYPE_GYROSCOPE, "status_switch_gyroscope_configuration"),
    MAGNETOMETER("magnetómetro", "magnetometer", Sensor.TYPE_MAGNETIC_FIELD, "status_switch_magnetometer_configuration"),
    ACCELEROMETER_UNCALIBRATED("acelerómetro sin calibrar","accelerometer_uncalibrated", Sensor.TYPE_ACCELEROMETER_UNCALIBRATED, "status_switch_uncalibrated_accelerometer_configuration"),
    GYROSCOPE_UNCALIBRATED("giroscopio sin calibrar","gyroscope_uncalibrated", Sensor.TYPE_GYROSCOPE_UNCALIBRATED, "status_switch_uncalibrated_gyroscope_configuration"),
    MAGNETOMETER_UNCALIBRATED("magnetómetro sin calibrar","magnetometer_uncalibrated", Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED, "status_switch_uncalibrated_magnetometer_configuration"),
    GRAVITY("gravedad","gravity", Sensor.TYPE_GRAVITY, "status_switch_gravity_configuration"),
    STEP_COUNTER("contador de pasos","num_of_steps", Sensor.TYPE_STEP_COUNTER, "status_switch_number_of_steps_configuration"),

    ; // Semicolon is important here

    private final String sensorNameES;
    private final String sensorNameEN;
    private final int sensorType;
    private final String statusKey;

    SensorInfo(String sensorNameES, String sensorNameEN, int sensorType, String statusKey) {
        this.sensorNameES = sensorNameES;
        this.sensorNameEN = sensorNameEN;
        this.sensorType = sensorType;
        this.statusKey = statusKey;
    }

    public String getSensorNameES() {
        return sensorNameES;
    }

    public String getSensorNameEN() {
        return sensorNameEN;
    }

    public int getSensorType() {
        return sensorType;
    }

    public String getStatusKey() {
        return statusKey;
    }

    public static SensorInfo fromSensorType(int sensorType) {
        for (SensorInfo info : values()) {
            if (info.sensorType == sensorType) {
                return info;
            }
        }
        return null; // Or throw an exception if sensor type not found
    }
}
