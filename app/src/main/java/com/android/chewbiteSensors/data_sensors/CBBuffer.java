package com.android.chewbiteSensors.data_sensors;


import android.annotation.SuppressLint;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * La clase CBBuffer implementa un buffer circular que almacena datos de eventos de sensores. <br><br>
 * Estos datos se almacenan en un arreglo sensorEventData y se accede a ellos mediante dos índices,
 * head y tail, que indican la posición del siguiente elemento a escribir y el siguiente elemento a
 * leer, respectivamente.<br><br>
 * Cuando se agrega un nuevo evento con append(), se escribe en la posición head y este índice se
 * actualiza. Si el buffer está lleno, se lanza una excepción.
 */
public abstract class CBBuffer {
    public static final String STRING_ACCELEROMETER = "accelerometer";
    public static final String STRING_ACCELEROMETER_UNCALIBRATED = "accelerometer_uncalibrated";
    public static final String STRING_GYROSCOPE = "gyroscope";
    public static final String STRING_GYROSCOPE_UNCALIBRATED = "gyroscope_uncalibrated";
    public static final String STRING_MAGNETIC_FIELD = "magnetometer";
    public static final String STRING_MAGNETIC_FIELD_UNCALIBRATED = "magnetometer_uncalibrated";
    public static final String STRING_GRAVITY = "gravity";
    public static final String STRING_NUM_OF_STEPS = "num_of_steps";
    public static final String STRING_GPS = "gps";
    public static final String STRING_SOUND = "audio";

    private static final int BUFFER_SIZE = 4096;  // Must be power of 2 since it uses bit operations
    private static final String FILE_EXTENSION = ".txt";

    private final String sensorName;
    private final SensorEventData[] sensorEventData;
    private int tail;
    private int head;
    private int fileNumber;

    CBBuffer(String sensorName) {
        this.tail = 0;
        this.head = 0;

        this.sensorName = sensorName;
        this.sensorEventData = new SensorEventData[BUFFER_SIZE];

        this.fileNumber = 1;
    }

    /**
     * agrega un nuevo objeto SensorEventData al búfer en la posición principal. Si el búfer está
     * lleno, genera una excepción.
     *
     * @param s
     * @throws Exception
     */
    public void append(SensorEventData s) throws Exception {
        // Comprobar si el buffer está lleno
        int next = (head + 1) & (BUFFER_SIZE - 1);
        // Si el buffer no está lleno
        if (next != tail) {
            // Escribe en la posición principal
            this.sensorEventData[head] = s;
            // Actualizar el índice
            head = next;
            // Si el buffer está lleno, lanzar una excepción
        } else {
            // Obtener la hora actual en milisegundos
            String ts = Long.toString(System.currentTimeMillis());
            // Lanzar una excepción
            String message = this.getSensorName() + ": FULL BUFFER AT " + ts;
            throw new Exception(message);
        }
    }

    /**
     * Devuelve el nombre del sensor
     *
     * @return sensorName
     */
    public String getSensorName() {
        return sensorName;
    }

    /**
     * Devuelve el nombre del archivo de datos del sensor
     * @return this.getSensorName() + String.format("_%02d", fileNumber) + FILE_EXTENSION
     */
    @SuppressLint("DefaultLocale")
    public String getSensorFileName() {
        return this.getSensorName() + String.format("_%02d", fileNumber) + FILE_EXTENSION;
    }

    /**
     * devuelve una lista de arreglos de bytes que representan los datos de los eventos almacenados
     * en el buffer.
     * @return
     */
    public ArrayList<byte[]> getSensorEventData(String sensorName) {
        // Obtener el índice de la cola
        int copyUntil = this.head;
        // Comprobar si el buffer está vacío
        ArrayList<byte[]> bytes = new ArrayList<>();
        // Si el buffer no está vacío, copiar los datos
        while (tail != copyUntil) {
            // Crear un buffer
            ByteBuffer buff = ByteBuffer.allocate(Long.BYTES + 3 * Float.BYTES);
            // Obtener el evento
            SensorEventData event = this.sensorEventData[tail];

            if (CBBuffer.STRING_NUM_OF_STEPS.equals(sensorName)) { // Identificar el Step Counter
                // Copiar los datos específicos del Step Counter al buffer
                buff.putLong(event.getTimestamp()).putFloat(event.getCount());
            } else if (CBBuffer.STRING_GPS.equals(sensorName)) {
                // Copiar los datos en el buffer
                buff.putLong(event.getTimestamp()).putFloat(event.getLat()).putFloat(event.getLon()).putFloat(event.getAlt());
            } else {
                // Copiar los datos en el buffer
                buff.putLong(event.getTimestamp()).putFloat(event.getX()).putFloat(event.getY()).putFloat(event.getZ());
            }
            // Agregar el buffer al arreglo
            bytes.add(buff.array());
            // Actualizar el índice
            tail = (tail + 1) & (BUFFER_SIZE - 1);
        }
        // Si el buffer está vacío, no hacer nada
        return bytes;
    }

    /**
     * Devuelve el número de archivo actual.
     * @return fileNumber
     */
    public int getFileNumber() {
        return fileNumber;
    }

    /**
     * Establece el número de archivo actual.
     * @param fileNumber
     */
    public void setFileNumber(int fileNumber) {
        this.fileNumber = fileNumber;
    }

    /**
     * Reinicia el índice de la cola
     */
    public void resetBuffer() {
        this.tail = 0;
        this.head = 0;
    }

}