package com.android.chewbiteSensors;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.TableLayout;
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
import com.android.chewbiteSensors.data_sensors.CBSensorEventListener;
import com.android.chewbiteSensors.data_sensors.CBService;
import com.android.chewbiteSensors.data_sensors.ExperimentData;
import com.android.chewbiteSensors.data_sensors.FileManager;
import com.android.chewbiteSensors.data_sensors.TestSensorsEventListener;
import com.android.chewbiteSensors.databinding.ActivityMainBinding;
import com.android.chewbiteSensors.deviceStatus.DeviceStatus;
import com.android.chewbiteSensors.deviceStatus.DeviceStatusService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{
    private final String tag = "MainActivity";
    public static String TEST_DATA_STRING = "testData";
    private static final int APPLICATION_PERMISSION_CODE = 1;

    private DeviceStatus currentStatus;
    private AppMode mode;
    private Intent batteryStatus;
    private ExperimentData data;
    private static final String INFO_FILE_NAME = "info.txt";
    private TestSensorsEventListener testSensorsEventListener;
    private static final String DATE_FORMAT = "dd/MM/yyyy-HH:mm:ss.S";
    private CBService mBoundService;
    private boolean mShouldUnbind;
    private static final String APP_MODE_STRING = "appMode";


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
        // Inicializa el boton
        //buttonStartStop = findViewById(R.id.btn_start);
        //buttonStartStop.setOnCheckedChangeListener(this);

        // Crear el ViewModel
        //viewModel = new ViewModelProvider(this).get(MyViewModel.class);

        // Obtener el objeto CompoundButton
        //CompoundButton compoundButton = findViewById(R.id.btn_start);
        //buttonStartStop = findViewById(R.id.btn_start);

        // Establecer el listener para el evento onCheckedChanged
        //buttonStartStop.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Guardar el estado del objeto CompoundButton en el ViewModel
         //   viewModel.setIsChecked(isChecked);
        //});

        /*----------------------------------------------------------------------------------------*/

        // Inicializa el switch
        /*@SuppressLint("UseSwitchCompatOrMaterialCode") Switch switchSoundConfiguration = findViewById(R.id.switch_sound_configuration);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch switchMovementConfiguration = findViewById(R.id.switch_movement_configuration);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch switchGpsConfiguration = findViewById(R.id.switch_gps_configuration);

        // Cargar las preferencias
        prefsConfig = getSharedPreferences(PREFS_KEY, MODE_PRIVATE);

        // Restaurar el estado del Switch
        boolean isSwitchSoundChecked = prefsConfig.getBoolean(STATUS_SWT_SOUND_CONFIG, true);
        boolean isSwitchMovementChecked = prefsConfig.getBoolean(STATUS_SWT_MOVEMENT_CONFIG, false);
        boolean isSwitchGPSChecked = prefsConfig.getBoolean(STATUS_SWT_GPS_CONFIG, false);
        switchSoundConfiguration.setChecked(isSwitchSoundChecked);
        switchMovementConfiguration.setChecked(isSwitchMovementChecked);
        switchGpsConfiguration.setChecked(isSwitchGPSChecked);

        // si se cambia el estado del switch se guarda el estado del mismo
        switchSoundConfiguration.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Guardar el estado del Switch
            SharedPreferences.Editor editor = prefsConfig.edit();
            editor.putBoolean(STATUS_SWT_SOUND_CONFIG, isChecked);
            editor.apply();
        });
        // si se cambia el estado del switch se guarda el estado del mismo
        switchMovementConfiguration.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Guardar el estado del Switch
            SharedPreferences.Editor editor = prefsConfig.edit();
            editor.putBoolean(STATUS_SWT_MOVEMENT_CONFIG, isChecked);
            editor.apply();
        });
        // si se cambia el estado del switch se guarda el estado del mismo
        switchGpsConfiguration.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Guardar el estado del Switch
            SharedPreferences.Editor editor = prefsConfig.edit();
            editor.putBoolean(STATUS_SWT_GPS_CONFIG, isChecked);
            editor.apply();
        });*/

        /*----------------------------------------------------------------------------------------*/
        // Esta sección  no va porque se cambio la frecuencia de muestreo a la otra panalla
        // Inicializa del Spinner
        //Spinner spinnerFrequencyConfiguration = findViewById(R.id.spn_frequency_options);

        // Obtén el array de opciones desde los recursos
        //String[] optionsArray = getResources().getStringArray(R.array.text_frequency_options);

        // Crea un ArrayAdapter usando el array de opciones y un diseño simple para el spinner
        //ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, optionsArray);

        // Especifica el diseño a utilizar cuando se despliega el spinner
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Aplica el adapter al spinner
        //spinnerFrequencyConfiguration.setAdapter(adapter);

        // Recuperar la última selección guardada en SharedPreferences
        //int selectedPosition = prefsConfig.getInt(STATUS_SPN_FREQUENCY_CONFIG, 0); // 0 es el valor por defecto (primera opción)
        // Seleccionar la opción guardada
        //spinnerFrequencyConfiguration.setSelection(selectedPosition);

        // Guardar la selección del Spinner
        /*spinnerFrequencyConfiguration.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor = prefsConfig.edit();
                // Guardar la posición seleccionada en SharedPreferences
                editor.putInt(STATUS_SPN_FREQUENCY_CONFIG, position);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No se necesita implementar, pero es necesario sobrescribir el método
            }

        });*/

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
    /*@Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // En caso de no tener permisos los vuelve a solicitar
        this.askForPermissions();
        // Corrobora si efectivamente se le dio los permisos
        if (!this.hasPermissions()) {
            this.buttonStartStop.setChecked(false);
            return;
        }
        //
        if (isChecked) {
            // Verifica el estado antes de comenzar la grabación
            if (!this.checkStatusBeforeStart()) {
                android.util.Log.d(tag, "if (!this.checkStatusBeforeStart())");
                this.openDialog();
                this.buttonStartStop.setChecked(false);
            } else {
                this.startTest();
            }
        } else {
            this.stopTest();
        }
    }*/
    /*----------------------------------------------------------------------------------------*/
    public void askForPermissions() {
        if (!this.hasPermissions()) {
            /*
            * la función shouldShowRequestPermissionRationale() devuelve:
            * true si el usuario ha rechazado previamente el permiso pero no ha marcado la opción “No preguntar de nuevo”,
            * false si el usuario ha rechazado el permiso y ha marcado la opción “No preguntar de nuevo” o si el permiso ha sido concedido.
            * */
            if ( ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) ) {
                // Solicitamos los permisos
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.RECORD_AUDIO
                        },
                        MainActivity.APPLICATION_PERMISSION_CODE);
            } else {
                // El usuario ha rechazado el permiso pero no ha marcado "No preguntar de nuevo",
                // por lo que podemos mostrar un mensaje explicando por qué necesitamos el permiso
                new AlertDialog.Builder(this)
                        .setMessage("Se requieren permisos para poder continuar")
                        .setPositiveButton("Aceptar", (dialog, which) ->
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{
                                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                Manifest.permission.RECORD_AUDIO
                                        },
                                        MainActivity.APPLICATION_PERMISSION_CODE))
                        .setNegativeButton("Cancelar", null)
                        .create()
                        .show();
            }
            /*-------------------------------------------------------------------------*/
        }
    }

    public boolean hasPermissions() {
        //int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        /* Código viejo para comprobar si tiene los permisos
        int writeStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int recordAudioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return writeStoragePermission == PackageManager.PERMISSION_GRANTED
                && recordAudioPermission == PackageManager.PERMISSION_GRANTED;*/


        // Comprobar si Android es inferior a Android 10
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            // Para Android 9 y anteriores, necesitas el permiso WRITE_EXTERNAL_STORAGE
            int readStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE );
            int writeStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int recordAudioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
            return readStoragePermission == PackageManager.PERMISSION_GRANTED
                    && writeStoragePermission == PackageManager.PERMISSION_GRANTED
                    && recordAudioPermission == PackageManager.PERMISSION_GRANTED;
        } else {
            // Para Android 10 y posteriores, no necesitas el permiso WRITE_EXTERNAL_STORAGE
            int readStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE );
            int writeStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int recordAudioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
            return writeStoragePermission == PackageManager.PERMISSION_GRANTED
                    && recordAudioPermission == PackageManager.PERMISSION_GRANTED
                    && readStoragePermission == PackageManager.PERMISSION_GRANTED;
        }
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
        //
        if (this.mode != AppMode.RUNNING) {
            this.data = new ExperimentData();
            this.initExperimentData();
            CBSensorEventListener.INSTANCE.setExperimentData(this.data);
            /*
            Acá es donde tengo que llamar a la clase setExperimentData(this.data) y pasarle los
            demás datos del experimento como por ejemplo: Formato, frecuencia de muestreo,
            tasa de bits, etc.
             */
            AudioRecorder.INSTANCE.setExperimentData(this.data);
            this.mode = AppMode.RUNNING;
        }
        this.showExperimentRunning();
        this.doBindService();
    }

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
        File[] testFiles = CBSensorEventListener.INSTANCE.getTestFiles(data.getTimestamp());
        this.showGeneratedFiles(testFiles);
        this.doUnbindService();
    }
    /*----------------------------------------------------------------------------------------*/

    private void logExtraInfo() {
        StringBuilder info = new StringBuilder();

        info.append("DEVICE INFO: ").append(Build.MANUFACTURER).append(" ").append(Build.MODEL).append("\n");
        info.append("ANDROID VERSION: : ").append(Build.VERSION.RELEASE).append("\n\n");

        @SuppressLint("SimpleDateFormat") String formattedStartDate = new SimpleDateFormat(DATE_FORMAT).format(this.data.getStartDate());
        info.append("STARTED AT ").append(formattedStartDate).append("\n");

        @SuppressLint("SimpleDateFormat") String formattedEndDate = new SimpleDateFormat(DATE_FORMAT).format(this.data.getEndDate());
        info.append("ENDED AT ").append(formattedEndDate).append("\n");

        info.append("\nBATTERY:\n");
        info.append("Battery at start (%): ").append(100 * this.data.getBatteryAtStart() / (float) this.data.getBatteryCapacity()).append("\n");
        info.append("Battery at end (%): ").append(100 * this.data.getBatteryAtEnd() / (float) this.data.getBatteryCapacity()).append("\n");
        // Agregar más información según sea necesario
        FileManager.writeToFile(this.data.getTimestamp(), INFO_FILE_NAME, info.toString());
    }
    /*----------------------------------------------------------------------------------------*/

    private void initExperimentData() {
        this.batteryStatus = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        assert this.batteryStatus != null;
        data.setBatteryAtStart(this.batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1));
        data.setBatteryCapacity(this.batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1));
    }

    /*----------------------------------------------------------------------------------------*/

    @SuppressLint("SetTextI18n")
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
    }

    /*----------------------------------------------------------------------------------------*/

    void doBindService() {
        Intent intent = new Intent(MainActivity.this, CBService.class);
        intent.putExtra(MainActivity.TEST_DATA_STRING, this.data);

        startForegroundService(intent);

        boolean  bind = bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        if (bind) {
            mShouldUnbind = true;
        } else {
            Log.e("MY_APP_TAG", "Error: The requested service doesn't " +
                    "exist, or this client isn't allowed access to it.");
        }
    }

    /*----------------------------------------------------------------------------------------*/

    private final ServiceConnection mConnection = new ServiceConnection() {
        @SuppressLint("RestrictedApi")
        public void onServiceConnected(ComponentName className, IBinder service) {
            mBoundService = ((CBService.CBBinder)service).getService();
            mBoundService.startTest(MainActivity.this);

            // Deshabilita los controles
            EnablingAndDisablingControls(false);

            Toast.makeText(MainActivity.this, "Service connected",
                    Toast.LENGTH_SHORT).show();
        }

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

        switch_sound.setEnabled(enable);
        switch_movement.setEnabled(enable);
        switch_gps.setEnabled(enable);
    }

    /*----------------------------------------------------------------------------------------*/
    private void showGeneratedFiles(File[] files) {
        //TableLayout filesTableLayout = findViewById(R.id.filesTableLayout);
        //filesTableLayout.removeAllViews();

        for (File file: files) {
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