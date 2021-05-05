package com.lihai.indexbarrecyclerview;

import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import androidx.recyclerview.widget.RecyclerView;

public abstract class IndexableAdapter<T extends IndexableEntity, IVH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<IVH> {
    protected List<T> mDatas;
    private List<String> mIndexList;
    private TreeMap<String, List<T>> mGroupMap = new TreeMap<>(new Comparator<String>() {
        @Override
        public int compare(String lhs, String rhs) {
            if (lhs.equals(INDEX_SIGN)) {
                return rhs.equals(INDEX_SIGN) ? 0 : 1;
            } else if (rhs.equals(INDEX_SIGN)) {
                return -1;
            }
            return lhs.compareTo(rhs);
        }
    });
    private IndexableDataObserver mDataObserver;
    static final String INDEX_SIGN = "#";

    public final void setData(List<T> data) {
        if (mDatas == null) {
            mDatas = new ArrayList<>();
        } else {
            mDatas.clear();
        }
        // 排序数据
        List<T> sortedData = sortDataByPinyin(data);
        mDatas.addAll(sortedData);
        // 通知数据变更
        if (mDataObserver != null) {
            mDataObserver.onChange();
        }
        notifyDataSetChanged();
    }

    private List<T> sortDataByPinyin(List<T> data) {
        ArrayList<T> resultList = new ArrayList<>();
        if (data == null || data.size() == 0) {
            return resultList;
        }
        try {
            mGroupMap.clear();
            // 解析拼音
            for (int i = 0; i < data.size(); i++) {
                T item = data.get(i);
                String indexName = item.getFieldForSort();
                String pinyin = PinyinUtil.getPingYin(indexName);

                // init EntityWrapper
                if (PinyinUtil.matchingLetter(pinyin)) {
                    item.setPinyin(pinyin);
                    item.setIndex(pinyin.substring(0, 1).toUpperCase());
                } else if (PinyinUtil.matchingPolyphone(pinyin)) {
                    item.setIndex(PinyinUtil.gePolyphoneInitial(pinyin).toUpperCase());
                    item.setPinyin(PinyinUtil.getPolyphoneRealPinyin(pinyin));
                } else {
                    item.setIndex(INDEX_SIGN);
                    item.setPinyin(pinyin);
                }

                String index = item.getIndex();

                List<T> list;
                if (!mGroupMap.containsKey(index)) {
                    list = new ArrayList<>();
                    mGroupMap.put(index, list);
                } else {
                    list = mGroupMap.get(index);
                }
                list.add(item);
            }

            // 解析index列表
            parseIndexList(mGroupMap);

            // 排序
            for (List<T> indexableEntities : mGroupMap.values()) {
                Collections.sort(indexableEntities, new Comparator<T>() {
                    @Override
                    public int compare(T o1, T o2) {
                        return o1.getPinyin().compareTo(o2.getPinyin());
                    }
                });
                resultList.addAll(indexableEntities);
            }
            return resultList;
        } catch (Exception e) {
            e.printStackTrace();
            return resultList;
        }
    }

    private void parseIndexList(TreeMap<String, List<T>> map) {
        if (mIndexList == null) {
            mIndexList = new ArrayList<>();
        } else {
            mIndexList.clear();
        }
        for (String index : map.keySet()) {
            mIndexList.add(index);
        }
    }

    public void registerDataSetObserver(IndexableDataObserver observer) {
        mDataObserver = observer;
        if (mDatas != null && mDatas.size() > 0) {
            mDataObserver.onChange();
        }
    }

    public void removeDataSetObserver() {
        mDataObserver = null;
    }

    public List<T> getDataSet() {
        return mDatas;
    }

    public List<String> getIndexList() {
        return mIndexList;
    }

    public TreeMap<String, List<T>> getGroupMap() {
        return mGroupMap;
    }
}