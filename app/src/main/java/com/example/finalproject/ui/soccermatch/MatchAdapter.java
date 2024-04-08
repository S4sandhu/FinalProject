package com.example.finalproject.ui.soccermatch;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.R;
import com.example.finalproject.data.soccermatch.Match;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * The MatchAdapter class is responsible for providing views that represent items in a data set.
 * It extends RecyclerView.Adapter to adapt soccer match data to be displayed in a RecyclerView.
 */
public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MatchViewHolder> {

    private List<Match> matches; // List of soccer matches
    private FragmentManager fragmentManager; // FragmentManager for managing fragments

    /**
     * Constructs a new MatchAdapter with the specified list of matches and FragmentManager.
     *
     * @param matches           The list of soccer matches to be displayed.
     * @param fragmentManager   The FragmentManager for managing fragments.
     */
    public MatchAdapter(List<Match> matches, FragmentManager fragmentManager) {
        this.matches = matches;
        this.fragmentManager = fragmentManager;
    }

    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to represent an item.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new MatchViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.soccermatch_item_match, parent, false);
        return new MatchViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     *
     * @param holder   The ViewHolder that should be updated to represent the contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        Match match = matches.get(position);

        // Load match thumbnail using Picasso library
        Picasso.get().load(match.getThumbnailUrl()).into(holder.imageViewThumbnail);

        holder.textViewTitle.setText(match.getTitle());
        holder.textViewDate.setText(String.format("Date: %s", match.getDate()));

        // Set click listener for the item view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open MatchDetailFragment
                openMatchDetailFragment(match);
            }
        });
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in the data set.
     */
    @Override
    public int getItemCount() {
        return matches.size();
    }

    /**
     * The MatchViewHolder class represents a ViewHolder for displaying soccer match items.
     */
    static class MatchViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewThumbnail;
        TextView textViewTitle;
        TextView textViewDate;

        /**
         * Constructs a new MatchViewHolder with the specified View.
         *
         * @param itemView The View containing the soccer match item layout.
         */
        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewThumbnail = itemView.findViewById(R.id.imageViewThumbnail);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDate = itemView.findViewById(R.id.textViewDate);
        }
    }

    /**
     * Opens the MatchDetailFragment with the specified match.
     *
     * @param match The soccer match to be displayed in the MatchDetailFragment.
     */
    private void openMatchDetailFragment(Match match) {
        // Create a new instance of MatchDetailFragment with the specified match
        MatchDetailFragment fragment = new MatchDetailFragment(match);

        // Replace the current fragment with MatchDetailFragment
        fragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, fragment)
                .addToBackStack(null)  // Optional: Add to back stack
                .commit();
    }
}
