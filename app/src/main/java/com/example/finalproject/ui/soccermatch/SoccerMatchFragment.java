package com.example.finalproject.ui.soccermatch;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.app.AlertDialog;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.finalproject.R;
import com.example.finalproject.data.soccermatch.Match;
import com.example.finalproject.data.soccermatch.MatchDao;
import com.example.finalproject.data.soccermatch.MatchDatabase;

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
 * A fragment to display soccer matches and perform related actions.
 */
public class SoccerMatchFragment extends Fragment {

    // UI elements
    private Button buttonSearch; // Button to initiate match search
    private RecyclerView recyclerViewMatches; // RecyclerView to display matches
    private List<Match> matchList; // List to hold retrieved matches
    private MatchAdapter matchAdapter; // Adapter for RecyclerView

    private Button buttonShowSaved; // Button to show saved matches

    private MatchDao matchDAO; // Data Access Object for Match entities

    // SharedPreferences file name
    private static final String PREFS_NAME = "MyMatch";
    // SharedPreferences key for search term
    private static final String SEARCH_TERM_KEY = "searchTerm";

    /**
     * Called to create the view hierarchy associated with the fragment.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_soccer_match, container, false);

        // Set up toolbar menu
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);

        // Set up menu item click listener
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Handle menu item clicks
                if (item.getItemId() == R.id.action_settings) {
                    // Show settings dialog
                    showAlertDialog();
                    return true;
                }
                return false;
            }
        });

        // Set author and version info
        TextView textViewAuthor = getActivity().findViewById(R.id.textViewAuthor);
        TextView textViewNameVer = getActivity().findViewById(R.id.textViewNameVer);
        textViewAuthor.setText("Manjot");
        textViewNameVer.setText("SoccerMatch Highlights@ Version: 1.0");

        // Initialize UI elements
        buttonSearch = view.findViewById(R.id.buttonSearch);
        recyclerViewMatches = view.findViewById(R.id.recyclerViewMatches);
        buttonShowSaved = view.findViewById(R.id.buttonShowSaved);

        // Initialize database
        MatchDatabase db = Room.databaseBuilder(getContext(),
                MatchDatabase.class, "match").build();
        matchDAO = db.matchDAO();

        // Initialize FragmentManager
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

        // Initialize match list and adapter for RecyclerView
        matchList = new ArrayList<>();
        matchAdapter = new MatchAdapter(matchList, fragmentManager);
        recyclerViewMatches.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewMatches.setAdapter(matchAdapter);

        // Set button click listeners
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform API request
                makeApiRequest();
            }
        });

        buttonShowSaved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear match list and fetch saved matches
                matchList.clear();
                Executor thread = Executors.newSingleThreadExecutor();
                thread.execute(() -> {
                    matchList.addAll(matchDAO.getAllMatches());
                    // Update UI on UI thread
                    getActivity().runOnUiThread(() -> recyclerViewMatches.setAdapter(matchAdapter));
                });
            }
        });

        // Return the inflated view
        return view;
    }

    /**
     * Method to show an AlertDialog explaining how to use the app.
     */
    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("How To Use")
                .setMessage("1: To get matches list first type search term and click on search button\n" +
                        "2: To see the saved favourite matches, click on Show Saved button\n" +
                        "3: To see more details click on the match row\n" +
                        "4: You also have options to save and remove the match in detailed view")
                .setPositiveButton("OK", null) // You can add buttons and listeners as needed
                .show();
    }

    /**
     * Method to make an API request to fetch soccer matches.
     * Uses Volley library for network requests.
     */
    private void makeApiRequest() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        // Define the URL with the search term.
        String url = "https://www.scorebat.com/video-api/v1/";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Parse the JSON response and update the matchList.
                        try {
                            JSONArray matchesArray = new JSONArray(response);
                            matchList.clear(); // Clear previous results
                            for (int i = 0; i < matchesArray.length(); i++) {
                                JSONObject matchObject = matchesArray.getJSONObject(i);
                                Match match = new Match(matchObject);
                                matchList.add(match);
                            }
                            // Notify adapter of changes
                            matchAdapter.notifyDataSetChanged();
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