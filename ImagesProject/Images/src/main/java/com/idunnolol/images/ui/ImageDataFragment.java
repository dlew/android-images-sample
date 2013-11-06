package com.idunnolol.images.ui;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.idunnolol.images.utils.Log;
import com.idunnolol.images.utils.VolleyUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Invisible Fragment which stores image data.  It is retained
 * so that it can store data (and process requests) indefinitely.
 */
public class ImageDataFragment extends Fragment {

    public static final String TAG = ImageDataFragment.class.getName();

    private RequestQueue mRequestQueue;

    private ImageDataFragmentListener mListener;

    private String mQuery;

    private List<String> mImageUrls = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mListener = (ImageDataFragmentListener) activity;

        if (mRequestQueue == null) {
            mRequestQueue = VolleyUtils.createRequestQueue(activity);
        }
        mRequestQueue.start();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mRequestQueue.stop();
    }

    // Controls

    public void setQuery(String query) {
        if (!TextUtils.equals(mQuery, query)) {
            mQuery = query;
            mImageUrls.clear();

            // Construct the URL
            Uri.Builder uriBuilder = Uri.parse("https://ajax.googleapis.com/ajax/services/search/images?v=1.0&rsz=8")
                    .buildUpon();
            uriBuilder.appendQueryParameter("q", query);

            mRequestQueue.add(new JsonObjectRequest(Request.Method.GET, uriBuilder.build().toString(), null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            Log.i("Response: " + jsonObject);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Log.e("VolleyError: " + volleyError);
                        }
                    }
            ));
        }
    }

    public void requestMoreImages() {
        if (canLoadMore()) {
            // TODO
        }
    }

    public List<String> getImageUrls() {
        return mImageUrls;
    }

    public boolean canLoadMore() {
        // TODO
        return true;
    }

    // Listener

    public interface ImageDataFragmentListener {
        public void onLoadingImages();

        public void onImagesLoaded(List<String> imageUrls, boolean canLoadMore);

        public void onImageLoadError();
    }
}
