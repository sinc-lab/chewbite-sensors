package com.android.chewbiteSensors.ui.movement;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.chewbiteSensors.R;

public class MovementFragment extends Fragment {

    private MovementViewModel mViewModel;
    private static final String PREFS_KEY = "status_controls";
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switchAccelerometerSetting;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switchGyroscopeSetting;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switchMagnetometerSetting;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switchUncalibratedAccelerometerSetting;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switchUncalibratedGyroscopeSetting;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switchUncalibratedMagnetometerSetting;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switchGravitySetting;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switchNumberOfStepsSetting;
    private boolean accelerometerStatus;
    private boolean gyroscopeStatus;
    private boolean magnetometerStatus;
    private boolean accelerometerUncalibratedStatus;
    private boolean gyroscopeUncalibratedStatus;
    private boolean magnetometerUncalibratedStatus;
    private boolean gravityStatus;
    private boolean numberOfStepsStatus;

    private static final String STATUS_SPN_FREQUENCY_MOVEMENT_CONFIG = "status_switch_frequency_movement_configuration";
    private static final String STATUS_SWT_ACCELEROMETER_CONFIG = "status_switch_accelerometer_configuration";
    private static final String STATUS_SWT_GYROSCOPE_CONFIG = "status_switch_gyroscope_configuration";
    private static final String STATUS_SWT_MAGNETOMETER_CONFIG = "status_switch_magnetometer_configuration";
    private static final String STATUS_SWT_UNCALIBRATED_ACCELEROMETER_CONFIG = "status_switch_uncalibrated_accelerometer_configuration";
    private static final String STATUS_SWT_UNCALIBRATED_GYROSCOPE_CONFIG = "status_switch_uncalibrated_gyroscope_configuration";
    private static final String STATUS_SWT_UNCALIBRATED_MAGNETOMETER_CONFIG = "status_switch_uncalibrated_magnetometer_configuration";
    private static final String STATUS_SWT_GRAVITY_CONFIG = "status_switch_gravity_configuration";
    private static final String STATUS_SWT_NUMBER_OF_STEPS_CONFIG = "status_switch_number_of_steps_configuration";
    private static final String STATUS_SWT_MOVEMENT_CONFIG = "status_switch_movement_configuration";

    public static MovementFragment newInstance() {
        return new MovementFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_movement_setting, container, false);

        /*----------------------------------------------------------------------------------------*/
        // Inicializa del Spinner
        Spinner spinnerFrequencyMovementConfiguration = view.findViewById(R.id.spn_frequency_options_movement);

        // Obtén el array de opciones desde los recursos
        String[] optionsFrequencyArray = getResources().getStringArray(R.array.text_frequency_options);

