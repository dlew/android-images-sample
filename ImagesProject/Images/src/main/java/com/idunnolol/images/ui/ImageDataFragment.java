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

    // The maximum number of images Google will return before erroring out
    private static final int MAX_IMAGES = 64;

    private ImageDataFragmentListener mListener;

    private String mQuery;

    private List<String> mImageUrls = new ArrayList<String>();

    private long mResultCount;

    private JsonObjectRequest mCurrentRequest;

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
        int size = mImageUrls.size();
        return size < mResultCount && size < MAX_IMAGES;
    }

    // Image requests

    public void requestMoreImages() {
        if (canLoadMore()
                && (mCurrentRequest == null || mCurrentRequest.hasHadResponseDelivered())) {
            // Construct the URL
            Uri.Builder uriBuilder = Uri.parse("https://ajax.googleapis.com/ajax/services/search/images?v=1.0")
                    .buildUpon();
            uriBuilder.appendQueryParameter("q", mQuery);

            // Make sure we don't go over the image limit
            int start = mImageUrls.size();
            int step = STEP;
            if (start + step > MAX_IMAGES) {
                step -= (start + step) - MAX_IMAGES;
            }

            uriBuilder.appendQueryParameter("start", Integer.toString(start));
            uriBuilder.appendQueryParameter("rsz", Integer.toString(step));

            String url = uriBuilder.build().toString();

            Log.i("Query: " + url);

            mCurrentRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    mImageResponseListener, mImageResponseErrorListener);
            ImagesApplication.sRequestQueue.add(mCurrentRequest);
        }
    }

    private final Response.Listener mImageResponseListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject jsonObject) {
            JSONObject responseData = jsonObject.optJSONObject("responseData");

            if (responseData == null) {
                Log.w("What is happening:\n" + jsonObject);
            }

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
