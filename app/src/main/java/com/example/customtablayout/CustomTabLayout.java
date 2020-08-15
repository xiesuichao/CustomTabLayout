package com.example.customtablayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

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
    private ViewPager hostViewPager;
    private int textSize, normalTextColor, tvBackgroundColor, underlineWidth, underlineHeight,
            underlineColor, checkedTextColor, animDuration, horizontalSpace, textPaddingLeft,
            textPaddingRight, underlineMarginBottom, underlineBgResId, indicatorResId,
            indicatorPaddingLeft, indicatorPaddingTop, indicatorPaddingRight, indicatorPaddingBottom,
            indicatorPadding, firstLeftSpace, lastRightSpace;
    private int initPosition = 0;
    private int totalTextViewWidth = 0;
    private int currentPosition = initPosition;
    private float animScaleStart = 1f;
    private final String TEXT_STYLE_NORMAL = "0";
    private final String TEXT_STYLE_BOLD = "1";
    private String textStyle;
    private OnTabClickListener tabClickListener;
    private OnTabScrollListener tabScrollListener;
    private boolean isCheckedTextSet = false;
    private boolean isAdaptive = false;//是否自适应, 空余间隔自动平分
    private boolean isSpaceEqualsForTwo = false;//title只有两个时，间隔分配是否全等
    private boolean isAddSpaceForTwo = false;//title只有两个时，最左边和最右边是否添加间隔
    private boolean isFirstLayout = true;
    private Rect textRect;

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
//        startAnim(position);
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
        addTextView();
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
        this.underlineColor = colorId;
        underlineView.setBackgroundColor(underlineColor);
        resetTextColor(currentPosition);
    }

    /**
     * 设置选中字体颜色
     */
    public void setCheckedTextColor(int colorId) {
        this.checkedTextColor = colorId;
        this.isCheckedTextSet = true;
        resetTextColor(currentPosition);
    }

    public void setHostViewPager(ViewPager viewPager){
        this.hostViewPager = viewPager;
        this.hostViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Print.w("position", position);
                Print.w("positionOffset", positionOffset);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measureWidth = getMeasuredWidth();
        if (isAdaptive){
            setAdaptiveLayout(measureWidth);
        } else {
            initTvLayout();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        setIndicatorLayout();
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CustomTabLayout);
            textSize = typedArray.getDimensionPixelSize(R.styleable.CustomTabLayout_ctlTvTextSize, 15);
            textPaddingLeft = typedArray.getDimensionPixelSize(R.styleable.CustomTabLayout_ctlTvPaddingLeft, 0);
            textPaddingRight = typedArray.getDimensionPixelSize(R.styleable.CustomTabLayout_ctlTvPaddingRight, 0);
            normalTextColor = typedArray.getColor(R.styleable.CustomTabLayout_ctlTvTextColorNormal, 0xff333333);
            checkedTextColor = typedArray.getInt(R.styleable.CustomTabLayout_ctlTvTextColorChecked, 0xff333333);
            textStyle = typedArray.getString(R.styleable.CustomTabLayout_ctlTvTextStyle);
            tvBackgroundColor = typedArray.getColor(R.styleable.CustomTabLayout_ctlTvBackgroundColor, 0x00000000);
            underlineWidth = typedArray.getDimensionPixelSize(R.styleable.CustomTabLayout_ctlUnderlineWidth, 20);
            underlineHeight = typedArray.getDimensionPixelSize(R.styleable.CustomTabLayout_ctlUnderlineHeight, 2);
            underlineMarginBottom = typedArray.getDimensionPixelSize(R.styleable.CustomTabLayout_ctlUnderlineMarginBottom, 0);
            underlineBgResId = typedArray.getResourceId(R.styleable.CustomTabLayout_ctlUnderlineBackground, -1);
            underlineColor = typedArray.getInt(R.styleable.CustomTabLayout_ctlUnderlineColor, 0xff3F51B5);
            indicatorResId = typedArray.getResourceId(R.styleable.CustomTabLayout_ctlIndicatorSrc, -1);
            indicatorPaddingLeft = typedArray.getDimensionPixelSize(R.styleable.CustomTabLayout_ctlIndicatorPaddingLeft, 0);
            indicatorPaddingTop = typedArray.getDimensionPixelSize(R.styleable.CustomTabLayout_ctlIndicatorPaddingTop, 0);
            indicatorPaddingRight = typedArray.getDimensionPixelSize(R.styleable.CustomTabLayout_ctlIndicatorPaddingRight, 0);
            indicatorPaddingBottom = typedArray.getDimensionPixelSize(R.styleable.CustomTabLayout_ctlIndicatorPaddingBottom, 0);
            indicatorPadding = typedArray.getDimensionPixelSize(R.styleable.CustomTabLayout_ctlIndicatorPadding, 0);
            animDuration = typedArray.getInt(R.styleable.CustomTabLayout_ctlAnimDuration, 150);
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

        textRect = new Rect();

        if (TextUtils.isEmpty(textStyle)) {
            textStyle = TEXT_STYLE_NORMAL;
        }

        if (indicatorResId != -1) {
            indicatorIv.setImageResource(indicatorResId);
        } else if (underlineBgResId != -1) {
            underlineView.setBackgroundResource(underlineBgResId);
        } else {
            underlineView.setBackgroundColor(underlineColor);
        }

        calculateLeftRightSpace();
    }

    private void addTextView() {
        tvContainerLl.removeAllViews();

        for (int i = 0; i < titleList.size(); i++) {
            final String title = titleList.get(i);
            final TextView textView = new TextView(getContext());
            textView.setText(title);
            if (i == initPosition) {
                textView.setTextColor(checkedTextColor);
            } else {
                textView.setTextColor(normalTextColor);
            }
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            textView.setGravity(Gravity.CENTER);
//            textView.setBackgroundColor(tvBackgroundColor);
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
            tvContainerLl.addView(textView, params);

            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int clickPosition = tvContainerLl.indexOfChild(textView);
                    Print.w("clickPosition", clickPosition);
                    startAnim(currentPosition, clickPosition);

                    currentPosition = clickPosition;
                    if (tabClickListener != null) {
                        tabClickListener.tabClick(currentPosition, title);
                    }
                }
            });
        }
    }

    //设置固定间距
    private void initTvLayout() {
        int count = tvContainerLl.getChildCount();
        for (int i = 0; i < count; i++) {
            TextView textView = (TextView) tvContainerLl.getChildAt(i);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textView.getLayoutParams();

            if (indicatorResId == -1) {
                if (i == titleList.size() - 1) {
                    params.rightMargin = 0;
                } else {
                    params.rightMargin = horizontalSpace;
                }
            } else if (indicatorPadding != 0) {
                if (i == 0) {
                    if (textPaddingLeft < indicatorPadding) {
                        params.leftMargin = indicatorPadding - textPaddingLeft;
                    }

                } else if (i == titleList.size() - 1) {
                    if (textPaddingRight < indicatorPadding) {
                        params.rightMargin = indicatorPadding - textPaddingRight;
                    }
                } else {
                    params.rightMargin = horizontalSpace;
                }
            } else if (indicatorPaddingLeft != 0 || indicatorPaddingRight != 0) {
                if (i == 0) {
                    if (textPaddingLeft < indicatorPaddingLeft) {
                        params.leftMargin = indicatorPaddingLeft - textPaddingLeft;
                        params.rightMargin = horizontalSpace;
                    }

                } else if (i == titleList.size() - 1) {
                    if (textPaddingRight < indicatorPaddingRight) {
                        params.rightMargin = indicatorPaddingRight - textPaddingRight;
                    }
                } else {
                    params.rightMargin = horizontalSpace;
                }
            } else {
                if (i == titleList.size() - 1) {
                    params.rightMargin = 0;
                } else {
                    params.rightMargin = horizontalSpace;
                }
            }
        }
    }

    //设置自适应间距
    private void setAdaptiveLayout(int measureWidth){
        int childCount = tvContainerLl.getChildCount();
        if (childCount <= 1) {
            return;
        }
        int titleCount = titleList.size();
        int totalViewWidth = measureWidth - getPaddingLeft() - getPaddingRight() - (textPaddingLeft + textPaddingRight) * titleCount;
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
            int space = (totalViewWidth - totalTextViewWidth - firstLeftSpace - lastRightSpace) / ((titleList.size() - 1));
            for (int i = 0; i < childCount; i++) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tvContainerLl.getChildAt(i).getLayoutParams();
                if (i == 0) {
                    params.leftMargin = firstLeftSpace;
                    params.rightMargin = space;
                } else if (i == titleList.size() - 1) {
                    params.rightMargin = lastRightSpace;
                } else {
                    params.rightMargin = space;
                }
            }
        }
    }

    //设置游标位置
    private void setIndicatorLayout(){
        TextView currentTv = null;
        try {
            if (tvContainerLl.getChildCount() > initPosition){
                currentTv = (TextView) tvContainerLl.getChildAt(initPosition);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (currentTv == null || !isFirstLayout) {
            return;
        }
        isFirstLayout = false;
        int tvMeasuredWidth = currentTv.getMeasuredWidth();
        int tvMeasuredHeight = currentTv.getMeasuredHeight();
        if (indicatorResId != -1) {
            Paint paint = currentTv.getPaint();
            String text = currentTv.getText().toString().trim();
            paint.getTextBounds(text, 0, text.length(), textRect);
            int textWidth = textRect.width();
            int textHeight = textRect.height();
            if (indicatorPadding != 0){
                indicatorIv.layout(currentTv.getLeft() + tvMeasuredWidth / 2 - textWidth / 2 - indicatorPadding,
                        currentTv.getTop() + tvMeasuredHeight / 2 - textHeight / 2 - indicatorPadding,
                        currentTv.getLeft() + tvMeasuredWidth / 2 + textWidth / 2 + indicatorPadding,
                        currentTv.getTop() + tvMeasuredHeight / 2 + textHeight / 2 + indicatorPadding);
            } else {
                indicatorIv.layout(currentTv.getLeft() + tvMeasuredWidth / 2 - textWidth / 2 - indicatorPaddingLeft,
                        currentTv.getTop() + tvMeasuredHeight / 2 - textHeight / 2 - indicatorPaddingTop,
                        currentTv.getLeft() + tvMeasuredWidth / 2 + textWidth / 2 + indicatorPaddingRight,
                        currentTv.getTop() + tvMeasuredHeight / 2 + textHeight / 2 + indicatorPaddingBottom);
            }

//            Print.w("indicatorIv.getLeft", indicatorIv.getLeft());
//            Print.w("indicatorIv.getX", indicatorIv.getX());
//            Print.w("indicatorIv.getWidth", indicatorIv.getWidth());
//            Print.w("indicatorIv.getMeasureWidth", indicatorIv.getMeasuredWidth());
        } else {
            underlineView.layout(currentTv.getLeft() + tvMeasuredWidth / 2 - underlineWidth / 2,
                    getMeasuredHeight() - underlineHeight - underlineMarginBottom - getPaddingBottom(),
                    currentTv.getLeft() + tvMeasuredWidth / 2 + underlineWidth / 2,
                    getMeasuredHeight() - underlineMarginBottom - getPaddingBottom());
            Print.w("underLineView.getLeft", underlineView.getLeft());
            Print.w("underLineView.getX", underlineView.getX());
        }
    }

    //平移缩放动画
    private void startAnim(int startPosition, final int clickPosition) {
        TextView firstTv = (TextView) tvContainerLl.getChildAt(initPosition);
        TextView startTv = (TextView) tvContainerLl.getChildAt(startPosition);
        final TextView endTv = (TextView) tvContainerLl.getChildAt(clickPosition);
        final float originWidth = firstTv.getWidth();
        final float clickWidth = endTv.getWidth();
//        Print.w("originWidth", originWidth);
//        Print.w("clickWidth", clickWidth);
        if (indicatorResId != -1){
            float startX = startTv.getX() + startTv.getWidth() / 2f - indicatorIv.getWidth() / 2f;
            float endX = endTv.getX() + endTv.getWidth() / 2f - indicatorIv.getWidth() / 2f;
            AnimatorSet animatorSet = new AnimatorSet();//组合动画
            ObjectAnimator translateAnimator = ObjectAnimator.ofFloat(indicatorIv, "translationX", startX, endX);
            ObjectAnimator scaleAnimator = ObjectAnimator.ofFloat(indicatorIv, "scaleX", animScaleStart, clickWidth / originWidth);
            scaleAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    animScaleStart = clickWidth / originWidth;
//                    Print.w("animScaleStart", animScaleStart);
                }
            });
            animatorSet.setDuration(animDuration);
            animatorSet.setInterpolator(new LinearInterpolator());
            animatorSet.play(translateAnimator)
                    .with(scaleAnimator);
            animatorSet.start();

        } else {
            float startX = startTv.getX() + startTv.getWidth() / 2f - underlineWidth / 2f;
            float endX = endTv.getX() + endTv.getWidth() / 2f - underlineWidth / 2f;

            Print.w("startX", startX);
            Print.w("endX", endX);
            ObjectAnimator.ofFloat(underlineView, "translationX", startX, endX)
                    .setDuration(animDuration)
                    .start();
            if (underlineBgResId == -1) {
                underlineView.setBackgroundColor(underlineColor);
            }
        }
        for (int i = 0; i < tvContainerLl.getChildCount(); i++) {
            ((TextView) tvContainerLl.getChildAt(i)).setTextColor(normalTextColor);
        }
        endTv.setTextColor(checkedTextColor);

        if (tabScrollListener != null) {
            tabScrollListener.scrollChange(clickPosition, endTv.getText().toString());
        }
    }

    //当背景图游标长度大于text padding长度，左右两边会显示不完整
    private void calculateLeftRightSpace(){
        if (indicatorResId != -1){
            if (indicatorPadding != 0){
                if (indicatorPadding > textPaddingLeft){
                    firstLeftSpace = indicatorPadding - textPaddingLeft;
                }
                if (indicatorPadding > textPaddingRight){
                    lastRightSpace = indicatorPadding - textPaddingRight;
                }
            } else if (indicatorPaddingLeft > 0 || indicatorPaddingRight > 0){
                if (indicatorPaddingLeft > textPaddingLeft){
                    firstLeftSpace = indicatorPaddingLeft - textPaddingLeft;
                }
                if (indicatorPaddingRight > textPaddingRight){
                    lastRightSpace = indicatorPaddingRight - textPaddingRight;
                }
            }
        }
    }

    private void resetTextColor(int position) {
        for (int i = 0; i < tvContainerLl.getChildCount(); i++) {
            if (i == position) {
                ((TextView) tvContainerLl.getChildAt(i)).setTextColor(getCheckedTextCol());
            } else {
                ((TextView) tvContainerLl.getChildAt(i)).setTextColor(normalTextColor);
            }
        }
    }

    private int getCheckedTextCol() {
        if (!isCheckedTextSet) {
            return underlineColor;
        } else {
            return checkedTextColor;
        }
    }

}
