package com.android.chewbiteSensors;

import static android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.android.chewbiteSensors.data_sensors.AppMode;
import com.android.chewbiteSensors.data_sensors.AudioRecorder;
import com.android.chewbiteSensors.data_sensors.CBGPSListener;
import com.android.chewbiteSensors.data_sensors.CBSensorEventListener;
import com.android.chewbiteSensors.data_sensors.CBService;
import com.android.chewbiteSensors.data_sensors.ExperimentData;
import com.android.chewbiteSensors.data_sensors.FileManager;
import com.android.chewbiteSensors.databinding.ActivityMainBinding;
import com.android.chewbiteSensors.deviceStatus.DeviceStatus;
import com.android.chewbiteSensors.deviceStatus.DeviceStatusService;
import com.android.chewbiteSensors.settings.GetSettings;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private final String tag = "MainActivity";
    public static String TEST_DATA_STRING = "testData";
    private static final int APPLICATION_PERMISSION_CODE = 1;

    private DeviceStatus currentStatus;
    private AppMode mode;
    private Intent batteryStatus;
    private ExperimentData data;
    private static final String INFO_FILE_NAME = "info.txt";
    //private TestSensorsEventListener testSensorsEventListener;
    private static final String DATE_FORMAT = "dd/MM/yyyy-HH:mm:ss.S";
    private CBService mBoundService;
    private boolean mShouldUnbind;
    private static final String APP_MODE_STRING = "appMode";
    private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Prueba Log
        //android.util.Log.d(tag, "onCreate");
        super.onCreate(savedInstanceState);

        com.android.chewbiteSensors.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //BottomNavigationView navView = findViewById(R.id.nav_view);
        //BottomNavigationView customBottomNavigationView = findViewById(R.id.customBottomNavigationView);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        /*
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications,
                R.id.navigation_settings, R.id.navigation_predictions)
                .build();
          */
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        /*----------------------------------------------------------------------------------------*/
        // Restore AppMode
        if (savedInstanceState != null && savedInstanceState.containsKey(APP_MODE_STRING)) {
            this.mode = (AppMode) savedInstanceState.getSerializable(APP_MODE_STRING);
        } else {
            this.mode = AppMode.TESTING_SENSORS;
        }
        /*----------------------------------------------------------------------------------------*/
        // Solicita los permisos para que la pueda acceder al micrófono
        this.askForPermissions();
        /*----------------------------------------------------------------------------------------*/
        FileManager.setContext(this);
        /*----------------------------------------------------------------------------------------*/
    }

    /*----------------------------------------------------------------------------------------*/
    public void askForPermissions() {
        if (!hasPermissions()) {
            String[] permissions = getRequiredPermissions();
            ActivityCompat.requestPermissions(this, permissions, APPLICATION_PERMISSION_CODE);
        }
    }

    public boolean hasPermissions() {
        String[] permissions = getRequiredPermissions();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private String[] getRequiredPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return new String[]{
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                    // No es necesario WRITE_EXTERNAL_STORAGE en Android 11 y posteriores
            };
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return new String[]{
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE // Necesario para Android 10
            };
        } else {
            return new String[]{
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE // Necesario para Android 9 y anteriores
            };
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == APPLICATION_PERMISSION_CODE) {
            boolean allPermissionsGranted = true;

            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                // Todos los permisos han sido concedidos
                Toast.makeText(this, "Permisos concedidos", Toast.LENGTH_SHORT).show();
                // Continuar con la funcionalidad que requiere los permisos
            } else {
                // Al menos un permiso fue denegado
                if (shouldShowRequestPermissionRationale(permissions[0])) {
                    // El usuario ha denegado el permiso pero no ha seleccionado "No volver a preguntar"
                    Toast.makeText(this, "Se requieren permisos para continuar", Toast.LENGTH_SHORT).show();
                } else {
                    // El usuario ha denegado el permiso y ha seleccionado "No volver a preguntar"
                    showPermissionDeniedDialog();
                }
            }
        }
    }

    private void showPermissionDeniedDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permisos necesarios")
                .setMessage("Esta aplicación necesita permisos para funcionar correctamente. Por favor, habilita los permisos en la configuración.")
                .setPositiveButton("Configuración", (dialog, which) -> {
                    // Abrir la configuración de la aplicación
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton("Cancelar", null)
                .create()
                .show();
    }

    /*----------------------------------------------------------------------------------------*/

    public Boolean checkStatusBeforeStart() {
        android.util.Log.d(tag, "checkStatusBeforeStart");
        DeviceStatus requiredStatus = DeviceStatusService.getRequiredStatus();
        this.currentStatus = DeviceStatusService.getDeviceStatus(this);
        return currentStatus.equals(requiredStatus);

    }

    /*----------------------------------------------------------------------------------------*/

    public void openDialog() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("WARNING");

        DeviceStatus requiredStatus = DeviceStatusService.getRequiredStatus();

        String message = "El test no puede comenzar por las siguientes razones:\n\n";
        if (this.currentStatus.getAirplane() != requiredStatus.getAirplane()) {
            message += "* MODO AVIÓN DESHABILITADO *\n\n";
        }
        if (this.currentStatus.getBluetooth() != requiredStatus.getBluetooth()) {
            message += "* BLUETOOTH ACTIVADO *\n\n";
        }
        if (this.currentStatus.getWifi() != requiredStatus.getWifi()) {
            message += "* WIFI ACTIVADO *\n\n";
        }
        alertDialogBuilder.setMessage(message);

        alertDialogBuilder.setPositiveButton("Accept", (dialog, arg1) -> Log.d("DIALOG", "yes"));

        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /*----------------------------------------------------------------------------------------*/

    /**
     * Inicia el test.
     */
    public void startTest() {

        // Configurar el TextView
        TextView tvFilePathinf = findViewById(R.id.tv_file_path);
        // Ocultar el TextView
        tvFilePathinf.setVisibility(View.GONE);

        //
        if (this.mode != AppMode.RUNNING) {
            // Inicializa los datos del experimento
            this.data = new ExperimentData(this);
            this.initExperimentData();
            FileManager.setExperimentData(data);
            // 2-) Inicializa la instancia del experimento
            CBSensorEventListener.INSTANCE.setExperimentData(this.data);
            CBGPSListener.INSTANCE.setExperimentData(this.data);
            AudioRecorder.INSTANCE.setExperimentData(this.data);
            this.mode = AppMode.RUNNING;
        }
        //this.showExperimentRunning();
        // 3.0-) Inicia el servicio
        this.doBindService();
        // Código existente para iniciar la grabación
        setStatusBarColor(R.color.green_700); // Cambia a verde (o el color definido en tu archivo de recursos).green_700); // Cambia a verde (o el color definido en tu archivo de recursos):color/holo_green_dark"); // Cambia a verde (o el color definido en tu archivo de recursos)@theme/colorPrimary""); // Cambia a verde (o el color definido en tu archivo de recursos)
    }

    @SuppressLint("SetTextI18n")
    public void stopTest() {
        this.mode = AppMode.STOPPED;
        //this.sendButton.show();
        mBoundService.stopTest();

        this.batteryStatus = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        assert this.batteryStatus != null;
        data.setBatteryAtEnd(this.batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1));
        data.setEndDate(new Date());

        this.logExtraInfo();

        // Obtener los archivos generados → 1
        //File[] testFiles = CBSensorEventListener.INSTANCE.getTestFiles(data.getTimestamp());
        //this.showGeneratedFiles(testFiles);
        this.doUnbindService();
        /*----------------------------------------------------------------------------------------*/
        // Obtener la ruta de los archivos generados
        String filePath = FileManager.getExperimentDirectory().toString();

        // Configurar el TextView
        TextView tvFilePathinf = findViewById(R.id.tv_file_path);
        // Mostrar la ruta al experimento
        tvFilePathinf.setVisibility(View.VISIBLE);
        tvFilePathinf.setText("El experimento finalizó correctamente. El mismo se encuentra almacenado en: " + filePath);
        /*----------------------------------------------------------------------------------------*/
        // Código existente para detener la grabación
        setStatusBarColor(android.R.color.black); // Cambia a negro
    }
    /*----------------------------------------------------------------------------------------*/

    /**
     * Establece el color de la barra de estado.
     *
     * @param colorResId
     */
    private void setStatusBarColor(int colorResId) {
        getWindow().setStatusBarColor(getResources().getColor(colorResId));
    }
    /*----------------------------------------------------------------------------------------*/

    private void logExtraInfo() {
        StringBuilder info = new StringBuilder();

        info.append("DEVICE INFO: ").append(Build.MANUFACTURER).append(" ").append(Build.MODEL).append("\n");
        info.append("ANDROID VERSION: ").append(Build.VERSION.RELEASE).append("\n\n");

        @SuppressLint("SimpleDateFormat") String formattedStartDate = new SimpleDateFormat(DATE_FORMAT).format(this.data.getStartDate());
        info.append("STARTED AT ").append(formattedStartDate).append("\n");

        @SuppressLint("SimpleDateFormat") String formattedEndDate = new SimpleDateFormat(DATE_FORMAT).format(this.data.getEndDate());
        info.append("ENDED AT ").append(formattedEndDate).append("\n");

        info.append("\nBATTERY:\n");
        info.append("Battery at start (%): ").append(100 * this.data.getBatteryAtStart() / (float) this.data.getBatteryCapacity()).append("\n");
        info.append("Battery at end (%): ").append(100 * this.data.getBatteryAtEnd() / (float) this.data.getBatteryCapacity()).append("\n");


        info.append("\nSETTINGS:\n");
        info.append("Audio: ").append(GetSettings.getStatusSwitch("sound", this)).append("\n");
        // Obtén el array de opciones desde los recursos
        String optionBitRate = getResources().getStringArray(R.array.text_bit_rate_options)[GetSettings.getStatusSpinner("bit_rate_sound", this)];
        info.append(this.getString(R.string.text_bit_rate)).append(": ").append(optionBitRate).append("\n");
        String optionFrequencySound = getResources().getStringArray(R.array.text_frequency_sound_options)[GetSettings.getStatusSpinner("frecuency_sound", this)];
        info.append(this.getString(R.string.text_frequency)).append(": ").append(optionFrequencySound).append("\n");
        String optionFileType = getResources().getStringArray(R.array.text_type_file_options)[GetSettings.getStatusSpinner("file_type", this)];
        info.append(this.getString(R.string.text_type_of_file)).append(": ").append(optionFileType).append("\n\n");

        info.append("Movement: ").append(GetSettings.getStatusSwitch("movement", this)).append("\n");
        String optionFrequencyMovement = getResources().getStringArray(R.array.text_frequency_movement_options)[GetSettings.getStatusSpinner("frecuency_movement", this)];
        info.append(this.getString(R.string.text_frequency)).append(": ").append(optionFrequencyMovement).append("\n");
        info.append("Accelerometer: ").append(GetSettings.getStatusSwitch("accelerometer", this)).append("\n");
        info.append("Gyroscope: ").append(GetSettings.getStatusSwitch("gyroscope", this)).append("\n");
        info.append("Magnetometer: ").append(GetSettings.getStatusSwitch("magnetometer", this)).append("\n");
        info.append("Accelerometer uncalibrated: ").append(GetSettings.getStatusSwitch("uncalibrated_accelerometer", this)).append("\n");
        info.append("Gyroscope uncalibrated: ").append(GetSettings.getStatusSwitch("uncalibrated_gyroscope", this)).append("\n");
        info.append("Magnetometer uncalibrated: ").append(GetSettings.getStatusSwitch("uncalibrated_magnetometer", this)).append("\n");
        info.append("Gravity: ").append(GetSettings.getStatusSwitch("gravity", this)).append("\n");
        info.append("Number of Steps: ").append(GetSettings.getStatusSwitch("number_of_steps", this)).append("\n\n");

        info.append("GPS: ").append(GetSettings.getStatusSwitch("gps", this)).append("\n");
        String optionFrequencyGPS = getResources().getStringArray(R.array.text_frequency_gps_options)[GetSettings.getStatusSpinner("frecuency_gps", this)];
        info.append(this.getString(R.string.text_frequency_gps)).append(": ").append(optionFrequencyGPS).append("\n");
        // Guardar la información en un archivo
        //FileManager.writeToFile(this.data.getTimestamp(), INFO_FILE_NAME, info.toString());
        FileManager.writeToFile(INFO_FILE_NAME, info.toString());
    }
    /*----------------------------------------------------------------------------------------*/

    /**
     * Inicializa los datos del experimento relacionados con el estado de la batería del dispositivo
     * al inicio del experimento.
     * Recupera el nivel y la capacidad de la batería actual y los almacena en un objeto
     * ExperimentData.
     */
    private void initExperimentData() {
        // contiene información sobre el estado de la batería del dispositivo.
        this.batteryStatus = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        // afirmación que verifica si batteryStatus no es nulo. Si la afirmación falla (es decir, batteryStatus es nulo), indica que no se pudo recuperar la información del estado de la batería.
        assert this.batteryStatus != null;
        data.setBatteryAtStart(this.batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1));
        data.setBatteryCapacity(this.batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1));
    }

    /*----------------------------------------------------------------------------------------*/

    /*@SuppressLint("SetTextI18n")
    private void showExperimentRunning() {
        //TableLayout filesTableLayout = findViewById(R.id.filesTableLayout);

        //filesTableLayout.removeAllViews();
        //this.filesCheckBoxList = new ArrayList<>();

        //TextView textView = new TextView(this);
        //textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        //textView.setText("Test running");
        //filesTableLayout.addView(textView);

        if (this.testSensorsEventListener != null) {
            this.testSensorsEventListener.stop();
            this.testSensorsEventListener = null;
        }
    }*/

    /*----------------------------------------------------------------------------------------*/

    /**
     * Inicia un servicio en primer plano (CBService) y vincula la actividad que lo llama
     * (MainActivity), lo que permite la interacción y el intercambio de datos.
     */
    void doBindService() {
        Intent intent = new Intent(MainActivity.this, CBService.class);
        intent.putExtra(MainActivity.TEST_DATA_STRING, this.data);

        startForegroundService(intent);
        // 3.1-) mConnection
        boolean bind = bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        if (bind) {
            mShouldUnbind = true;
        } else {
            Log.e("MY_APP_TAG", "Error: The requested service doesn't " +
                    "exist, or this client isn't allowed access to it.");
        }
    }

    /*----------------------------------------------------------------------------------------*/

    /**
     * Es una instancia de la clase ServiceConnection que actúa como un puente entre la actividad
     * principal (MainActivity) y el servicio en segundo plano (CBService). <br></br>
     * Su función principal es gestionar la conexión y la comunicación entre ambos componentes.
     */
    private final ServiceConnection mConnection = new ServiceConnection() {
        /**
         * Se ejecuta cuando la actividad se conecta exitosamente al servicio.
         * Obtiene una instancia del servicio (mBoundService) a través del IBinder proporcionado.
         * Inicia la prueba: Llama al método startTest del servicio, iniciando la lógica principal del experimento.
         * Deshabilita los controles de la interfaz: Impide que el usuario interactúe con ciertos controles mientras la prueba está en ejecución.
         * Muestra un mensaje breve ("Service connected") al usuario.
         *
         * @param className El nombre del componente concreto del servicio al que se ha conectado.
         *
         * @param service El IBinder del canal de comunicación del Servicio,
         *                desde el cual ahora podrás realizar llamadas.
         */
        @SuppressLint("RestrictedApi")
        public void onServiceConnected(ComponentName className, IBinder service) {
            mBoundService = ((CBService.CBBinder) service).getService();
            // 3.2-) Inicia el test
            mBoundService.startTest(MainActivity.this);

            // Deshabilita los controles
            EnablingAndDisablingControls(false);

            Toast.makeText(MainActivity.this, "Service connected",
                    Toast.LENGTH_SHORT).show();
        }

        /**
         * Se ejecuta cuando la conexión con el servicio se pierde o se interrumpe inesperadamente.
         * Libera la instancia del servicio (mBoundService).
         * Muestra un mensaje breve ("Service disconnected") al usuario.
         *
         * @param className El nombre del componente concreto del servicio
         *                  cuya conexión se ha perdido.
         */
        @SuppressLint("RestrictedApi")
        public void onServiceDisconnected(ComponentName className) {
            mBoundService = null;

            Toast.makeText(MainActivity.this, "Service disconnected",
                    Toast.LENGTH_SHORT).show();
        }
    };

    /*----------------------------------------------------------------------------------------*/

    /**
     * Método que habilita o deshabilita los controles (switch y menú de navegación)
     * cuando se inicia o se finaliza la grabación.
     *
     * @param enable (true o false)
     */
    private void EnablingAndDisablingControls(boolean enable) {
        // Habilitar BottomNavigationView
        BottomNavigationView navView = findViewById(R.id.nav_view);
        for (int i = 0; i < navView.getMenu().size(); i++) {
            navView.getMenu().getItem(i).setEnabled(enable);
        }

        // Habilitar switch
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch switch_sound = findViewById(R.id.switch_sound_home);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch switch_movement = findViewById(R.id.switch_movement_home);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch switch_gps = findViewById(R.id.switch_gps_home);
        TextView textViews_sound = findViewById(R.id.txt_sound);
        TextView textViews_movement = findViewById(R.id.txt_movement);
        TextView textViews_gps = findViewById(R.id.txt_gps);

        switch_sound.setEnabled(enable);
        switch_movement.setEnabled(enable);
        switch_gps.setEnabled(enable);
        textViews_sound.setAlpha(enable ? 1.0f : 0.5f);
        textViews_movement.setAlpha(enable ? 1.0f : 0.5f);
        textViews_gps.setAlpha(enable ? 1.0f : 0.5f);
    }

    /*----------------------------------------------------------------------------------------*/
    private void showGeneratedFiles(File[] files) {
        //TableLayout filesTableLayout = findViewById(R.id.filesTableLayout);
        //filesTableLayout.removeAllViews();

        for (File file : files) {
            CheckBox fileCheckBox = new CheckBox(this);
            fileCheckBox.setLayoutParams(new TableLayout.LayoutParams());
            fileCheckBox.setText(file.getName());
            fileCheckBox.setChecked(true);

            //filesTableLayout.addView(fileCheckBox);
            //this.filesCheckBoxList.add(fileCheckBox);
        }
    }
    /*----------------------------------------------------------------------------------------*/

    void doUnbindService() {
        if (mShouldUnbind) {
            // Habilita los controles
            EnablingAndDisablingControls(true);
            // Release information about the service's state.
            unbindService(mConnection);
            mShouldUnbind = false;
        }
    }

    /*----------------------------------------------------------------------------------------*/

    /**
     * ViewModel para la persistencia de datos. <br>
     * Utilizar el ViewModel para almacenar el estado del objeto CompoundButton. El ViewModel es una
     * clase que se utiliza para almacenar datos que deben sobrevivir a los cambios de configuración,
     * como la rotación de la pantalla.
     */
    public static class MyViewModel extends ViewModel {
        private boolean isChecked;

        public boolean isChecked() {
            return isChecked;
        }

        public void setIsChecked(boolean isChecked) {
            this.isChecked = isChecked;
        }
    }

    /*----------------------------------------------------------------------------------------*/
    @Override
    protected void onStart() {
        android.util.Log.d(tag, "onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        //android.util.Log.d(tag, "onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        //android.util.Log.d(tag, "onPause"); // Prueba para ver cuando se activa el onPause
        super.onPause();
    }

    @Override
    protected void onStop() {
        android.util.Log.d(tag, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        android.util.Log.d(tag, "onDestroy");
        super.onDestroy();
    }
}