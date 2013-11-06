package com.idunnolol.images;

import android.app.Application;

import com.idunnolol.images.utils.Log;

public class ImagesApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Log.configure("Images", true);
    }

}
