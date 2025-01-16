package com.android.chewbiteSensors.ui.gps;

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

public class GPSFragment extends Fragment {

    private GPSViewModel mViewModel;
    private static final String PREFS_KEY = "status_controls";
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switchGpsSetting;

    public static GPSFragment newInstance() {
        return new GPSFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_gps_setting, container, false);
        /*----------------------------------------------------------------------------------------*/
        // Inicializa el switch para la configuración de sonido
        switchGpsSetting = view.findViewById(R.id.switch_gps_configuration);

        // Recupera el estado guardado
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);

        // 1. Deshabilitar el listener inmediatamente
        switchGpsSetting.setOnCheckedChangeListener(null);

        // 2. Publicar un ejecutable para establecer el estado y volver a habilitar el listener
        switchGpsSetting.post(() -> {
            // 3. Establecer el estado marcado
            switchGpsSetting.setChecked(GetSettings.getStatusSwitch("gps", requireActivity()));

            // 4. Saltar al estado actual para evitar la animación
            switchGpsSetting.jumpDrawablesToCurrentState();

            // 5. Volver a habilitar el listener
            switchGpsSetting.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // se pasa a TRUE el switch de movimiento
                GetSettings.setStatusSwitch("gps", isChecked, requireActivity());
            });
        });
        /*----------------------------------------------------------------------------------------*/
        // Inicializa del Spinner
        Spinner spinnerFrequencyGPSConfiguration = view.findViewById(R.id.spn_frequency_options_gps);

        // Obtén el array de opciones desde los recursos
        String[] optionsFrequencyArray = getResources().getStringArray(R.array.text_frequency_gps_options);

        // Crea un ArrayAdapter usando el array de opciones y un diseño simple para el spinner
        ArrayAdapter<String> adapterFrequency = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, optionsFrequencyArray);

        // Especifica el diseño a utilizar cuando se despliega el spinner
        adapterFrequency.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Aplica el adapter al spinner
        spinnerFrequencyGPSConfiguration.setAdapter(adapterFrequency);

        // Recuperar la última selección guardada en SharedPreferences
        int selectedFrequencyPosition = GetSettings.getStatusSpinner("frecuency_gps", requireActivity()); // 0 es el valor por defecto (primera opción)
        // Seleccionar la opción guardada
        spinnerFrequencyGPSConfiguration.setSelection(selectedFrequencyPosition);


        // Guardar la selección del Spinner
        spinnerFrequencyGPSConfiguration.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                // Guardar la posición seleccionada en SharedPreferences
                GetSettings.setStatusSpinner("frecuency_gps", position, requireActivity());
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
        mViewModel = new ViewModelProvider(this).get(GPSViewModel.class);
        // TODO: Use the ViewModel
    }*/
    
}