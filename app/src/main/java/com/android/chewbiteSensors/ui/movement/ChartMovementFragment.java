package com.android.chewbiteSensors.ui.movement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.chewbiteSensors.databinding.FragmentChartMovementBinding;

public class ChartMovementFragment extends Fragment {

    private ChartMovementViewModel mViewModel;
    private FragmentChartMovementBinding binding;

    public static ChartMovementFragment newInstance() {
        return new ChartMovementFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_chart_movement, container, false);
        binding = FragmentChartMovementBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        return root;
    }

    /*@Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ChartMovementViewModel.class);
        // TODO: Use the ViewModel
    }*/

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}