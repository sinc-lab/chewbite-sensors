package com.android.chewbiteSensors.deviceStatus;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;

public abstract class DeviceStatusService {

    public static DeviceStatus getDeviceStatus(Context context) {
        DeviceStatus deviceStatus = new DeviceStatus();
        deviceStatus.setAirplane(DeviceStatusService.getAirplaneMode(context));
        deviceStatus.setBluetooth(DeviceStatusService.getBluetoothMode());
        deviceStatus.setWifi(DeviceStatusService.getWifiMode(context));
        deviceStatus.setSilence(DeviceStatusService.getSilence(context));
        return deviceStatus;
    }

    public static DeviceStatus getRequiredStatus() {
        return new DeviceStatus(Status.ENABLED, Status.DISABLED, Status.DISABLED, Status.ENABLED);
    }

    private static Status getAirplaneMode(Context context) {
        return (Settings.System.getInt(context.getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON, 0) != 0) ? Status.ENABLED : Status.DISABLED;
    }

    private static Status getBluetoothMode() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) ? Status.ENABLED : Status.DISABLED;
    }

    /*private static Status getWifiMode(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getActiveNetworkInfo();
        return (mWifi != null && mWifi.isConnected()) ? Status.ENABLED : Status.DISABLED;
    }*/
    private static Status getWifiMode(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connManager != null) {

            // For versions prior to Android 10
            NetworkInfo mWifi = connManager.getActiveNetworkInfo();
            return (mWifi != null && mWifi.isConnected() && mWifi.getType() == ConnectivityManager.TYPE_WIFI)
                    ? Status.ENABLED : Status.DISABLED;

        }

        return Status.DISABLED; // Return DISABLED by default or handle appropriately
    }

    private static Status getSilence(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            // Verifica si el modo de sonido está en silencio
            if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
                return Status.ENABLED; // El dispositivo está en modo silencio
            } else {
                return Status.DISABLED; // El dispositivo no está en modo silencio
            }
        }
        return Status.DISABLED; // Retorna DISABLED por defecto si no se puede obtener el AudioManager
    }
}
