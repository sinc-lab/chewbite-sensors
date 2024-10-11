package com.android.chewbiteSensors.ui.audio;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TableLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.chewbiteSensors.R;
import com.android.chewbiteSensors.data_sensors.AppMode;
import com.android.chewbiteSensors.data_sensors.CBBuffer;
import com.android.chewbiteSensors.data_sensors.CBSensorEventListener;
import com.android.chewbiteSensors.data_sensors.ExperimentData;
import com.android.chewbiteSensors.data_sensors.TestSensorsEventListenerSound;
import com.android.chewbiteSensors.databinding.FragmentChartAudioBinding;
import com.github.mikephil.charting.charts.LineChart;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ChartAudioFragment extends Fragment {

    private ChartAudioViewModel mViewModel;
    private FragmentChartAudioBinding binding;
    private View root;
    private List<CheckBox> filesCheckBoxList;
    public static String TEST_DATA_STRING = "testData";
    private ExperimentData data;
    private TestSensorsEventListenerSound testSensorsEventListenerSound ;
    private AppMode mode;


    public static ChartAudioFragment newInstance() {
        return new ChartAudioFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // return inflater.inflate(R.layout.fragment_chart_audio, container, false);
        binding = FragmentChartAudioBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        /*----------------------------------------------------------------------------------------*/
        // Restore views
        this.filesCheckBoxList = new ArrayList<>();
        if (savedInstanceState != null && savedInstanceState.containsKey(TEST_DATA_STRING)) {
            this.data = (ExperimentData) savedInstanceState.getSerializable(TEST_DATA_STRING);
            CBSensorEventListener.INSTANCE.setExperimentData(this.data);
            if (this.mode == AppMode.STOPPED) {
                // Show files
                assert data != null;
                File[] testFiles = CBSensorEventListener.INSTANCE.getTestFiles(data.getTimestamp());
                // se comenta temporalmente
                //this.showGeneratedFiles(testFiles);
            }
        } else {
            this.showTestChart();
        }
        /*----------------------------------------------------------------------------------------*/

        return root;
    }

    /*@Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ChartAudioViewModel.class);
        // TODO: Use the ViewModel
    }*/
    /*----------------------------------------------------------------------------------------*/

    /**
     * Crea y mostra un gráfico de líneas dentro de una tabla
     */
    private void showTestChart() {
        // Busca un elemento TableLayout en tu layout XML con el ID filesTableLayout y lo asigna a la variable filesTableLayout. TableLayout es un contenedor que organiza sus elementos hijos en filas y columnas.
        TableLayout filesTableLayoutSound = root.findViewById(R.id.filesTableLayoutSound);
        // Crea una nueva instancia de un objeto LineChart. Este objeto representará el gráfico de líneas.
        LineChart chart_Sound = new LineChart(requireContext());
        // Define los parámetros de diseño para el gráfico de líneas. En este caso, se establece que
        // el gráfico ocupe todo el ancho y alto disponible dentro de la celda de la tabla donde se va a colocar.
        // ViewGroup.LayoutParams.MATCH_PARENT indica que la vista debe expandirse para coincidir con el tamaño de su elemento padre.
        chart_Sound.setLayoutParams(new TableLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        );

        // Agrega el gráfico de líneas a la tabla
        filesTableLayoutSound.addView(chart_Sound);

        // Crea una nueva instancia de un TestSensorsEventListener. Este listener se encargue de recibir datos de los sensores y actualizar el gráfico de líneas con esos datos.
        //this.testSensorsEventListenerSound = new TestSensorsEventListener(requireContext(), chart_Sound, Sensor.TYPE_ACCELEROMETER, CBBuffer.STRING_SOUND ,  Color.RED);
        this.testSensorsEventListenerSound = new TestSensorsEventListenerSound(requireContext(), chart_Sound, CBBuffer.STRING_SOUND, Color.RED);
    }
    /*----------------------------------------------------------------------------------------*/



    @Override
    public void onPause() {
        super.onPause();
        // Limpia los recursos asociados al gráfico de líneas
        if (testSensorsEventListenerSound != null) {
            testSensorsEventListenerSound.stopRecording();
        }
        // Limpia los recursos asociados al binding
        binding = null;

    }

    @Override
    public void onResume() {
        super.onResume();
        if (testSensorsEventListenerSound != null) {
            testSensorsEventListenerSound.startRecording();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}