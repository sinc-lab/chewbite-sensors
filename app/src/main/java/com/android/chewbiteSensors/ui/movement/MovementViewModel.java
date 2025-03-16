package com.android.chewbiteSensors.ui.movement;

import static com.android.chewbiteSensors.data_sensors.SensorInfo.STEP_COUNTER;

import android.hardware.Sensor;
import android.hardware.SensorManager;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
     * @param sensorType El tipo de sensor a comprobar.
     * @return true si hay un sensor disponible, false en caso contrario.
     */
    public boolean isSensorAvailable(int sensorType) {
        Sensor sensor = sensorManager.getDefaultSensor(sensorType);
        return sensor != null;
    }

    public int getMinHzSensor(int sensorInfo) {
        int minMicrosegundos = Objects.requireNonNull(sensorManager.getDefaultSensor(sensorInfo)).getMinDelay();

        if (sensorInfo == STEP_COUNTER.getSensorType()) {
            minMicrosegundos = 1_000_000;
        }
        // Conversión a Hertz
        return 1_000_000 / minMicrosegundos;
    }
}