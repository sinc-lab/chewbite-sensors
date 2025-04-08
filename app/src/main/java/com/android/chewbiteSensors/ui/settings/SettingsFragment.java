package com.android.chewbiteSensors.ui.settings;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.android.chewbiteSensors.R;
import com.android.chewbiteSensors.data_sensors.FileManager;
import com.android.chewbiteSensors.databinding.FragmentSettingsBinding;
import com.android.chewbiteSensors.settings.GetSettings;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingsFragment extends Fragment {

    private Button btnPathStorage;
    private SettingsViewModel mViewModel;
    private FragmentSettingsBinding binding;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    /* Método original
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }*/

    /**
     * Método modificado para agregar BottomNavigationView
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottom_navigation_view);
        NavHostFragment navHostFragment = (NavHostFragment) getChildFragmentManager().findFragmentById(R.id.nav_host_fragment_setting_main);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(bottomNavigationView, navController);
        }
        /*----------------------------------------------------------------------------------------*/
        // Inicializa del EditText
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        EditText editTextExperimentName = view.findViewById(R.id.et_name_experiment);
        // Se le configura el nombre del experimento
        editTextExperimentName.setText(GetSettings.getExperimentName(requireActivity()));

        editTextExperimentName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No es necesario implementar este método en este caso
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No es necesario implementar este método en este caso
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Guardar el texto en SharedPreferences
                String nameExperiment = s.toString();
                GetSettings.setExperimentName(requireActivity(), nameExperiment);
            }
        });
        /*----------------------------------------------------------------------------------------*/
        btnPathStorage = view.findViewById(R.id.btn_path_storage);
        btnPathStorage.setOnClickListener(v -> openDialog());
        /*----------------------------------------------------------------------------------------*/
        return view;
    }

    private void openDialog() {
        // Obtener la ruta de los archivos generados
        String baseDir = FileManager.getbaseDir().toString();
        String filePath = baseDir + "/" + GetSettings.getExperimentName(requireActivity());
        String message = "La ruta de almacenamiento de los archivos generados es: " + filePath;
        // Usar MaterialAlertDialogBuilder si tienes Material Components
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireContext());
        alertDialogBuilder.setTitle("Información");
        alertDialogBuilder.setMessage(message);

        // Icono opcional (recomendado para diálogos informativos)
        //alertDialogBuilder.setIcon(R.drawable.warning_blue); // Añade tu propio ícono

        alertDialogBuilder.setPositiveButton("Aceptar", (dialog, which) -> {
            // Acción al hacer clic en Aceptar
            dialog.dismiss(); // Cierra el diálogo
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /*@Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        // TODO: Use the ViewModel
    }*/
}