package com.example.wadechen.taipeizoo.util;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Process;
import android.util.Log;
import android.util.LruCache;

import com.example.wadechen.taipeizoo.L;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageProcess {
    private Context mContext;
    private LruCache<Integer, Bitmap> mMemoryCache;

    public ImageProcess(Context context) {
        mContext = context.getApplicationContext();
    }

    public void saveImageToApp(Bitmap bitmap, String id) {
        ContextWrapper cw = new ContextWrapper(mContext);
        File directory = cw.getDir("images", Context.MODE_PRIVATE);
        if (!directory.exists()) {
            directory.mkdir();
        }
        File path = new File(directory, id + ".png");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, fos);
            fos.close();
        } catch (Exception e) {
            L.e("SAVE_IMAGE" + e.getMessage());
        }
    }

    public Bitmap loadImageFromApp(String id) {
        try {
            ContextWrapper cw = new ContextWrapper(mContext);
            File directory = cw.getDir("images", Context.MODE_PRIVATE);
            File f = new File(directory.getPath(), id + ".png");
            FileInputStream fInputStream = new FileInputStream(f);
            Bitmap b = BitmapFactory.decodeStream(fInputStream);

            fInputStream.close();
            return b;

        } catch (FileNotFoundException e) {
            Log.e("load image fail", e.toString());
            return null;
        } catch (IOException e) {
            Log.e("load mage fail", e.toString());
            return null;
        }
    }

    public boolean isFileExist(String id) {
        ContextWrapper cw = new ContextWrapper(mContext);
        File directory = cw.getDir("images", Context.MODE_PRIVATE);
        File f = new File(directory.getPath(), id + ".png");
        return f.exists() && f.isFile();
    }
}
