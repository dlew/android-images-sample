package com.idunnolol.images;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.idunnolol.images.utils.BitmapLruCache;
import com.idunnolol.images.utils.Log;

public class ImagesApplication extends Application {

    // Singleton request queue
    public static RequestQueue sRequestQueue;

    // Singleton image loader
    public static ImageLoader sImageLoader;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.configure("Images", true);

        sRequestQueue = Volley.newRequestQueue(this);
        sImageLoader = new ImageLoader(sRequestQueue, new BitmapLruCache());
    }

}
