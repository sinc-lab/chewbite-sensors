package com.android.chewbiteSensors.ui.audio;

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

public class AudioFragment extends Fragment {

    private AudioViewModel mViewModel;
    private final String tag = "MainActivity";
    private static final String PREFS_KEY = "status_controls";
    private static final String STATUS_SWT_SOUND_CONFIG = "status_switch_sound_configuration";
    private static boolean switchSoundState;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switchAudioSetting;

    public static AudioFragment newInstance() {
        return new AudioFragment();
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_audio_setting, container, false);

        /*----------------------------------------------------------------------------------------*/
        // Inicializa el switch para la configuración de sonido
        switchAudioSetting = view.findViewById(R.id.switch_sound_configuration);

        // Recupera el estado guardado
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        switchSoundState = sharedPreferences.getBoolean(STATUS_SWT_SOUND_CONFIG, true);

        // Configura el listener
        switchAudioSetting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(STATUS_SWT_SOUND_CONFIG, isChecked);
                editor.apply();
            }
        });
        /*----------------------------------------------------------------------------------------*/

        return view;
    }

    /*@Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AudioViewModel.class);
        // TODO: Use the ViewModel
    }*/

    @Override
    public void onStart() {
        android.util.Log.d(tag, "onStart AudioFragment.java");
        super.onStart();

        // Setear el estado del switch
        switchAudioSetting.setChecked(switchSoundState);
    }
}