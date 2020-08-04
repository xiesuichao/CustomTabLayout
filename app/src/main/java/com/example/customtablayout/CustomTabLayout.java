package com.example.customtablayout;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 自定义 TabLayout
 * Created by darren on 2018/6/23.
 */

public class CustomTabLayout extends HorizontalScrollView {

    private Context mContext;
    private List<String> mTitleList = new ArrayList<>();
    private LinearLayout mTvContainerLl;
    private View mUnderlineView;
    private int mTextSize;
    private int mDefaultTextColor;
    private int mTvBackground;
    private int mUnderlineWidth;
    private int mUnderlineHeight;
    private int mUnderlineCol;
    private int mCheckedTextCol;
    private int mUnderlineDuration;
    private int mHorizontalSpace;
    private int initPosition = 0;
    private int currentPosition = initPosition;
    private final String TEXT_STYLE_NORMAL = "0";
    private final String TEXT_STYLE_BOLD = "1";
    private String mTextStyle;
    private OnTabClickListener mTabClickListener;
    private OnTabScrollListener mTabScrollListener;
    private boolean isCheckedTextSet = false;

    public CustomTabLayout(Context context) {
        this(context, null);
    }

    public CustomTabLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomTabLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public interface OnTabClickListener {
        void tabClick(int position, String str);
    }

    /**
     * 点击事件
     */
    public void setOnTabClickListener(OnTabClickListener mTabClickListener) {
        this.mTabClickListener = mTabClickListener;
    }

    public interface OnTabScrollListener {
        void scrollChange(int position, String text);
    }

    /**
     * 滑动监听
     */
    public void setOnTabScrollListener(OnTabScrollListener scrollListener) {
        this.mTabScrollListener = scrollListener;
    }

    /**
     * 如果初始下划线position不为0，则调该方法调整初始position
     */
    public void initUnderlinePosition(int position) {
        if (position > mTvContainerLl.getChildCount() - 1) {
            return;
        }
        this.initPosition = position;
        this.currentPosition = position;
        resetTextColor(position);
    }

    /**
     * 移动下划线
     */
    public void moveToPosition(int position) {
        if (position < 0) {
            return;
        }
        this.currentPosition = position;
        startAnim(position);
    }

    /**
     * 设置tab标签
     */
    public void setTitleArr(String[] titleArr) {
        if (titleArr == null || titleArr.length == 0) {
            return;
        }
        setTitleList(Arrays.asList(titleArr));
    }

    /**
     * 设置tab标签
     */
    public void setTitleList(List<String> titleList) {
        if (titleList == null || titleList.isEmpty()) {
            return;
        }
        mTitleList.clear();
        this.mTitleList.addAll(titleList);
        setChildLayout();
    }

    /**
     * 获取当前下划线position
     */
    public int getUnderlinePosition() {
        return currentPosition;
    }

    /**
     * 获取titleList
     */
    public List<String> getTitleList() {
        return mTitleList;
    }

    /**
     * 获取当前被选中的title
     */
    public String getCheckedText() {
        return mTitleList.get(currentPosition);
    }

    /**
     * 获取当前被选中的TextView
     */
    public TextView getCheckedTextView() {
        return (TextView) mTvContainerLl.getChildAt(currentPosition);
    }

    /**
     * 设置下划线颜色
     */
    public void setUnderlineColor(int colorId) {
        this.mUnderlineCol = colorId;
        mUnderlineView.setBackgroundColor(mUnderlineCol);
        resetTextColor(currentPosition);
    }

    /**
     * 设置选中字体颜色
     */
    public void setCheckedTextColor(int colorId) {
        this.mCheckedTextCol = colorId;
        this.isCheckedTextSet = true;
        resetTextColor(currentPosition);
    }

