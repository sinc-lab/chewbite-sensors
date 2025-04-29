package com.android.chewbiteSensors.ui.sensorsChart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.android.chewbiteSensors.R;
import com.android.chewbiteSensors.databinding.FragmentSensorsChartBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SensorsChartFragment extends Fragment {

    private SensorsChartViewModel mViewModel;
    private FragmentSensorsChartBinding binding; // Atributo para el binding

    public static SensorsChartFragment newInstance() {
        return new SensorsChartFragment();
    }

    /*@Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sensors_chart, container, false);
    }*/

    /**
     * Método modificado para agregar BottomNavigationView
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sensors_chart, container, false);

        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottom_navigation_view_sensors_chart);
        NavHostFragment navHostFragment = (NavHostFragment) getChildFragmentManager().findFragmentById(R.id.nav_host_fragment_sensors_chart_main);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(bottomNavigationView, navController);
        }

        return view;
    }

    /*@Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SensorsChartViewModel.class);
        // TODO: Use the ViewModel
    }*/

}