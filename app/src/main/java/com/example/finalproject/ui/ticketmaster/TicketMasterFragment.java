package com.example.finalproject.ui.ticketmaster;

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
import com.example.finalproject.data.ticketmaster.Event;
import com.example.finalproject.data.ticketmaster.EventDao;
import com.example.finalproject.data.ticketmaster.EventDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Fragment to display events retrieved from Ticket Master API.
 */
public class TicketMasterFragment extends Fragment {

    // EditText for entering the search term
    private EditText editTextSearch;

    // EditText for entering the search radius
    private EditText editTextRadius;

    // Button for initiating the search
    private Button buttonSearch;

    // RecyclerView for displaying the list of events
    private RecyclerView recyclerViewEvents;

    // List to hold the events retrieved from the API
    private List<Event> eventList;

    // Adapter for populating the RecyclerView with events
    private EventAdapter eventAdapter;

    // Button for displaying saved events
    private Button buttonShowSaved;


    // DAO object
    private EventDao eventDao;

    // SharedPreferences file name
    private static final String PREFS_NAME = "MyEvents";
    // SharedPreferences key for search term
    private static final String SEARCH_TERM_KEY = "searchTerm";

    /**
     * Overrides the onCreateView method to initialize and set up the fragment's layout.
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return The View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ticket_master, container, false);

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
        textViewAuthor.setText("Harman");
        textViewNameVer.setText("Ticket Master@ Version: 1.0");

        // Initialize UI elements
        editTextSearch = view.findViewById(R.id.editTextSearch);
        editTextRadius = view.findViewById(R.id.editTextRadius);
        buttonSearch = view.findViewById(R.id.buttonSearch);
        recyclerViewEvents = view.findViewById(R.id.recyclerViewEvents);
        buttonShowSaved = view.findViewById(R.id.buttonShowSaved);

        // Initialize database and DAO
        EventDatabase db = Room.databaseBuilder(getContext(),
                EventDatabase.class, "events").build();
        eventDao = db.eventDao();

        // Initialize FragmentManager
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

        // Initialize event list and adapter
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(eventList, fragmentManager);
        recyclerViewEvents.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewEvents.setAdapter(eventAdapter);

        // Handle search button click
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchTerm = editTextSearch.getText().toString();
                String radius = editTextRadius.getText().toString();
                // Save search term to SharedPreferences
                saveSearchTerm(searchTerm);
                makeApiRequest(searchTerm, radius);
            }
        });

        // Handle show saved button click
        buttonShowSaved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventList.clear();
                Executor thread = Executors.newSingleThreadExecutor();
                thread.execute(() -> {
                    eventList.addAll(eventDao.getAllEvents());
                    getActivity().runOnUiThread(() -> recyclerViewEvents.setAdapter(eventAdapter));
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
     * Method to show AlertDialog explaining how to use the app.
     */
    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("How To Use")
                .setMessage("1: To get event list first type search term and click on search button\n" +
                        "2: To see the saved event, click on Show Saved button\n" +
                        "3: To see more details click on the event row\n" +
                        "4: You also have options to save and remove the event in detailed view")
                .setPositiveButton("OK", null) // You can add buttons and listeners as needed
                .show();
    }

    /**
     * Method to save the search term to SharedPreferences.
     * @param searchTerm The search term to be saved.
     */
    private void saveSearchTerm(String searchTerm) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SEARCH_TERM_KEY, searchTerm);
        editor.apply();
    }

    /**
     * Method to retrieve the saved search term from SharedPreferences.
     * @return The saved search term, or null if not found.
     */
    private String getSavedSearchTerm() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(SEARCH_TERM_KEY, null);
    }

    /**
     * Method to make an API request to Ticket Master API.
     * @param searchTerm The search term used for the API request.
     * @param radius The search radius used for the API request.
     */
    private void makeApiRequest(String searchTerm, String radius) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        // Define the URL with the search term.
        String url;
        try {
            url = "https://app.ticketmaster.com/discovery/v2/events.json?apikey=Rzk9LAGNqRN0q0Uo1GJbsLn0snbt3a7k&city=" + URLEncoder.encode(searchTerm, "UTF-8") + "&radius=" + radius;
            System.out.println(url);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Parse the JSON response and update the eventList.
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONObject _embedded = jsonResponse.getJSONObject("_embedded");
                            JSONArray eventsArray = _embedded.getJSONArray("events");
                            eventList.clear(); // Clear previous results
                            for (int i = 0; i < eventsArray.length(); i++) {
                                JSONObject eventObject = eventsArray.getJSONObject(i);
                                Event event = new Event(eventObject);
                                eventList.add(event);
                            }
                            // Notify adapter of changes
                            eventAdapter.notifyDataSetChanged();
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
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}