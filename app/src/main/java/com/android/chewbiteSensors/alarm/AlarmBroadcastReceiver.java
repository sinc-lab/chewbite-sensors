package com.android.chewbiteSensors.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.android.chewbiteSensors.data_sensors.CBSensorEventListener;

/**
 * Actúa como escucha de un evento del sistema, actualiza un sistema de seguimiento de archivos y
 * luego programa la siguiente aparición del evento.
 */
public class AlarmBroadcastReceiver extends BroadcastReceiver {

    /**
     * Este método se llama cuando BroadcastReceiver recibe una transmisión de intención.
     * responde a una transmisión del sistema, actualiza un contador de archivos y programa la
     * próxima aparición del evento que activa este receptor.
     * @param context El contexto en el que se está ejecutando el receptor.
     * @param intent La intención que se recibe.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        /**
         * Actualiza el sistema de seguimiento de archivos
         * y luego programa la siguiente aparición del evento
         * en el sistema
         */
        CBSensorEventListener.INSTANCE.changeFileNumber();
        /**
         * Programa la siguiente aparición del evento
         * en el sistema
         */
        AlarmScheduler.INSTANCE.schedule();
    }
}
