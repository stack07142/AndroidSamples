package io.github.stack07142.sample_customview2;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MyCustomView extends LinearLayout {

    private ImageView mStar1;
    private ImageView mStar2;
    private ImageView mStar3;
    private int mSelected = 0; // 선택된 별의 번호

    public MyCustomView(Context context) {
        super(context);

        initializeViews(context, null);
    }

    public MyCustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        initializeViews(context, attrs);
    }

    public MyCustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initializeViews(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MyCustomView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        initializeViews(context, attrs);
    }

    private void initializeViews(Context context, AttributeSet attrs) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 1. 레이아웃 전개
        inflater.inflate(R.layout.three_stars_indicator, this);

        if (attrs != null) {

            // 2. attrs.xml에 정의한 스타일을 가져온다
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MyCustomView);

            mSelected = a.getInteger(0, 0);

            a.recycle(); // 이용이 끝났으면 recycle()을 호출한다
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mStar1 = (ImageView) findViewById(R.id.star1);
        mStar2 = (ImageView) findViewById(R.id.star2);
        mStar3 = (ImageView) findViewById(R.id.star3);

        // 처음에만 XML의 지정을 반영하고자 2번째 인수인 force를 true로 한다
        setSelected(mSelected, true);
    }

    private void setSelected(int select, boolean force) {

        if (force || mSelected != select) {
            if (2 > mSelected && mSelected < 0) {
                return;
            }
            mSelected = select;
            if (mSelected == 0) {

                mStar1.setImageResource(R.drawable.star);
                mStar2.setImageResource(R.drawable.empty_star);
                mStar3.setImageResource(R.drawable.empty_star);
            } else if (mSelected == 1) {

                mStar1.setImageResource(R.drawable.empty_star);
                mStar2.setImageResource(R.drawable.star);
                mStar3.setImageResource(R.drawable.empty_star);
            } else if (mSelected == 2) {

                mStar1.setImageResource(R.drawable.empty_star);
                mStar2.setImageResource(R.drawable.empty_star);
                mStar3.setImageResource(R.drawable.star);
            }
        }
    }

    public void setSelected(int select) {

        setSelected(select, false);
    }


    public int getSelected() {

        return mSelected;
    }
}
