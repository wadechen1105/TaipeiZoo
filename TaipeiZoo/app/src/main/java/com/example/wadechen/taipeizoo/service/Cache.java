package com.example.wadechen.taipeizoo.service;


import java.util.ArrayList;
import java.util.List;

public abstract class Cache<T> {
    private List<T> mCacheData = new ArrayList<>();

    public void addToCache(T data) {
        mCacheData.add(data);
    }

    public List<T> getData() {
        return mCacheData;
    }
    public void clear() {
        mCacheData.clear();
    }

    public abstract void sort();

    public int size() {
        return mCacheData.size();
    }
}
