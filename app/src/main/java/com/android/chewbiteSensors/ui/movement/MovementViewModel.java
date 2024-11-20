package com.android.chewbiteSensors.ui.movement;

import android.hardware.Sensor;
import android.hardware.SensorManager;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.Map;

public class MovementViewModel extends ViewModel {
    private final Map<Integer, MutableLiveData<Boolean>> sensorEnabledLiveData = new HashMap<>();

    private SensorManager sensorManager; // Get an instance of SensorManager

    public void setSensorManager(SensorManager sensorManager) {
        this.sensorManager = sensorManager;
    }

    public void checkSensorAvailability(int sensorType) {
        Sensor sensor = sensorManager.getDefaultSensor(sensorType);

        // Ensure the LiveData exists in the map
        if (!sensorEnabledLiveData.containsKey(sensorType)) {
            sensorEnabledLiveData.put(sensorType, new MutableLiveData<>(true));
        }

        // Now you can safely set the value
        sensorEnabledLiveData.get(sensorType).setValue(sensor != null);
    }

    /**
     * Compruebe si hay un sensor disponible en el dispositivo.
     * @param sensorType
     * @return
     */
    public boolean isSensorAvailable(int sensorType) {
        Sensor sensor = sensorManager.getDefaultSensor(sensorType);
        return sensor != null;
    }
}