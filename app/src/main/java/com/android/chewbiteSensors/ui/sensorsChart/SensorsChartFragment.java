package com.android.chewbiteSensors.ui.sensorsChart;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.chewbiteSensors.R;
import com.android.chewbiteSensors.databinding.FragmentSensorsChartBinding;

public class SensorsChartFragment extends Fragment {

    private SensorsChartViewModel mViewModel;
    private FragmentSensorsChartBinding binding; // Atributo para el binding

    public static SensorsChartFragment newInstance() {
        return new SensorsChartFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sensors_chart, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SensorsChartViewModel.class);
        // TODO: Use the ViewModel
    }

}