package com.android.opstudio.sample_recyclerview;

import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

// Adapters provide a binding from an app-specific data set to views that are displayed within a RecyclerView.
public class MyRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    int images[] = {R.drawable.image_1, R.drawable.image_2, R.drawable.image_3};


    // Called when RecyclerView needs a new RecyclerView.ViewHolder of the given type to represent an item.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // 화면 width를 받아와서 1/3
        int width = parent.getResources().getDisplayMetrics().widthPixels / 3;

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        // view 크기 조정 : 화면의 1/3
        view.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, width));


        return new RowCell(view);
    }

    // Called by RecyclerView to display the data at the specified position.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ((RowCell) holder).imageView.setImageResource(images[position]);
    }

    // Returns the total number of items in the data set held by the adapter.
    @Override
    public int getItemCount() {

        return images.length;
    }

    private static class RowCell extends RecyclerView.ViewHolder {

        public ImageView imageView;

        public RowCell(View view) {

            super(view);
            imageView = (ImageView) view.findViewById(R.id.recyclerview_item_imageview);
        }
    }
}
