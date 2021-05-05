package com.lihai.indexbarrecyclerview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class IndexBar extends View {
    private int mTotalHeight;
    private float mTextSpace;

    private List<String> mIndexList = new ArrayList<>();
    // 首字母 到 mIndexList 的映射
    private HashMap<String, Integer> mMapping = new HashMap<>();
    private ArrayList<IndexableEntity> mDatas;

    private int mSelectionPosition;
    private float mIndexHeight;

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mFocusPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mFocusBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public IndexBar(Context context) {
        super(context);
        mockData();
    }

    private void mockData() {
        mIndexList.add("A");
        mIndexList.add("B");
        mIndexList.add("C");
        mIndexList.add("D");
        mIndexList.add("E");
    }

    private int barFocusTextBgColor;
    void init(Drawable barBg, int barTextColor, int barFocusTextColor, int barFocusTextBgColor, float barTextSize, float textSpace) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(barBg);
        } else {
            setBackgroundDrawable(barBg);
        }
        this.barFocusTextBgColor = barFocusTextBgColor;
        this.mTextSpace = textSpace;

        mPaint.setColor(barTextColor);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(barTextSize);

        mFocusPaint.setTextAlign(Paint.Align.CENTER);
        mFocusPaint.setTextSize(barTextSize + (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        mFocusPaint.setColor(barFocusTextColor);

        mFocusBgPaint.setColor(barFocusTextBgColor);

        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if (mIndexList.size() > 0) {
            mTotalHeight = (int) (((mIndexList.size() - 1) * mPaint.getTextSize()
                    + mFocusPaint.getTextSize())
                    + (mIndexList.size() + 1) * mTextSpace);
        }

        if (mTotalHeight > height) {
            mTotalHeight = height;
        }

//        // TODO: 16/10/8  Measure AT_MOST
//        if (mode == MeasureSpec.AT_MOST) {
//            int maxWidth = (int) getResources().getDimension(R.dimen.default_indexBar_layout_width);
//            super.onMeasure(MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(mTotalHeight, MeasureSpec.EXACTLY));
//            return;
//        }
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(mTotalHeight, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mIndexList.size() == 0) return;

        mIndexHeight = ((float) getHeight()) / mIndexList.size();

        for (int i = 0; i < mIndexList.size(); i++) {
            if (mSelectionPosition == i) {
                canvas.drawCircle(getWidth() / 2, mIndexHeight * 0.6f + mIndexHeight * i, mPaint.getTextSize() / 2 + 2, mFocusBgPaint);
                canvas.drawText(mIndexList.get(i), getWidth() / 2, mIndexHeight * 0.85f + mIndexHeight * i, mFocusPaint);
            } else {
                canvas.drawText(mIndexList.get(i), getWidth() / 2, mIndexHeight * 0.85f + mIndexHeight * i, mPaint);
            }
        }
    }

    int getPositionForPointY(float y) {
        if (mIndexList.size() <= 0) return -1;

        int position = (int) (y / mIndexHeight);

        if (position < 0) {
            position = 0;
        } else if (position > mIndexList.size() - 1) {
            position = mIndexList.size() - 1;
        }

        return position;
    }


    int getSelectionPosition() {
        return mSelectionPosition;
    }

    void setSelectionPosition(int position) {
        this.mSelectionPosition = position;
        invalidate();
    }

    int getFirstRecyclerViewPositionBySelection() {
        String index = mIndexList.get(mSelectionPosition);
        if (mMapping.containsKey(index)) {
            return mMapping.get(index);
        }
        return -1;
    }

    List<String> getIndexList() {
        return mIndexList;
    }

    void setDatas(boolean showAllLetter, ArrayList<IndexableEntity> datas) {
        //从数据集合里面解析出所有索引

    }

    void setIndexList(List<String> indexList) {
        mIndexList = indexList;
        invalidate();
    }

    void setSelectionPos(int position) {
        mSelectionPosition = position;
        invalidate();
    }

    private OnIndexChangedListener mClickListener;

    public static interface OnIndexChangedListener {
        void onSideBarScrollUpdateItem(String word);//滚动位置
        void onSideBarScrollEndHideText();//隐藏提示文本
    }

    public void setIndexChangedListener(OnIndexChangedListener listener) {
        this.mClickListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                int index = (int) (event.getY() / getHeight() * mIndexList.size());
                if (index >= 0 && index < mIndexList.size()) {
                    if(mClickListener!=null){
                        mClickListener.onSideBarScrollUpdateItem(mIndexList.get(index));
                    }
                    mSelectionPosition=index;
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if(mClickListener!=null){
                    mClickListener.onSideBarScrollEndHideText();
                }
                break;
        }
        return true;
    }
}
