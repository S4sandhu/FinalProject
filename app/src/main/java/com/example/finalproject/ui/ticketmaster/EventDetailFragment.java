package com.example.finalproject.ui.ticketmaster;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.example.finalproject.R;
import com.example.finalproject.data.ticketmaster.Event;
import com.example.finalproject.data.ticketmaster.EventDao;
import com.example.finalproject.data.ticketmaster.EventDatabase;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Fragment to display event details.
 */
public class EventDetailFragment extends Fragment {

    // TextView for displaying the title of the event
    TextView textViewTitle;

    // Button for saving the event
    Button buttonSave;

    // ImageView for displaying the promotional image of the event
    ImageView imageViewPromo;

    // TextView for displaying the price range of the event
    TextView textViewPriceRange;

    // TextView for displaying the starting date of the event
    TextView textViewStartingDate;

    // TextView for displaying the URL of the event
    TextView textViewUrl;

    // Button for removing the event
    Button buttonRemove;

    // Event object representing the current event
    Event event;

    // Data Access Object for handling event data
    EventDao eventDao;

    // Context object for the current context
    Context thiscontext;

    // RequestQueue for handling network requests
    protected RequestQueue queue = null;


    /**
     * Constructor for EventDetailFragment.
     * @param event The event object to display details.
     */
    protected EventDetailFragment(Event event){
        this.event = event;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_detail, container, false);

        thiscontext = getActivity().getApplicationContext();

        // Initialize database and DAO
        EventDatabase db = Room.databaseBuilder(thiscontext,
                EventDatabase.class, "events").build();
        eventDao = db.eventDao();

        // Initialize UI elements
        textViewTitle = view.findViewById(R.id.textViewTitle);
        imageViewPromo = view.findViewById(R.id.imageViewPromo);
        textViewStartingDate = view.findViewById(R.id.textViewDate);
        textViewPriceRange = view.findViewById(R.id.textViewPriceRange);
        textViewUrl = view.findViewById(R.id.textViewUrl);
        buttonSave = view.findViewById(R.id.buttonSave);
        buttonRemove = view.findViewById(R.id.buttonRemove);

        // Load event details into UI
        Picasso.get().load(event.getPromoImageUrl()).into(imageViewPromo);
        textViewTitle.setText(event.getName());
        textViewPriceRange.setText(String.format("Price Range: %s",event.getPriceRange()));
        textViewStartingDate.setText(String.format("Start Date: %s",event.getStartingDate()));
        textViewUrl.setText(String.format("URL: %s",event.getUrl()));

        // Handle URL click to open in web browser
        textViewUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String url = textViewUrl.getText().toString();
                openUrlInBrowser(event.getUrl());
            }
        });

        // Handle save button click
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEventToStorage();
            }
        });

        // Handle remove button click
        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show confirmation dialog for event deletion
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Do you want to delete.")
                        .setTitle("Question")
                        .setNegativeButton("No",(dialog, cl)->{})
                        .setPositiveButton("Yes",(dialog, cl)->{
                            // Delete event from database
                            Executor thread = Executors.newSingleThreadExecutor();
                            thread.execute(()-> eventDao.delete(event));
                            // Pop back stack to previous fragment
                            getActivity().getSupportFragmentManager().popBackStack();
                            // Show undo option with Snackbar
                            Snackbar.make(view, "You deleted it.",Snackbar.LENGTH_LONG)
                                    .setAction("Undo", click ->{
                                        // Insert event back to database
                                        thread.execute(()-> eventDao.insert(event));
                                    })
                                    .show();
                        }).create().show();
            }
        });

        // Initialize Volley request queue
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());

        return view;
    }

    /**
     * Opens the URL in a web browser.
     * @param url The URL to open.
     */
    private void openUrlInBrowser(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    /**
     * Saves the event details and promo image to internal storage.
     */
    private void saveEventToStorage() {

        // Get the directory for the app's private internal storage directory.
        File directory = requireContext().getFilesDir();

        // Generate a unique filename for the image.
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";

        String imageUrl = event.getPromoImageUrl();
        ImageRequest imgReq = new ImageRequest(imageUrl, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {
                try {
                    // Create a new file in the directory with the generated filename.
                    File file = new File(directory, imageFileName);
                    // Write the bitmap to the file.
                    FileOutputStream fos = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.close();

                    // Insert event into database
                    Executor thread = Executors.newSingleThreadExecutor();
                    thread.execute(()-> eventDao.insert(event));

                    // Notify the user that the image has been saved.
                    Toast.makeText(requireContext(), "Promo image saved to " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }, 2000, 2000, ImageView.ScaleType.CENTER, null, (error ) -> {
        });
        queue.add(imgReq);
    }
}