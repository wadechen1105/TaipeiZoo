package com.example.wadechen.taipeizoo.category;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wadechen.taipeizoo.Animal;
import com.example.wadechen.taipeizoo.R;
import com.example.wadechen.taipeizoo.network.NetWorkHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * show animal name and images
 */
public class CategoryFragment extends Fragment {
    private RecyclerView mRecycleView;
    private CategoryAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_gategory, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecycleView = view.findViewById(R.id.animal_list_view);

        mAdapter = new CategoryAdapter(getContext(), mRecycleView, Animal.getCache());
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecycleView.setLayoutManager(layoutManager);
        mRecycleView.setAdapter(mAdapter);
    }

    public void showView() {
        mAdapter.reloadData();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

}
