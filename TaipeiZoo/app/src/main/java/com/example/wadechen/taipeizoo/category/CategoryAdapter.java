package com.example.wadechen.taipeizoo.category;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wadechen.taipeizoo.R;

/**
 * Created by wadechen on 2017/10/4.
 */

public class CategoryAdapter extends RecyclerView.Adapter {

    public CategoryAdapter() {
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_item, parent, false);
        ViewHolder VH = new ViewHolder(v);
        return VH;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder VH = (ViewHolder) holder;
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {


        ViewHolder(View v) {
            super(v);
        }
    }

}