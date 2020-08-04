package com.example.customtablayout;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
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
    private ImageView indicatorIv;
    private HorizontalScrollView rootHs;
    private int textSize, defaultTextColor, tvBackgroundColor, underlineWidth, underlineHeight,
            underlineCol, checkedTextCol, underlineDuration, horizontalSpace, textPaddingLeft,
            textPaddingRight, underlineMarginBottom;
    private int underlineBackground;
    private int initPosition = 0;
    private int totalTextViewWidth = 0;
    private int currentPosition = initPosition;
    private final String TEXT_STYLE_NORMAL = "0";
    private final String TEXT_STYLE_BOLD = "1";
    private String textStyle;
    private OnTabClickListener tabClickListener;
    private OnTabScrollListener tabScrollListener;
    private boolean isCheckedTextSet = false;
    private boolean isAdaptive = false;//是否自适应, 空余间隔自动平分
    private boolean isSpaceEqualsForTwo = false;//title只有两个时，间隔分配是否全等
    private boolean isAddSpaceForTwo = false;//title只有两个时，最左边和最右边是否添加间隔

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
        Print.w("setTitleList");
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (isAdaptive){
            int childCount = tvContainerLl.getChildCount();
            if (childCount <= 1) {
                return;
            }
            int titleCount = titleList.size();
            int totalViewWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight() - (textPaddingLeft + textPaddingRight) * titleCount;
            //若title只有两个，且需要给最左边和最右边间隔，间隔自动平分需要给第一个leftMargin，和最后一个rightMargin
            if (childCount == 2 && isAddSpaceForTwo) {
                int space;
                if (isSpaceEqualsForTwo) {
                    space = (totalViewWidth - totalTextViewWidth) / 3;
                } else {
                    space = (totalViewWidth - totalTextViewWidth) / 4;
                }
                for (int i = 0; i < childCount; i++) {
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tvContainerLl.getChildAt(i).getLayoutParams();
                    if (isSpaceEqualsForTwo) {
                        if (i == 0) {
                            params.leftMargin = space;
                            params.rightMargin = space / 2;
                        } else {
                            params.leftMargin = space / 2;
                            params.rightMargin = space;
                        }
                    } else {
                        params.leftMargin = space;
                        params.rightMargin = space;
                    }
                }

            } else {
                //若title大于2个，最左边和最右边不给间隔，剩下的平分
                int space = (totalViewWidth - totalTextViewWidth) / ((titleList.size() - 1));
                for (int i = 0; i < childCount; i++) {
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tvContainerLl.getChildAt(i).getLayoutParams();
                    if (i == titleList.size() - 1) {
                        params.rightMargin = 0;
                    } else {
                        params.rightMargin = space;
                    }
                }
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        TextView firstTv = (TextView) tvContainerLl.getChildAt(initPosition);
        if (firstTv == null) {
            return;
        }
        underlineView.layout(firstTv.getLeft() + firstTv.getMeasuredWidth() / 2 - underlineWidth / 2,
                getMeasuredHeight() - underlineHeight - underlineMarginBottom - getPaddingBottom(),
                firstTv.getLeft() + firstTv.getMeasuredWidth() / 2 + underlineWidth / 2,
                getMeasuredHeight() - underlineMarginBottom - getPaddingBottom());
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CustomTabLayout);
            textSize = typedArray.getDimensionPixelSize(R.styleable.CustomTabLayout_ctlTvTextSize, 15);
            textPaddingLeft = typedArray.getDimensionPixelSize(R.styleable.CustomTabLayout_ctlTvPaddingLeft, 0);
            textPaddingRight = typedArray.getDimensionPixelSize(R.styleable.CustomTabLayout_ctlTvPaddingRight, 0);
            defaultTextColor = typedArray.getColor(R.styleable.CustomTabLayout_ctlTvTextColor, 0xff333333);
            textStyle = typedArray.getString(R.styleable.CustomTabLayout_ctlTvTextStyle);
            checkedTextCol = typedArray.getInt(R.styleable.CustomTabLayout_ctlTvCheckedTextColor, 0xff333333);
            tvBackgroundColor = typedArray.getColor(R.styleable.CustomTabLayout_ctlTvBackgroundColor, 0x00000000);
            underlineWidth = typedArray.getDimensionPixelSize(R.styleable.CustomTabLayout_ctlUnderlineWidth, 20);
            underlineHeight = typedArray.getDimensionPixelSize(R.styleable.CustomTabLayout_ctlUnderlineHeight, 2);
            underlineMarginBottom = typedArray.getDimensionPixelSize(R.styleable.CustomTabLayout_ctlUnderlineMarginBottom, 0);
            underlineBackground = typedArray.getResourceId(R.styleable.CustomTabLayout_ctlUnderlineBackground, -1);
            underlineCol = typedArray.getInt(R.styleable.CustomTabLayout_ctlUnderlineColor, 0xff3F51B5);
            underlineDuration = typedArray.getInt(R.styleable.CustomTabLayout_ctlUnderlineDuration, 150);
            horizontalSpace = typedArray.getDimensionPixelSize(R.styleable.CustomTabLayout_ctlHorizontalSpace, 20);
            isAdaptive = typedArray.getBoolean(R.styleable.CustomTabLayout_ctlAdaptive, false);
            isSpaceEqualsForTwo = typedArray.getBoolean(R.styleable.CustomTabLayout_ctlSpaceEqualsForTwo, false);
            isAddSpaceForTwo = typedArray.getBoolean(R.styleable.CustomTabLayout_ctlSpaceEqualsForTwo, false);
            typedArray.recycle();
        }

        LayoutInflater.from(getContext()).inflate(R.layout.layout_custom_tab, this, true);
        tvContainerLl = findViewById(R.id.ll_tab_container);
        underlineView = findViewById(R.id.view_tab_underline);
        indicatorIv = findViewById(R.id.iv_tab_indicator);
        rootHs = findViewById(R.id.hs_tab_root);

        if (TextUtils.isEmpty(textStyle)) {
            textStyle = TEXT_STYLE_NORMAL;
        }

        if (underlineBackground == -1){
            underlineView.setBackgroundColor(underlineCol);
        } else {
            underlineView.setBackgroundResource(underlineBackground);
        }
    }

    private void startAnim(int clickPosition) {
        TextView firstTv = (TextView) tvContainerLl.getChildAt(initPosition);
        TextView clickTv = (TextView) tvContainerLl.getChildAt(clickPosition);
        float animStartX = firstTv.getLeft() + firstTv.getMeasuredWidth() / 2f - underlineWidth / 2f;
        float clickEndX = clickTv.getLeft() + clickTv.getMeasuredWidth() / 2f - underlineWidth / 2f;
        ObjectAnimator.ofFloat(underlineView, "translationX", clickEndX - animStartX)
                .setDuration(underlineDuration)
                .start();
        for (int i = 0; i < tvContainerLl.getChildCount(); i++) {
            ((TextView) tvContainerLl.getChildAt(i)).setTextColor(defaultTextColor);
        }
        clickTv.setTextColor(checkedTextCol);
        if (underlineBackground == -1){
            underlineView.setBackgroundColor(underlineCol);
        }
        if (tabScrollListener != null) {
            tabScrollListener.scrollChange(clickPosition, clickTv.getText().toString());
        }
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
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            textView.setGravity(Gravity.CENTER);
            textView.setBackgroundColor(tvBackgroundColor);
            textView.setPadding(textPaddingLeft, 0, textPaddingRight, 0);
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

            totalTextViewWidth += textPaint.measureText(title);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            if (i == titleList.size() - 1) {
                params.rightMargin = 0;
            } else {
                params.rightMargin = horizontalSpace;
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