        // Crea un ArrayAdapter usando el array de opciones y un diseño simple para el spinner
        ArrayAdapter<String> adapterFrequency = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, optionsFrequencyArray);

        // Especifica el diseño a utilizar cuando se despliega el spinner
        adapterFrequency.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Aplica el adapter al spinner
        spinnerFrequencyMovementConfiguration.setAdapter(adapterFrequency);

        // Recupera el estado guardado
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        // Recuperar la última selección guardada en SharedPreferences
        int selectedFrequencyPosition = sharedPreferences.getInt(STATUS_SPN_FREQUENCY_MOVEMENT_CONFIG, 0); // 0 es el valor por defecto (primera opción)

        // Seleccionar la opción guardada
        spinnerFrequencyMovementConfiguration.setSelection(selectedFrequencyPosition);

        // Guardar la selección del Spinner
        spinnerFrequencyMovementConfiguration.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                // Guardar la posición seleccionada en SharedPreferences
                editor.putInt(STATUS_SPN_FREQUENCY_MOVEMENT_CONFIG, position);
                editor.apply();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No se necesita implementar, pero es necesario sobrescribir el método
            }
        });

        /*----------------------------------------------------------------------------------------*/

        // Inicializa el switch para la configuración
        switchAccelerometerSetting = view.findViewById(R.id.switch_accelerometer_configuration);
        switchGyroscopeSetting = view.findViewById(R.id.switch_gyroscope_configuration);
        switchMagnetometerSetting = view.findViewById(R.id.switch_magnetometer_configuration);
        switchUncalibratedAccelerometerSetting = view.findViewById(R.id.switch_accelerometer_uncalibrated_configuration);
        switchUncalibratedGyroscopeSetting = view.findViewById(R.id.switch_gyroscope_uncalibrated_configuration);
        switchUncalibratedMagnetometerSetting = view.findViewById(R.id.switch_magnetometer_uncalibrated_configuration);
        switchUncalibratedGyroscopeSetting = view.findViewById(R.id.switch_gyroscope_uncalibrated_configuration);
        switchUncalibratedMagnetometerSetting = view.findViewById(R.id.switch_magnetometer_uncalibrated_configuration);
        switchGravitySetting = view.findViewById(R.id.switch_gravity_configuration);
        switchNumberOfStepsSetting = view.findViewById(R.id.switch_number_of_steps_configuration);

        boolean movementStatus = sharedPreferences.getBoolean(STATUS_SWT_MOVEMENT_CONFIG, false);
        accelerometerStatus = sharedPreferences.getBoolean(STATUS_SWT_ACCELEROMETER_CONFIG, false);
        accelerometerStatus = accelerometerStatus && movementStatus;
        gyroscopeStatus = sharedPreferences.getBoolean(STATUS_SWT_GYROSCOPE_CONFIG, false);
        gyroscopeStatus = gyroscopeStatus && movementStatus;
        magnetometerStatus = sharedPreferences.getBoolean(STATUS_SWT_MAGNETOMETER_CONFIG, false);
        magnetometerStatus = magnetometerStatus && movementStatus;
        /*------------------------------------------------*/
        accelerometerUncalibratedStatus = sharedPreferences.getBoolean(STATUS_SWT_UNCALIBRATED_ACCELEROMETER_CONFIG, false);
        accelerometerUncalibratedStatus = accelerometerUncalibratedStatus && movementStatus;
        gyroscopeUncalibratedStatus = sharedPreferences.getBoolean(STATUS_SWT_UNCALIBRATED_GYROSCOPE_CONFIG, false);
        gyroscopeUncalibratedStatus = gyroscopeUncalibratedStatus && movementStatus;
        magnetometerUncalibratedStatus = sharedPreferences.getBoolean(STATUS_SWT_UNCALIBRATED_MAGNETOMETER_CONFIG, false);
        magnetometerUncalibratedStatus = magnetometerUncalibratedStatus && movementStatus;
        gravityStatus = sharedPreferences.getBoolean(STATUS_SWT_GRAVITY_CONFIG, false);
        gravityStatus = gravityStatus && movementStatus;
        numberOfStepsStatus = sharedPreferences.getBoolean(STATUS_SWT_NUMBER_OF_STEPS_CONFIG, false);
        numberOfStepsStatus = numberOfStepsStatus && movementStatus;
        /*------------------------------------------------*/

        // Configura el listener
        switchAccelerometerSetting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(STATUS_SWT_ACCELEROMETER_CONFIG, isChecked);
                // si se pasa a true el switch de acelerometro
                if (isChecked) {
                    // si se pasa a true el switch de movimiento
                    editor.putBoolean(STATUS_SWT_MOVEMENT_CONFIG, true);
                } else {  // si se pasa a false el switch de acelerometro
                    // se obtiene el estado de los otros switches
                    //boolean gyroscope = sharedPreferences.getBoolean(STATUS_SWT_GYROSCOPE_CONFIG, false);
                    //boolean magnetometer = sharedPreferences.getBoolean(STATUS_SWT_MAGNETOMETER_CONFIG, false);
                    // si los otros switches estan en false (es decir: todos los switches estan en false)
                    if (!gyroscopeStatus && !magnetometerStatus && !accelerometerUncalibratedStatus
                            && !gyroscopeUncalibratedStatus && !magnetometerUncalibratedStatus &&
                            !gravityStatus && !numberOfStepsStatus ) {
                        // se pasa a false el switch de movimiento
                        editor.putBoolean(STATUS_SWT_MOVEMENT_CONFIG, false);
                    }
                }
                editor.apply();
            }
        });
        // Configura el listener
        switchGyroscopeSetting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(STATUS_SWT_GYROSCOPE_CONFIG, isChecked);
                // si se pasa a true el switch de giroscopio
                if (isChecked) {
                    // si se pasa a true el switch de movimiento
                    editor.putBoolean(STATUS_SWT_MOVEMENT_CONFIG, true);
                } else {  // si se pasa a false el switch de giroscopio
                    // se obtiene el estado de los otros switches
                    //boolean accelerometer = sharedPreferences.getBoolean(STATUS_SWT_ACCELEROMETER_CONFIG, false);
                    //boolean magnetometer = sharedPreferences.getBoolean(STATUS_SWT_MAGNETOMETER_CONFIG, false);
                    // si los otros switches estan en false (es decir: todos los switches estan en false)
                    if (!accelerometerStatus && !magnetometerStatus && !accelerometerUncalibratedStatus
                            && !gyroscopeUncalibratedStatus && !magnetometerUncalibratedStatus &&
                            !gravityStatus && !numberOfStepsStatus ) {
                        // se pasa a false el switch de movimiento
                        editor.putBoolean(STATUS_SWT_MOVEMENT_CONFIG, false);
                    }
                }
                editor.apply();
            }
        });
        // Configura el listener
        switchMagnetometerSetting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(STATUS_SWT_MAGNETOMETER_CONFIG, isChecked);
                // si se pasa a true el switch de acelerometro
                if (isChecked) {
                    // si se pasa a true el switch de movimiento
                    editor.putBoolean(STATUS_SWT_MOVEMENT_CONFIG, true);
                } else {  // si se pasa a false el switch de acelerometro
                    // se obtiene el estado de los otros switches
                    //boolean accelerometer = sharedPreferences.getBoolean(STATUS_SWT_ACCELEROMETER_CONFIG, false);
                    //boolean gyroscope = sharedPreferences.getBoolean(STATUS_SWT_GYROSCOPE_CONFIG, false);
                    // si los otros switches estan en false (es decir: todos los switches estan en false)
                    if (!accelerometerStatus && !gyroscopeStatus &&  !accelerometerUncalibratedStatus
                            && !gyroscopeUncalibratedStatus && !magnetometerUncalibratedStatus &&
                            !gravityStatus && !numberOfStepsStatus ) {
                        // se pasa a false el switch de movimiento
                        editor.putBoolean(STATUS_SWT_MOVEMENT_CONFIG, false);
                    }
                }
                editor.apply();
            }
        });
        /*------------------------------------------------*/
        // Configura el listener
        switchUncalibratedAccelerometerSetting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(STATUS_SWT_UNCALIBRATED_ACCELEROMETER_CONFIG, isChecked);
                // si se pasa a true el switch de acelerometro
                if (isChecked) {
                    // si se pasa a true el switch de movimiento
                    editor.putBoolean(STATUS_SWT_MOVEMENT_CONFIG, true);
                } else {  // si se pasa a false el switch de acelerometro
                    // si los otros switches estan en false (es decir: todos los switches están en false)
                    if (!accelerometerStatus && !gyroscopeStatus && !magnetometerStatus &&
                            !gyroscopeUncalibratedStatus && !magnetometerUncalibratedStatus &&
                            !gravityStatus && !numberOfStepsStatus ) {
                        // se pasa a false el switch de movimiento
                        editor.putBoolean(STATUS_SWT_MOVEMENT_CONFIG, false);
                    }
                }
                editor.apply();
            }
        });
        // Configura el listener
        switchUncalibratedGyroscopeSetting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(STATUS_SWT_UNCALIBRATED_GYROSCOPE_CONFIG, isChecked);
                // si se pasa a true el switch de giroscopio
                if (isChecked) {
                    // si se pasa a true el switch de movimiento
                    editor.putBoolean(STATUS_SWT_MOVEMENT_CONFIG, true);
                } else {  // si se pasa a false el switch de giroscopio
                    // si los otros switches estan en false (es decir: todos los switches estan en false)
                    if (!accelerometerStatus && !gyroscopeStatus && !magnetometerStatus &&
                            !accelerometerUncalibratedStatus && !magnetometerUncalibratedStatus &&
                            !gravityStatus && !numberOfStepsStatus ) {
                        // se pasa a false el switch de movimiento
                        editor.putBoolean(STATUS_SWT_MOVEMENT_CONFIG, false);
                    }
                }
                editor.apply();
            }
        });
        // Configura el listener
        switchUncalibratedMagnetometerSetting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(STATUS_SWT_UNCALIBRATED_MAGNETOMETER_CONFIG, isChecked);
                // si se pasa a true el switch de acelerometro
                if (isChecked) {
                    // si se pasa a true el switch de movimiento
                    editor.putBoolean(STATUS_SWT_MOVEMENT_CONFIG, true);
                } else {  // si se pasa a false el switch de acelerometro
                    // si los otros switches estan en false (es decir: todos los switches estan en false)
                    if (!accelerometerStatus && !gyroscopeStatus && !magnetometerStatus &&
                            !accelerometerUncalibratedStatus && !gyroscopeUncalibratedStatus &&
                            !gravityStatus && !numberOfStepsStatus ) {
                        // se pasa a false el switch de movimiento
                        editor.putBoolean(STATUS_SWT_MOVEMENT_CONFIG, false);
                    }
                }
                editor.apply();
            }
        });
        /*------------------------------------------------*/
        switchGravitySetting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(STATUS_SWT_GRAVITY_CONFIG, isChecked);
                // si se pasa a true el switch de acelerometro
                if (isChecked) {
                    // si se pasa a true el switch de movimiento
                    editor.putBoolean(STATUS_SWT_MOVEMENT_CONFIG, true);
                } else {  // si se pasa a false el switch de acelerometro
                    // si los otros switches estan en false (es decir: todos los switches estan en false)
                    if (!accelerometerStatus && !gyroscopeStatus && !magnetometerStatus &&
                            !accelerometerUncalibratedStatus && !gyroscopeUncalibratedStatus &&
                            !magnetometerUncalibratedStatus && !numberOfStepsStatus ) {
                        // se pasa a false el switch de movimiento
                        editor.putBoolean(STATUS_SWT_MOVEMENT_CONFIG, false);
                    }
                }
                editor.apply();
            }
        });
        switchNumberOfStepsSetting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(STATUS_SWT_NUMBER_OF_STEPS_CONFIG, isChecked);
                // si se pasa a true el switch de acelerometro
                if (isChecked) {
                    // si se pasa a true el switch de movimiento
                    editor.putBoolean(STATUS_SWT_MOVEMENT_CONFIG, true);
                } else {  // si se pasa a false el switch de acelerometro
                    // si los otros switches estan en false (es decir: todos los switches estan en false)
                    if (!accelerometerStatus && !gyroscopeStatus && !magnetometerStatus &&
                            !accelerometerUncalibratedStatus && !gyroscopeUncalibratedStatus &&
                            !magnetometerUncalibratedStatus && !gravityStatus) {
                        // se pasa a false el switch de movimiento
                        editor.putBoolean(STATUS_SWT_MOVEMENT_CONFIG, false);
                    }
                }
                editor.apply();
            }
        });
        /*------------------------------------------------*/
        return view;
    }

    /*@Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MovementViewModel.class);
        // TODO: Use the ViewModel
    }*/

    @Override
    public void onStart() {
        //android.util.Log.d(tag, "onStart HomeFragment.java");
        super.onStart();

        switchAccelerometerSetting.setChecked(accelerometerStatus);
        switchGyroscopeSetting.setChecked(gyroscopeStatus);
        switchMagnetometerSetting.setChecked(magnetometerStatus);
        switchUncalibratedAccelerometerSetting.setChecked(accelerometerUncalibratedStatus);
        switchUncalibratedGyroscopeSetting.setChecked(gyroscopeUncalibratedStatus);
        switchUncalibratedMagnetometerSetting.setChecked(magnetometerUncalibratedStatus);
        switchGravitySetting.setChecked(gravityStatus);
        switchNumberOfStepsSetting.setChecked(numberOfStepsStatus);
    }
}