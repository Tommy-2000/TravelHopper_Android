package uk.ac.tees.b1662096.travelhopper_travelapp;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import uk.ac.tees.b1662096.travelhopper_travelapp.databinding.FragmentMyGalleryBinding;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Photo}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyGalleryRecyclerViewAdapter extends RecyclerView.Adapter<MyGalleryRecyclerViewAdapter.ViewHolder> {

    private final List<Photo> photo;

    public MyGalleryRecyclerViewAdapter(List<Photo> items) {
        photo = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentMyGalleryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = photo.get(position);
//        holder.mIdView.setText(placeholderItem.get(position).id);
//        holder.mContentView.setText(placeholderItem.get(position).content);
    }

    @Override
    public int getItemCount() {
        return photo.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
//        public final TextView mIdView;
//        public final TextView mContentView;
        public Photo mItem;

        public ViewHolder(FragmentMyGalleryBinding binding) {
            super(binding.getRoot());
//            mIdView = binding.itemNumber;
//            mContentView = binding.content;
        }

//        @Override
//        public String toString() {
//            return super.toString() + " '" + mContentView.getText() + "'";
//        }
    }
}