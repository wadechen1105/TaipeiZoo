package com.example.wadechen.taipeizoo.category;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.util.AsyncListUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wadechen.taipeizoo.Animal;
import com.example.wadechen.taipeizoo.L;
import com.example.wadechen.taipeizoo.R;
import com.example.wadechen.taipeizoo.service.Cache;
import com.example.wadechen.taipeizoo.util.ImageProcess;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class CategoryAdapter extends RecyclerView.Adapter {


    private ScrollListener mScrollListener;
    private Context mContext;
    private ImageProcess mImageProcess;
    private RecyclerView mRecyclerView;
    private DataCallback mDataCallback;
    private AsyncListUtil<Animal> mListUtil;

    public CategoryAdapter(Context context, RecyclerView recyclerView, Cache<Animal> cache) {
        mContext = context;
        mRecyclerView = recyclerView;
        mImageProcess = new ImageProcess(context);
        mDataCallback = new DataCallback(cache);
        mListUtil = new AsyncListUtil(Animal.class, 30, mDataCallback, new ViewCallback(mRecyclerView));
        mScrollListener = new ScrollListener(mListUtil);
        mRecyclerView.addOnScrollListener(mScrollListener);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_item, parent, false);
        ViewHolder VH = new ViewHolder(v);
        L.d("onCreateViewHolder");
        return VH;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Animal a = (Animal) Animal.getCache().getData().get(position);
        ViewHolder VH = (ViewHolder) holder;
        VH.bindView(a);
    }

    @Override
    public int getItemCount() {
        return mListUtil.getItemCount();
    }

    public void reloadData() {
        mListUtil.refresh();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView textViewLocation;
        ImageView imageViewPic1;
        ImageView imageViewPic2;
        View view;


        ViewHolder(View v) {
            super(v);
            view = v;
            textViewName = v.findViewById(R.id.tv_animal_name);
            textViewLocation = v.findViewById(R.id.tv_animal_location);
            imageViewPic1 = v.findViewById(R.id.iv_animal_pic1);
            imageViewPic2 = v.findViewById(R.id.iv_animal_pic2);
        }

        void bindView(Animal a) {
            textViewName.setText(mContext.getString(R.string.animal_name, a.name, a.nameEn));
            textViewLocation.setText(a.loaction);
            L.d("mid = " + a.mId + " / url = " + a.pic1_url);
            Bitmap[] bs = a.pics;
            if (bs[0] != null)
                imageViewPic1.setImageBitmap(bs[0]);
            else
                imageViewPic1.setImageResource(R.mipmap.default_image);
            if (bs[1] != null)
                imageViewPic2.setImageBitmap(bs[1]);
            else
                imageViewPic2.setImageResource(R.mipmap.default_image);
        }

    }

    private class DataCallback extends AsyncListUtil.DataCallback<Animal> {
        private Cache<Animal> mCache;

        DataCallback(Cache<Animal> cache) {
            mCache = cache;
        }

        @Override
        public int refreshData() {
            L.v("DataCallback - refreshData");
            return Animal.getCache().size();
        }

        @Override
        public void fillData(Animal[] data, int startPosition, int itemCount) {
            L.v("DataCallback - fillData");
            if (data != null) {
                List<Animal> l = mCache.getData();
                for (int i = 0; i < itemCount; i++) {
                    Animal a = l.get(i);
                    data[i] = a;

                    data[i].pics[0] = mImageProcess.loadImageFromApp(a.mId + "_pic1");
                    data[i].pics[1] = mImageProcess.loadImageFromApp(a.mId + "_pic2");
                }
            }
        }

    }

    private class ViewCallback extends AsyncListUtil.ViewCallback {
        RecyclerView mView;

        ViewCallback(RecyclerView view) {
            mView = view;
        }

        @Override
        public void getItemRangeInto(int[] outRange) {
            L.v("ViewCallback - getItemRangeInto");
            if (outRange == null) {
                return;
            }

            LinearLayoutManager llm = (LinearLayoutManager) mView.getLayoutManager();
            outRange[0] = llm.findFirstVisibleItemPosition();
            outRange[1] = llm.findLastVisibleItemPosition();

            if (outRange[0] == -1 && outRange[1] == -1) {
                outRange[0] = 0;
                outRange[1] = 0;
            }
        }

        @Override
        public void onDataRefresh() {
            L.v("ViewCallback - onDataRefresh");
            mView.getAdapter().notifyDataSetChanged();
        }

        @Override
        public void onItemLoaded(int position) {
            L.v("ViewCallback - onItemLoaded");
            mView.getAdapter().notifyItemChanged(position);
        }
    }

    private class ScrollListener extends RecyclerView.OnScrollListener {
        AsyncListUtil<Animal> mListUtil;

        ScrollListener(AsyncListUtil<Animal> listUtil) {
            mListUtil = listUtil;
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            mListUtil.onRangeChanged();
        }
    }

}