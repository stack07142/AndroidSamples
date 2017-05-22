package com.android.sample_viewpager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ItemFragment extends Fragment {

    // 반복되는 코드를 재사용하여 뷰페이저를 최적화 하는 소스
    public static ItemFragment newInstance(int imgResId) {

        Bundle args = new Bundle();
        args.putInt("imgResId", imgResId);

        ItemFragment fragment = new ItemFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.item, container, false);

        ImageView imageView = (ImageView) view.findViewById(R.id.item_imageview);
        imageView.setImageResource(getArguments().getInt("imgResId"));

        return view;
    }

}
