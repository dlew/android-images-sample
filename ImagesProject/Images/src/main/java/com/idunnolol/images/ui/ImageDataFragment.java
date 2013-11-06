package com.idunnolol.images.ui;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.idunnolol.images.ImagesApplication;
import com.idunnolol.images.utils.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Invisible Fragment which stores image data.  It is retained
 * so that it can store data (and process requests) indefinitely.
 */
public class ImageDataFragment extends Fragment {

    public static final String TAG = ImageDataFragment.class.getName();

    // Number of results to request at a time
    private static final int STEP = 8;

    private ImageDataFragmentListener mListener;

    private String mQuery;

    private List<String> mImageUrls = new ArrayList<String>();

    private long mResultCount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mListener = (ImageDataFragmentListener) activity;
    }

    // Controls

    public void setQuery(String query) {
        if (!TextUtils.equals(mQuery, query)) {
            mQuery = query;
            mImageUrls.clear();
            mResultCount = Long.MAX_VALUE;

            // Notify that we have now loaded zero images
            mListener.onImagesLoaded(mImageUrls, canLoadMore());

            requestMoreImages();
        }
    }

    public List<String> getImageUrls() {
        return mImageUrls;
    }

    public boolean canLoadMore() {
        return mImageUrls.size() < mResultCount;
    }

    // Image requests

    public void requestMoreImages() {
        if (canLoadMore()) {
            // Construct the URL
            Uri.Builder uriBuilder = Uri.parse("https://ajax.googleapis.com/ajax/services/search/images?v=1.0")
                    .buildUpon();
            uriBuilder.appendQueryParameter("q", mQuery);
            uriBuilder.appendQueryParameter("start", Integer.toString(mImageUrls.size()));
            uriBuilder.appendQueryParameter("rsz", Integer.toString(STEP));

            String url = uriBuilder.build().toString();

            Log.i("Query: " + url);

            ImagesApplication.sRequestQueue.add(new JsonObjectRequest(Request.Method.GET, url, null,
                    mImageResponseListener, mImageResponseErrorListener));
        }
    }

    private final Response.Listener mImageResponseListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            JSONObject responseData = jsonObject.optJSONObject("responseData");

            // Parse results
            JSONArray results = responseData.optJSONArray("results");
            int len = results.length();
            for (int a = 0; a < len; a++) {
                JSONObject result = results.optJSONObject(a);
                mImageUrls.add(result.optString("tbUrl"));
            }

            // Parse result count
            JSONObject cursor = responseData.optJSONObject("cursor");
            mResultCount = cursor.optLong("estimatedResultCount");

            // Notify listeners
            mListener.onImagesLoaded(mImageUrls, canLoadMore());

            // TODO: Handle error situations
        }
    };

    private final Response.ErrorListener mImageResponseErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            mListener.onImageLoadError();
        }
    };

    // Listener

    public interface ImageDataFragmentListener {
        public void onImagesLoaded(List<String> imageUrls, boolean canLoadMore);

        public void onImageLoadError();
    }
}
