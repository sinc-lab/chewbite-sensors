package com.android.chewbiteSensors.data_sensors;

import android.annotation.SuppressLint;

import com.github.mikephil.charting.formatter.ValueFormatter;

public class TimeXAxisValueFormatter extends ValueFormatter {

    @SuppressLint("DefaultLocale")
    @Override
    public String getFormattedValue(float value) {
        // Convierte el valor en segundos
        int totalSeconds = (int) (value / 10000);

        // Calcula minutos y segundos
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        // Devuelve el valor formateado como MM:SS
        return String.format("%02d:%02d", minutes, seconds);
    }
}