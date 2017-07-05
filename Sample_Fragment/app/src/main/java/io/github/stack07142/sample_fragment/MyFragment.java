package io.github.stack07142.sample_fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

// Fragment를 상속한다
public class MyFragment extends Fragment {

    // 리스너 선언
    private OnFragmentInteractionListener mListener;

    /**
     * Activity와 연계하기 위한 리스너
     */
    public interface OnFragmentInteractionListener {

        void onFragmentInteraction();
    }

    // 빈 생성자는 Fragment를 이용하는 데 필요
    public MyFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_my, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // mListener : Activity와 연계하기 위한 리스너
                if (mListener != null) {

                    mListener.onFragmentInteraction();
                }
            }
        });
    }

    // Activity에 Fragment가 연결될 때, 리스너 연결
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Activity쪽에 필요한 Interface가 구현되었는지 확인
        // MainActivity에 대한 참조를 직접 갖지 않고 인터페이스로서 가지는 것은 특정 Activity에 의존하지 않도록 결합을 느슨하게 만들기 위함
        if (context instanceof OnFragmentInteractionListener) {

            mListener = (OnFragmentInteractionListener) context;
        } else {

            throw new RuntimeException(context.toString() + "OnFragmentInterationListener를 구현해 주세요");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Activity와 Fragment의 연결을 끊을 때, mListener의 참조를 해제한다
        mListener = null;
    }
}
