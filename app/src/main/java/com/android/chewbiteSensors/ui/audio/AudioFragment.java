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
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.chewbiteSensors.R;
import com.android.chewbiteSensors.settings.GetSettings;

public class AudioFragment extends Fragment {

    private AudioViewModel mViewModel;
    private static final String PREFS_KEY = "status_controls";
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

        // 1. Deshabilitar el listener inmediatamente
        switchAudioSetting.setOnCheckedChangeListener(null);

        // 2. Publicar un ejecutable para establecer el estado y volver a habilitar el listener
        switchAudioSetting.post(() -> {
            // 3. Establecer el estado marcado
            switchAudioSetting.setChecked(GetSettings.getStatusSwitch("sound", requireActivity()));

            // 4. Saltar al estado actual para evitar la animación
            switchAudioSetting.jumpDrawablesToCurrentState();

            // 5. Volver a habilitar el listener
            switchAudioSetting.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // Guarda el estado del switch
                GetSettings.setStatusSwitch("sound", isChecked, requireActivity());
            });
        });

        /*----------------------------------------------------------------------------------------*/

        // Inicializa del Spinner
        Spinner spinnerBitRateConfiguration = view.findViewById(R.id.spn_bit_rate);
        Spinner spinnerFrequencyAudioConfiguration = view.findViewById(R.id.spn_frequency_options);
        Spinner spinnerFileTypeConfiguration = view.findViewById(R.id.spn_file_type);

        // Obtén el array de opciones desde los recursos
        String[] optionsBitRateArray = getResources().getStringArray(R.array.text_bit_rate_options);
        String[] optionsFrequencyArray = getResources().getStringArray(R.array.text_frequency_sound_options);
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
        int selectedBitRatePosition = GetSettings.getStatusSpinner("bit_rate_sound", requireActivity()); // 0 es el valor por defecto (primera opción)
        int selectedFrequencyPosition = GetSettings.getStatusSpinner("frecuency_sound", requireActivity()); // 0 es el valor por defecto (primera opción)
        int selectedFileTypePosition = GetSettings.getStatusSpinner("file_type", requireActivity()); // 0 es el valor por defecto (primera opción)
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
                GetSettings.setStatusSpinner("bit_rate_sound", position, requireActivity());
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
                GetSettings.setStatusSpinner("frecuency_sound", position, requireActivity());
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
                GetSettings.setStatusSpinner("file_type", position, requireActivity());
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
}