    private void startAnim(int clickPosition) {
        TextView firstTv = (TextView) mTvContainerLl.getChildAt(initPosition);
        TextView clickTv = (TextView) mTvContainerLl.getChildAt(clickPosition);
        float animStartX = firstTv.getLeft() + firstTv.getMeasuredWidth() / 2 - dp2px(mUnderlineWidth) / 2;
        float clickEndX = clickTv.getLeft() + clickTv.getMeasuredWidth() / 2 - dp2px(mUnderlineWidth) / 2;
        ObjectAnimator.ofFloat(mUnderlineView, "translationX", clickEndX - animStartX)
                .setDuration(mUnderlineDuration)
                .start();
        for (int i = 0; i < mTvContainerLl.getChildCount(); i++) {
            ((TextView) mTvContainerLl.getChildAt(i)).setTextColor(mDefaultTextColor);
        }
        clickTv.setTextColor(getCheckedTextCol());
        mUnderlineView.setBackgroundColor(mUnderlineCol);
        if (mTabScrollListener != null) {
            mTabScrollListener.scrollChange(clickPosition, clickTv.getText().toString());
        }
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomTabLayout);
            mTextSize = typedArray.getInt(R.styleable.CustomTabLayout_ctlTextSize, -1);
            mDefaultTextColor = typedArray.getColor(R.styleable.CustomTabLayout_ctlTextColor, -1);
            mTvBackground = typedArray.getColor(R.styleable.CustomTabLayout_ctlTvBackground, -1);
            mUnderlineWidth = typedArray.getInt(R.styleable.CustomTabLayout_ctlUnderlineWidth, -1);
            mUnderlineHeight = typedArray.getInt(R.styleable.CustomTabLayout_ctlUnderlineHeight, -1);
            mUnderlineCol = typedArray.getInt(R.styleable.CustomTabLayout_ctlUnderlineColor, -1);
            mCheckedTextCol = typedArray.getInt(R.styleable.CustomTabLayout_ctlCheckedTextColor, -1);
            mUnderlineDuration = typedArray.getInt(R.styleable.CustomTabLayout_ctlUnderlineDuration, -1);
            mHorizontalSpace = typedArray.getInt(R.styleable.CustomTabLayout_ctlHorizontalSpace, -1);
            mTextStyle = typedArray.getString(R.styleable.CustomTabLayout_ctlTextStyle);
            typedArray.recycle();
        }

        if (mTextSize == -1) {
            mTextSize = 15;
        }

        if (mDefaultTextColor == -1) {
            mDefaultTextColor = Color.parseColor("#333333");
        }

        if (mTvBackground == -1) {
            mTvBackground = Color.parseColor("#00000000");
        }

        if (mUnderlineWidth == -1) {
            mUnderlineWidth = 20;
        }

        if (mUnderlineHeight == -1) {
            mUnderlineHeight = 2;
        }

        if (mUnderlineCol == -1) {
            mUnderlineCol = Color.parseColor("#3F51B5");
        }

        if (mCheckedTextCol == -1) {
            mCheckedTextCol = mUnderlineCol;
        }

        if (mUnderlineDuration == -1) {
            mUnderlineDuration = 150;
        }

        if (mHorizontalSpace == -1) {
            mHorizontalSpace = 20;
        }

        if (TextUtils.isEmpty(mTextStyle)) {
            mTextStyle = TEXT_STYLE_NORMAL;
        }

        LayoutInflater.from(context).inflate(R.layout.common_layout_custom_tab, this, true);
        mTvContainerLl = findViewById(R.id.ll_text_view_container);
        mUnderlineView = findViewById(R.id.view_underline);
        mUnderlineView.setBackgroundColor(mUnderlineCol);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        TextView firstTv = (TextView) mTvContainerLl.getChildAt(initPosition);
        if (firstTv == null) {
            return;
        }
        mUnderlineView.layout(getPaddingLeft() + firstTv.getLeft() + firstTv.getMeasuredWidth() / 2 - dp2px(mUnderlineWidth) / 2,
                getMeasuredHeight() - dp2px(mUnderlineHeight - getPaddingBottom()),
                getPaddingLeft() + firstTv.getLeft() + firstTv.getMeasuredWidth() / 2 + dp2px(mUnderlineWidth) / 2,
                getMeasuredHeight() - getPaddingBottom());
    }

    private int dp2px(float dpValue) {
        float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private void setChildLayout() {
        mTvContainerLl.removeAllViews();
        for (int i = 0; i < mTitleList.size(); i++) {
            final String title = mTitleList.get(i);
            final TextView textView = new TextView(mContext);
            textView.setText(title);
            if (i == initPosition) {
                textView.setTextColor(mCheckedTextCol);
            } else {
                textView.setTextColor(mDefaultTextColor);
            }
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize);
            textView.setGravity(Gravity.CENTER);
            textView.setBackgroundColor(mTvBackground);
            TextPaint textPaint = textView.getPaint();
            switch (mTextStyle) {
                case TEXT_STYLE_NORMAL:
                    textPaint.setFakeBoldText(false);
                    break;

                case TEXT_STYLE_BOLD:
                    textPaint.setFakeBoldText(true);
                    break;

                default:
                    break;
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            if (i == mTitleList.size() - 1) {
                params.rightMargin = 0;
            } else {
                params.rightMargin = dp2px(mHorizontalSpace);
            }

            mTvContainerLl.addView(textView, params);

            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentPosition = mTitleList.indexOf(title);
                    if (mTabClickListener != null) {
                        mTabClickListener.tabClick(currentPosition, title);
                    }
                    startAnim(currentPosition);
                }
            });
        }
    }

    private void resetTextColor(int position) {
        for (int i = 0; i < mTvContainerLl.getChildCount(); i++) {
            if (i == position) {
                ((TextView) mTvContainerLl.getChildAt(i)).setTextColor(getCheckedTextCol());
            } else {
                ((TextView) mTvContainerLl.getChildAt(i)).setTextColor(mDefaultTextColor);
            }
        }
    }

    private int getCheckedTextCol() {
        if (!isCheckedTextSet) {
            return mUnderlineCol;
        } else {
            return mCheckedTextCol;
        }
    }


}
