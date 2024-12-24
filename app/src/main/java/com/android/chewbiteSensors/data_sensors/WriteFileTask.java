package com.android.chewbiteSensors.data_sensors;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

public class WriteFileTask<T extends CBBuffer> extends TimerTask {

    public static int PERIOD_TIME_MS = 5000;
    public static int DELAY_TIME_MS = 5000;

    private final List<T> sensors;

    //private String directoryName;

    WriteFileTask() {
        this.sensors = new ArrayList<>();
    }

    @Override
    public void run() {
        for (CBBuffer sensorBuffer : this.sensors) {
            //FileManager.writeToFile(this.directoryName, sensorBuffer.getSensorFileName(), sensorBuffer.getSensorEventData("Movimiento"));
            FileManager.writeToFile(sensorBuffer.getSensorFileName(), sensorBuffer.getSensorEventData("Movimiento"));
        }
    }

    public void addSensors(List<T> sensors) {
        this.sensors.addAll(sensors);
    }

    /*public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }*/
}
