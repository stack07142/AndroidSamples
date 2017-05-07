package com.android.sample_listview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemViewHolder extends ConstraintLayout {

    ImageView mIcon;
    TextView mText01, mText02, mText03;

    public ItemViewHolder(Context context, Item aItem) {
        super(context);

        // 메모리 객체화
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.list_item, this, true);

        mIcon = (ImageView) findViewById(R.id.icon_item);
        mText01 = (TextView) findViewById(R.id.data_item_01);
        mText02 = (TextView) findViewById(R.id.data_item_02);
        mText03 = (TextView) findViewById(R.id.data_item_03);


    }

    public void setIcon(Drawable icon) {

        mIcon.setImageDrawable(icon);
    }

    public void setText(int index, String data) {

        switch (index) {

            case 0:
                mText01.setText(data);
                break;

            case 1:
                mText02.setText(data);
                break;

            case 2:
                mText03.setText(data);
                break;

            default:
                throw new IllegalArgumentException();
        }
    }
}
