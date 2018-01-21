package io.github.stack07142.toolbar;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import io.github.stack07142.toolbar.databinding.LayoutCardBinding;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.MyViewHolder> {

    private final Context context;
    private List<Card> items;

    /* Constructor & functions ------------------------------------------------------------------ */
    RVAdapter(Context context) {

        this.context = context;
    }

    void setItemsAndRefresh(List<Card> items) {

        this.items = items;
        notifyDataSetChanged();
    }

    /* Override Methods ------------------------------------------------------------------------- */
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.layout_card, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Card item = items.get(position);

        holder.bindItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /* View Holder ------------------------------------------------------------------------------ */

    class MyViewHolder extends RecyclerView.ViewHolder {

        private LayoutCardBinding binding;

        public MyViewHolder(View itemView) {
            super(itemView);

            binding = DataBindingUtil.bind(itemView);
        }

        void bindItem(Card item) {

            binding.setCard(item);
        }

        LayoutCardBinding getBinding() {

            return binding;
        }
    }
}
