package com.example.finalproject.ui.soccermatch;

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

import com.example.finalproject.data.soccermatch.Match;
import com.example.finalproject.data.soccermatch.MatchDao;
import com.example.finalproject.data.soccermatch.MatchDatabase;
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
 * A fragment to display details of a soccer match.
 */
public class MatchDetailFragment extends Fragment {

    // UI elements
    Button buttonSave; // Button to save the match
//    Button buttonView; // Button to view the match

    Button buttonInBrowser; // Button to open match in browser

    TextView textViewTitle; // TextView to display match title
    TextView textViewSide1; // TextView to display the first side/team
    Button buttonRemove; // Button to remove the match

    TextView textViewSide2; // TextView to display the second side/team
    ImageView imageViewThumbnail; // ImageView to display match thumbnail

    TextView textViewDate; // TextView to display match date

//     VideoView videoViewHighlight; // VideoView to display match highlight

    MatchDao matchDAO; // Data Access Object for Match entities
    Context thiscontext; // Context reference

    Match match; // Match object representing the current match
    protected RequestQueue queue = null; // RequestQueue for network requests

    /**
     * Constructs a new MatchDetailFragment with the specified match.
     *
     * @param match The soccer match to be displayed.
     */
    protected MatchDetailFragment(Match match){
        this.match = match;
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_match_detail ,container, false);

        thiscontext = getActivity().getApplicationContext();

        MatchDatabase db = Room.databaseBuilder(thiscontext,
                MatchDatabase.class, "match").build();
        matchDAO = db.matchDAO();


        imageViewThumbnail = view.findViewById(R.id.imageViewThumbnail);
        textViewTitle = view.findViewById(R.id.textViewTitle);
        textViewSide1 = view.findViewById(R.id.textViewSide1);
        textViewSide2 = view.findViewById(R.id.textViewSide2);
        textViewDate = view.findViewById(R.id.textViewDate);
//        buttonView = view.findViewById(R.id.buttonView);
        buttonSave = view.findViewById(R.id.buttonSave);
        buttonInBrowser = view.findViewById(R.id.buttonInBrowser);
        buttonRemove = view.findViewById(R.id.buttonRemove);

//        videoViewHighlight = view.findViewById(R.id.videoViewHighlight);

        Picasso.get().load(match.getThumbnailUrl()).into(imageViewThumbnail);
        textViewTitle.setText(match.getTitle());
        textViewSide1.setText(match.getTeam1());
        textViewSide2.setText(match.getTeam2());
        textViewDate.setText(match.getDate());


//        buttonView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String url = match.getUrl();
//                System.out.println(url);
//                Uri uri = Uri.parse(url);
//                // Set media controller
//                MediaController mediaController = new MediaController(thiscontext);
//                mediaController.setAnchorView(videoViewHighlight);
//                videoViewHighlight.setMediaController(mediaController);
//                // Set the URI of the video to be played
//                videoViewHighlight.setVideoURI(uri);
//                videoViewHighlight.setVisibility(View.VISIBLE);
//
//                // Start playback
//                videoViewHighlight.start();
//            }
//        });


        // Handle URL click to open in web browser
        buttonInBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = match.getUrl();
                openUrlInBrowser(url);
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMatchToStorage();
            }
        });

        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder( getContext() );
                builder.setMessage("Do you want to delete.")
                        .setTitle("Question")
                        .setNegativeButton("No",(dialog, cl)->{})
                        .setPositiveButton("Yes",(dialog, cl)->{

                            Executor thread = Executors.newSingleThreadExecutor();
                            thread.execute(()-> matchDAO.delete(match));
                            getActivity().getSupportFragmentManager().popBackStack();

                            Snackbar.make(view, "You deleted it.",Snackbar.LENGTH_LONG)
                                    .setAction("Undo", click ->{
                                        thread.execute(()-> matchDAO.insert(match));
                                    })
                                    .show();
                        }).create().show();

            }
        });

        queue = Volley.newRequestQueue(getActivity().getApplicationContext());

        return view;
    }

    /**
     * Opens the provided URL in a web browser.
     *
     * @param url The URL to be opened in the web browser.
     */
    private void openUrlInBrowser(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    /**
     * Saves the match thumbnail to the internal storage directory.
     */
    private void saveMatchToStorage() {

        // Get the directory for the app's private internal storage directory.
        File directory = requireContext().getFilesDir();

        // Generate a unique filename for the image.
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";

        String imageUrl = match.getThumbnailUrl();
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

                    Executor thread = Executors.newSingleThreadExecutor();
                    thread.execute(()-> matchDAO.insert(match));


                    // Notify the user that the image has been saved.
                    Toast.makeText(requireContext(), "Poster saved to " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();

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