package com.example.finalproject.ui.movieinfo;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
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
import com.example.finalproject.data.movieinfo.Movie;
import com.example.finalproject.data.movieinfo.MovieDao;
import com.example.finalproject.data.movieinfo.MovieDatabase;
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
 * The MovieDetailFragment class represents a fragment displaying detailed information about a movie.
 * It extends Fragment to provide a portion of user interface in an Activity.
 */
public class MovieDetailFragment extends Fragment {

    private Button buttonSave; // Button to save the movie
    private ImageView imageViewPoster; // ImageView to display movie poster
    private TextView textViewTitle; // TextView to display movie title
    private TextView textViewYear; // TextView to display movie release year
    private TextView textViewRating; // TextView to display movie rating
    private TextView textViewRuntime; // TextView to display movie runtime
    private TextView textViewMainActors; // TextView to display main actors in the movie
    private TextView textViewPlot; // TextView to display movie plot
    private Movie movie; // Movie object to display details
    private Button buttonRemove; // Button to remove the movie

    private MovieDao movieDao; // Data Access Object for Movie entity
    private Context thiscontext; // Context of the fragment

    protected RequestQueue queue = null;

    /**
     * Constructs a new MovieDetailFragment with the given movie.
     *
     * @param movie The movie object to display details.
     */
    protected MovieDetailFragment(Movie movie) {
        this.movie = movie;
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
        View view = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        thiscontext = getActivity().getApplicationContext();

        // Initialize Room database and DAO
        MovieDatabase db = Room.databaseBuilder(thiscontext,
                MovieDatabase.class, "movie").build();
        movieDao = db.movieDAO();

        // Initialize views
        imageViewPoster = view.findViewById(R.id.imageViewPoster);
        textViewTitle = view.findViewById(R.id.textViewTitle);
        textViewYear = view.findViewById(R.id.textViewYear);
        textViewRating = view.findViewById(R.id.textViewRating);
        textViewRuntime = view.findViewById(R.id.textViewRuntime);
        textViewMainActors = view.findViewById(R.id.textViewMainActors);
        textViewPlot = view.findViewById(R.id.textViewPlot);
        buttonRemove = view.findViewById(R.id.buttonRemove);
        buttonSave = view.findViewById(R.id.buttonSave);

        // Load and display movie details
        Picasso.get().load(movie.getPosterUrl()).into(imageViewPoster);
        textViewTitle.setText(movie.getTitle());
        textViewYear.setText(String.format("Year : %s", movie.getYear()));
        textViewRating.setText(String.format("Rating : %s", movie.getRating()));
        textViewRuntime.setText(String.format("Runtime : %s", movie.getRuntime()));
        textViewMainActors.setText(String.format("Actors : %s", movie.getMainActors()));
        textViewPlot.setText(String.format("Plot : %s", movie.getPlot()));

        // Set onClickListener for saving the movie
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMovieToStorage();
            }
        });

        // Set onClickListener for removing the movie
        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Display confirmation dialog for deletion
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Do you want to delete.")
                        .setTitle("Question")
                        .setNegativeButton("No", (dialog, cl) -> {})
                        .setPositiveButton("Yes", (dialog, cl) -> {
                            // Delete the movie from database
                            Executor thread = Executors.newSingleThreadExecutor();
                            thread.execute(() -> movieDao.delete(movie));
                            // Pop back stack to return to previous fragment
                            getActivity().getSupportFragmentManager().popBackStack();
                            // Display undo Snackbar
                            Snackbar.make(view, "You deleted it.", Snackbar.LENGTH_LONG)
                                    .setAction("Undo", click -> {
                                        // Insert the movie back to database
                                        thread.execute(() -> movieDao.insert(movie));
                                    })
                                    .show();
                        }).create().show();
            }
        });

        queue = Volley.newRequestQueue(getActivity().getApplicationContext());

        return view;
    }

    /**
     * Method to save the movie poster to internal storage.
     */
    private void saveMovieToStorage() {
        // Get the directory for the app's private internal storage directory.
        File directory = requireContext().getFilesDir();

        // Generate unique filename for the image
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";

        // Request image using Volley library
        String imageUrl = movie.getPosterUrl();
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
                    // Insert the movie to database
                    Executor thread = Executors.newSingleThreadExecutor();
                    thread.execute(() -> movieDao.insert(movie));
                    // Notify the user that the image has been saved.
                    Toast.makeText(requireContext(), "Poster saved to " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }, 1000, 1000, ImageView.ScaleType.CENTER, null, (error) -> {
        });
        queue.add(imgReq);
    }
}
