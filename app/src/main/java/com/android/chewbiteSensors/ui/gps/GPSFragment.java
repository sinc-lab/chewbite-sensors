package com.android.chewbiteSensors.ui.gps;

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

public class GPSFragment extends Fragment {

    private GPSViewModel mViewModel;
    private static final String PREFS_KEY = "status_controls";
    private static final String STATUS_SWT_GPS_CONFIG = "status_switch_gps_configuration";
    private static boolean switchGpsState;
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
        switchGpsState = sharedPreferences.getBoolean(STATUS_SWT_GPS_CONFIG, true);

        // Configura el listener
        switchGpsSetting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(STATUS_SWT_GPS_CONFIG, isChecked);
                editor.apply();
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

    @Override
    public void onStart() {
        super.onStart();

        // Setear el estado del switch
        switchGpsSetting.setChecked(switchGpsState);
    }
}