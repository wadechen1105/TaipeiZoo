package com.example.wadechen.taipeizoo.category;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.wadechen.taipeizoo.Animal;
import com.example.wadechen.taipeizoo.MainActivity;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;


public class CategoryFragmentTest {

    @Test
    public void onViewCreated() throws Exception {
        RecyclerView recyclerView = Mockito.mock(RecyclerView.class);
        Animal animal = new Animal();
        animal.mId = 100;
        animal.nameEn = "test100";
        Animal.getCache().addToCache(animal);

        CategoryAdapter adapter = Mockito.mock(CategoryAdapter.class);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(Mockito.mock(MainActivity.class));
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        View child = Mockito.verify(recyclerView).getChildAt(0);

        int p = recyclerView.getChildLayoutPosition(child);

        //check if method was invoke
        int count = Mockito.verify(adapter).getItemCount();

        assertEquals(count, 1);
    }

}