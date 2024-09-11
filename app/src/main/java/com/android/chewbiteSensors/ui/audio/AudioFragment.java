package com.android.chewbiteSensors.ui.audio;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.chewbiteSensors.R;
import com.android.chewbiteSensors.deviceStatus.ControlsStatusSettings;

public class AudioFragment extends Fragment {

    private AudioViewModel mViewModel;
    private static final String STATUS_SWT_SOUND_CONFIG = "status_switch_sound_configuration";

    public static AudioFragment newInstance() {
        return new AudioFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_audio_setting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializa el switch para la configuración de sonido
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch switchSoundConfiguration = view.findViewById(R.id.switch_sound_configuration_audio);

        // Recuperar el estado del switch desde las preferencias compartidas
        boolean isSwitchSoundChecked = ControlsStatusSettings.getSwitchEstado(STATUS_SWT_SOUND_CONFIG);

        // Establecer el estado del switch
        switchSoundConfiguration.setChecked(isSwitchSoundChecked);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AudioViewModel.class);
        // TODO: Use the ViewModel
    }

}