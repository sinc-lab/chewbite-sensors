package com.android.chewbiteSensors.data_sensors;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.android.chewbiteSensors.MainActivity;
import com.android.chewbiteSensors.R;
import com.android.chewbiteSensors.alarm.AlarmScheduler;


public class CBService extends Service {

    private final IBinder mBinder = new CBBinder();

    private final String CHANNEL_ID = "CB_NOTIFICATION_DEFAULT_CHANNEL";

    public class CBBinder extends Binder {
        public CBService getService() {
            return CBService.this;
        }
    }

    public void startTest(Context context) {
        this.startForegroudService();
        CBSensorEventListener.INSTANCE.start(context);
        AlarmScheduler.INSTANCE.setContext(context);
        AlarmScheduler.INSTANCE.schedule();
        AudioRecorder.INSTANCE.start(this);
    }

    public void stopTest() {
        CBSensorEventListener.INSTANCE.stop();
        this.stopForegroundService();
        AlarmScheduler.INSTANCE.cancel();
        AudioRecorder.INSTANCE.stop();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("Service", "on bind");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("CBService", "on unbind");
        if (CBSensorEventListener.INSTANCE.isRunning()) {
            this.stopTest();
        }
        return true;
    }

    @SuppressLint("ForegroundServiceType")
    private void startForegroudService() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        this.createNotificationChannel();

        Notification notification = new Notification.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(getString(R.string.notification_title))
                    .setContentText(getString(R.string.notification_text))
                    .setContentIntent(pendingIntent).build();

        int NOTIFICATION_ID = 1336;
        this.startForeground(NOTIFICATION_ID, notification);
    }

    private void createNotificationChannel() {
        CharSequence name = getString(R.string.channel_name);
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    private void stopForegroundService() {
        this.stopForeground(true);
    }
}
