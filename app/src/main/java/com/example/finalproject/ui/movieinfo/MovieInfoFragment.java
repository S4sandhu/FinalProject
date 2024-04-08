package com.example.finalproject.ui.movieinfo;

import androidx.appcompat.widget.Toolbar;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.finalproject.R;
import com.example.finalproject.data.movieinfo.Movie;
import com.example.finalproject.data.movieinfo.MovieDao;
import com.example.finalproject.data.movieinfo.MovieDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * The MovieInfoFragment class represents a fragment for displaying movie information and performing related actions.
 * It extends Fragment to provide a portion of user interface in an Activity.
 */
public class MovieInfoFragment extends Fragment {

    private EditText editTextSearch; // EditText for entering search term
    private Button buttonSearch; // Button for initiating search
    private Button buttonShowSaved; // Button for displaying saved movies
    private RecyclerView recyclerViewMovies; // RecyclerView for displaying movies
    private List<Movie> movieList; // List to hold movies
    private MovieAdapter movieAdapter; // Adapter for RecyclerView
    private MovieDao movieDao; // Data Access Object for Movie entity

    // SharedPreferences file name
    private static final String PREFS_NAME = "MoviesTerms";
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
        View view = inflater.inflate(R.layout.fragment_movie_info, container, false);

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

        // Initialize Room database and DAO
        MovieDatabase db = Room.databaseBuilder(getContext(), MovieDatabase.class, "movie").build();
        movieDao = db.movieDAO();

        // Set author and version information
        TextView textViewAuthor = getActivity().findViewById(R.id.textViewAuthor);
        TextView textViewNameVer = getActivity().findViewById(R.id.textViewNameVer);
        textViewAuthor.setText("Ashpreet");
        textViewNameVer.setText("MovieInfo@ Version: 1.0");

        // Initialize views
        editTextSearch = view.findViewById(R.id.editTextSearch);
        buttonSearch = view.findViewById(R.id.buttonSearch);
        recyclerViewMovies = view.findViewById(R.id.recyclerViewMovies);
        buttonShowSaved = view.findViewById(R.id.buttonShowSaved);

        // Initialize FragmentManager
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

        // Initialize movie list and adapter
        movieList = new ArrayList<>();
        movieAdapter = new MovieAdapter(movieList, fragmentManager);
        recyclerViewMovies.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewMovies.setAdapter(movieAdapter);

        // Set onClickListener for search button
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchTerm = editTextSearch.getText().toString();
                // Save search term to SharedPreferences
                saveSearchTerm(searchTerm);
                // Make API request with search term
                makeApiRequest(searchTerm);
            }
        });

        // Set onClickListener for "Show Saved" button
        buttonShowSaved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear movie list
                movieList.clear();
                // Fetch saved movies from database and update the list
                Executor thread = Executors.newSingleThreadExecutor();
                thread.execute(() -> {
                    movieList.addAll(movieDao.getAllMovies());
                    getActivity().runOnUiThread(() -> recyclerViewMovies.setAdapter(movieAdapter));
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

    /**
     * Method to show AlertDialog with usage instructions.
     */
    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("How To Use")
                .setMessage("1: To get movies list, first type search term and click on search button\n" +
                        "2: To see the saved movies, click on 'Show Saved' button\n" +
                        "3: To see more details, click on a movie row\n" +
                        "4: You also have options to save and remove the movie in detailed view")
                .setPositiveButton("OK", null) // You can add buttons and listeners as needed
                .show();
    }

    /**
     * Method to save search term to SharedPreferences.
     *
     * @param searchTerm The search term to be saved.
     */
    private void saveSearchTerm(String searchTerm) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SEARCH_TERM_KEY, searchTerm);
        editor.apply();
    }

    /**
     * Method to retrieve saved search term from SharedPreferences.
     *
     * @return The saved search term, or null if not found.
     */
    private String getSavedSearchTerm() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(SEARCH_TERM_KEY, null);
    }

    /**
     * Method to make API request with the provided search term.
     *
     * @param searchTerm The search term to be used in the API request.
     */
    private void makeApiRequest(String searchTerm) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        // Define the URL with the search term.
        String url;
        try {
            url = "https://www.omdbapi.com/?apikey=81b1f826&t=" + URLEncoder.encode(searchTerm, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Parse the JSON response and update the movieList.
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            Movie movie = new Movie(jsonResponse);
                            movieList.add(movie);
                            // Notify adapter of changes
                            movieAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle errors
                Toast.makeText(requireContext(), "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
