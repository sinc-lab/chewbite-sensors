package com.android.chewbiteSensors.data_sensors;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

public class WriteFileTaskGPS<T extends CBGpsBuffer> extends TimerTask {

    public static int PERIOD_TIME_MS = 5000;
    public static int DELAY_TIME_MS = 5000;
    private final List<T> sensors;


    WriteFileTaskGPS() {
        this.sensors = new ArrayList<>();
    }

    @Override
    public void run() {
        for (CBGpsBuffer sensorBuffer : this.sensors) {
            FileManager.writeToFile(
                    sensorBuffer.getSensorFileName(),
                    sensorBuffer.getSensorEventData(CBBuffer.STRING_GPS)
            );
        }
    }

    public void addSensor(T sensor) {
        this.sensors.add(sensor);
    }
}