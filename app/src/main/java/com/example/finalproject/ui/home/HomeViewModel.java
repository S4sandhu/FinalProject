package com.example.finalproject.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * The HomeViewModel class represents the ViewModel for the HomeFragment.
 * It provides data to the UI and survives configuration changes.
 */
public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText; // LiveData to hold text data

    /**
     * Constructs a new HomeViewModel instance.
     * Initializes LiveData with a default value.
     */
    public HomeViewModel() {
        // Initialize LiveData with default value
        mText = new MutableLiveData<>();
        mText.setValue("Android application in which\n\t1: Pexels\n\t2: Movie Information" +
                "\n\t3: Ticket Master\n\t4: Soccer match highlights\n are implemented");
    }

    /**
     * Retrieves LiveData containing text data.
     *
     * @return LiveData object containing text data.
     */
    public LiveData<String> getText() {
        return mText;
    }
}
