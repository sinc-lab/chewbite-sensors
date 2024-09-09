package com.android.chewbiteSensors.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.chewbiteSensors.R;
import com.android.chewbiteSensors.databinding.FragmentHomeBinding;
import com.android.chewbiteSensors.deviceStatus.ControlsStatusSettings;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
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
        // Inicializa el switch
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch switchSoundConfiguration = root.findViewById(R.id.switch_sound_configuration);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch switchMovementConfiguration = root.findViewById(R.id.switch_movement_configuration);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch switchGpsConfiguration = root.findViewById(R.id.switch_gps_configuration);

        ControlsStatusSettings.inicializarSwitch(requireContext(), switchSoundConfiguration, STATUS_SWT_SOUND_CONFIG, true);
        ControlsStatusSettings.inicializarSwitch(requireContext(), switchMovementConfiguration, STATUS_SWT_MOVEMENT_CONFIG, false);
        ControlsStatusSettings.inicializarSwitch(requireContext(), switchGpsConfiguration, STATUS_SWT_GPS_CONFIG, false);
        /*----------------------------------------------------------------------------------------*/

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}