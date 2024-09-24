package com.android.chewbiteSensors.ui.gps;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.chewbiteSensors.databinding.FragmentChartGpsBinding;

public class ChartGPSFragment extends Fragment {

    private ChartGPSViewModel mViewModel;
    private FragmentChartGpsBinding binding;

    public static ChartGPSFragment newInstance() {
        return new ChartGPSFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_chart_gps, container, false);
        binding = FragmentChartGpsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        return root;
    }

    /*@Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ChartGPSViewModel.class);
        // TODO: Use the ViewModel
    }*/

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}