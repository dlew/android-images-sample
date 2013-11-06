package com.idunnolol.images;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.idunnolol.images.utils.Log;

public class ImagesApplication extends Application {

    // Singleton request queue
    public static RequestQueue sRequestQueue;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.configure("Images", true);

        sRequestQueue = Volley.newRequestQueue(this);
    }

}
