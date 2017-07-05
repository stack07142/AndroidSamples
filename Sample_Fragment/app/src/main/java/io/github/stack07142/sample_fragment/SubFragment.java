package io.github.stack07142.sample_fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SubFragment extends Fragment {

    private static final String ARG_NO = "ARG_NO";

    public SubFragment() {

    }

    public static SubFragment getInstance(int num) {

        SubFragment fragment = new SubFragment();

        Bundle args = new Bundle();

        args.putInt(ARG_NO, num);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int num = getArguments().getInt(ARG_NO, 0);

        String text = "" + num + "번째 프래그먼트";

        Log.d("SubFragment", "onCreate " + text);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_sub, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView textView = (TextView) view.findViewById(R.id.text);

        int num = getArguments().getInt(ARG_NO, 0);

        String text = "" + num + "번째 프래그먼트";
        Log.d("SubFragment", "onViewCreated " + text);

        textView.setText(text);
    }
}
