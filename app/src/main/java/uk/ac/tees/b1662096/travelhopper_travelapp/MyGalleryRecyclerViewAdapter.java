package uk.ac.tees.b1662096.travelhopper_travelapp;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;

import uk.ac.tees.b1662096.travelhopper_travelapp.databinding.MediaCardViewBinding;

import java.util.ArrayList;


public class MyGalleryRecyclerViewAdapter extends RecyclerView.Adapter<MyGalleryRecyclerViewAdapter.MyGalleryViewHolder> {

    private MediaCardViewBinding bindingMediaCardView;

    private final ArrayList<Bitmap> galleryMediaList;

    public MyGalleryRecyclerViewAdapter(ArrayList<Bitmap> galleryMediaList) {
        this.galleryMediaList = galleryMediaList;
    }

    public static class MyGalleryViewHolder extends RecyclerView.ViewHolder {
        public final ImageView mediaImageView;

        public MyGalleryViewHolder(MediaCardViewBinding mediaCardViewBinding) {
            super(mediaCardViewBinding.getRoot());
            mediaImageView = mediaCardViewBinding.mediaImageView;
        }
    }

    @NonNull
    @Override
    public MyGalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        bindingMediaCardView = MediaCardViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyGalleryViewHolder(bindingMediaCardView);
    }

    @Override
    public int getItemCount() {
        return galleryMediaList.size();
    }

    @Override
    public void onBindViewHolder(final MyGalleryViewHolder myGalleryViewHolder, int position) {
        // Try this in case Glide doesn't work
//        myGalleryViewHolder.mediaImageView.setImageURI(galleryMediaList.get(position));
        // Use Glide with the requestOptions to load the image into the viewHolder
        Glide.with(myGalleryViewHolder.mediaImageView.getContext()).load(galleryMediaList.get(position)).skipMemoryCache(true).apply(RequestOptions.circleCropTransform()).into(bindingMediaCardView.mediaImageView);

        // Check that the item in the ViewHolder is clickable
        myGalleryViewHolder.mediaImageView.setOnClickListener(view -> Snackbar.make(myGalleryViewHolder.mediaImageView.getRootView(), "Trip Item has been clicked", Snackbar.LENGTH_SHORT).show());

        // Set a fade-in animation for each item in the RecyclerView
        setRecyclerViewAnimation(myGalleryViewHolder.mediaImageView, position);
    }

    // Create the animation for each item in RecyclerView
    private void setRecyclerViewAnimation(View animationItemView, int position) {
        boolean on_attach = true;
        if(!on_attach) {
            position = -1;
        }
        // Set the first position as a boolean
        boolean isNotFirstItem = position == -1;
        position++;
        animationItemView.setAlpha(0.f);
        AnimatorSet animatorSet = new AnimatorSet();
        // Create the animation for each item using ObjectAnimator
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(animationItemView, "alpha", 0.f, 0.5f, 1.0f);
        ObjectAnimator.ofFloat(animationItemView, "alpha", 0.f).start();
        // Set the duration of the animation with a long number
        long ANIM_DURATION = 500;
        objectAnimator.setStartDelay(isNotFirstItem ? ANIM_DURATION / 2 : (position * ANIM_DURATION / 3));
        objectAnimator.setDuration(ANIM_DURATION);
        // Start the animation
        animatorSet.start();
        animatorSet.play(objectAnimator);
        objectAnimator.start();
    }

}