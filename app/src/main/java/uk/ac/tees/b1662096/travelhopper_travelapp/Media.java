package uk.ac.tees.b1662096.travelhopper_travelapp;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class Media {

    private int mediaID;
    private String mediaUri;

    public Media(int mediaID, String mediaUri) {
        this.mediaID = mediaID;
        this.mediaUri = mediaUri;
    }

    public int getMediaID() {
        return this.mediaID;
    }

    public String getMediaUri() {
        return this.mediaUri;
    }

    public void setMediaID(int mediaID) {
        this.mediaID = mediaID;
    }

    public void setMediaUri(String mediaUri) {
        this.mediaUri = mediaUri;
    }

    @BindingAdapter("getMediaFromGallery")
    public static void getMediaFromGallery(ImageView mediaView, String mediaUri) {
        Glide.with(mediaView.getContext()).load(mediaUri).apply(new RequestOptions().circleCrop()).into(mediaView);
    }
}
