package com.android.chewbiteSensors.permissions;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.content.ContextCompat;

public class PermissionChecker {
    private final Context context;

    public PermissionChecker(Context context) {
        this.context = context;
    }

    public boolean hasPermissions() {
        boolean hasRecordAudioPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            // Para Android 9 y anteriores, necesitas el permiso WRITE_EXTERNAL_STORAGE
            boolean hasWriteStoragePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
            return hasRecordAudioPermission && hasWriteStoragePermission;
        } else {
            // Para Android 10 y posteriores, no necesitas el permiso WRITE_EXTERNAL_STORAGE
            return hasRecordAudioPermission;
        }
    }
}
