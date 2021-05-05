package com.lihai.app;

import com.lihai.indexbarrecyclerview.IndexableEntity;

public class TestBean extends IndexableEntity {
    private String name;

    public TestBean(String name) {
        this.name = name;
    }

    @Override
    public String getFieldForSort() {
        return name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
