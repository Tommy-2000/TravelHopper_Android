package uk.ac.tees.b1662096.travelhopper_travelapp;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

import uk.ac.tees.b1662096.travelhopper_travelapp.databinding.FragmentMyFavouritesBinding;


public class MyFavouritesRecyclerViewAdapter extends RecyclerView.Adapter<MyFavouritesRecyclerViewAdapter.ViewHolder> {

    private final List<Photo> adapterValues;

    public MyFavouritesRecyclerViewAdapter(List<Photo> items) {
        adapterValues = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentMyFavouritesBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.photo = adapterValues.get(position);
        holder.mediaTitleView.setText(adapterValues.get(position).getPhotoTitle());
        holder.mediaDescriptionView.setText(adapterValues.get(position).getPhotoDescription());
    }

    @Override
    public int getItemCount() {
        return adapterValues.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mediaTitleView;
        public final TextView mediaDescriptionView;
        public Photo photo;

        public ViewHolder(FragmentMyFavouritesBinding binding) {
            super(binding.getRoot());
            mediaTitleView = binding.travelTitle;
            mediaDescriptionView = binding.travelInformation;
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mediaDescriptionView.getText() + "'";
        }
    }
}