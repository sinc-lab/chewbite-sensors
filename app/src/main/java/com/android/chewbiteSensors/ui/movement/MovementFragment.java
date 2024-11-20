package com.android.chewbiteSensors.ui.movement;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.chewbiteSensors.R;
import com.android.chewbiteSensors.data_sensors.SensorInfo;

import kotlin.Pair;

public class MovementFragment extends Fragment {

    private MovementViewModel mViewModel;
    private static final String PREFS_KEY = "status_controls";
    TextView textViewAccelerometer;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switchAccelerometerSetting;
    TextView textViewGyroscope;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switchGyroscopeSetting;
    TextView textViewMagnetometer;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switchMagnetometerSetting;
    TextView textViewUncalibratedAccelerometer;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switchUncalibratedAccelerometerSetting;
    TextView textViewUncalibratedGyroscope;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switchUncalibratedGyroscopeSetting;
    TextView textViewUncalibratedMagnetometer;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switchUncalibratedMagnetometerSetting;
    TextView textViewGravity;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switchGravitySetting;
    TextView textViewNumberOfSteps;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switchNumberOfStepsSetting;
    private boolean movementStatus;
    private boolean accelerometerStatus;
    private boolean gyroscopeStatus;
    private boolean magnetometerStatus;
    private boolean accelerometerUncalibratedStatus;
    private boolean gyroscopeUncalibratedStatus;
    private boolean magnetometerUncalibratedStatus;
    private boolean gravityStatus;
    private boolean numberOfStepsStatus;

    private static final String STATUS_SPN_FREQUENCY_MOVEMENT_CONFIG = "status_spinner_frequency_movement_configuration";
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

    @SuppressLint("MissingInflatedId")
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
        textViewAccelerometer = view.findViewById(R.id.txt_accelerometer);
        switchAccelerometerSetting = view.findViewById(R.id.switch_accelerometer_configuration);
        textViewGyroscope = view.findViewById(R.id.txt_gyroscope);
        switchGyroscopeSetting = view.findViewById(R.id.switch_gyroscope_configuration);
        textViewMagnetometer = view.findViewById(R.id.txt_magnetometer);
        switchMagnetometerSetting = view.findViewById(R.id.switch_magnetometer_configuration);
        textViewUncalibratedAccelerometer = view.findViewById(R.id.txt_accelerometer_uncalibrated);
        switchUncalibratedAccelerometerSetting = view.findViewById(R.id.switch_accelerometer_uncalibrated_configuration);
        textViewUncalibratedGyroscope = view.findViewById(R.id.txt_gyroscope_uncalibrated);
        switchUncalibratedGyroscopeSetting = view.findViewById(R.id.switch_gyroscope_uncalibrated_configuration);
        textViewUncalibratedMagnetometer = view.findViewById(R.id.txt_magnetometer_uncalibrated);
        switchUncalibratedMagnetometerSetting = view.findViewById(R.id.switch_magnetometer_uncalibrated_configuration);
        textViewGravity = view.findViewById(R.id.txt_gravity);
        switchGravitySetting = view.findViewById(R.id.switch_gravity_configuration);
        textViewNumberOfSteps = view.findViewById(R.id.txt_number_of_steps);
        switchNumberOfStepsSetting = view.findViewById(R.id.switch_number_of_steps_configuration);

        // Recupera el estado guardado
        movementStatus = sharedPreferences.getBoolean(STATUS_SWT_MOVEMENT_CONFIG, false);
        accelerometerStatus = sharedPreferences.getBoolean(STATUS_SWT_ACCELEROMETER_CONFIG, false);
        accelerometerStatus = accelerometerStatus && movementStatus;
        gyroscopeStatus = sharedPreferences.getBoolean(STATUS_SWT_GYROSCOPE_CONFIG, false);
        gyroscopeStatus = gyroscopeStatus && movementStatus;
        magnetometerStatus = sharedPreferences.getBoolean(STATUS_SWT_MAGNETOMETER_CONFIG, false);
        magnetometerStatus = magnetometerStatus && movementStatus;
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

