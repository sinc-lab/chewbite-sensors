package com.android.chewbiteSensors.ui.movement;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
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
    private boolean accelerometerStatus;
    private boolean gyroscopeStatus;
    private boolean magnetometerStatus;
    private static final String STATUS_SWT_ACCELEROMETER_CONFIG = "status_switch_accelerometer_configuration";
    private static final String STATUS_SWT_GYROSCOPE_CONFIG = "status_switch_gyroscope_configuration";
    private static final String STATUS_SWT_MAGNETOMETER_CONFIG = "status_switch_magnetometer_configuration";
    private static final String STATUS_SWT_MOVEMENT_CONFIG = "status_switch_movement_configuration";

    public static MovementFragment newInstance() {
        return new MovementFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_movement_setting, container, false);
        /*----------------------------------------------------------------------------------------*/
        // Inicializa el switch para la configuración
        switchAccelerometerSetting = view.findViewById(R.id.switch_accelerometer_configuration);
        switchGyroscopeSetting = view.findViewById(R.id.switch_gyroscope_configuration);
        switchMagnetometerSetting = view.findViewById(R.id.switch_magnetometer_configuration);

        // Recupera el estado guardado
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        boolean movementStatus = sharedPreferences.getBoolean(STATUS_SWT_MOVEMENT_CONFIG, false);
        accelerometerStatus = sharedPreferences.getBoolean(STATUS_SWT_ACCELEROMETER_CONFIG, false);
        accelerometerStatus = accelerometerStatus && movementStatus;
        gyroscopeStatus = sharedPreferences.getBoolean(STATUS_SWT_GYROSCOPE_CONFIG, false);
        gyroscopeStatus = gyroscopeStatus && movementStatus;
        magnetometerStatus = sharedPreferences.getBoolean(STATUS_SWT_MAGNETOMETER_CONFIG, false);
        magnetometerStatus = magnetometerStatus && movementStatus;

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
                    boolean gyroscope = sharedPreferences.getBoolean(STATUS_SWT_GYROSCOPE_CONFIG, false);
                    boolean magnetometer = sharedPreferences.getBoolean(STATUS_SWT_MAGNETOMETER_CONFIG, false);
                    // si los otros switches estan en false (es decir: todos los switches estan en false)
                    if (!gyroscope && !magnetometer) {
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
                    boolean accelerometer = sharedPreferences.getBoolean(STATUS_SWT_ACCELEROMETER_CONFIG, false);
                    boolean magnetometer = sharedPreferences.getBoolean(STATUS_SWT_MAGNETOMETER_CONFIG, false);
                    // si los otros switches estan en false (es decir: todos los switches estan en false)
                    if (!accelerometer && !magnetometer) {
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
                    boolean accelerometer = sharedPreferences.getBoolean(STATUS_SWT_ACCELEROMETER_CONFIG, false);
                    boolean gyroscope = sharedPreferences.getBoolean(STATUS_SWT_GYROSCOPE_CONFIG, false);
                    // si los otros switches estan en false (es decir: todos los switches estan en false)
                    if (!accelerometer && !gyroscope) {
                        // se pasa a false el switch de movimiento
                        editor.putBoolean(STATUS_SWT_MOVEMENT_CONFIG, false);
                    }
                }
                editor.apply();
            }
        });

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
    }
}