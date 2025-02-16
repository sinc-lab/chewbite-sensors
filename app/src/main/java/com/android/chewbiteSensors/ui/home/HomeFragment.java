package com.android.chewbiteSensors.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
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
    private ToggleButton buttonStartStop;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switchSoundConfiguration;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switchMovementConfiguration;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switchGpsConfiguration;
    private boolean itIsTheFirstNotification = true;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Se obtiene la instancia del HomeViewModel
        // Declaramos la instancia del ViewModel
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Si en el layout tienes, por ejemplo, un TextView (binding.textHome)
        // Puedes observar el LiveData y actualizar la UI:
        // homeViewModel.getText().observe(getViewLifecycleOwner(), text -> binding.textHome.setText(text));

        // Inicializa el botón y configura su listener
        buttonStartStop = root.findViewById(R.id.btn_start);
        buttonStartStop.setOnCheckedChangeListener(this);
        buttonStartStop.setChecked(false);

        // Inicializa los switches
        switchSoundConfiguration = root.findViewById(R.id.switch_sound_home);
        switchMovementConfiguration = root.findViewById(R.id.switch_movement_home);
        switchGpsConfiguration = root.findViewById(R.id.switch_gps_home);

        // Recupera el estado guardado de cada switch (por ejemplo, desde las preferencias)
        boolean soundStatus = GetSettings.getStatusSwitch("sound", requireActivity());
        boolean movementStatus = GetSettings.getStatusSwitch("movement", requireActivity());
        boolean gpsStatus = GetSettings.getStatusSwitch("gps", requireActivity());

        // Configura cada switch de forma similar para evitar que el listener se dispare al establecer el estado
        configureSwitch(switchSoundConfiguration, soundStatus, "sound");
        configureSwitch(switchMovementConfiguration, movementStatus, "movement");
        configureSwitch(switchGpsConfiguration, gpsStatus, "gps");

        return root;
    }

    /**
     * Método auxiliar para configurar un Switch.
     * Se deshabilita el listener, se establece el estado inicial, y luego se vuelve a habilitar.
     *
     * @param sw Switch a configurar
     * @param initialStatus Estado inicial del Switch
     * @param key Clave del sensor correspondiente al Switch
     */
    private void configureSwitch(@SuppressLint("UseSwitchCompatOrMaterialCode") Switch sw, boolean initialStatus, String key) {
        // 1. Deshabilitar el listener inmediatamente
        sw.setOnCheckedChangeListener(null);
        // 2. Publicar un ejecutable para establecer el estado y volver a habilitar el listener
        sw.post(() -> {
            // 3. Establecer el estado marcado
            sw.setChecked(initialStatus);
            // 4. Saltar al estado actual para evitar la animación
            sw.jumpDrawablesToCurrentState();
            // 5. Volver a habilitar el listener
            sw.setOnCheckedChangeListener((buttonView, isChecked) -> {
                GetSettings.setStatusSwitch(key, isChecked, requireActivity());
                // Lógica adicional para el switch de "movement"
                if (key.equals("movement")) {
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
                }
            });
        });
    }

    /**
     * Listener para el ToggleButton de iniciar/detener.
     *
     * @param buttonView The compound button view whose state has changed.
     * @param isChecked  The new checked state of buttonView.
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        MainActivity main = (MainActivity) getActivity();
        if (main == null) {
            return;
        }
        // Solicita permisos si es necesario
        main.askForPermissions();
        if (!main.hasPermissions()) {
            buttonStartStop.setChecked(false);
            return;
        }
        // Verifica que al menos un sensor esté seleccionado
        if (!switchSoundConfiguration.isChecked() && !switchMovementConfiguration.isChecked() && !switchGpsConfiguration.isChecked()) {
            buttonStartStop.setChecked(false);
            Toast.makeText(requireContext(), "Debe seleccionar al menos un sensor para continuar", Toast.LENGTH_SHORT).show();
            return;
        }
        // Inicia o detiene la grabación según el estado del botón
        if (isChecked) {
            if (!main.checkStatusBeforeStart() && itIsTheFirstNotification) {
                itIsTheFirstNotification = false;
                main.openDialog();
                buttonStartStop.setChecked(false);
            } else {
                // 1-) Presina el boton de "Iniciar" y si se cumplen las validaciones comienza la grabación
                main.startTest();
            }
        } else {
            main.stopTest();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