        // 1. Deshabilitar el listener inmediatamente
        switchAccelerometerSetting.setOnCheckedChangeListener(null);
        switchGyroscopeSetting.setOnCheckedChangeListener(null);
        switchMagnetometerSetting.setOnCheckedChangeListener(null);
        switchUncalibratedAccelerometerSetting.setOnCheckedChangeListener(null);
        switchUncalibratedGyroscopeSetting.setOnCheckedChangeListener(null);
        switchUncalibratedMagnetometerSetting.setOnCheckedChangeListener(null);
        switchGravitySetting.setOnCheckedChangeListener(null);
        switchNumberOfStepsSetting.setOnCheckedChangeListener(null);

        /*---------- Configura el listener de ACELERÓMETRO ------------*/
        // 2. Publicar un ejecutable para establecer el estado y volver a habilitar el listener
        switchAccelerometerSetting.post(() -> {
            // 3. Establecer el estado marcado
            switchAccelerometerSetting.setChecked(accelerometerStatus);

            // 4. Saltar al estado actual para evitar la animación
            switchAccelerometerSetting.jumpDrawablesToCurrentState();

            // 5. Volver a habilitar el listener
            switchAccelerometerSetting.setOnCheckedChangeListener((buttonView, isChecked) -> {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(STATUS_SWT_ACCELEROMETER_CONFIG, isChecked);
                // si se pasa a TRUE el switch de acelerómetro
                if (isChecked) {
                    // se pasa a TRUE el switch de movimiento
                    editor.putBoolean(STATUS_SWT_MOVEMENT_CONFIG, true);

                // si se pasa a FALSE el switch de acelerómetro
                // se obtiene el estado de los otros switches
                // si los otros switches están en false (es decir: todos los switches están en false)
                } else if (!gyroscopeStatus && !magnetometerStatus && !accelerometerUncalibratedStatus
                            && !gyroscopeUncalibratedStatus && !magnetometerUncalibratedStatus &&
                            !gravityStatus && !numberOfStepsStatus ) {
                        // se pasa a false el switch de movimiento
                        editor.putBoolean(STATUS_SWT_MOVEMENT_CONFIG, false);

                }
                editor.apply();
            });
        });
        /*---------- Configura el listener de ACELERÓMETRO ------------*/

        /*---------- Configura el listener de GIROSCOPIO ------------*/
        // 2. Publicar un ejecutable para establecer el estado y volver a habilitar el listener
        switchGyroscopeSetting.post(() -> {
            // 3. Establecer el estado marcado
            switchGyroscopeSetting.setChecked(gyroscopeStatus);

            // 4. Saltar al estado actual para evitar la animación
            switchGyroscopeSetting.jumpDrawablesToCurrentState();

            // 5. Volver a habilitar el listener
            switchGyroscopeSetting.setOnCheckedChangeListener((buttonView, isChecked) -> {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(STATUS_SWT_GYROSCOPE_CONFIG, isChecked);
                // si se pasa a TRUE el switch de giroscopio
                if (isChecked) {
                    // se pasa a TRUE el switch de movimiento
                    editor.putBoolean(STATUS_SWT_MOVEMENT_CONFIG, true);
                // si se pasa a false el switch de giroscopio
                // se obtiene el estado de los otros switches
                // si los otros switches estan en false (es decir: todos los switches estan en false)
                } else if (!accelerometerStatus && !magnetometerStatus && !accelerometerUncalibratedStatus
                            && !gyroscopeUncalibratedStatus && !magnetometerUncalibratedStatus &&
                            !gravityStatus && !numberOfStepsStatus ) {
                        // se pasa a false el switch de movimiento
                        editor.putBoolean(STATUS_SWT_MOVEMENT_CONFIG, false);
                }
                editor.apply();
            });
        });
        /*---------- Configura el listener de GIROSCOPIO ------------*/

