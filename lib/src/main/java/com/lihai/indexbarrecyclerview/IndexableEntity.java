package com.lihai.indexbarrecyclerview;

public abstract class IndexableEntity {
    private String pinyin;
    private String index;
    public abstract String getFieldForSort();

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }
}
