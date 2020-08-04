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

    private List<String> titleList = new ArrayList<>();
    private LinearLayout tvContainerLl;
    private View underlineView;
    private int textSize, defaultTextColor, tvBackground, underlineWidth, underlineHeight, 
            underlineCol, checkedTextCol, underlineDuration, horizontalSpace;
    private int initPosition = 0;
    private int currentPosition = initPosition;
    private final String TEXT_STYLE_NORMAL = "0";
    private final String TEXT_STYLE_BOLD = "1";
    private String textStyle;
    private OnTabClickListener tabClickListener;
    private OnTabScrollListener tabScrollListener;
    private boolean isCheckedTextSet = false;

    public CustomTabLayout(Context context) {
        this(context, null);
    }

    public CustomTabLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomTabLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public interface OnTabClickListener {
        void tabClick(int position, String str);
    }

    /**
     * 点击事件
     */
    public void setOnTabClickListener(OnTabClickListener tabClickListener) {
        this.tabClickListener = tabClickListener;
    }

    public interface OnTabScrollListener {
        void scrollChange(int position, String text);
    }

    /**
     * 滑动监听
     */
    public void setOnTabScrollListener(OnTabScrollListener scrollListener) {
        this.tabScrollListener = scrollListener;
    }

    /**
     * 如果初始下划线position不为0，则调该方法调整初始position
     */
    public void initUnderlinePosition(int position) {
        if (position > tvContainerLl.getChildCount() - 1) {
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
        this.titleList.clear();
        this.titleList.addAll(titleList);
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
        return titleList;
    }

    /**
     * 获取当前被选中的title
     */
    public String getCheckedText() {
        return titleList.get(currentPosition);
    }

    /**
     * 获取当前被选中的TextView
     */
    public TextView getCheckedTextView() {
        return (TextView) tvContainerLl.getChildAt(currentPosition);
    }

    /**
     * 设置下划线颜色
     */
    public void setUnderlineColor(int colorId) {
        this.underlineCol = colorId;
        underlineView.setBackgroundColor(underlineCol);
        resetTextColor(currentPosition);
    }

    /**
     * 设置选中字体颜色
     */
    public void setCheckedTextColor(int colorId) {
        this.checkedTextCol = colorId;
        this.isCheckedTextSet = true;
        resetTextColor(currentPosition);
    }

    private void startAnim(int clickPosition) {
        TextView firstTv = (TextView) tvContainerLl.getChildAt(initPosition);
        TextView clickTv = (TextView) tvContainerLl.getChildAt(clickPosition);
        float animStartX = firstTv.getLeft() + firstTv.getMeasuredWidth() / 2f - dp2px(underlineWidth) / 2f;
        float clickEndX = clickTv.getLeft() + clickTv.getMeasuredWidth() / 2f - dp2px(underlineWidth) / 2f;
        ObjectAnimator.ofFloat(underlineView, "translationX", clickEndX - animStartX)
                .setDuration(underlineDuration)
                .start();
        for (int i = 0; i < tvContainerLl.getChildCount(); i++) {
            ((TextView) tvContainerLl.getChildAt(i)).setTextColor(defaultTextColor);
        }
        clickTv.setTextColor(getCheckedTextCol());
        underlineView.setBackgroundColor(underlineCol);
        if (tabScrollListener != null) {
            tabScrollListener.scrollChange(clickPosition, clickTv.getText().toString());
        }
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CustomTabLayout);
            textSize = typedArray.getInt(R.styleable.CustomTabLayout_ctlTextSize, -1);
            defaultTextColor = typedArray.getColor(R.styleable.CustomTabLayout_ctlTextColor, -1);
            tvBackground = typedArray.getColor(R.styleable.CustomTabLayout_ctlTvBackground, -1);
            underlineWidth = typedArray.getInt(R.styleable.CustomTabLayout_ctlUnderlineWidth, -1);
            underlineHeight = typedArray.getInt(R.styleable.CustomTabLayout_ctlUnderlineHeight, -1);
            underlineCol = typedArray.getInt(R.styleable.CustomTabLayout_ctlUnderlineColor, -1);
            checkedTextCol = typedArray.getInt(R.styleable.CustomTabLayout_ctlCheckedTextColor, -1);
            underlineDuration = typedArray.getInt(R.styleable.CustomTabLayout_ctlUnderlineDuration, -1);
            horizontalSpace = typedArray.getInt(R.styleable.CustomTabLayout_ctlHorizontalSpace, -1);
            textStyle = typedArray.getString(R.styleable.CustomTabLayout_ctlTextStyle);
            typedArray.recycle();
        }

        if (textSize == -1) {
            textSize = 15;
        }

        if (defaultTextColor == -1) {
            defaultTextColor = Color.parseColor("#333333");
        }

        if (tvBackground == -1) {
            tvBackground = Color.parseColor("#00000000");
        }

        if (underlineWidth == -1) {
            underlineWidth = 20;
        }

        if (underlineHeight == -1) {
            underlineHeight = 2;
        }

        if (underlineCol == -1) {
            underlineCol = Color.parseColor("#3F51B5");
        }

        if (checkedTextCol == -1) {
            checkedTextCol = underlineCol;
        }

        if (underlineDuration == -1) {
            underlineDuration = 150;
        }

        if (horizontalSpace == -1) {
            horizontalSpace = 20;
        }

        if (TextUtils.isEmpty(textStyle)) {
            textStyle = TEXT_STYLE_NORMAL;
        }

        LayoutInflater.from(getContext()).inflate(R.layout.common_layout_custom_tab, this, true);
        tvContainerLl = findViewById(R.id.ll_tab_container);
        underlineView = findViewById(R.id.view_tab_underline);
        underlineView.setBackgroundColor(underlineCol);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        TextView firstTv = (TextView) tvContainerLl.getChildAt(initPosition);
        if (firstTv == null) {
            return;
        }
        underlineView.layout(getPaddingLeft() + firstTv.getLeft() + firstTv.getMeasuredWidth() / 2 - dp2px(underlineWidth) / 2,
                getMeasuredHeight() - dp2px(underlineHeight - getPaddingBottom()),
                getPaddingLeft() + firstTv.getLeft() + firstTv.getMeasuredWidth() / 2 + dp2px(underlineWidth) / 2,
                getMeasuredHeight() - getPaddingBottom());
    }

    private int dp2px(float dpValue) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private void setChildLayout() {
        tvContainerLl.removeAllViews();
        for (int i = 0; i < titleList.size(); i++) {
            final String title = titleList.get(i);
            final TextView textView = new TextView(getContext());
            textView.setText(title);
            if (i == initPosition) {
                textView.setTextColor(checkedTextCol);
            } else {
                textView.setTextColor(defaultTextColor);
            }
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            textView.setGravity(Gravity.CENTER);
            textView.setBackgroundColor(tvBackground);
            TextPaint textPaint = textView.getPaint();
            switch (textStyle) {
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
            if (i == titleList.size() - 1) {
                params.rightMargin = 0;
            } else {
                params.rightMargin = dp2px(horizontalSpace);
            }

            tvContainerLl.addView(textView, params);

            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentPosition = titleList.indexOf(title);
                    if (tabClickListener != null) {
                        tabClickListener.tabClick(currentPosition, title);
                    }
                    startAnim(currentPosition);
                }
            });
        }
    }

    private void resetTextColor(int position) {
        for (int i = 0; i < tvContainerLl.getChildCount(); i++) {
            if (i == position) {
                ((TextView) tvContainerLl.getChildAt(i)).setTextColor(getCheckedTextCol());
            } else {
                ((TextView) tvContainerLl.getChildAt(i)).setTextColor(defaultTextColor);
            }
        }
    }

    private int getCheckedTextCol() {
        if (!isCheckedTextSet) {
            return underlineCol;
        } else {
            return checkedTextCol;
        }
    }


}
