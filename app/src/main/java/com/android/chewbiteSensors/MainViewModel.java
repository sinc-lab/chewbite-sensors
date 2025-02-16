package com.android.chewbiteSensors;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {
    // LiveData para almacenar la ruta del experimento
    private final MutableLiveData<String> experimentPath = new MutableLiveData<>();

    public LiveData<String> getExperimentPath() {
        return experimentPath;
    }

    public void setExperimentPath(String path) {
        experimentPath.setValue(path);
    }
}
