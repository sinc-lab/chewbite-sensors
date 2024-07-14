package com.android.chewbiteSensors.alarm;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

/**
 * Esta clase proporciona métodos para gestionar las alarmas.
 */
public enum AlarmScheduler {
    // Almacena una única instancia de AlarmScheduler.
    @SuppressLint("StaticFieldLeak") INSTANCE;
    // El contexto de la aplicación.
    private Context context;

    /**
     * Establece el contexto de la aplicación.
     * @param context
     */
    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * El método schedule() programa una alarma para que se ejecute cada 6 horas.
     */
    public void schedule() {
        // Verifica si el contexto es nulo.
        if (this.context == null) {
            return;
        }

        // Establece la fecha y hora de la próxima alarma.
        Calendar cal = Calendar.getInstance();
        // Establece la hora de la próxima alarma a las 6 horas.
        int PERIOD_TIME_HOURS = 6;
        // Establece la hora de la próxima alarma.
        cal.add(Calendar.HOUR, PERIOD_TIME_HOURS);

        // Establece la hora de la próxima alarma a las 6 horas.
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // Crea un Intent para la clase AlarmBroadcastReceiver.
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        /**
         * Crea un PendingIntent asociado al Intent y la clase AlarmBroadcastReceiver.
         * Este PendingIntent es un token que representa de forma única la alarma programada.
         * El indicador FLAG_IMMUTABLE indica que otras aplicaciones no pueden modificar PendingIntent.
         */
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        // Programa la alarma para que se ejecute cada 6 horas.
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
    }

    /**
     * El método cancel() cancela una alarma programada previamente que está asociada con la clase
     * AlarmBroadcastReceiver.
     */
    public void cancel() {
        // Recupera el servicio AlarmManager del sistema, que se encarga de gestionar las alarmas.
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // Crea un Intent para la clase AlarmBroadcastReceiver.
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        // Crea un PendingIntent asociado al Intent y la clase AlarmBroadcastReceiver.
        // Este PendingIntent es un token que representa de forma única la alarma programada.
        // El indicador FLAG_IMMUTABLE indica que otras aplicaciones no pueden modificar PendingIntent.
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        // Cancela la alarma programada con el PendingIntent.
        alarmManager.cancel(pendingIntent);
    }

}
