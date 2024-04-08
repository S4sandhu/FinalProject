package com.example.finalproject.ui.pexels;

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

import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.example.finalproject.R;
import com.example.finalproject.data.pexels.Photo;
import com.example.finalproject.data.pexels.PhotoDao;
import com.example.finalproject.data.pexels.PhotoDatabase;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

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
 * The PexelsDetailFragment class represents a fragment displaying detailed information about a photo from Pexels.
 * It extends Fragment to provide a portion of user interface in an Activity.
 */
public class PexelsDetailFragment extends Fragment {

    private TextView textViewUrl; // TextView to display photo URL
    private Button buttonSave; // Button to save the photo
    private Button buttonRemove; // Button to remove the photo
    private ImageView imageViewPicture; // ImageView to display photo
    private TextView textViewDimensions; // TextView to display photo dimensions
    private Photo photo; // Photo object to display details
    private PhotoDao pDAO; // Data Access Object for Photo entity
    private Context thiscontext; // Context of the fragment
    protected RequestQueue queue = null; // RequestQueue for network requests

    /**
     * Constructs a new PexelsDetailFragment with the given photo.
     *
     * @param photo The photo object to display details.
     */
    protected PexelsDetailFragment(Photo photo) {
        this.photo = photo;
    }

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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pexels_detail, container, false);

        thiscontext = getActivity().getApplicationContext();

        // Initialize Room database and DAO
        PhotoDatabase db = Room.databaseBuilder(thiscontext, PhotoDatabase.class, "pexels").build();
        pDAO = db.photoDAO();

        // Initialize views
        imageViewPicture = view.findViewById(R.id.imageViewPoster);
        textViewUrl = view.findViewById(R.id.textViewUrl);
        textViewDimensions = view.findViewById(R.id.textViewDimensions);
        buttonSave = view.findViewById(R.id.buttonSave);
        buttonRemove = view.findViewById(R.id.buttonRemove);

        // Load and display photo details
        Picasso.get().load(photo.getImageUrl()).into(imageViewPicture);
        String dimensions = "Dimensions: " + photo.getWidth() + "x" + photo.getHeight();
        textViewUrl.setText(String.format("URL: %s",photo.getUrl()));
        textViewDimensions.setText(dimensions);

        // Handle URL click to open in web browser
        textViewUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String url = textViewUrl.getText().toString();
                openUrlInBrowser(photo.getUrl());
            }
        });

        // Handle save button click to save the image
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImageToStorage();
            }
        });

        // Handle remove button click to remove the photo
        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Display confirmation dialog for deletion
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Do you want to delete.")
                        .setTitle("Question")
                        .setNegativeButton("No", (dialog, cl) -> {})
                        .setPositiveButton("Yes", (dialog, cl) -> {
                            // Delete the photo from database
                            Executor thread = Executors.newSingleThreadExecutor();
                            thread.execute(() -> {
                                pDAO.delete(photo);
                                // Pop back stack to return to previous fragment
                                getActivity().getSupportFragmentManager().popBackStack();
                                // Show Snackbar for undo option
                                Snackbar.make(view, "You deleted it.", Snackbar.LENGTH_LONG)
                                        .setAction("Undo", click -> {
                                            thread.execute(() -> pDAO.insert(photo));
                                        })
                                        .show();
                            });
                        }).create().show();
            }
        });

        queue = Volley.newRequestQueue(getActivity().getApplicationContext());

        return view;
    }

    /**
     * Method to open the given URL in a web browser.
     *
     * @param url The URL to be opened.
     */
    private void openUrlInBrowser(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    /**
     * Method to save the photo to internal storage.
     */
    private void saveImageToStorage() {
        // Get the directory for the app's private internal storage directory.
        File directory = requireContext().getFilesDir();

        // Generate a unique filename for the image.
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";

        String imageUrl = photo.getImageUrl();
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

                    // Insert the photo to database
                    Executor thread = Executors.newSingleThreadExecutor();
                    thread.execute(() -> pDAO.insert(photo));

                    // Notify the user that the image has been saved.
                    Toast.makeText(requireContext(), "Image saved to " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }, photo.getWidth(), photo.getHeight(), ImageView.ScaleType.CENTER, null, (error) -> {
        });
        queue.add(imgReq);
    }
}
