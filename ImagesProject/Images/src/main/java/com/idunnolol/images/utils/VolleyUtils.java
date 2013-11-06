package com.idunnolol.images.utils;

import android.content.Context;

import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;

public class VolleyUtils {

    public static RequestQueue createRequestQueue(Context context) {
        DiskBasedCache cache = new DiskBasedCache(context.getCacheDir());
        Network network = new BasicNetwork(new HurlStack());
        return new RequestQueue(cache, network);
    }

}
