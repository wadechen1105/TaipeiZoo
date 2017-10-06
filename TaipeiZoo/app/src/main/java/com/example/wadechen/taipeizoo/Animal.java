package com.example.wadechen.taipeizoo;

import com.example.wadechen.taipeizoo.service.Cache;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;

/**
 * DTO for pass animal info
 */
public class Animal {
    public static final String WEB_URL = "http://data.taipei/opendata/datalist/apiAccess?" +
            "scope=resourceAquire&rid=a3e2b221-75e0-45c1-8f97-75acbd43d613";

    private static Cache<Animal> mAnimals;

    public static Cache getCache() {
        if (mAnimals == null) {
            synchronized (Animal.class) {
                if (mAnimals == null) {
                    mAnimals = new AnimalCache();
                }
            }
        }
        return mAnimals;
    }

    public int mId;
    public String name;
    public String loaction;
    public String nameEn;
    public String distribution;
    public String interpretation;
    public String pic1_url;
    public String pic2_url;

    /**
     * get total count of animals
     * @param obj
     * @return
     */
    public static int getCount(JSONObject obj) {
        try {
            JSONObject root = obj.getJSONObject("result");
            return root.getInt("count");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     *
     * @param obj root json from web response
     * @return
     */
    public static Cache<Animal> getAnimalsFromJSONAndSaveToCache(JSONObject obj) {
        Cache<Animal> cache = getCache();

        try {
            JSONObject root = obj.getJSONObject("result");
            JSONArray animals = root.getJSONArray("results");
            for (int i = 0; i < animals.length(); i++) {
                JSONObject animal = animals.getJSONObject(i);
                Animal a = new Animal();
                a.mId = animal.getInt("_id");
                a.name = animal.getString("A_Name_Ch");
                a.loaction = animal.getString("A_Location");
                a.nameEn = animal.getString("A_Name_En");
                a.distribution = animal.getString("A_Distribution");
                a.interpretation = animal.getString("A_Interpretation");
                a.pic1_url = animal.getString("A_Pic01_URL");
                a.pic2_url = animal.getString("A_Pic02_URL");
                cache.addToCache(a);
                L.d("download --");
            }

        } catch (Exception e) {
            e.printStackTrace();
            L.w("err : " + e.getMessage());
            cache.clear();
        } finally {
            return cache;
        }
    }

    private static class AnimalCache extends Cache<Animal> {

        @Override
        public void sort() {
            Collections.sort(getData(), new Comparator<Animal>() {
                @Override
                public int compare(Animal lhs, Animal rhs) {
                    // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                    return lhs.mId < rhs.mId ? -1 : (lhs.mId > rhs.mId) ? 1 : 0;
                }
            });
        }
    }

}
