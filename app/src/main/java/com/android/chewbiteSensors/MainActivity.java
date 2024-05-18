package com.android.chewbiteSensors;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.ToggleButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.android.chewbiteSensors.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener,
        ActivityCompat.OnRequestPermissionsResultCallback{
    private final String tag = "MainActivity";
    private static final int APPLICATION_PERMISSION_CODE = 1;
    private ToggleButton buttonStartStop;
    // Atributos de la clase
    private SharedPreferences prefsConfig;
    private static final String PREFS_KEY = "appConfiguration";
    private static final String STATUS_SWT_SOUND_CONFIG = "status_switch_sound_configuration";
    private static final String STATUS_SWT_MOVEMENT_CONFIG = "status_switch_movement_configuration";
    private static final String STATUS_SWT_GPS_CONFIG = "status_switch_gps_configuration";
    private static final String STATUS_SPN_FREQUENCY_CONFIG = "status_spinner_frequency_configuration";



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
        buttonStartStop = findViewById(R.id.btn_start);
        buttonStartStop.setOnCheckedChangeListener(this);

        /*----------------------------------------------------------------------------------------*/
        // BottomNavigationView navView = findViewById(R.id.nav_view);
        // navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
        /*   @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.navigation_home) {
                    // Aquí activas el evento onCheckedChanged
                    buttonStartStop.setChecked(!buttonStartStop.isChecked());
                    return true;
                } else if (item.getItemId() == R.id.navigation_settings) {
                    // Maneja la selección de la opción de configuración
                    // Si hay algo que hacer cuando se selecciona "Settings"
                    return false;
                } else if (item.getItemId() == R.id.navigation_predictions) {
                    // Maneja la selección de la opción de predicciones
                    // Si hay algo que hacer cuando se selecciona "Predictions"
                    return true;
                }
                return false;
            }
        });*/
        /*----------------------------------------------------------------------------------------*/

        // Inicializa el switch
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch switchSoundConfiguration = findViewById(R.id.switch_sound_configuration);
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
        });

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
        // Solicita los permisos para que la pueda acceder al micrófono
        this.askForPermissions();
        /*----------------------------------------------------------------------------------------*/
    }

    public void onClick_btn_start(View v){
        //Toast.makeText(getApplicationContext(), "otro metodo", Toast.LENGTH_LONG).show();
    }


    /*----------------------------------------------------------------------------------------*/
    @Override
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
            /*if (!this.checkStatusBeforeStart()) {
                this.openDialog();
                this.buttonStartStop.setChecked(false);
            } else {
                //this.startTest();
                buttonStartStop.setEnabled(false);
            }*/
        } else {
            //this.stopTest();
            buttonStartStop.setEnabled(true);
        }
    }
    /*----------------------------------------------------------------------------------------*/
    private void askForPermissions() {
        if (!this.hasPermissions()) {
            /*
            * la función shouldShowRequestPermissionRationale() devuelve:
            * true si el usuario ha rechazado previamente el permiso pero no ha marcado la opción “No preguntar de nuevo”,
            * false si el usuario ha rechazado el permiso y ha marcado la opción “No preguntar de nuevo” o si el permiso ha sido concedido.
            * */
            if ( ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                // Solicitamos los permisos
                ActivityCompat.requestPermissions(this,
                        new String[]{
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

    private boolean hasPermissions() {
        // int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int writeStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int recordAudioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return writeStoragePermission == PackageManager.PERMISSION_GRANTED
                && recordAudioPermission == PackageManager.PERMISSION_GRANTED;
    }

    /*----------------------------------------------------------------------------------------*/



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
        buttonStartStop.setChecked(false);
    }

    @Override
    protected void onPause() {
        //android.util.Log.d(tag, "onPause"); // Prueba para ver cuando se activa el onPause
        super.onPause();

            @SuppressLint("UseSwitchCompatOrMaterialCode") Switch switchSoundConfiguration = findViewById(R.id.switch_sound_configuration);
            @SuppressLint("UseSwitchCompatOrMaterialCode") Switch switchMovementConfiguration = findViewById(R.id.switch_movement_configuration);
            @SuppressLint("UseSwitchCompatOrMaterialCode") Switch switchGpsConfiguration = findViewById(R.id.switch_gps_configuration);
        // Obtener el estado del Switch
        boolean switchSoundConfigurationChecked = switchSoundConfiguration.isChecked();
        boolean switchMovementConfigurationChecked = switchMovementConfiguration.isChecked();
        boolean switchGpsConfigurationChecked = switchGpsConfiguration.isChecked();
        
        // Se Utiliza SharedPreferences para almacenar pequeñas cantidades de datos clave-valor.
        prefsConfig = getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefsConfig.edit();
        editor.putBoolean(STATUS_SWT_SOUND_CONFIG, switchSoundConfigurationChecked);
        editor.putBoolean(STATUS_SWT_MOVEMENT_CONFIG, switchMovementConfigurationChecked);
        editor.putBoolean(STATUS_SWT_GPS_CONFIG, switchGpsConfigurationChecked);
        editor.apply();
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