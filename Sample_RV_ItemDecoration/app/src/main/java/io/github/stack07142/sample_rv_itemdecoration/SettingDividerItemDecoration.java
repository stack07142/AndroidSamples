package io.github.stack07142.sample_rv_itemdecoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class SettingDividerItemDecoration extends RecyclerView.ItemDecoration {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({VERTICAL_ALL, VERTICAL_OUTBOUND_TOP, VERTICAL_OUTBOUND_BOTTOM, VERTICAL_OUTBOUNDS, VERTICAL_FIRST_AND_OUTBOUND_BOTTOM, VERTICAL_SKIP_FIRST, VERTICAL_SKIP_LAST})
    @interface Type {
    }

    public static final int VERTICAL_ALL = 0;
    public static final int VERTICAL_OUTBOUND_TOP = 1;
    public static final int VERTICAL_OUTBOUND_BOTTOM = 2;
    public static final int VERTICAL_OUTBOUNDS = 3;
    public static final int VERTICAL_FIRST_AND_OUTBOUND_BOTTOM = 4;
    public static final int VERTICAL_SKIP_FIRST = 5;
    public static final int VERTICAL_SKIP_LAST = 6;

    private Drawable mDivider;
    private DisplayMetrics displayMetrics;
    @Type
    private int mType;

    private int outboundTopLeftMargin;
    private int outboundTopRightMargin;
    private int outboundBottomLeftMargin;
    private int outboundBottomRightMargin;
    private int innerLeftMargin;
    private int innerRightMargin;

    private SettingDividerItemDecoration(Builder builder) {
        displayMetrics = builder.context.getResources().getDisplayMetrics();
        mDivider = ResourcesCompat.getDrawable(builder.context.getResources(), R.drawable.line_divider, builder.context.getTheme());
        mType = builder.type;

        this.outboundTopLeftMargin = getPx(builder.outboundTopLeftMargin);
        this.outboundTopRightMargin = getPx(builder.outboundTopRightMargin);
        this.outboundBottomLeftMargin = getPx(builder.outboundBottomLeftMargin);
        this.outboundBottomRightMargin = getPx(builder.outboundBottomRightMargin);
        this.innerLeftMargin = getPx(builder.innerLeftMargin);
        this.innerRightMargin = getPx(builder.innerRightMargin);
    }

    public static class Builder {
        private Context context;
        @Type
        private int type;
        private int outboundTopLeftMargin = 0;
        private int outboundTopRightMargin = 0;
        private int outboundBottomLeftMargin = 0;
        private int outboundBottomRightMargin = 0;
        private int innerLeftMargin = 0;
        private int innerRightMargin = 0;

        public Builder(Context context, @Type int type) {
            this.context = context;
            this.type = type;
        }

        public Builder setOutboundTopLeftMargin(int margin) {
            this.outboundTopLeftMargin = margin;
            return this;
        }

        public Builder setOutboundTopRightMargin(int margin) {
            this.outboundTopRightMargin = margin;
            return this;
        }

        public Builder setOutboundBottomLeftMargin(int margin) {
            this.outboundBottomLeftMargin = margin;
            return this;
        }

        public Builder setOutboundBottomRightMargin(int margin) {
            this.outboundBottomRightMargin = margin;
            return this;
        }

        public Builder setInnerLeftMargin(int margin) {
            this.innerLeftMargin = margin;
            return this;
        }

        public Builder setInnerRightMargin(int margin) {
            this.innerRightMargin = margin;
            return this;
        }

        public SettingDividerItemDecoration build() {
            return new SettingDividerItemDecoration(this);
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        final int right = parent.getWidth() - parent.getPaddingRight();

        // 맨 위 아이템 윗 선
        if (mType == VERTICAL_ALL || mType == VERTICAL_OUTBOUNDS || mType == VERTICAL_OUTBOUND_TOP || mType == VERTICAL_SKIP_LAST) {
            mDivider.setBounds(outboundTopLeftMargin, 0, right - outboundTopRightMargin, mDivider.getIntrinsicHeight());
            mDivider.draw(c);
        }

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin + Math.round(ViewCompat.getTranslationY(child));
            final int bottom = top + mDivider.getIntrinsicHeight();

            if (mType == VERTICAL_FIRST_AND_OUTBOUND_BOTTOM) {
                if (i == 0) {
                    mDivider.setBounds(innerLeftMargin, top, right - innerRightMargin, bottom);
                    mDivider.draw(c);
                } else if (i == childCount - 1) {
                    mDivider.setBounds(outboundBottomLeftMargin, bottom - mDivider.getIntrinsicHeight(), right - outboundBottomRightMargin, bottom);
                    mDivider.draw(c);
                }
            } else if (mType == VERTICAL_OUTBOUNDS || mType == VERTICAL_OUTBOUND_BOTTOM) {
                if (i == childCount - 1) {
                    mDivider.setBounds(outboundBottomLeftMargin, bottom - mDivider.getIntrinsicHeight(), right - outboundBottomRightMargin, bottom);
                    mDivider.draw(c);
                }
            } else if (mType == VERTICAL_SKIP_LAST) {
                if (i != childCount - 1) {
                    mDivider.setBounds(innerLeftMargin, bottom - mDivider.getIntrinsicHeight(), right - innerRightMargin, bottom);
                    mDivider.draw(c);
                }
            } else if (mType == VERTICAL_ALL || mType == VERTICAL_SKIP_FIRST) {
                if (i == childCount - 1) {
                    mDivider.setBounds(outboundBottomLeftMargin, bottom - mDivider.getIntrinsicHeight(), right - outboundBottomRightMargin, bottom);
                    mDivider.draw(c);
                } else {
                    mDivider.setBounds(innerLeftMargin, top, right - innerRightMargin, bottom);
                    mDivider.draw(c);
                }
            }
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
    }

    private int getPx(int dp) {
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}