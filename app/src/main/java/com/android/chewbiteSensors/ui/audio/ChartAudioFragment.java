package com.android.chewbiteSensors.ui.audio;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.chewbiteSensors.databinding.FragmentChartAudioBinding;

public class ChartAudioFragment extends Fragment {

    private ChartAudioViewModel mViewModel;
    private FragmentChartAudioBinding binding;

    public static ChartAudioFragment newInstance() {
        return new ChartAudioFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // return inflater.inflate(R.layout.fragment_chart_audio, container, false);
        binding = FragmentChartAudioBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        return root;
    }

    /*@Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ChartAudioViewModel.class);
        // TODO: Use the ViewModel
    }*/

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}