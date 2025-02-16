package com.android.chewbiteSensors.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    // LiveData para almacenar el texto (u otros datos)
    private final MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    // Método para obtener el LiveData que se observará en el Fragment
    public LiveData<String> getText() {
        return mText;
    }

    // Si necesitas actualizar el texto, puedes agregar un setter:
    public void setText(String text) {
        mText.setValue(text);
    }
}