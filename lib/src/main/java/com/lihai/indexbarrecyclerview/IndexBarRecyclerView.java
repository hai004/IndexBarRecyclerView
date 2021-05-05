package com.lihai.indexbarrecyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lihai.sidebarrecyclerview.R;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;

public class IndexBarRecyclerView extends FrameLayout implements IndexBar.OnIndexChangedListener {
    private RecyclerView mRecyclerView;
    private IndexableAdapter mAdapter;
    private IndexBar mIndexBar;
    private TreeMap<String, List<IndexableEntity>> mGroupMap;

    public IndexBarRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public IndexBarRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndexBarRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private Drawable mBarBg;
    private int mBarTextColor;
    private int mIndexTitleBgColor;
    private int mIndexTitleTextColor;
    private float mIndexTitleTextSize;
    private int mBarFocusTextColor;
    private int mSideBarFocusTextBgColor;
    private float mBarTextSize;
    private float mBarTextSpace;
    private float mBarWidth;
    private int mCenterOverlayWidth;
    private int mCenterOverlayHeight;
    private int mCenterOverlayTextColor;
    private float mCenterOverlayTextSize;
    private Context mContext;
    private List<GroupInfo> mGroupInfos = new ArrayList<>();
    private List<IndexableEntity> mDatas;
    private List<String> mIndexList;

