package uk.ac.tees.b1662096.travelhopper_travelapp;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import uk.ac.tees.b1662096.travelhopper_travelapp.databinding.MediaCardViewBinding;


public class MyHomeRecyclerViewAdapter extends RecyclerView.Adapter<MyHomeRecyclerViewAdapter.MyHomeViewHolder> {

    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private StorageReference storageReference = firebaseStorage.getReference();

    private MediaCardViewBinding bindingMediaCardView;

    private final ArrayList<Uri> homeMediaList;

    public MyHomeRecyclerViewAdapter(ArrayList<Uri> homeMediaList) {
        this.homeMediaList = homeMediaList;
    }

    public static class MyHomeViewHolder extends RecyclerView.ViewHolder {
        public final ImageView mediaImageView;

        public MyHomeViewHolder(MediaCardViewBinding mediaCardViewBinding) {
            super(mediaCardViewBinding.getRoot());
            mediaImageView = mediaCardViewBinding.mediaImageView;
        }
    }

    @NonNull
    @Override
    public MyHomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        bindingMediaCardView = MediaCardViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyHomeViewHolder(bindingMediaCardView);
    }

    @Override
    public int getItemCount() {
        return homeMediaList.size();
    }

    @Override
    public void onBindViewHolder(final MyHomeViewHolder myHomeViewHolder, int position) {
        StorageReference childStorageRef = storageReference.child("");
        childStorageRef.getDownloadUrl().addOnSuccessListener(downloadedUrl -> {
            // Use Glide with the requestOptions to load the image into the viewHolder
            Glide.with(myHomeViewHolder.mediaImageView.getContext())
                    .load(homeMediaList.get(myHomeViewHolder.getAbsoluteAdapterPosition()))
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .thumbnail(Glide.with(myHomeViewHolder.mediaImageView.getContext()).load(R.drawable.ic_media_placeholder_icon))
                    .error(R.drawable.ic_error_icon)
                    .into(bindingMediaCardView.mediaImageView);
        }).addOnFailureListener(exception -> Glide.with(myHomeViewHolder.mediaImageView.getContext())
                .load(R.drawable.ic_error_icon)
                .fitCenter()
                .into(bindingMediaCardView.mediaImageView));

        // Check that the item in the ViewHolder is clickable
        myHomeViewHolder.mediaImageView.setOnClickListener(view -> Snackbar.make(myHomeViewHolder.mediaImageView.getRootView(), "Trip Item has been clicked", Snackbar.LENGTH_SHORT).show());

//        // Set a fade-in animation for each item in the RecyclerView
//        setRecyclerViewAnimation(myHomeViewHolder.mediaImageView, position);
    }

    // Create the animation for each item in RecyclerView
//    private void setRecyclerViewAnimation(View animationItemView, int position) {
//        boolean on_attach = true;
//        if(!on_attach) {
//            position = -1;
//        }
//        // Set the first position as a boolean
//        boolean isNotFirstItem = position == -1;
//        position++;
//        animationItemView.setAlpha(0.f);
//        AnimatorSet animatorSet = new AnimatorSet();
//        // Create the animation for each item using ObjectAnimator
//        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(animationItemView, "alpha", 0.f, 0.5f, 1.0f);
//        ObjectAnimator.ofFloat(animationItemView, "alpha", 0.f).start();
//        // Set the duration of the animation with a long number
//        long ANIM_DURATION = 500;
//        objectAnimator.setStartDelay(isNotFirstItem ? ANIM_DURATION / 2 : (position * ANIM_DURATION / 3));
//        objectAnimator.setDuration(ANIM_DURATION);
//        // Start the animation
//        animatorSet.start();
//        animatorSet.play(objectAnimator);
//        objectAnimator.start();
//    }

}