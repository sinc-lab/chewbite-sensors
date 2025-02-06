package com.android.chewbiteSensors.data_sensors;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class FileManager {

    private static final HashMap<String, Object> lockMap = new HashMap<>();

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    private static final String BASE_FOLDER = "Recordings";
    private static final String SUB_FOLDER = "chewBite sensor";
    private static ExperimentData data;


    public static void writeToFile(String fileName, String data) {
        File file = getFile(fileName);
        if (file != null) {
            synchronized (getLock(fileName)) {
                try (FileOutputStream fileOutputStream = new FileOutputStream(file, true)) {
                    fileOutputStream.write(data.getBytes(StandardCharsets.UTF_8));
                } catch (IOException e) {
                    Log.e("File Error", e.toString());
                }
            }
        }
    }

    public static void writeToFile(String fileName, ArrayList<byte[]> data) {
        File file = getFile(fileName);
        if (file != null) {
            synchronized (getLock(fileName)) {
                try (FileOutputStream fileOutputStream = new FileOutputStream(file, true)) {
                    for (byte[] bytes : data) {
                        fileOutputStream.write(bytes);
                    }
                } catch (IOException e) {
                    Log.e("File Error", e.toString());
                }
            }
        }
    }

    public static File getFile(String fileName) {
        if (isExternalStorageWritable()) {
            File experimentDir = getExperimentDirectory();
            if (experimentDir != null) {
                return new File(experimentDir, fileName);
            }
        }
        return null;
    }

    public static File getExperimentDirectory() {
        // Crear carpeta base si no existe
        //File baseDir = new File(context.getFilesDir(), BASE_FOLDER);
        //File baseDir = new File(context.getExternalFilesDir(null), BASE_FOLDER);
        File baseDir = null;

        // Crear carpeta base si no existe

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            // menor o igual a Android 9
            baseDir = new File(Environment.getExternalStorageDirectory(), BASE_FOLDER.concat("/").concat(SUB_FOLDER));
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S) {
            // mayor a Android 9 y menor a Android 12
            baseDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), BASE_FOLDER.concat("/").concat(SUB_FOLDER));
        } else {
            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            // mayor o igual a Android 12
            baseDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RECORDINGS), SUB_FOLDER);
        }

        File subDir = new File(baseDir, data.getExperimentName());

        if (!subDir.exists() && !subDir.mkdirs()) {
            Log.e("File Error", "Couldn't create subfolder");
            return null;
        }

        @SuppressLint("SimpleDateFormat")
        // Crear carpeta para el experimento, asegurando que no se sobrescriba
        File experimentDir = new File(subDir, data.getTimestampFolder());
        /*int count = 1;
        while (experimentDir.exists()) {
            experimentDir = new File(subDir, experimentDir + "_" + count);
            count++;
        }*/

        /*if (!experimentDir.mkdirs()) {
            Log.e("File Error", "Couldn't create experiment folder");
            return null;
        }*/
        // Crear carpeta para el experimento, asegurando que no se sobrescriba
        if (!experimentDir.exists()) {
            experimentDir.mkdirs();
        }

        return experimentDir;
    }

    private static Object getLock(String key) {
        synchronized (lockMap) {
            return lockMap.computeIfAbsent(key, k -> new Object());
        }
    }

    /**
     * Comprueba si el almacenamiento externo está disponible para al menos leer
     *
     * @return
     */
    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }


    public static void setContext(Context context) {
        FileManager.context = context;
    }

    public static void setExperimentData(ExperimentData data) {
        FileManager.data = data;
    }
}