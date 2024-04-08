package com.example.finalproject.ui.pexels;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.R;
import com.example.finalproject.data.pexels.Photo;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * The PhotoAdapter class is responsible for providing views that represent items in a data set.
 * It extends RecyclerView.Adapter to adapt photos to be displayed in a RecyclerView.
 */
public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    private List<Photo> photos; // List of photos
    private FragmentManager fragmentManager; // FragmentManager for managing fragments

    /**
     * Constructs a new PhotoAdapter with the specified list of photos and FragmentManager.
     *
     * @param photos           The list of photos to be displayed.
     * @param fragmentManager The FragmentManager for managing fragments.
     */
    public PhotoAdapter(List<Photo> photos, FragmentManager fragmentManager) {
        this.photos = photos;
        this.fragmentManager = fragmentManager;
    }

    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to represent an item.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new PhotoViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pexels_item_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     *
     * @param holder   The ViewHolder that should be updated to represent the contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        Photo photo = photos.get(position);
        holder.textViewPhotographer.setText(photo.getPhotographer());
        holder.textViewDimensions.setText(String.format("%s x %s", photo.getWidth(), photo.getHeight()));
        // Load image using Picasso library
        Picasso.get().load(photo.getThumbnailUrl()).into(holder.imageViewThumbnail);

        // Set click listener for the item view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open PhotoDetailFragment
                openPhotoDetailFragment(photo);
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
        return photos.size();
    }

    /**
     * The PhotoViewHolder class represents a ViewHolder for displaying photo items.
     */
    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewThumbnail;
        TextView textViewPhotographer;
        TextView textViewDimensions;

        /**
         * Constructs a new PhotoViewHolder with the specified View.
         *
         * @param itemView The View containing the photo item layout.
         */
        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewThumbnail = itemView.findViewById(R.id.imageViewThumbnail);
            textViewPhotographer = itemView.findViewById(R.id.textViewPhotographer);
            textViewDimensions = itemView.findViewById(R.id.textViewDimensions);
        }
    }

    /**
     * Opens the PhotoDetailFragment with the specified photo.
     *
     * @param photo The photo to be displayed in the PhotoDetailFragment.
     */
    private void openPhotoDetailFragment(Photo photo) {
        // Create a new instance of PhotoDetailFragment with the specified photo
        PexelsDetailFragment fragment = new PexelsDetailFragment(photo);

        // Replace the current fragment with PhotoDetailFragment
        fragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, fragment)
                .addToBackStack(null)  // Optional: Add to back stack
                .commit();
    }
}
