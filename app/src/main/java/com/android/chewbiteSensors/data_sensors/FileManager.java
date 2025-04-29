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

    /**
     * Escribe un array de bytes a un archivo.
     *
     * @param fileName nombre del archivo
     * @param data     array de byte a escribir
     */
    /*public static void writeToFile(String fileName, ArrayList<byte[]> data) {
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
    }*/


    /**
     * Nuevo método que escribe un array de strings a un archivo.
     *
     * @param fileName nombre del archivo
     * @param data     array de strings a escribir
     */
    public static void writeToFile(String fileName, ArrayList<String> data) {
        File file = getFile(fileName);
        if (file == null) return;

        synchronized (getLock(fileName)) {
            // Comprobamos si hay que agregar cabecera:
            boolean needHeader = !file.exists() || file.length() == 0;

            try (FileOutputStream fileOutputStream = new FileOutputStream(file, true)) {
                // Determinamos si el archivo debe tener cabecera
                if (needHeader) {
                    // Determinamos el tipo de sensor por el prefijo del nombre de fichero
                    String sensorType = fileName.substring(0, fileName.indexOf("_"));
                    String header;
                    switch (sensorType) {
                        case CBBuffer.STRING_NUM_OF_STEPS:
                            header = "Marca de tiempo, Cantidad de pasos";
                            break;
                        case CBBuffer.STRING_GPS:
                            header = "Marca de tiempo, Latitud, Longitud, Altitud";
                            break;
                        default:
                            // Para acelerómetro, giroscopio, magnetómetro, gravedad, etc.
                            header = "Marca de tiempo, Eje X, Eje Y, Eje Z";
                    }
                    fileOutputStream.write((header + "\n").getBytes(StandardCharsets.UTF_8));
                }

                for (String line : data) {
                    fileOutputStream.write(line.getBytes(StandardCharsets.UTF_8));
                }
            } catch (IOException e) {
                Log.e("File Error", e.toString());
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

    /**
     * Crea la carpeta base si no existe
     * Se sacó del método "getExperimentDirectory" porque se necesitaba para mostrar la ruta de
     * almacenamiento del experimento.
     *
     * @return File path base de almacenamiento.
     */
    public static File getbaseDir() {
        // Crear carpeta base si no existe
        //File baseDir = new File(context.getFilesDir(), BASE_FOLDER);
        //File baseDir = new File(context.getExternalFilesDir(null), BASE_FOLDER);
        File baseDir = null;

        // Crear carpeta base si no existe

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            // menor o igual a Android 9
            baseDir = new File(Environment.getExternalStorageDirectory(), BASE_FOLDER.concat("/").concat(SUB_FOLDER));
        } else {
            // mayor a Android 9
            baseDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), BASE_FOLDER.concat("/").concat(SUB_FOLDER));
        }
        return baseDir;
    }

    public static File getExperimentDirectory() {
        File baseDir = getbaseDir();

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