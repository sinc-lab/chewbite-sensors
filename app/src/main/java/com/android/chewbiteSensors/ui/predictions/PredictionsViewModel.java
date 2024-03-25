package com.android.chewbiteSensors.ui.predictions;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PredictionsViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public PredictionsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is predictions fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}