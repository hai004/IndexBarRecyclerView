package com.lihai.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lihai.indexbarrecyclerview.IndexableAdapter;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by frank on 2017/4/11.
 */

public class TestAdapter extends IndexableAdapter<TestBean, TestAdapter.TestHolder> {
    @Override
    public TestHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item,parent,false);
//        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
//        layoutParams.topMargin = 1;
//        view.setLayoutParams(layoutParams);
        return new TestHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TestHolder holder, int position) {
        holder.textView.setText(mDatas.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    static class TestHolder extends  RecyclerView.ViewHolder{
        public TextView textView;
        public TestHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.tv_content);
            textView.setTextSize(38.0f);
        }

    }
}
