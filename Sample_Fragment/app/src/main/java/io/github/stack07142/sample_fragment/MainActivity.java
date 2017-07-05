package io.github.stack07142.sample_fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import io.github.stack07142.sample_fragment.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements MyFragment.OnFragmentInteractionListener {

    //private static final String FRAGMENT_TAG = "FRAGMENT_TAG";
    private static final String KEY_NUMBER = "KEY_NUMBER";

    ActivityMainBinding mBinding;

    private int mNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // 프래그먼트 추가/삭제는 트랜잭션 단위로 한다
        // addButton : 프래그먼트 추가
        mBinding.addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().add(R.id.fragment_container, SubFragment.getInstance(mNumber))
                        .addToBackStack(null)
                        .commit();
            }
        });

        // removeButton : 프래그먼트 추가
        mBinding.removeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (mNumber == 0) {

                    return;
                }

                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.popBackStack();
            }
        });

        // 리스너
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {

            @Override
            public void onBackStackChanged() {

                FragmentManager fragmentManager = getSupportFragmentManager();

                mNumber = fragmentManager.getBackStackEntryCount();

                /*

                int count = 0;
                for (Fragment f : fragmentManager.getFragments()) {

                    if (f != null) {

                        count++;
                    }
                }
                mNumber = count;
                */


                Log.d("MainActivity", "onBackStackChanged mNumber = " + mNumber);
            }
        });

        //
        /*
        Fragment fragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG);

        if (savedInstanceState == null) {
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, SubFragment.getInstance(mNumber), FRAGMENT_TAG)
                    .addToBackStack(null)
                    .commit();
        }
*/

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(KEY_NUMBER, mNumber);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mNumber = savedInstanceState.getInt(KEY_NUMBER);
    }


    @Override
    public void onFragmentInteraction() {

        Toast.makeText(getApplicationContext(), "버튼이 눌렸습니다", Toast.LENGTH_SHORT).show();

    }
}