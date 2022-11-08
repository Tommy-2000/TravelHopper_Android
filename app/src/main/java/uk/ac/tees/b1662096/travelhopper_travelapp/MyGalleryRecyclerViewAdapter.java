package uk.ac.tees.b1662096.travelhopper_travelapp;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import uk.ac.tees.b1662096.travelhopper_travelapp.databinding.MediaCardViewBinding;
import uk.ac.tees.b1662096.travelhopper_travelapp.room.MediaEntity;

import java.util.List;


public class MyGalleryRecyclerViewAdapter extends RecyclerView.Adapter<MyGalleryRecyclerViewAdapter.ViewHolder> {

    private final List<MediaEntity> mediaEntityList;

    public MyGalleryRecyclerViewAdapter(List<MediaEntity> mediaEntityItems) {
        mediaEntityList = mediaEntityItems;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mediaName;
        public final ImageView mediaImageUri;

        public ViewHolder(MediaCardViewBinding binding) {
            super(binding.getRoot());
            mediaName = binding.materialCardTitle;
            mediaImageUri = binding.materialCardImage;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MediaCardViewBinding viewHolderBinding = MediaCardViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(viewHolderBinding);
    }

    @Override
    public int getItemCount() {
        return mediaEntityList.size();
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mediaName.setText(mediaEntityList.get(position).getMediaName());
    }
}