package com.android.chewbiteSensors.data_sensors;

import android.hardware.Sensor;

/**
 * Actúa como un contenedor alrededor de un objeto Sensor y le agrega un nombre
 */
public class CBSensorBuffer extends CBBuffer {

    private final Sensor sensor;

    /**
     * Cuando crea un objeto CBSensorBuffer, proporciona dos cosas:
     * @param sensorName Cadena para identificar el sensor (por ejemplo, "Acelerómetro", "Giroscopio").
     * @param sensor El objeto Sensor real que desea envolver.
     */
    CBSensorBuffer(String sensorName, Sensor sensor) {
        super(sensorName);
        this.sensor = sensor;
    }

    /**
     * Este método simplemente devuelve el objeto Sensor subyacente que se almacena dentro de CBSensorBuffer.
     * @return
     */
    public Sensor getSensor() {
        return sensor;
    }
}
