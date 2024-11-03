package com.android.chewbiteSensors.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.chewbiteSensors.MainActivity;
import com.android.chewbiteSensors.R;
import com.android.chewbiteSensors.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private FragmentHomeBinding binding;
    private ToggleButton buttonStartStop; // Variable de clase para la referencia
    private final String tag = "MainActivity";
    private static final String PREFS_KEY = "status_controls";
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switchSoundConfiguration;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switchMovementConfiguration;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switchGpsConfiguration;
    private boolean soundStatus;
    private boolean movementStatus;
    private boolean gpsStatus;
    private boolean accelerometerStatus;
    private boolean gyroscopeStatus;
    private boolean magnetometerStatus;
    private boolean accelerometerUncalibratedStatus;
    private boolean gyroscopeUncalibratedStatus;
    private boolean magnetometerUncalibratedStatus;
    private boolean gravityStatus;
    private boolean numberOfStepsStatus;
    private static final String STATUS_SWT_SOUND_CONFIG = "status_switch_sound_configuration";
    private static final String STATUS_SWT_MOVEMENT_CONFIG = "status_switch_movement_configuration";
    private static final String STATUS_SWT_GPS_CONFIG = "status_switch_gps_configuration";
    private static final String STATUS_SWT_ACCELEROMETER_CONFIG = "status_switch_accelerometer_configuration";
    private static final String STATUS_SWT_GYROSCOPE_CONFIG = "status_switch_gyroscope_configuration";
    private static final String STATUS_SWT_MAGNETOMETER_CONFIG = "status_switch_magnetometer_configuration";
    private static final String STATUS_SWT_UNCALIBRATED_ACCELEROMETER_CONFIG = "status_switch_uncalibrated_accelerometer_configuration";
    private static final String STATUS_SWT_UNCALIBRATED_GYROSCOPE_CONFIG = "status_switch_uncalibrated_gyroscope_configuration";
    private static final String STATUS_SWT_UNCALIBRATED_MAGNETOMETER_CONFIG = "status_switch_uncalibrated_magnetometer_configuration";
    private static final String STATUS_SWT_GRAVITY_CONFIG = "status_switch_gravity_configuration";
    private static final String STATUS_SWT_NUMBER_OF_STEPS_CONFIG = "status_switch_number_of_steps_configuration";
    private boolean it_is_the_first_notification = true;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //final TextView textView = binding.textHome;
        //homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        /*----------------------------------------------------------------------------------------*/
        // Inicializa el boton
        buttonStartStop = root.findViewById(R.id.btn_start); // Obtén la referencia en onCreate
        buttonStartStop.setOnCheckedChangeListener(this); // Establece el listener
        buttonStartStop.setChecked(false);
        /*----------------------------------------------------------------------------------------*/
        // Inicializa el switch
        switchSoundConfiguration = root.findViewById(R.id.switch_sound_home);
        switchMovementConfiguration = root.findViewById(R.id.switch_movement_home);
        switchGpsConfiguration = root.findViewById(R.id.switch_gps_home);

        // Recupera el estado guardado
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);

        soundStatus = sharedPreferences.getBoolean(STATUS_SWT_SOUND_CONFIG, true);
        /*----------------------------*/
        accelerometerStatus = sharedPreferences.getBoolean(STATUS_SWT_ACCELEROMETER_CONFIG, false);
        gyroscopeStatus = sharedPreferences.getBoolean(STATUS_SWT_GYROSCOPE_CONFIG, false);
        magnetometerStatus = sharedPreferences.getBoolean(STATUS_SWT_MAGNETOMETER_CONFIG, false);
        accelerometerUncalibratedStatus = sharedPreferences.getBoolean(STATUS_SWT_UNCALIBRATED_ACCELEROMETER_CONFIG, false);
        gyroscopeUncalibratedStatus = sharedPreferences.getBoolean(STATUS_SWT_UNCALIBRATED_GYROSCOPE_CONFIG, false);
        magnetometerUncalibratedStatus = sharedPreferences.getBoolean(STATUS_SWT_UNCALIBRATED_MAGNETOMETER_CONFIG, false);
        gravityStatus = sharedPreferences.getBoolean(STATUS_SWT_GRAVITY_CONFIG, false);
        numberOfStepsStatus = sharedPreferences.getBoolean(STATUS_SWT_NUMBER_OF_STEPS_CONFIG, false);

        movementStatus = sharedPreferences.getBoolean(STATUS_SWT_MOVEMENT_CONFIG, false);
        movementStatus = (accelerometerStatus || gyroscopeStatus || magnetometerStatus ||
                accelerometerUncalibratedStatus || gyroscopeUncalibratedStatus ||
                magnetometerUncalibratedStatus || gravityStatus || numberOfStepsStatus) && movementStatus;
        /*----------------------------*/
        gpsStatus = sharedPreferences.getBoolean(STATUS_SWT_GPS_CONFIG, false);

        // Configura el listener
        switchSoundConfiguration.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(STATUS_SWT_SOUND_CONFIG, isChecked);
                editor.apply();
            }
        });
        // Configura el listener
        switchMovementConfiguration.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(STATUS_SWT_MOVEMENT_CONFIG, isChecked);
                // Busca el estado de los switch
                //boolean accelerometerStatus = sharedPreferences.getBoolean(STATUS_SWT_ACCELEROMETER_CONFIG, false);
                //boolean gyroscopeStatus = sharedPreferences.getBoolean(STATUS_SWT_GYROSCOPE_CONFIG, false);
                //boolean magnetometerStatus = sharedPreferences.getBoolean(STATUS_SWT_MAGNETOMETER_CONFIG, false);
                // En el caso que el Switch de "Movimiento" sea TRUE
                // y todos los demás sean FALSE
                // Los pasa a positivos
                if (isChecked && !accelerometerStatus && !gyroscopeStatus && !magnetometerStatus &&
                        !accelerometerUncalibratedStatus && !gyroscopeUncalibratedStatus &&
                        !magnetometerUncalibratedStatus && !gravityStatus && !numberOfStepsStatus){
                    editor.putBoolean(STATUS_SWT_ACCELEROMETER_CONFIG, true);
                    editor.putBoolean(STATUS_SWT_GYROSCOPE_CONFIG, true);
                    editor.putBoolean(STATUS_SWT_MAGNETOMETER_CONFIG, true);
                }
                editor.apply();
            }
        });
        // Configura el listener
        switchGpsConfiguration.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(STATUS_SWT_GPS_CONFIG, isChecked);
                editor.apply();
            }
        });

        /*----------------------------------------------------------------------------------------*/

        return root;
    }


    /**
     *
     * @param buttonView The compound button view whose state has changed.
     * @param isChecked  The new checked state of buttonView.
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // En caso de no tener permisos los vuelve a solicitar
        MainActivity main = (MainActivity) getActivity();
        if (main == null) {
            android.util.Log.d(tag, "if (main == null)");
        }
        main.askForPermissions();
        // Corrobora si efectivamente se le dio los permisos
        if (!main.hasPermissions()) {
            this.buttonStartStop.setChecked(false);
            return;
        }
        //
        if (isChecked) {
            // Verifica el estado antes de comenzar la grabación
            if (!main.checkStatusBeforeStart() && it_is_the_first_notification) {
                it_is_the_first_notification = false;
                android.util.Log.d(tag, "if (!this.checkStatusBeforeStart())");
                main.openDialog();
                this.buttonStartStop.setChecked(false);
            } else {
                main.startTest();
            }
        } else {
            main.stopTest();
        }
    }
    /*----------------------------------------------------------------------------------------*/

    @Override
    public void onStart() {
        //android.util.Log.d(tag, "onStart HomeFragment.java");
        super.onStart();

        switchSoundConfiguration.setChecked(soundStatus);
        switchMovementConfiguration.setChecked(movementStatus);
        switchGpsConfiguration.setChecked(gpsStatus);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}