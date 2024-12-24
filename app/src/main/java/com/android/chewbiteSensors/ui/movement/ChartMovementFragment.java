package com.android.chewbiteSensors.ui.movement;

import android.graphics.Color;
import android.hardware.Sensor;
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
import com.android.chewbiteSensors.data_sensors.TestSensorsEventListener;
import com.android.chewbiteSensors.databinding.FragmentChartMovementBinding;
import com.github.mikephil.charting.charts.LineChart;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChartMovementFragment extends Fragment {

    private ChartMovementViewModel mViewModel;
    private FragmentChartMovementBinding binding;
    private View root;
    private List<CheckBox> filesCheckBoxList;
    public static String TEST_DATA_STRING = "testData";
    private ExperimentData data;
    private TestSensorsEventListener testSensorsEventListenerAccelerometer ;
    private TestSensorsEventListener testSensorsEventListenerGyroscope;
    private TestSensorsEventListener testSensorsEventListenerMagnetometer;
    private AppMode mode;

    public static ChartMovementFragment newInstance() {
        return new ChartMovementFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_chart_movement, container, false);
        binding = FragmentChartMovementBinding.inflate(inflater, container, false);
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
                //File[] testFiles = CBSensorEventListener.INSTANCE.getTestFiles(data.getTimestamp());
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
        mViewModel = new ViewModelProvider(this).get(ChartMovementViewModel.class);
        // TODO: Use the ViewModel
    }*/

    /*----------------------------------------------------------------------------------------*/

    /**
     * Crea y mostra un gráfico de líneas dentro de una tabla
     */
    private void showTestChart() {
        // Busca un elemento TableLayout en tu layout XML con el ID filesTableLayout y lo asigna a la variable filesTableLayout. TableLayout es un contenedor que organiza sus elementos hijos en filas y columnas.
        TableLayout filesTableLayoutAccelerometer = root.findViewById(R.id.filesTableLayoutAccelerometer);
        TableLayout filesTableLayoutGyroscope = root.findViewById(R.id.filesTableLayoutGyroscope);
        TableLayout filesTableLayoutMagnetometer = root.findViewById(R.id.filesTableLayoutMagnetometer);
        // Crea una nueva instancia de un objeto LineChart. Este objeto representará el gráfico de líneas.
        LineChart chart_Accelerometer = new LineChart(requireContext());
        LineChart chart_Gyroscope = new LineChart(requireContext());
        LineChart chart_Magnetometer = new LineChart(requireContext());
        // Define los parámetros de diseño para el gráfico de líneas. En este caso, se establece que
        // el gráfico ocupe todo el ancho y alto disponible dentro de la celda de la tabla donde se va a colocar.
        // ViewGroup.LayoutParams.MATCH_PARENT indica que la vista debe expandirse para coincidir con el tamaño de su elemento padre.
        chart_Accelerometer.setLayoutParams(new TableLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        );
        chart_Gyroscope.setLayoutParams(new TableLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        );
        chart_Magnetometer.setLayoutParams(new TableLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        );
        // Agrega el gráfico de líneas a la tabla
        filesTableLayoutAccelerometer.addView(chart_Accelerometer);
        filesTableLayoutGyroscope.addView(chart_Gyroscope);
        filesTableLayoutMagnetometer.addView(chart_Magnetometer);

        // Crea una nueva instancia de un TestSensorsEventListener. Este listener se encargue de recibir datos de los sensores y actualizar el gráfico de líneas con esos datos.
        this.testSensorsEventListenerAccelerometer = new TestSensorsEventListener(requireContext(), chart_Accelerometer, Sensor.TYPE_ACCELEROMETER, CBBuffer.STRING_ACCELEROMETER,  Color.RED);
        this.testSensorsEventListenerGyroscope = new TestSensorsEventListener(requireContext(), chart_Gyroscope, Sensor.TYPE_GYROSCOPE, CBBuffer.STRING_GYROSCOPE, Color.GREEN);
        this.testSensorsEventListenerMagnetometer = new TestSensorsEventListener(requireContext(), chart_Magnetometer, Sensor.TYPE_MAGNETIC_FIELD, CBBuffer.STRING_MAGNETIC_FIELD, Color.BLUE);
    }
    /*----------------------------------------------------------------------------------------*/



    @Override
    public void onPause() {
        super.onPause();
        // Limpia los recursos asociados al gráfico de líneas
        if (this.testSensorsEventListenerAccelerometer != null) {
            this.testSensorsEventListenerAccelerometer.stop();
            this.testSensorsEventListenerAccelerometer = null;
        }
        if (this.testSensorsEventListenerGyroscope != null) {
            this.testSensorsEventListenerGyroscope.stop();
            this.testSensorsEventListenerGyroscope = null;
        }
        if (this.testSensorsEventListenerMagnetometer != null) {
            this.testSensorsEventListenerMagnetometer.stop();
            this.testSensorsEventListenerMagnetometer = null;
        }
        // Limpia los recursos asociados al binding
        binding = null;

    }
}