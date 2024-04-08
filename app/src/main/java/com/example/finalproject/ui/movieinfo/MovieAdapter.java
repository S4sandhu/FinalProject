package com.example.finalproject.ui.movieinfo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.R;
import com.example.finalproject.data.movieinfo.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * The MovieAdapter class is responsible for managing and displaying movie data in a RecyclerView.
 * It extends RecyclerView.Adapter to provide data to the RecyclerView.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> movies; // List to hold movie data
    private FragmentManager fragmentManager; // FragmentManager to handle fragment transactions

    /**
     * Constructs a new MovieAdapter with the given list of movies and FragmentManager.
     *
     * @param movies           List of movies to be displayed.
     * @param fragmentManager FragmentManager to handle fragment transactions.
     */
    public MovieAdapter(List<Movie> movies, FragmentManager fragmentManager) {
        this.movies = movies;
        this.fragmentManager = fragmentManager;
    }

    /**
     * Called when RecyclerView needs a new RecyclerView.ViewHolder of the given type to represent an item.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new MovieViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the movie item layout and return a new MovieViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movieinfo_item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     *
     * @param holder   The ViewHolder that should be updated to represent the contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);

        // Load image using Picasso library
        Picasso.get().load(movie.getPosterUrl()).into(holder.imageViewPoster);
        holder.textViewTitle.setText(movie.getTitle());
        holder.textViewYear.setText(String.format("Year : %s", movie.getYear()));
        holder.textViewRuntime.setText(String.format("Runtime : %s", movie.getRuntime()));

        // Set onClickListener to open MovieDetailFragment when item is clicked
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMovieDetailFragment(movie);
            }
        });
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return movies.size();
    }

    /**
     * ViewHolder class to hold the views of each item in the RecyclerView.
     */
    static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewPoster;
        TextView textViewTitle;
        TextView textViewYear;
        TextView textViewRuntime;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewPoster = itemView.findViewById(R.id.imageViewPoster);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewYear = itemView.findViewById(R.id.textViewYear);
            textViewRuntime = itemView.findViewById(R.id.textViewRuntime);
        }
    }

    /**
     * Method to open MovieDetailFragment with the selected movie.
     *
     * @param movie The movie object to display details.
     */
    private void openMovieDetailFragment(Movie movie) {
        // Create new instance of MovieDetailFragment with selected movie
        MovieDetailFragment fragment = new MovieDetailFragment(movie);

        // Replace the current fragment with MovieDetailFragment
        fragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, fragment)
                .addToBackStack(null)  // Optional: Add to back stack
                .commit();
    }
}

