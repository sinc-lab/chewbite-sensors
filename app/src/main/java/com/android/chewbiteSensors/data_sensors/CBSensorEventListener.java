package com.android.chewbiteSensors.data_sensors;

import static android.content.Context.POWER_SERVICE;
import static android.content.Context.SENSOR_SERVICE;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.PowerManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

/**
 * Es el corazón de la recopilación y gestión de datos de sus sensores en el proyecto "chewBite Sensors".
 * Implementa la interfaz SensorEventListener, lo que significa que es responsable de manejar los
 * eventos del sensor desde el dispositivo Android.
 */
public enum CBSensorEventListener implements SensorEventListener {
    INSTANCE;

    private SensorManager sensorManager;

    private Timer timer;

    private List<CBSensorBuffer> sensors;

    private ExperimentData data;

    private SensorEventListenerState state = SensorEventListenerState.IDLE;

    private HandlerThread mSensorThread;

    PowerManager.WakeLock wakeLock;

    /**
     * Establece los datos del experimento.
     * @param data
     */
    public void setExperimentData(ExperimentData data) {
        this.data = data;
    }

    /**
     * Establece el número de archivo del experimento.
     */
    public void changeFileNumber() {
        this.data.setFileNumber(this.data.getFileNumber() + 1);
        // Establece el número de archivo de los sensores
        for (CBSensorBuffer sensor: this.sensors) {
            // Establece el número de archivo del sensor
            sensor.setFileNumber(this.data.getFileNumber());
        }
    }

    /**
     * Inicia el experimento.
     * @param context
     */
    public void start(Context context) throws SecurityException {
        //
        PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "CB::MyWakelockTag");
        wakeLock.acquire(10*60*1000L /*10 minutes*/);
        // Establece el estado del sensor
        this.state = SensorEventListenerState.RUNNING;
        // Establece el número de archivo del experimento
        this.sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        // Establece los sensores
        this.sensors = new ArrayList<>();
        // En estas lineas es donde se pueden agregar los sensores
        // Se agrega el acelerómetro, el giroscopio y el campo magnético
        /**
         * Acá es donde se debe modificar para que se agreguen o saquen los sensores
         */
        this.addSensor(CBBuffer.STRING_ACCELEROMETER, Sensor.TYPE_ACCELEROMETER);
        this.addSensor(CBBuffer.STRING_GYROSCOPE, Sensor.TYPE_GYROSCOPE);
        this.addSensor(CBBuffer.STRING_MAGNETIC_FIELD, Sensor.TYPE_MAGNETIC_FIELD);
        // Registra los sensores
        this.registerDeviceSensors();
        // Establece el archivo
        this.scheduleWriteFile(data.getTimestamp());
    }

    /**
     * Finaliza el experimento.
     */
    public void stop() {
        this.cancelTimer();
        this.unregisterDeviceSensors();
        this.state = SensorEventListenerState.IDLE;

        wakeLock.release();
    }

    /**
     * Indica si el experimento está corriendo.
     * @return
     */
    public boolean isRunning() {
        return this.state == SensorEventListenerState.RUNNING;
    }

    /**
     * Indica si el experimento está finalizado.
     * @param event the {@link android.hardware.SensorEvent SensorEvent}.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        // Establece el valor de los sensores
        for (CBSensorBuffer sensor: sensors) {
            // Verifica que el tipo de sensor sea el mismo
            if (event.sensor.getType() == sensor.getSensor().getType()) {
                try {
                    sensor.append(new SensorEventData(event.timestamp, event.values[0], event.values[1], event.values[2]));
                } catch (Exception e) {
                    FileManager.writeToFile(this.data.getTimestamp(), "error.txt", e.getMessage() + '\n');
                }
            }
        }
    }

    /**
     * Se llama cuando la precisión de un sensor ha cambiado.
     * @param arg0
     * @param arg1 La nueva precisión de este sensor, uno de
     *         {@code SensorManager.SENSOR_STATUS_*}
     */
    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
    }

    /**
     * Este método proporciona una manera conveniente de obtener una lista de archivos asociados
     * con una prueba o experimento.
     * El nombre del directorio apunte a una ruta donde su aplicación almacena datos relacionados
     * con una ejecución de prueba específica.
     * Al llamar a este método, accede a estos archivos para su posterior procesamiento, análisis o
     * visualización.
     * @param directoryName
     */
    public File[] getTestFiles(String directoryName) {
        // Establece el número de archivo del experimento → 2
        return FileManager.getDirectoryContent(directoryName);
    }

    public void cancelTimer() {
        if (this.timer != null) {
            this.timer.cancel();
        }
    }

    private void addSensor(String sensorName, int sensorType) {
        Sensor sensor = this.sensorManager.getDefaultSensor(sensorType);
        if (sensor != null) {
            CBSensorBuffer sensorBuffer = new CBSensorBuffer(sensorName, sensor);
            sensorBuffer.setFileNumber(this.data.getFileNumber());
            this.sensors.add(sensorBuffer);
        }
    }

    private void registerDeviceSensors() {
        mSensorThread = new HandlerThread("Sensor thread", Thread.MAX_PRIORITY);
        mSensorThread.start();
        Handler mSensorHandler = new Handler(mSensorThread.getLooper()); //Blocks until looper is prepared, which is fairly quick

        for (CBSensorBuffer sensor: sensors) {
            this.sensorManager.registerListener(this, sensor.getSensor(), 10000, 1000000, mSensorHandler);
        }
    }

    private void unregisterDeviceSensors() {
        this.sensorManager.unregisterListener(this);
        for (CBSensorBuffer sensor: sensors) {
            sensor.resetBuffer();
        }
        mSensorThread.quitSafely();
    }

    private void scheduleWriteFile(String directoryName) {
        this.timer = new Timer();
        WriteFileTask<CBSensorBuffer> writeFileTask = new WriteFileTask<>();

        writeFileTask.addSensors(this.sensors);

        writeFileTask.setDirectoryName(directoryName);
        timer.schedule(writeFileTask, WriteFileTask.DELAY_TIME_MS, WriteFileTask.PERIOD_TIME_MS);
    }
}