        /*---------- Configura el listener de MAGNETÓMETRO ------------*/
        // 2. Publicar un ejecutable para establecer el estado y volver a habilitar el listener
        switchMagnetometerSetting.post(() -> {
            // 3. Establecer el estado marcado
            switchMagnetometerSetting.setChecked(magnetometerStatus);

            // 4. Saltar al estado actual para evitar la animación
            switchMagnetometerSetting.jumpDrawablesToCurrentState();

            // 5. Volver a habilitar el listener
            switchMagnetometerSetting.setOnCheckedChangeListener((buttonView, isChecked) -> {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(STATUS_SWT_MAGNETOMETER_CONFIG, isChecked);
                // si se pasa a TRUE el switch de magnetómetro
                if (isChecked) {
                    // se pasa a TRUE el switch de movimiento
                    editor.putBoolean(STATUS_SWT_MOVEMENT_CONFIG, true);
                // si se pasa a false el switch de magnetómetro
                // se obtiene el estado de los otros switches
                // si los otros switches estan en false (es decir: todos los switches estan en false)
                } else if (!accelerometerStatus && !gyroscopeStatus &&  !accelerometerUncalibratedStatus
                            && !gyroscopeUncalibratedStatus && !magnetometerUncalibratedStatus &&
                            !gravityStatus && !numberOfStepsStatus ) {
                        // se pasa a false el switch de movimiento
                        editor.putBoolean(STATUS_SWT_MOVEMENT_CONFIG, false);
                }
                editor.apply();
            });
        });
        /*---------- Configura el listener de MAGNETÓMETRO ------------*/

        /*---------- Configura el listener de ACELERÓMETRO SIN CALIBRAR ------------*/
        // 2. Publicar un ejecutable para establecer el estado y volver a habilitar el listener
        switchUncalibratedAccelerometerSetting.post(() -> {
            // 3. Establecer el estado marcado
            switchUncalibratedAccelerometerSetting.setChecked(accelerometerUncalibratedStatus);

            // 4. Saltar al estado actual para evitar la animación
            switchUncalibratedAccelerometerSetting.jumpDrawablesToCurrentState();

            // 5. Volver a habilitar el listener
            switchUncalibratedAccelerometerSetting.setOnCheckedChangeListener((buttonView, isChecked) -> {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(STATUS_SWT_UNCALIBRATED_ACCELEROMETER_CONFIG, isChecked);
                // si se pasa a TRUE el switch de acelerómetro sin calibrar
                if (isChecked) {
                    // se pasa a TRUE el switch de movimiento
                    editor.putBoolean(STATUS_SWT_MOVEMENT_CONFIG, true);
                // si se pasa a false el switch de acelerómetro sin calibrar
                // se obtiene el estado de los otros switches
                // si los otros switches estan en false (es decir: todos los switches estan en false)
                } else if (!accelerometerStatus && !gyroscopeStatus && !magnetometerStatus &&
                            !gyroscopeUncalibratedStatus && !magnetometerUncalibratedStatus &&
                            !gravityStatus && !numberOfStepsStatus ) {
                        // se pasa a false el switch de movimiento
                        editor.putBoolean(STATUS_SWT_MOVEMENT_CONFIG, false);
                }
                editor.apply();
            });
        });
        /*---------- Configura el listener de ACELERÓMETRO SIN CALIBRAR ------------*/

        /*---------- Configura el listener de GIROSCOPIO SIN CALIBRAR ------------*/
        // 2. Publicar un ejecutable para establecer el estado y volver a habilitar el listener
        switchUncalibratedGyroscopeSetting.post(() -> {
            // 3. Establecer el estado marcado
            switchUncalibratedGyroscopeSetting.setChecked(gyroscopeUncalibratedStatus);

            // 4. Saltar al estado actual para evitar la animación
            switchUncalibratedGyroscopeSetting.jumpDrawablesToCurrentState();

            // 5. Volver a habilitar el listener
            switchUncalibratedGyroscopeSetting.setOnCheckedChangeListener((buttonView, isChecked) -> {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(STATUS_SWT_UNCALIBRATED_GYROSCOPE_CONFIG, isChecked);
                // si se pasa a TRUE el switch de giroscopio sin calibrar
                if (isChecked) {
                    // si se pasa a TRUE el switch de movimiento
                    editor.putBoolean(STATUS_SWT_MOVEMENT_CONFIG, true);
                // si se pasa a false el switch de giroscopio sin calibrar
                // se obtiene el estado de los otros switches
                // si los otros switches estan en false (es decir: todos los switches estan en false)
                } else if (!accelerometerStatus && !gyroscopeStatus && !magnetometerStatus &&
                            !accelerometerUncalibratedStatus && !magnetometerUncalibratedStatus &&
                            !gravityStatus && !numberOfStepsStatus ) {
                        // se pasa a false el switch de movimiento
                        editor.putBoolean(STATUS_SWT_MOVEMENT_CONFIG, false);
                }
                editor.apply();
            });
        });
        /*---------- Configura el listener de GIROSCOPIO SIN CALIBRAR ------------*/

