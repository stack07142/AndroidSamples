package com.android.sample_listview;

import android.graphics.drawable.Drawable;

public class Item {

    private Drawable mIcon;
    private String[] mData;

    public Item(Drawable icon, String[] obj) {

        mIcon = icon;
        mData = obj;
    }

    public Item(Drawable icon, String obj01, String obj02, String obj03) {

        mIcon = icon;

        mData = new String[3];
        mData[0] = obj01;
        mData[1] = obj02;
        mData[2] = obj03;
    }

    // get
    public Drawable getIcon() {

        return mIcon;
    }

    public String[] getData() {

        return mData;
    }

    public String getData(int index) {

        if (mData == null || index >= mData.length) {

            return null;
        }

        return mData[index];
    }

    // set
    public void setData(String[] obj) {

        mData = obj;
    }

    public void setIcon(Drawable icon) {

        mIcon = icon;
    }
}
