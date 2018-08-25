package com.example.admin.customtablayout;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 自定义 TabLayout
 * Created by xiesuichao on 2018/6/23.
 */

public class CustomTabLayout extends HorizontalScrollView {

    private Context mContext;
    private List<String> mStrList = new ArrayList<>();
    private String[] mTitleArr;
    private LinearLayout mTvContainerLl;
    private View mUnderlineView;
    private int mTextSize = 15;
    private int mTextColor = Color.parseColor("#333333");
    private int mTvBackground = Color.parseColor("#ffffff");
    private int mUnderlineWidth = 20;
    private int mUnderlineHeight = 2;
    private int mUnderlineCol = Color.parseColor("#3F51B5");
    private int mUnderlineDuration = 150;
    private int mHorizontalSpace = 20;
    private int initPosition = 0;
    private int currentPosition = initPosition;
    private final String TEXT_STYLE_NORMAL = "0";
    private final String TEXT_STYLE_BOLD = "1";
    private String mTextStyle = TEXT_STYLE_NORMAL;
    private OnTabClickListener mTabClickListener;

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

    public void setOnTabClickListener(OnTabClickListener mTabClickListener) {
        this.mTabClickListener = mTabClickListener;
    }

    /**
     * 如果初始下划线position不为0，则调该方法调整初始position
     * @param position
     */
    public void initUnderlinePosition(int position) {
        if (position > mTvContainerLl.getChildCount() - 1){
            return;
        }
        this.initPosition = position;
        this.currentPosition = position;
        for (int i = 0; i < mTvContainerLl.getChildCount(); i++) {
            if (i == position){
                ((TextView) mTvContainerLl.getChildAt(i)).setTextColor(mUnderlineCol);
            }else {
                ((TextView) mTvContainerLl.getChildAt(i)).setTextColor(mTextColor);
            }
        }
    }

    /**
     * 移动下划线
     * @param position
     */
    public void moveToPosition(int position) {
        if (position < 0){
            return;
        }
        this.currentPosition = position;
        startAnim((TextView) mTvContainerLl.getChildAt(position));
    }

    /**
     * 设置tab标签
     * @param titleArr
     */
    public void setTitleArr(final String[] titleArr) {
        if (titleArr == null) {
            return;
        }
        mTitleArr = titleArr;
        setChildLayout();
    }

    /**
     * 获取当前下划线position
     * @return
     */
    public int getUnderlinePosition(){
        return currentPosition;
    }

    private void startAnim(TextView clickTv) {
        TextView firstTv = (TextView) mTvContainerLl.getChildAt(initPosition);
        float animStartX = firstTv.getLeft() + firstTv.getMeasuredWidth() / 2 - dp2px(mUnderlineWidth) / 2;
        float clickEndX = clickTv.getLeft() + clickTv.getMeasuredWidth() / 2 - dp2px(mUnderlineWidth) / 2;
        ObjectAnimator.ofFloat(mUnderlineView, "translationX", clickEndX - animStartX)
                .setDuration(mUnderlineDuration)
                .start();
        for (int i = 0; i < mTvContainerLl.getChildCount(); i++) {
            ((TextView) mTvContainerLl.getChildAt(i)).setTextColor(mTextColor);
        }
        clickTv.setTextColor(mUnderlineCol);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomTabLayout);
            mTextSize = typedArray.getInt(R.styleable.CustomTabLayout_ctlTextSize, mTextSize);
            mTextColor = typedArray.getColor(R.styleable.CustomTabLayout_ctlTextColor, mTextColor);
            mTvBackground = typedArray.getColor(R.styleable.CustomTabLayout_ctlTvBackground, mTvBackground);
            mUnderlineWidth = typedArray.getInt(R.styleable.CustomTabLayout_ctlUnderlineWidth, mUnderlineWidth);
            mUnderlineHeight = typedArray.getInt(R.styleable.CustomTabLayout_ctlUnderlineHeight, mUnderlineHeight);
            mUnderlineCol = typedArray.getInt(R.styleable.CustomTabLayout_ctlUnderlineColor, mUnderlineCol);
            mUnderlineDuration = typedArray.getInt(R.styleable.CustomTabLayout_ctlUnderlineDuration, mUnderlineDuration);
            mHorizontalSpace = typedArray.getInt(R.styleable.CustomTabLayout_ctlHorizontalSpace, mHorizontalSpace);
            mTextStyle = typedArray.getString(R.styleable.CustomTabLayout_ctlTextStyle);
            if (TextUtils.isEmpty(mTextStyle)) {
                mTextStyle = TEXT_STYLE_NORMAL;
            }
            typedArray.recycle();
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
        mUnderlineView.layout(getPaddingLeft() + firstTv.getLeft() + firstTv.getMeasuredWidth() / 2 - dp2px(mUnderlineWidth) / 2,
                getMeasuredHeight() - dp2px(mUnderlineHeight - getPaddingBottom()),
                getPaddingLeft() + firstTv.getLeft() + firstTv.getMeasuredWidth() / 2 + dp2px(mUnderlineWidth) / 2,
                getMeasuredHeight() - getPaddingBottom());

    }

    private int dp2px(float dpValue) {
        float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private void setChildLayout(){
        mTvContainerLl.removeAllViews();
        Collections.addAll(mStrList, mTitleArr);

        for (int i = 0; i < mTitleArr.length; i++) {
            final String title = mTitleArr[i];
            final TextView textView = new TextView(mContext);
            textView.setText(mTitleArr[i]);
            if (i == initPosition){
                textView.setTextColor(mUnderlineCol);
            }else {
                textView.setTextColor(mTextColor);
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
            if (i == mTitleArr.length - 1){
                params.rightMargin = 0;
            }else {
                params.rightMargin = dp2px(mHorizontalSpace);
            }

            mTvContainerLl.addView(textView, params);

            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentPosition = mStrList.indexOf(title);
                    startAnim((TextView) v);
                    if (mTabClickListener != null){
                        mTabClickListener.tabClick(currentPosition, title);
                    }
                }
            });

        }

    }

}
