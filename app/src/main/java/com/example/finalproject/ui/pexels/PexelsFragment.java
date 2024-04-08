package com.example.finalproject.ui.pexels;

import androidx.fragment.app.FragmentManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.finalproject.R;
import com.example.finalproject.data.pexels.Photo;
import com.example.finalproject.data.pexels.PhotoDao;
import com.example.finalproject.data.pexels.PhotoDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * The PexelsFragment class represents a fragment for displaying photos from the Pexels API.
 * It extends Fragment to provide a portion of user interface in an Activity.
 */
public class PexelsFragment extends Fragment {

    private EditText editTextSearch; // EditText for entering search term
    private Button buttonSearch; // Button for initiating search
    private Button buttonShowSaved; // Button for displaying saved photos
    private RecyclerView recyclerViewPhotos; // RecyclerView for displaying photos
    private List<Photo> photoList; // List of photos
    private PhotoAdapter photoAdapter; // Adapter for RecyclerView
    private PhotoDao photoDao; // Data Access Object for Photo entity

    // SharedPreferences file name
    private static final String PREFS_NAME = "MyPrefs";
    // SharedPreferences key for search term
    private static final String SEARCH_TERM_KEY = "searchTerm";

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pexels, container, false);

        // Set toolbar menu
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);

        // Set menu item click listener
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Handle menu item clicks
                if (item.getItemId() == R.id.action_settings) {
                    // Show AlertDialog
                    showAlertDialog();
                    return true;
                }
                return false;
            }
        });

        // Set author and version information
        TextView textViewAuthor = getActivity().findViewById(R.id.textViewAuthor);
        TextView textViewNameVer = getActivity().findViewById(R.id.textViewNameVer);
        textViewAuthor.setText("Mohammad");
        textViewNameVer.setText("Pexels@ Version: 1.0");

        // Initialize views
        editTextSearch = view.findViewById(R.id.editTextSearch);
        buttonSearch = view.findViewById(R.id.buttonSearch);
        recyclerViewPhotos = view.findViewById(R.id.recyclerViewPhotos);
        buttonShowSaved = view.findViewById(R.id.buttonShowSaved);

        // Initialize Room database and DAO
        PhotoDatabase db = Room.databaseBuilder(getContext(), PhotoDatabase.class, "pexels").build();
        photoDao = db.photoDAO();

        // Initialize FragmentManager
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

        // Initialize photo list and adapter
        photoList = new ArrayList<>();
        photoAdapter = new PhotoAdapter(photoList, fragmentManager);
        recyclerViewPhotos.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewPhotos.setAdapter(photoAdapter);

        // Handle search button click
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchTerm = editTextSearch.getText().toString();
                // Save search term to SharedPreferences
                saveSearchTerm(searchTerm);
                makeApiRequest(searchTerm);
            }
        });

        // Handle show saved button click
        buttonShowSaved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoList.clear();
                Executor thread = Executors.newSingleThreadExecutor();
                thread.execute(() -> {
                    photoList.addAll(photoDao.getAllPhotos());
                    getActivity().runOnUiThread(() -> recyclerViewPhotos.setAdapter(photoAdapter));
                });
            }
        });

        // Retrieve and display the saved search term
        String savedSearchTerm = getSavedSearchTerm();
        if (savedSearchTerm != null) {
            editTextSearch.setText(savedSearchTerm);
        }

        return view;
    }

    // Method to show AlertDialog
    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("How To Use")
                .setMessage("1: To get photos list first type search term and click on search button\n" +
                        "2: To see the saved photo, click on Show Saved button\n" +
                        "3: To see more details click on the photo\n" +
                        "4: You also have options to save and remove the photo in detailed view")
                .setPositiveButton("OK", null) // You can add buttons and listeners as needed
                .show();
    }

    // Method to save search term to SharedPreferences
    private void saveSearchTerm(String searchTerm) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SEARCH_TERM_KEY, searchTerm);
        editor.apply();
    }

    // Method to retrieve saved search term from SharedPreferences
    private String getSavedSearchTerm() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(SEARCH_TERM_KEY, null);
    }

    // Method to make API request to Pexels for photos
    private void makeApiRequest(String searchTerm) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        // Define the URL with the search term.
        String url = "https://api.pexels.com/v1/search?query=" + searchTerm;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Parse the JSON response and update the photoList.
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray photosArray = jsonResponse.getJSONArray("photos");
                            photoList.clear(); // Clear previous results
                            for (int i = 0; i < photosArray.length(); i++) {
                                JSONObject photoObject = photosArray.getJSONObject(i);
                                Photo photo = new Photo(photoObject);
                                photoList.add(photo);
                            }
                            // Notify adapter of changes
                            photoAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle errors
                Toast.makeText(requireContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // Set headers, including Authorization header with your API key
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "HylTNwr7lJv53y3ocDv9c4CWSZKxROwR7opQJJwM0tKxlsF0kjDpFei4");
                return headers;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
