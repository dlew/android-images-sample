package com.idunnolol.images.utils;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

/**
 * Some of this is lovingly borrowed from IOSched 2013
 */
public class BitmapLruCache implements ImageLoader.ImageCache {

    private static final double MEM_CACHE_PERCENT = .25;

    private LruCache<String, Bitmap> mMemoryCache;

    public BitmapLruCache() {
        int memCacheSizeKb = (int) Math.round(MEM_CACHE_PERCENT * Runtime.getRuntime().maxMemory() / 1024);

        mMemoryCache = new LruCache<String, Bitmap>(memCacheSizeKb) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    @Override
    public Bitmap getBitmap(String s) {
        synchronized (mMemoryCache) {
            return mMemoryCache.get(s);
        }
    }

    @Override
    public void putBitmap(String s, Bitmap bitmap) {
        synchronized (mMemoryCache) {
            mMemoryCache.put(s, bitmap);
        }
    }
}
