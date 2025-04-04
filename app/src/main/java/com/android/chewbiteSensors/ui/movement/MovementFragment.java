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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.chewbiteSensors.R;
import com.android.chewbiteSensors.data_sensors.SensorInfo;
import com.android.chewbiteSensors.settings.GetSettings;

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

    public static MovementFragment newInstance() {
        return new MovementFragment();
    }

    @SuppressLint({"MissingInflatedId", "ClickableViewAccessibility"})
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_movement_setting, container, false);

        /*----------------------------------------------------------------------------------------*/
        // Inicializa del Spinner
        Spinner spinnerFrequencyMovementConfiguration = view.findViewById(R.id.spn_frequency_options_movement);

        // Obtén el array de opciones desde los recursos
        String[] optionsFrequencyArray = getResources().getStringArray(R.array.text_frequency_movement_options);

        // Crea un ArrayAdapter usando el array de opciones y un diseño simple para el spinner
        ArrayAdapter<String> adapterFrequency = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, optionsFrequencyArray);

        // Especifica el diseño a utilizar cuando se despliega el spinner
        adapterFrequency.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Aplica el adapter al spinner
        spinnerFrequencyMovementConfiguration.setAdapter(adapterFrequency);

        // Recupera el estado guardado
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        // Recuperar la última selección guardada en SharedPreferences
        int selectedFrequencyPosition = GetSettings.getStatusSpinner("frecuency_movement", requireActivity()); // 0 es el valor por defecto (primera opción)

        // Seleccionar la opción guardada
        spinnerFrequencyMovementConfiguration.setSelection(selectedFrequencyPosition);

        // Guardar la selección del Spinner
        spinnerFrequencyMovementConfiguration.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                // Guardar la posición seleccionada en SharedPreferences
                GetSettings.setStatusSpinner("frecuency_movement", position, requireActivity());
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
            switchAccelerometerSetting.setChecked(GetSettings.getStatusSwitch("accelerometer", requireActivity()));

            // 4. Saltar al estado actual para evitar la animación
            switchAccelerometerSetting.jumpDrawablesToCurrentState();

            // 5. Volver a habilitar el listener
            switchAccelerometerSetting.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // Guarda el estado del switch
                GetSettings.setStatusSwitch("accelerometer", isChecked, requireActivity());
                // si se pasa a TRUE el switch de acelerómetro
                if (isChecked) {
                    // se pasa a TRUE el switch de movimiento
                    GetSettings.setStatusSwitch("movement", true, requireActivity());
                }
            });
        });

        View switchContainerAccelerometer = view.findViewById(R.id.accelerometer);
        switchContainerAccelerometer.setOnClickListener(v -> {
            if (!switchAccelerometerSetting.isEnabled()) {
                // Mostrar el mensaje si el switch está deshabilitado
                this.toastMessage();
            }
        });
        /*---------- Configura el listener de ACELERÓMETRO ------------*/

        /*---------- Configura el listener de GIROSCOPIO ------------*/
        // 2. Publicar un ejecutable para establecer el estado y volver a habilitar el listener
        switchGyroscopeSetting.post(() -> {
            // 3. Establecer el estado marcado
            switchGyroscopeSetting.setChecked(GetSettings.getStatusSwitch("gyroscope", requireActivity()));

            // 4. Saltar al estado actual para evitar la animación
            switchGyroscopeSetting.jumpDrawablesToCurrentState();

            // 5. Volver a habilitar el listener
            switchGyroscopeSetting.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // Guarda el estado del switch
                GetSettings.setStatusSwitch("gyroscope", isChecked, requireActivity());
                // si se pasa a TRUE el switch de giroscopio
                if (isChecked) {
                    // se pasa a TRUE el switch de movimiento
                    GetSettings.setStatusSwitch("movement", true, requireActivity());
                }
            });
        });

        View switchContainerGyroscope = view.findViewById(R.id.gyroscope);
        switchContainerGyroscope.setOnClickListener(v -> {
            if (!switchGyroscopeSetting.isEnabled()) {
                // Mostrar el mensaje si el switch está deshabilitado
                this.toastMessage();
            }
        });
        /*---------- Configura el listener de GIROSCOPIO ------------*/

        /*---------- Configura el listener de MAGNETÓMETRO ------------*/
        // 2. Publicar un ejecutable para establecer el estado y volver a habilitar el listener
        switchMagnetometerSetting.post(() -> {
            // 3. Establecer el estado marcado
            switchMagnetometerSetting.setChecked(GetSettings.getStatusSwitch("magnetometer", requireActivity()));

            // 4. Saltar al estado actual para evitar la animación
            switchMagnetometerSetting.jumpDrawablesToCurrentState();

            // 5. Volver a habilitar el listener
            switchMagnetometerSetting.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // Guarda el estado del switch
                GetSettings.setStatusSwitch("magnetometer", isChecked, requireActivity());
                // si se pasa a TRUE el switch de magnetómetro
                if (isChecked) {
                    // se pasa a TRUE el switch de movimiento
                    GetSettings.setStatusSwitch("movement", true, requireActivity());
                }
            });
        });

        View switchContainerMagnetometer = view.findViewById(R.id.magnetometer);
        switchContainerMagnetometer.setOnClickListener(v -> {
            if (!switchMagnetometerSetting.isEnabled()) {
                // Mostrar el mensaje si el switch está deshabilitado
                this.toastMessage();
            }
        });
        /*---------- Configura el listener de MAGNETÓMETRO ------------*/

        /*---------- Configura el listener de ACELERÓMETRO SIN CALIBRAR ------------*/
        // 2. Publicar un ejecutable para establecer el estado y volver a habilitar el listener
        switchUncalibratedAccelerometerSetting.post(() -> {
            // 3. Establecer el estado marcado
            switchUncalibratedAccelerometerSetting.setChecked(GetSettings.getStatusSwitch("uncalibrated_accelerometer", requireActivity()));

            // 4. Saltar al estado actual para evitar la animación
            switchUncalibratedAccelerometerSetting.jumpDrawablesToCurrentState();

            // 5. Volver a habilitar el listener
            switchUncalibratedAccelerometerSetting.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // Guarda el estado del switch
                GetSettings.setStatusSwitch("uncalibrated_accelerometer", isChecked, requireActivity());
                // si se pasa a TRUE el switch de acelerómetro sin calibrar
                if (isChecked) {
                    // se pasa a TRUE el switch de movimiento
                    GetSettings.setStatusSwitch("movement", true, requireActivity());
                }
            });
        });

        View switchContainerAccelerometerUncalibrated = view.findViewById(R.id.accelerometer_uncalibrated);
        switchContainerAccelerometerUncalibrated.setOnClickListener(v -> {
            if (!switchUncalibratedAccelerometerSetting.isEnabled()) {
                // Mostrar el mensaje si el switch está deshabilitado
                this.toastMessage();
            }
        });
        /*---------- Configura el listener de ACELERÓMETRO SIN CALIBRAR ------------*/

        /*---------- Configura el listener de GIROSCOPIO SIN CALIBRAR ------------*/
        // 2. Publicar un ejecutable para establecer el estado y volver a habilitar el listener
        switchUncalibratedGyroscopeSetting.post(() -> {
            // 3. Establecer el estado marcado
            switchUncalibratedGyroscopeSetting.setChecked(GetSettings.getStatusSwitch("uncalibrated_gyroscope", requireActivity()));

            // 4. Saltar al estado actual para evitar la animación
            switchUncalibratedGyroscopeSetting.jumpDrawablesToCurrentState();

            // 5. Volver a habilitar el listener
            switchUncalibratedGyroscopeSetting.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // Guarda el estado del switch
                GetSettings.setStatusSwitch("uncalibrated_gyroscope", isChecked, requireActivity());
                // si se pasa a TRUE el switch de giroscopio sin calibrar
                if (isChecked) {
                    // si se pasa a TRUE el switch de movimiento
                    GetSettings.setStatusSwitch("movement", true, requireActivity());
                }
            });
        });

        View switchContainerGyroscopeUncalibrated = view.findViewById(R.id.gyroscope_uncalibrated);
        switchContainerGyroscopeUncalibrated.setOnClickListener(v -> {
            if (!switchUncalibratedGyroscopeSetting.isEnabled()) {
                // Mostrar el mensaje si el switch está deshabilitado
                this.toastMessage();
            }
        });
        /*---------- Configura el listener de GIROSCOPIO SIN CALIBRAR ------------*/

        /*---------- Configura el listener de MAGNETÓMETRO SIN CALIBRAR ------------*/
        // 2. Publicar un ejecutable para establecer el estado y volver a habilitar el listener
        switchUncalibratedMagnetometerSetting.post(() -> {
            // 3. Establecer el estado marcado
            switchUncalibratedMagnetometerSetting.setChecked(GetSettings.getStatusSwitch("uncalibrated_magnetometer", requireActivity()));

            // 4. Saltar al estado actual para evitar la animación
            switchUncalibratedMagnetometerSetting.jumpDrawablesToCurrentState();

            // 5. Volver a habilitar el listener
            switchUncalibratedMagnetometerSetting.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // Guarda el estado del switch
                GetSettings.setStatusSwitch("uncalibrated_magnetometer", isChecked, requireActivity());
                // si se pasa a TRUE el switch de magnetómetro sin calibrar
                if (isChecked) {
                    // se pasa a TRUE el switch de movimiento
                    GetSettings.setStatusSwitch("movement", true, requireActivity());
                }
            });
        });

        View switchContainerMagnetometerUncalibrated = view.findViewById(R.id.magnetometer_uncalibrated);
        switchContainerMagnetometerUncalibrated.setOnClickListener(v -> {
            if (!switchUncalibratedMagnetometerSetting.isEnabled()) {
                // Mostrar el mensaje si el switch está deshabilitado
                this.toastMessage();
            }
        });
        /*---------- Configura el listener de MAGNETÓMETRO SIN CALIBRAR ------------*/

        /*---------- Configura el listener de GRAVEDAD ------------*/
        // 2. Publicar un ejecutable para establecer el estado y volver a habilitar el listener
        switchGravitySetting.post(() -> {
            // 3. Establecer el estado marcado
            switchGravitySetting.setChecked(GetSettings.getStatusSwitch("gravity", requireActivity()));

            // 4. Saltar al estado actual para evitar la animación
            switchGravitySetting.jumpDrawablesToCurrentState();

            // 5. Volver a habilitar el listener
            switchGravitySetting.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // Guarda el estado del switch
                GetSettings.setStatusSwitch("gravity", isChecked, requireActivity());
                // si se pasa a TRUE el switch de gravedad
                if (isChecked) {
                    // se pasa a true el switch de movimiento
                    GetSettings.setStatusSwitch("movement", true, requireActivity());
                }
            });
        });

        View switchContainerGravity = view.findViewById(R.id.gravity);
        switchContainerGravity.setOnClickListener(v -> {
            if (!switchGravitySetting.isEnabled()) {
                // Mostrar el mensaje si el switch está deshabilitado
                this.toastMessage();
            }
        });
        /*---------- Configura el listener de GRAVEDAD ------------*/

        /*---------- Configura el listener de CONTADOR DE PASOS ------------*/
        // 2. Publicar un ejecutable para establecer el estado y volver a habilitar el listener
        switchNumberOfStepsSetting.post(() -> {
            // 3. Establecer el estado marcado
            switchNumberOfStepsSetting.setChecked(GetSettings.getStatusSwitch("number_of_steps", requireActivity()));

            // 4. Saltar al estado actual para evitar la animación
            switchNumberOfStepsSetting.jumpDrawablesToCurrentState();

            // 5. Volver a habilitar el listener
            switchNumberOfStepsSetting.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // Guarda el estado del switch
                GetSettings.setStatusSwitch("number_of_steps", isChecked, requireActivity());
                // si se pasa a TRUE el switch de contador de pasos
                if (isChecked) {
                    // se pasa a TRUE el switch de movimiento
                    GetSettings.setStatusSwitch("movement", true, requireActivity());
                }
            });
        });

        View switchContainerNumberOfSteps = view.findViewById(R.id.number_of_steps);
        switchContainerNumberOfSteps.setOnClickListener(v -> {
            if (!switchNumberOfStepsSetting.isEnabled()) {
                // Mostrar el mensaje si el switch está deshabilitado
                this.toastMessage();
            }
        });
        /*---------- Configura el listener de CONTADOR DE PASOS ------------*/

        /*---------- Habilita y Deshabilita los switch ------------*/
        MovementViewModel viewModel = new ViewModelProvider(this).get(MovementViewModel.class);

        // Get an instance of SensorManager
        SensorManager sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        viewModel.setSensorManager(sensorManager);

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
                sensorSwitch.setClickable(isSensorAvailable);

                // Disable TextView and reduce opacity only if sensor is not available
                textView.setEnabled(isSensorAvailable);
                textView.setAlpha(isSensorAvailable ? 1.0f : 0.5f);

                // Save sensor status to SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(sensorInfo.getStatusKey(), isSensorAvailable);
                editor.apply();
            } else {
                int minHz = viewModel.getMinHzSensor(sensorInfo.getSensorType());

                if (minHz < 100) {
                    mostrarTextViewLimitation(sensorInfo, minHz, view);
                }

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

    private void toastMessage() {
        // Mostrar mensaje de que el sensor no está disponible
        Toast.makeText(requireActivity(), "El dispositivo no cuenta con este sensor.", Toast.LENGTH_SHORT).show();
    }

    private void mostrarTextViewLimitation(SensorInfo sensorInfo, int Hz, View view) {

        String text = "* Limitado a " + Hz + "Hz como máximo";
        switch (sensorInfo) {
            case ACCELEROMETER:
                // Inicializa el switch para la configuración
                TextView textViewAccelerometerLimitation = view.findViewById(R.id.txt_accelerometer_limitation);
                textViewAccelerometerLimitation.setText(text);
                textViewAccelerometerLimitation.setVisibility(View.VISIBLE);
                break;
            case GYROSCOPE:
                // Inicializa el switch para la configuración
                TextView textViewGyroscopeLimitation = view.findViewById(R.id.txt_gyroscope_limitation);
                textViewGyroscopeLimitation.setText(text);
                textViewGyroscopeLimitation.setVisibility(View.VISIBLE);
                break;
            case MAGNETOMETER:
                // Inicializa el switch para la configuración
                TextView textViewMagnetometerLimitation = view.findViewById(R.id.txt_magnetometer_limitation);
                textViewMagnetometerLimitation.setText(text);
                textViewMagnetometerLimitation.setVisibility(View.VISIBLE);
                break;
            case ACCELEROMETER_UNCALIBRATED:
                // Inicializa el switch para la configuración
                TextView textViewUncalibratedAccelerometerLimitation = view.findViewById(R.id.txt_accelerometer_uncalibrated_limitation);
                textViewUncalibratedAccelerometerLimitation.setText(text);
                textViewUncalibratedAccelerometerLimitation.setVisibility(View.VISIBLE);
                break;
            case GYROSCOPE_UNCALIBRATED:
                // Inicializa el switch para la configuración
                TextView textViewUncalibratedGyroscopeLimitation = view.findViewById(R.id.txt_gyroscope_uncalibrated_limitation);
                textViewUncalibratedGyroscopeLimitation.setText(text);
                textViewUncalibratedGyroscopeLimitation.setVisibility(View.VISIBLE);
                break;
            case MAGNETOMETER_UNCALIBRATED:
                // Inicializa el switch para la configuración
                TextView textViewUncalibratedMagnetometerLimitation = view.findViewById(R.id.txt_magnetometer_uncalibrated_limitation);
                textViewUncalibratedMagnetometerLimitation.setText(text);
                textViewUncalibratedMagnetometerLimitation.setVisibility(View.VISIBLE);
                break;
            case GRAVITY:
                // Inicializa el switch para la configuración
                TextView textViewGravityLimitation = view.findViewById(R.id.txt_gravity_limitation);
                textViewGravityLimitation.setText(text);
                textViewGravityLimitation.setVisibility(View.VISIBLE);
                break;
            case STEP_COUNTER:
                // Inicializa el switch para la configuración
                /*TextView textViewNumberOfStepsLimitation = view.findViewById(R.id.txt_number_of_steps_limitation);
                textViewNumberOfStepsLimitation.setText(text);
                textViewNumberOfStepsLimitation.setVisibility(View.VISIBLE);*/
                RelativeLayout rl_war_rect = view.findViewById(R.id.rl_warning_rectangle);
                rl_war_rect.setVisibility(View.VISIBLE);
                rl_war_rect.setOnClickListener(v -> openDialog());
                break;
        }
    }

    private void openDialog() {
        String message = "Los datos del contador de pasos son almacenados cuando el sensor del sistema detecta un cambio.\n" +
                "Sin importar la frecuencia de muestreo que se asigne.";
        // Usar MaterialAlertDialogBuilder si tienes Material Components
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireContext());
        alertDialogBuilder.setTitle("Información");
        alertDialogBuilder.setMessage(message);

        // Icono opcional (recomendado para diálogos informativos)
        //alertDialogBuilder.setIcon(R.drawable.warning_blue); // Añade tu propio ícono

        alertDialogBuilder.setPositiveButton("Aceptar", (dialog, which) -> {
            // Acción al hacer clic en Aceptar
            dialog.dismiss(); // Cierra el diálogo
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}