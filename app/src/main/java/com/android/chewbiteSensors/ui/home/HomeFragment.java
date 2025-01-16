package com.android.chewbiteSensors.ui.home;

import android.annotation.SuppressLint;
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
import com.android.chewbiteSensors.settings.GetSettings;

public class HomeFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private FragmentHomeBinding binding;
    private ToggleButton buttonStartStop; // Variable de clase para la referencia
    private final String tag = "MainActivity";
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switchSoundConfiguration;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switchMovementConfiguration;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switchGpsConfiguration;
    private boolean soundStatus;
    private boolean movementStatus;
    private boolean gpsStatus;
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

        /*----------------------------*/
        soundStatus = GetSettings.getStatusSwitch("sound", requireActivity());
        movementStatus = GetSettings.getStatusSwitch("movement", requireActivity());
        gpsStatus = GetSettings.getStatusSwitch("gps", requireActivity());
        /*----------------------------*/

        // 1. Deshabilitar el listener inmediatamente
        switchSoundConfiguration.setOnCheckedChangeListener(null);
        switchMovementConfiguration.setOnCheckedChangeListener(null);
        switchGpsConfiguration.setOnCheckedChangeListener(null);

        /*---------- Configura el listener de audio ------------*/
        // 2. Publicar un ejecutable para establecer el estado y volver a habilitar el listener
        switchSoundConfiguration.post(() -> {
            // 3. Establecer el estado marcado
            switchSoundConfiguration.setChecked(soundStatus);

            // 4. Saltar al estado actual para evitar la animación
            switchSoundConfiguration.jumpDrawablesToCurrentState();

            // 5. Volver a habilitar el listener
            switchSoundConfiguration.setOnCheckedChangeListener((buttonView, isChecked) -> {
                GetSettings.setStatusSwitch("sound", isChecked, requireActivity());
            });
        });
        /*---------- Configura el listener de audio ------------*/

        /*---------- Configura el listener de movimiento ------------*/
        // 2. Publicar un ejecutable para establecer el estado y volver a habilitar el listener
        switchMovementConfiguration.post(() -> {
            // 3. Establecer el estado marcado
            switchMovementConfiguration.setChecked(movementStatus);

            // 4. Saltar al estado actual para evitar la animación
            switchMovementConfiguration.jumpDrawablesToCurrentState();

            // 5. Volver a habilitar el listener
            switchMovementConfiguration.setOnCheckedChangeListener((buttonView, isChecked) -> {
                GetSettings.setStatusSwitch("movement", isChecked, requireActivity());

                if (isChecked) {
                    // En el caso que el Switch de "Movimiento" sea TRUE → se pasan solo a TRUE los 3 primeros
                    GetSettings.setStatusSwitch("accelerometer", true, requireActivity());
                    GetSettings.setStatusSwitch("gyroscope", true, requireActivity());
                    GetSettings.setStatusSwitch("magnetometer", true, requireActivity());
                } else {
                    // En el caso que el Switch de "Movimiento" sea FALSE → se pasan todos a FALSE
                    String[] sensors = {"accelerometer", "gyroscope", "magnetometer",
                            "uncalibrated_accelerometer", "uncalibrated_gyroscope",
                            "uncalibrated_magnetometer", "gravity", "number_of_steps"};
                    for (String sensor : sensors) {
                        GetSettings.setStatusSwitch(sensor, false, requireActivity());
                    }
                }
            });
        });
        /*---------- Configura el listener de movimiento ------------*/

        /*---------- Configura el listener de GPS ------------*/
        // 2. Publicar un ejecutable para establecer el estado y volver a habilitar el listener
        switchGpsConfiguration.post(() -> {
            // 3. Establecer el estado marcado
            switchGpsConfiguration.setChecked(gpsStatus);

            // 4. Saltar al estado actual para evitar la animación
            switchGpsConfiguration.jumpDrawablesToCurrentState();

            // 5. Volver a habilitar el listener
            switchGpsConfiguration.setOnCheckedChangeListener((buttonView, isChecked) -> {
                GetSettings.setStatusSwitch("gps", isChecked, requireActivity());
            });
        });
        /*---------- Configura el listener de GPS ------------*/
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
                // 1-) Presina el boton de "Iniciar" y si no se cumplen las validaciones comienza la grabación
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}