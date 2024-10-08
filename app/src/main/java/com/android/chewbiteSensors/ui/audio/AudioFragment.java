package com.android.chewbiteSensors.ui.audio;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
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
    private static final String STATUS_SPN_BIT_RATE_CONFIG = "status_switch_bit_rate_configuration";
    private static final String STATUS_SPN_FREQUENCY_CONFIG = "status_switch_frequency_sound_configuration";
    private static final String STATUS_SPN_FILE_TYPE_CONFIG = "status_switch_file_type_configuration";
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

        // Inicializa del Spinner
        Spinner spinnerBitRateConfiguration = view.findViewById(R.id.spn_bit_rate);
        Spinner spinnerFrequencyAudioConfiguration = view.findViewById(R.id.spn_frequency_options);
        Spinner spinnerFileTypeConfiguration = view.findViewById(R.id.spn_file_type);

        // Obtén el array de opciones desde los recursos
        String[] optionsBitRateArray = getResources().getStringArray(R.array.text_bit_rate_options);
        String[] optionsFrequencyArray = getResources().getStringArray(R.array.text_frequency_options);
        String[] optionsFileTypeArray = getResources().getStringArray(R.array.text_type_file_options);

        // Crea un ArrayAdapter usando el array de opciones y un diseño simple para el spinner
        ArrayAdapter<String> adapterBitRate = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, optionsBitRateArray);
        ArrayAdapter<String> adapterFrequency = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, optionsFrequencyArray);
        ArrayAdapter<String> adapterFileType = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, optionsFileTypeArray);

        // Especifica el diseño a utilizar cuando se despliega el spinner
        adapterBitRate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterFrequency.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterFileType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Aplica el adapter al spinner
        spinnerBitRateConfiguration.setAdapter(adapterBitRate);
        spinnerFrequencyAudioConfiguration.setAdapter(adapterFrequency);
        spinnerFileTypeConfiguration.setAdapter(adapterFileType);

        // Recuperar la última selección guardada en SharedPreferences
        int selectedBitRatePosition = sharedPreferences.getInt(STATUS_SPN_BIT_RATE_CONFIG, 0); // 0 es el valor por defecto (primera opción)
        int selectedFrequencyPosition = sharedPreferences.getInt(STATUS_SPN_FREQUENCY_CONFIG, 0); // 0 es el valor por defecto (primera opción)
        int selectedFileTypePosition = sharedPreferences.getInt(STATUS_SPN_FILE_TYPE_CONFIG, 0); // 0 es el valor por defecto (primera opción)
        // Seleccionar la opción guardada
        spinnerBitRateConfiguration.setSelection(selectedBitRatePosition);
        spinnerFrequencyAudioConfiguration.setSelection(selectedFrequencyPosition);
        spinnerFileTypeConfiguration.setSelection(selectedFileTypePosition);

        // Guardar la selección del Spinner
        spinnerBitRateConfiguration.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                // Guardar la posición seleccionada en SharedPreferences
                editor.putInt(STATUS_SPN_BIT_RATE_CONFIG, position);
                editor.apply();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No se necesita implementar, pero es necesario sobrescribir el método
            }
        });
        // Guardar la selección del Spinner
        spinnerFrequencyAudioConfiguration.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                // Guardar la posición seleccionada en SharedPreferences
                editor.putInt(STATUS_SPN_FREQUENCY_CONFIG, position);
                editor.apply();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No se necesita implementar, pero es necesario sobrescribir el método
            }
        });
        // Guardar la selección del Spinner
        spinnerFileTypeConfiguration.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                // Guardar la posición seleccionada en SharedPreferences
                editor.putInt(STATUS_SPN_FILE_TYPE_CONFIG, position);
                editor.apply();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No se necesita implementar, pero es necesario sobrescribir el método
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