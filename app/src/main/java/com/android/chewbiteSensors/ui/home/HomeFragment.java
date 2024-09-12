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
import com.android.chewbiteSensors.deviceStatus.ControlsStatusSettings;

public class HomeFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private FragmentHomeBinding binding;
    private ToggleButton buttonStartStop; // Variable de clase para la referencia
    private final String tag = "MainActivity";
    private static final String STATUS_SWT_SOUND_CONFIG = "status_switch_sound_configuration";
    private static final String STATUS_SWT_MOVEMENT_CONFIG = "status_switch_movement_configuration";
    private static final String STATUS_SWT_GPS_CONFIG = "status_switch_gps_configuration";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //final TextView textView = binding.textHome;
        //homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        /*----------------------------------------------------------------------------------------*/
        // Inicializa el Context
        ControlsStatusSettings.setContext(requireContext());
        /*----------------------------------------------------------------------------------------*/
        // Inicializa el boton
        buttonStartStop = root.findViewById(R.id.btn_start); // Obtén la referencia en onCreate
        buttonStartStop.setOnCheckedChangeListener(this); // Establece el listener
        buttonStartStop.setChecked(false);
        /*----------------------------------------------------------------------------------------*/
        // Inicializa el switch
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch switchSoundConfiguration = root.findViewById(R.id.switch_sound_configuration);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch switchMovementConfiguration = root.findViewById(R.id.switch_movement_configuration);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch switchGpsConfiguration = root.findViewById(R.id.switch_gps_configuration);

        ControlsStatusSettings.inicializarSwitch(switchSoundConfiguration, STATUS_SWT_SOUND_CONFIG, true);
        ControlsStatusSettings.inicializarSwitch(switchMovementConfiguration, STATUS_SWT_MOVEMENT_CONFIG, false);
        ControlsStatusSettings.inicializarSwitch(switchGpsConfiguration, STATUS_SWT_GPS_CONFIG, false);
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
            if (!main.checkStatusBeforeStart()) {
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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}