        /*---------- Configura el listener de MAGNETÓMETRO SIN CALIBRAR ------------*/
        // 2. Publicar un ejecutable para establecer el estado y volver a habilitar el listener
        switchUncalibratedMagnetometerSetting.post(() -> {
            // 3. Establecer el estado marcado
            switchUncalibratedMagnetometerSetting.setChecked(magnetometerUncalibratedStatus);

            // 4. Saltar al estado actual para evitar la animación
            switchUncalibratedMagnetometerSetting.jumpDrawablesToCurrentState();

            // 5. Volver a habilitar el listener
            switchUncalibratedMagnetometerSetting.setOnCheckedChangeListener((buttonView, isChecked) -> {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(STATUS_SWT_UNCALIBRATED_MAGNETOMETER_CONFIG, isChecked);
                // si se pasa a TRUE el switch de magnetómetro sin calibrar
                if (isChecked) {
                    // se pasa a TRUE el switch de movimiento
                    editor.putBoolean(STATUS_SWT_MOVEMENT_CONFIG, true);
                // si se pasa a false el switch de magnetómetro sin calibrar
                // se obtiene el estado de los otros switches
                // si los otros switches estan en false (es decir: todos los switches estan en false)
                } else if (!accelerometerStatus && !gyroscopeStatus && !magnetometerStatus &&
                            !accelerometerUncalibratedStatus && !gyroscopeUncalibratedStatus &&
                            !gravityStatus && !numberOfStepsStatus ) {
                        // se pasa a false el switch de movimiento
                        editor.putBoolean(STATUS_SWT_MOVEMENT_CONFIG, false);
                }
                editor.apply();
            });
        });
        /*---------- Configura el listener de MAGNETÓMETRO SIN CALIBRAR ------------*/

        /*---------- Configura el listener de GRAVEDAD ------------*/
        // 2. Publicar un ejecutable para establecer el estado y volver a habilitar el listener
        switchGravitySetting.post(() -> {
            // 3. Establecer el estado marcado
            switchGravitySetting.setChecked(gravityStatus);

            // 4. Saltar al estado actual para evitar la animación
            switchGravitySetting.jumpDrawablesToCurrentState();

            // 5. Volver a habilitar el listener
            switchGravitySetting.setOnCheckedChangeListener((buttonView, isChecked) -> {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(STATUS_SWT_GRAVITY_CONFIG, isChecked);
                // si se pasa a TRUE el switch de gravedad
                if (isChecked) {
                    // se pasa a true el switch de movimiento
                    editor.putBoolean(STATUS_SWT_MOVEMENT_CONFIG, true);
                // si se pasa a false el switch de gravedad
                // se obtiene el estado de los otros switches
                // si los otros switches estan en false (es decir: todos los switches estan en false)
                } else if (!accelerometerStatus && !gyroscopeStatus && !magnetometerStatus &&
                            !accelerometerUncalibratedStatus && !gyroscopeUncalibratedStatus &&
                            !magnetometerUncalibratedStatus && !numberOfStepsStatus ) {
                        // se pasa a false el switch de movimiento
                        editor.putBoolean(STATUS_SWT_MOVEMENT_CONFIG, false);
                }
                editor.apply();
            });
        });
        /*---------- Configura el listener de GRAVEDAD ------------*/

        /*---------- Configura el listener de CONTADOR DE PASOS ------------*/
        // 2. Publicar un ejecutable para establecer el estado y volver a habilitar el listener
        switchNumberOfStepsSetting.post(() -> {
            // 3. Establecer el estado marcado
            switchNumberOfStepsSetting.setChecked(numberOfStepsStatus);

            // 4. Saltar al estado actual para evitar la animación
            switchNumberOfStepsSetting.jumpDrawablesToCurrentState();

            // 5. Volver a habilitar el listener
            switchNumberOfStepsSetting.setOnCheckedChangeListener((buttonView, isChecked) -> {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(STATUS_SWT_NUMBER_OF_STEPS_CONFIG, isChecked);
                // si se pasa a TRUE el switch de contador de pasos
                if (isChecked) {
                    // se pasa a TRUE el switch de movimiento
                    editor.putBoolean(STATUS_SWT_MOVEMENT_CONFIG, true);
                // si se pasa a false el switch de contador de pasos
                // se obtiene el estado de los otros switches
                // si los otros switches estan en false (es decir: todos los switches estan en false)
                } else if (!accelerometerStatus && !gyroscopeStatus && !magnetometerStatus &&
                            !accelerometerUncalibratedStatus && !gyroscopeUncalibratedStatus &&
                            !magnetometerUncalibratedStatus && !gravityStatus) {
                        // se pasa a false el switch de movimiento
                        editor.putBoolean(STATUS_SWT_MOVEMENT_CONFIG, false);
                }
                editor.apply();
            });
        });
        /*---------- Configura el listener de CONTADOR DE PASOS ------------*/

