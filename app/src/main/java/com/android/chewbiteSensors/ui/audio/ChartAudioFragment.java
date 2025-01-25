package com.android.chewbiteSensors.ui.audio;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.chewbiteSensors.R;
import com.android.chewbiteSensors.data_sensors.AppMode;
import com.android.chewbiteSensors.data_sensors.CBSensorEventListener;
import com.android.chewbiteSensors.data_sensors.ExperimentData;
import com.android.chewbiteSensors.data_sensors.TestSensorsEventListenerAudio;
import com.android.chewbiteSensors.data_sensors.TestSensorsEventListenerSound;
import com.android.chewbiteSensors.databinding.FragmentChartAudioBinding;

import java.util.ArrayList;
import java.util.List;

public class ChartAudioFragment extends Fragment {

    private ChartAudioViewModel mViewModel;
    private FragmentChartAudioBinding binding;
    private View root;
    private List<CheckBox> filesCheckBoxList;
    public static String TEST_DATA_STRING = "testData";
    private ExperimentData data;
    private TestSensorsEventListenerSound testSensorsEventListenerSound;
    private TestSensorsEventListenerAudio testSensorsEventListenerAudio;
    private AppMode mode;
    private TextView messageTextViewChartAudio;


    public static ChartAudioFragment newInstance() {
        return new ChartAudioFragment();
    }


    @SuppressLint("WrongViewCast")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // return inflater.inflate(R.layout.fragment_chart_audio, container, false);
        binding = FragmentChartAudioBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        audioWaveformView = root.findViewById(R.id.audio_waveform_surface);
        messageTextViewChartAudio = root.findViewById(R.id.messageTextViewChartAudio); // Referencia al TextView
        /*----------------------------------------------------------------------------------------*/
        // Restore views
        this.filesCheckBoxList = new ArrayList<>();
        if (savedInstanceState != null && savedInstanceState.containsKey(TEST_DATA_STRING)) {
            this.data = (ExperimentData) savedInstanceState.getSerializable(TEST_DATA_STRING);
            CBSensorEventListener.INSTANCE.setExperimentData(this.data);
            if (this.mode == AppMode.STOPPED) {
                // Show files
                assert data != null;
            }
        } else {
            this.startAudioRecording();
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
    /*private void showTestChart() {
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
    }*/
    /*----------------------------------------------------------------------------------------*/

    /*private void showTestChart_1() {
        // Busca un elemento TableLayout en tu layout XML con el ID filesTableLayoutSound
        //TableLayout filesTableLayoutAudio = root.findViewById(R.id.filesTableLayoutAudio);

        // Crea una nueva instancia de un objeto LineChart para mostrar la onda de audio
        LineChart chart_Audio = new LineChart(requireContext());

        // Configura los parámetros de diseño para el gráfico de líneas
        chart_Audio.setLayoutParams(new TableLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        );

        // Agrega el gráfico de líneas a la tabla
        filesTableLayoutAudio.addView(chart_Audio);

        // Invoca el AudioWaveformListener para empezar a registrar los datos de audio y graficar la onda
        this.testSensorsEventListenerAudio = new TestSensorsEventListenerAudio(requireContext(), chart_Audio, "Waveform Audio", Color.BLUE);

        // Inicia la grabación de audio para que comience a graficar
        this.testSensorsEventListenerAudio.startRecording();
    }*/


    /*------------------------ GRÁFICO DE ONDAS DE AUDIO  --------------------------------------*/
    private static final int REQUEST_MIC_PERMISSION = 1;
    private AudioWaveformView audioWaveformView;
    private AudioRecord audioRecord;
    private boolean isRecording = false;

    private static final int SAMPLE_RATE = 44100;
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, android.media.AudioFormat.CHANNEL_IN_MONO, android.media.AudioFormat.ENCODING_PCM_16BIT);

    private void startAudioRecording() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            showMessage("Se requieren permisos de microfono para mostrar el gráfico de ondas de audio.");

            return;
        }


        // Reinicia el gráfico
        audioWaveformView.clearAmplitudes();

        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, android.media.AudioFormat.CHANNEL_IN_MONO, android.media.AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE);

        audioRecord.startRecording();
        isRecording = true;

        new Thread(() -> {
            short[] buffer = new short[BUFFER_SIZE];
            while (isRecording) {
                int read = audioRecord.read(buffer, 0, buffer.length);
                if (read > 0) {
                    float amplitude = 0;
                    for (int i = 0; i < read; i++) {
                        amplitude += Math.abs(buffer[i]);
                    }
                    amplitude /= read;
                    audioWaveformView.addAmplitude(amplitude);
                }
                try {
                    Thread.sleep(16); // ~60 FPS
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void stopAudioRecording() {
        isRecording = false;
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
        if (audioWaveformView != null) {
            // Reinicia el gráfico
            audioWaveformView.clearAmplitudes();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!isRecording) {
            this.startAudioRecording();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isRecording) {
            this.startAudioRecording();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopAudioRecording();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopAudioRecording();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopAudioRecording();
    }

    /*------------------------ GRÁFICO DE ONDAS DE AUDIO  --------------------------------------*/

    private void showMessage(String message) {
        messageTextViewChartAudio.setText(message);
        messageTextViewChartAudio.setVisibility(View.VISIBLE); // Mostrar el mensaje
        audioWaveformView.setVisibility(View.GONE);  // Ocultar el mapa
    }

}