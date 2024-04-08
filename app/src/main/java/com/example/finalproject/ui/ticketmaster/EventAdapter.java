package com.example.finalproject.ui.ticketmaster;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.R;
import com.example.finalproject.data.ticketmaster.Event;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * The EventAdapter class is responsible for adapting a list of Event objects to be displayed in a RecyclerView.
 */
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> events; // List of events
    private FragmentManager fragmentManager; // FragmentManager to handle fragment transactions

    /**
     * Constructs a new EventAdapter with the specified list of events and FragmentManager.
     *
     * @param events          The list of events to be displayed.
     * @param fragmentManager The FragmentManager to handle fragment transactions.
     */
    public EventAdapter(List<Event> events, FragmentManager fragmentManager) {
        this.events = events;
        this.fragmentManager = fragmentManager;
    }

    /**
     * Called when RecyclerView needs a new RecyclerView.ViewHolder of the given type to represent an item.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The type of the new View.
     * @return A new EventViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for the event item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ticketmaster_item_event, parent, false);
        return new EventViewHolder(view); // Return a new EventViewHolder
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the item at the given position.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position); // Get the event at the specified position
        // Load event image using Picasso library
        Picasso.get().load(event.getPromoImageUrl()).into(holder.imageViewPromo);
        holder.textViewTitle.setText(event.getName()); // Set event title
        // Set click listener to open EventDetailFragment when the item is clicked
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEventDetailFragment(event);
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
        return events.size(); // Return the size of the events list
    }

    /**
     * The EventViewHolder class represents a ViewHolder for Event items in the RecyclerView.
     */
    static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewPromo; // ImageView for event promo image
        TextView textViewTitle; // TextView for event title

        /**
         * Constructs a new EventViewHolder with the specified View.
         *
         * @param itemView The View for this ViewHolder.
         */
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize views
            imageViewPromo = itemView.findViewById(R.id.imageViewPromo);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
        }
    }

    /**
     * Opens the EventDetailFragment with the specified event.
     *
     * @param event The event to display in the detail fragment.
     */
    private void openEventDetailFragment(Event event) {
        EventDetailFragment fragment = new EventDetailFragment(event); // Create a new instance of EventDetailFragment
        // Replace the current fragment with EventDetailFragment
        fragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, fragment)
                .addToBackStack(null)  // Optional: Add to back stack
                .commit();
    }
}