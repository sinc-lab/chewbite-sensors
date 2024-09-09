package com.android.chewbiteSensors.deviceStatus;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.NonNull;

public class ControlsStatusSettings {
        private static final String PREFS_KEY = "status_controls";
        private static SharedPreferences prefsConfig;

        public static void inicializarSwitch(@NonNull Context context,
                                             @SuppressLint("UseSwitchCompatOrMaterialCode") @NonNull Switch switchControl,
                                             String key, boolean defaultValue) {
            prefsConfig = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
            boolean status = prefsConfig.getBoolean(key, defaultValue);
            switchControl.setChecked(status);
            switchControl.setOnCheckedChangeListener((buttonView, isChecked) -> {
                SharedPreferences.Editor editor = prefsConfig.edit();
                editor.putBoolean(key, isChecked);
                editor.apply();
            });
        }

        public static void inicializarSpinner(@NonNull Context context, @NonNull Spinner spinnerControl, String[] opciones, String key, int defaultValue) {
            prefsConfig = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(spinnerControl.getContext(), android.R.layout.simple_spinner_item, opciones);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerControl.setAdapter(adapter);
            int seleccionado = prefsConfig.getInt(key, defaultValue);
            spinnerControl.setSelection(seleccionado);
            spinnerControl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    SharedPreferences.Editor editor = prefsConfig.edit();
                    editor.putInt(key, position);
                    editor.apply();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // No se necesita implementar
                }
            });
        }

        public static boolean getSwitchEstado(Context context, String key) {
            prefsConfig = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
            return prefsConfig.getBoolean(key, false);
        }

        public static int getSpinnerSeleccionado(Context context, String key) {
            prefsConfig = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
            return prefsConfig.getInt(key, 0);
        }
    }
