package uk.ac.tees.b1662096.travelhopper_travelapp;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Px;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

public class MyHomeRecyclerViewDecorator extends RecyclerView.ItemDecoration {

    private int innerItemsGap;
    private int oneSidedGap;
    private float itemPeekingWidth = .035f;

    public MyHomeRecyclerViewDecorator(Context context, @Px int cardWidth, float itemPeekingWidth) {
        this.innerItemsGap = context.getResources().getDisplayMetrics().widthPixels;
        this.oneSidedGap = cardWidth;
        this.itemPeekingWidth = itemPeekingWidth;
    }

    public MyHomeRecyclerViewDecorator(@Px int totalWidth, @Px int cardWidth, float itemPeekingWidth) {
        int cardPeekingWidth = (int) (cardWidth * itemPeekingWidth + .5f);

        innerItemsGap = (totalWidth - cardWidth) / 2;
        oneSidedGap = innerItemsGap / 2 - cardPeekingWidth;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, recyclerView, state);

        int index = Objects.requireNonNull(recyclerView).getChildAdapterPosition(view);
        boolean isFirstCard = isFirstCard(index);
        boolean isLastCard = isLastCard(index, recyclerView);

        int leftInset = isFirstCard ? innerItemsGap : oneSidedGap;
        int rightInset = isLastCard ? innerItemsGap : oneSidedGap;

        Objects.requireNonNull(outRect).set(leftInset, 0, rightInset, 0);

    }

    private boolean isFirstCard(int index) {
        return index == 0;
    }

    private boolean isLastCard(int index, RecyclerView recyclerView) {
        return index == recyclerView.getAdapter().getItemCount() - 1;
    }
}