    private void init(Context context, AttributeSet attrs) {
        this.mContext = context;
        //读取xml属性
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IndexBarRecyclerView);
            mBarTextColor = a.getColor(R.styleable.IndexBarRecyclerView_indexBar_textColor, ContextCompat.getColor(context, R.color.default_indexBar_textColor));
            mBarTextSize = a.getDimension(R.styleable.IndexBarRecyclerView_indexBar_textSize, getResources().getDimension(R.dimen.default_indexBar_textSize));
            mBarFocusTextColor = a.getColor(R.styleable.IndexBarRecyclerView_indexBar_selectedTextColor, ContextCompat.getColor(context, R.color.default_indexBar_selectedTextColor));
            mSideBarFocusTextBgColor = a.getColor(R.styleable.IndexBarRecyclerView_indexBar_selectedTextBg, ContextCompat.getColor(context, R.color.default_sideBar_selectedTextBg));
            mBarTextSpace = a.getDimension(R.styleable.IndexBarRecyclerView_indexBar_textSpace, getResources().getDimension(R.dimen.default_indexBar_textSpace));
            mBarBg = a.getDrawable(R.styleable.IndexBarRecyclerView_indexBar_background);
            mCenterOverlayBg = a.getDrawable(R.styleable.IndexBarRecyclerView_center_overlay_bg);
            mCenterOverlayWidth = (int) a.getDimension(R.styleable.IndexBarRecyclerView_center_overlay_width, getResources().getDimension(R.dimen.default_center_overlay_width));
            mCenterOverlayHeight = (int) a.getDimension(R.styleable.IndexBarRecyclerView_center_overlay_height, getResources().getDimension(R.dimen.default_center_overlay_height));
            mCenterOverlayTextColor = (int) a.getColor(R.styleable.IndexBarRecyclerView_center_overlay_text_color, getResources().getColor(R.color.default_center_overlay_text_color));
            mCenterOverlayTextSize = a.getDimension(R.styleable.IndexBarRecyclerView_center_overlay_text_size, getResources().getDimension(R.dimen.default_center_overlay_text_size));
            mBarWidth = a.getDimension(R.styleable.IndexBarRecyclerView_indexBar_layout_width, getResources().getDimension(R.dimen.default_indexBar_layout_width));
            mIndexTitleBgColor = a.getColor(R.styleable.IndexBarRecyclerView_index_title_bg_color, getResources().getColor(R.color.default_index_title_bg));
            mIndexTitleTextColor = a.getColor(R.styleable.IndexBarRecyclerView_index_title_text_color, getResources().getColor(R.color.default_index_title_text));
            mIndexTitleTextSize = a.getDimension(R.styleable.IndexBarRecyclerView_index_title_text_size, getResources().getDimension(R.dimen.default_index_title_text_size));
            a.recycle();
        }
        if (mCenterOverlayBg == null) {
            mCenterOverlayBg = getResources().getDrawable(R.drawable.default_center_overlay_bg);
        }
        //初始化视图
        mRecyclerView = new RecyclerView(context);
        mRecyclerView.setVerticalScrollBarEnabled(false);
        mRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        StickySectionDecoration decoration = new StickySectionDecoration(
                context,
                mIndexTitleTextSize,
                mIndexTitleTextColor,
                mIndexTitleBgColor,
                new StickySectionDecoration.GroupInfoCallback() {
                    @Override
                    public GroupInfo getGroupInfo(int position) {
                        return mGroupInfos.get(position);
                    }
                });
        mRecyclerView.addItemDecoration(decoration);
        addView(mRecyclerView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        mIndexBar = new IndexBar(context);
        mIndexBar.init(mBarBg, mBarTextColor, mBarFocusTextColor, mSideBarFocusTextBgColor, mBarTextSize, mBarTextSpace);
        LayoutParams params = new LayoutParams((int) mBarWidth, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
        addView(mIndexBar, params);
        mIndexBar.setIndexChangedListener(this);

        initCenterOverlay();
        initListener();
    }

    private void initListener() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!mScrollFromIndexBar) {
                    handleRecyclerViewScroll();
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (mScrollFromIndexBar && newState == SCROLL_STATE_IDLE) {
                    mScrollFromIndexBar = false;
                }
            }
        });
    }

    private void handleRecyclerViewScroll() {
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (!(layoutManager instanceof LinearLayoutManager)) {
            return;
        }
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
        int firstItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
        if (firstItemPosition == RecyclerView.NO_POSITION) {
            return;
        }

        mIndexBar.setSelectionPos(firstItemPosition);
    }

    public void setAdapter(IndexableAdapter adapter) {
        mAdapter = adapter;
        mRecyclerView.setAdapter(adapter);
        // 注册数据监听
        adapter.registerDataSetObserver(new IndexableDataObserver() {
            @Override
            public void onChange() {
                refreshData();
            }
        });
        refreshData();
    }

    /**
     * 显示索引条的阈值，默认10
     */
    private int indexBarThreshold = 10;

    public void setIndexBarThreshold(int indexBarThreshold) {
        this.indexBarThreshold = indexBarThreshold;
    }

    public void refreshData() {
        mDatas = mAdapter.getDataSet();
        mIndexList = mAdapter.getIndexList();
        mGroupMap = mAdapter.getGroupMap();
        if (mGroupMap != null) {
            mGroupInfos.clear();
            int groupId = 0;
            for (String key : mGroupMap.keySet()) {
                List<IndexableEntity> entities = mGroupMap.get(key);
                for (int i = 0; i < entities.size(); i++) {
                    GroupInfo groupInfo = new GroupInfo(groupId, entities.get(i).getIndex());
                    groupInfo.setPosition(i);
                    groupInfo.setGroupLength(entities.size());
                    mGroupInfos.add(groupInfo);
                }
            }
        }
        refreshIndexBar();
    }

    private void refreshIndexBar() {
        if (mIndexList != null) {
            mIndexBar.setVisibility(mIndexList.size() < indexBarThreshold ? INVISIBLE : VISIBLE);
            mIndexBar.setIndexList(mIndexList);
        }
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    /**
     * Set the enabled state of this IndexBar.
     */
    public void setIndexBarVisibility(boolean visible) {
        mIndexBar.setVisibility(visible ? VISIBLE : INVISIBLE);
    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        mRecyclerView.setLayoutManager(layoutManager);
    }

    private TextView mCenterOverlay;
    private Drawable mCenterOverlayBg;

    private void initCenterOverlay() {
        mCenterOverlay = new TextView(mContext);
        mCenterOverlay.setBackground(mCenterOverlayBg);
        mCenterOverlay.setTextColor(mCenterOverlayTextColor);
        mCenterOverlay.setTextSize(mCenterOverlayTextSize);
        mCenterOverlay.setGravity(Gravity.CENTER);
        LayoutParams params = new LayoutParams(mCenterOverlayWidth, mCenterOverlayHeight);
        params.gravity = Gravity.CENTER;
        mCenterOverlay.setLayoutParams(params);
        mCenterOverlay.setVisibility(INVISIBLE);

        addView(mCenterOverlay);
    }

    private boolean mScrollFromIndexBar = false;

    @Override
    public void onSideBarScrollUpdateItem(String word) {
        //TODO 显示文字
        mCenterOverlay.setText(word);
        mCenterOverlay.setVisibility(VISIBLE);
        mHandler.removeCallbacksAndMessages(null);
        //循环判断点击的拼音导航栏和集合中姓名的首字母,如果相同recyclerView就跳转指定位置
        for (int i = 0; i < mDatas.size(); i++) {
            if (mDatas.get(i).getIndex().equals(word)) {
                mScrollFromIndexBar = true;
                mRecyclerView.smoothScrollToPosition(i);
                break;
            }
        }
    }

    @Override
    public void onSideBarScrollEndHideText() {
        //TODO 隐藏文字
        //延时隐藏
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCenterOverlay.setVisibility(INVISIBLE);
            }
        }, 500);
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());
}