        /*---------- Habilita y Deshabilita los switch ------------*/
        MovementViewModel viewModel = new ViewModelProvider(this).get(MovementViewModel.class);

        // Get an instance of SensorManager
        SensorManager sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        viewModel.setSensorManager(sensorManager);

        // Check sensor availability and update Switch
        /*viewModel.checkSensorAvailability(Sensor.TYPE_ACCELEROMETER); // Replace with your desired sensor type

        viewModel.isSwitchEnabled.observe(getViewLifecycleOwner(), isEnabled -> {
            switchAccelerometerSetting.setEnabled(isEnabled);
        });*/
        //viewModel.checkSensorAvailability(Sensor.TYPE_ACCELEROMETER_UNCALIBRATED); // Replace with your desired sensor type

        /*viewModel.isSwitchEnabled.observe(getViewLifecycleOwner(), isEnabled -> {
            switchUncalibratedAccelerometerSetting.setEnabled(isEnabled);
            //switchUncalibratedAccelerometerSetting.setClickable(isEnabled);
            //switchUncalibratedAccelerometerSetting.setFocusable(isEnabled);
        });*/
        /*---------- Habilita y Deshabilita los switch ------------*/
        // Iterate through all sensors in SensorInfo
        for (SensorInfo sensorInfo : SensorInfo.values()) {
            // Check sensor availability
            viewModel.checkSensorAvailability(sensorInfo.getSensorType());

            // Get sensor availability status
            boolean isSensorAvailable = viewModel.isSensorAvailable(sensorInfo.getSensorType());

            // Get the corresponding Switch and TextView for the sensor
            @SuppressLint("UseSwitchCompatOrMaterialCode")
            Switch sensorSwitch = getSensorTextViewAndSwitch(sensorInfo).component2();
            TextView textView = getSensorTextViewAndSwitch(sensorInfo).component1();

            if (!isSensorAvailable) {
                sensorSwitch.setEnabled(isSensorAvailable);

                // Disable TextView and reduce opacity only if sensor is not available
                textView.setEnabled(isSensorAvailable);
                textView.setAlpha(isSensorAvailable ? 1.0f : 0.5f);

                // Save sensor status to SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(sensorInfo.getStatusKey(), isSensorAvailable);
                editor.apply();
            }
        }
        /*---------- Habilita y Deshabilita los switch ------------*/
        return view;
    }

    // Helper method to get the Switch view for a given sensor
    private Pair<TextView, Switch> getSensorTextViewAndSwitch(SensorInfo sensorInfo) {

        switch (sensorInfo) {
            case ACCELEROMETER:
                return new Pair<>(textViewAccelerometer, switchAccelerometerSetting);
            case GYROSCOPE:
                return new Pair<>(textViewGyroscope, switchGyroscopeSetting);
            case MAGNETOMETER:
                return new Pair<>(textViewMagnetometer, switchMagnetometerSetting);
            case ACCELEROMETER_UNCALIBRATED:
                return new Pair<>(textViewUncalibratedAccelerometer, switchUncalibratedAccelerometerSetting);
            case GYROSCOPE_UNCALIBRATED:
                return new Pair<>(textViewUncalibratedGyroscope, switchUncalibratedGyroscopeSetting);
            case MAGNETOMETER_UNCALIBRATED:
                return new Pair<>(textViewUncalibratedMagnetometer, switchUncalibratedMagnetometerSetting);
            case GRAVITY:
                return new Pair<>(textViewGravity, switchGravitySetting);
            case STEP_COUNTER:
                return new Pair<>(textViewNumberOfSteps, switchNumberOfStepsSetting);
            default:
                return null;
        }
    }
}