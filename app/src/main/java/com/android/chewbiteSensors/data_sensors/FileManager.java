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

/**
 * Clase para manejar archivos en el almacenamiento externo.
 * Revisar: La clase utiliza un contexto estático, lo que podría provocar pérdidas de memoria si no se maneja correctamente. Generalmente se recomienda evitar referencias estáticas a objetos de contexto.
 */
public class FileManager {

    private static final HashMap<String, Object> lockMap = new HashMap<>();

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    /**
     * Escribe un archivo de datos de texto en el almacenamiento externo.
     * @param folderName Nombre del directorio en el almacenamiento externo.
     * @param fileName Nombre del archivo en el almacenamiento externo.
     * @param data Datos a escribir en el archivo.
     */
    public static void writeToFile(String folderName, String fileName, String data) {
        if (FileManager.isExternalStorageWritable()) {
            File file = FileManager.getPublicFile(folderName, fileName);
            synchronized (FileManager.getLock(fileName)) {
                try {
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    FileOutputStream fileOutputStream = new FileOutputStream(file, true);
                    fileOutputStream.write(data.getBytes(StandardCharsets.UTF_8));
                    fileOutputStream.close();
                } catch (IOException e) {
                    Log.e("File Error DIR", e.toString());
                }
            }
        }
    }

    /**
     * Escribe un archivo de datos en el almacenamiento externo.
     * @param folderName Nombre del directorio en el almacenamiento externo.
     * @param fileName Nombre del archivo en el almacenamiento externo.
     * @param data Datos a escribir en el archivo.
     */
    public static void writeToFile(String folderName, String fileName, ArrayList<byte[]> data) {
        if (FileManager.isExternalStorageWritable()) {
            File file = FileManager.getPublicFile(folderName, fileName);
            synchronized (FileManager.getLock(fileName)) {
                try {
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    FileOutputStream fileOutputStream = new FileOutputStream(file, true);
                    for (byte[] bytes: data) {
                        fileOutputStream.write(bytes);
                    }
                    fileOutputStream.close();
                } catch (IOException e) {
                    Log.e("File Error DIR", e.toString());
                }
            }
        }
    }

    /**
     * Devuelve todos los archivos en el directorio dado.
     * @param directoryName
     * @return
     */
    public static File[] getDirectoryContent(String directoryName) {
        // → 3
        File testDirectory = null;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            // Para Android 8 y 9
            testDirectory = FileManager.getTestPublicDirectory(directoryName);
        } else {
            // Para Android 10 o superior (API 29+) → 3
            //testDirectory = new File(context.getExternalFilesDir(directoryName) + "");
            //testDirectory = context.getExternalFilesDir(directoryName);
            //testDirectory = context.getExternalFilesDir(null);

            testDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

            /*try {
                File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                // Crear la nueva carpeta dentro de la carpeta de descargas
                File newFolder = new File(documentsDir, directoryName);
                if (!newFolder.exists()) {
                    newFolder.mkdirs(); // Crea la carpeta y cualquier carpeta padre necesaria
                }
                testDirectory = new File(documentsDir, directoryName);
            }
            catch (Exception e) {
                Log.e("File Error DIR", e.toString());
            }*/

            //testDirectory = FileManager.getTestPublicDirectory(directoryName);
        }
        return testDirectory.listFiles();
    }

    private static Object getLock(String key) {
        Object lock;
        synchronized (lockMap) {
            if (lockMap.containsKey(key)) {
                lock = lockMap.get(key);
            } else {
                lock = new Object();
                lockMap.put(key, lock);
            }
        }
        return lock;
    }

    /**
     * Comprueba si hay almacenamiento externo disponible para lectura y escritura.
     * @return True si hay almacenamiento disponible, false en caso contrario.
     */
    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /*public static File getPublicFile(String directoryName, String fileName) {
        File publicDirectory = FileManager.getTestPublicDirectory(directoryName);
        if (!publicDirectory.exists() && !publicDirectory.mkdirs()) {
            Log.e("File Error", "Couldn't create folder");
        }
        return new File(publicDirectory + File.separator + fileName);
    }*/

    /**
     * Devuelve el directorio público en el almacenamiento externo.
     * @param directoryName Nombre del directorio
     * @param fileName Nombre del archivo
     * @return El directorio público en el almacenamiento externo.
     */
    public static File getPublicFile(String directoryName, String fileName) {
        File publicDirectory = null;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
           publicDirectory = FileManager.getTestPublicDirectory(directoryName);
        } else {
            //publicDirectory = new File(context.getFilesDir() + File.separator + directoryName);
            //publicDirectory = new File(context.getExternalFilesDir(directoryName) + "");
            //publicDirectory = context.getExternalFilesDir(directoryName);
            //publicDirectory = context.getExternalFilesDir(null);

            publicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

            /*try {
                File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                // Crear la nueva carpeta dentro de la carpeta de documentos
                publicDirectory = new File(documentsDir, directoryName);
                if (!publicDirectory.exists()) {
                    publicDirectory.mkdirs(); // Crea la carpeta y cualquier carpeta padre necesaria
                }
            }
            catch (Exception e) {
                Log.e("File Error DIR", e.toString());
            }*/

            //publicDirectory = FileManager.getTestPublicDirectory(directoryName);
        }
        if (!publicDirectory.exists() && !publicDirectory.mkdirs()) {
            Log.e("File Error", "Couldn't create folder");
        }

        //return new File(publicDirectory + File.separator + fileName);
        return new File(publicDirectory, fileName);
    }

    /**
     * Devuelve el directorio público en el almacenamiento externo.
     * @param folderName Nombre del directorio
     * @return El directorio público en el almacenamiento externo.
     */
    public static File getTestPublicDirectory(String folderName) {
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), folderName);
    }

    /**
     * Establece el contexto para la clase.
     * @param context El contexto para la clase.
     */
    public static void setContext(Context context) {
        FileManager.context = context;
    }
}
