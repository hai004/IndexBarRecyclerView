package com.lihai.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.lihai.indexbarrecyclerview.IndexBarRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private IndexBarRecyclerView mIndexBarRecyclerView;
    private List<TestBean> mDatas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mIndexBarRecyclerView = findViewById(R.id.irv);
        mIndexBarRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mockData();
        TestAdapter testAdapter = new TestAdapter();
        mIndexBarRecyclerView.setAdapter(testAdapter);
        testAdapter.setData(mDatas);
    }

    private void mockData() {
        mDatas = new ArrayList<>();
        mDatas.add(new TestBean("123"));
        mDatas.add(new TestBean("456"));
        mDatas.add(new TestBean("阿大"));
        mDatas.add(new TestBean("安以轩"));
        mDatas.add(new TestBean("Boy"));
        mDatas.add(new TestBean("曹雪芹"));
        mDatas.add(new TestBean("华老师"));
        mDatas.add(new TestBean("黄老师"));
        mDatas.add(new TestBean("张三"));
        mDatas.add(new TestBean("李四"));
        mDatas.add(new TestBean("王二"));
        mDatas.add(new TestBean("王重阳"));
        mDatas.add(new TestBean("蔡徐坤"));
        mDatas.add(new TestBean("电老师"));
        mDatas.add(new TestBean("钱进"));
        mDatas.add(new TestBean("孙权"));
        mDatas.add(new TestBean("唐太宗"));
    }
}