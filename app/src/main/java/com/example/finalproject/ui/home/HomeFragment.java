package com.example.finalproject.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.finalproject.R;
import com.example.finalproject.databinding.FragmentHomeBinding;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * The HomeFragment class represents the home screen fragment of the application.
 * It extends Fragment to provide a portion of user interface in an Activity.
 */
public class HomeFragment extends Fragment {

    private boolean firstTime = true; // Flag to track if it's the first time fragment is created

    private FragmentHomeBinding binding; // Binding for the home fragment layout

    /**
     * Called to have the fragment instantiate its user interface view. This is where
     * you inflate a layout XML file and return the View hierarchy. You can also perform
     * initialization of components here.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to. The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from
     *                           a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Create HomeViewModel instance
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        // Find TextViews from activity layout
        TextView textViewAuthor = getActivity().findViewById(R.id.textViewAuthor);
        TextView textViewNameVer = getActivity().findViewById(R.id.textViewNameVer);

        // Update TextViews if not the first time fragment is created
        if (!firstTime) {
            textViewAuthor.setText("Android Studio");
            textViewNameVer.setText("android.studio@android.com");
        }

        // Set firstTime flag to false
        firstTime = false;

        // Inflate the layout using ViewBinding
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Set up TextView with ViewModel data
        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }

    /**
     * Called when the view previously created by onCreateView has been detached from the fragment.
     * The next time the fragment needs to be displayed, a new view will be created.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Nullify binding to avoid memory leaks
    }
}
