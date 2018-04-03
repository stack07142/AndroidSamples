package io.github.stack07142.sample_rv_itemdecoration;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyRVAdapter extends RecyclerView.Adapter<MyRVAdapter.ItemViewHolder> {

    @NonNull
    @Override
    public MyRVAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyRVAdapter.ItemViewHolder holder, int position) {
        holder.textView.setText("pos = " + position);
    }

    @Override
    public int getItemCount() {
        return 10;
    }


    class ItemViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;

        public ItemViewHolder(View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.tv_item);
        }
    }
}
