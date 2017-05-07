package com.android.sample_listview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public class ListViewAdapter extends BaseAdapter {

    private Context mContext;
    private List<Item> mItems = new ArrayList<>();

    public ListViewAdapter(Context context) {

        mContext = context;
    }

    public void addItem(Item item) {

        mItems.add(item);
    }

    @Override
    public int getCount() {

        return mItems.size();
    }

    @Override
    public Object getItem(int position) {

        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ItemViewHolder itemView;

        if(convertView == null) {

            itemView = new ItemViewHolder(mContext, mItems.get(position));

        } else {

            itemView = (ItemViewHolder) convertView;
        }

        itemView.setIcon(mItems.get(position).getIcon());
        itemView.setText(0, mItems.get(position).getData(0));
        itemView.setText(1, mItems.get(position).getData(1));
        itemView.setText(2, mItems.get(position).getData(2));

        return itemView;
    }
}
