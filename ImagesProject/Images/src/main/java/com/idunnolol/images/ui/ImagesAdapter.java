package com.idunnolol.images.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.android.volley.toolbox.NetworkImageView;
import com.idunnolol.images.ImagesApplication;
import com.idunnolol.images.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows images.  The last item is a loading spinner, such that
 * more images can be loaded and shown.
 */
public class ImagesAdapter extends BaseAdapter {

    private static final int VIEW_TYPE_IMAGE = 0;
    private static final int VIEW_TYPE_LOADING = 1;

    private Context mContext;

    private List<String> mImageUrls = new ArrayList<String>();

    private boolean mCanLoadMoreImages = true;

    public ImagesAdapter(Context context) {
        mContext = context;
    }

    public void bind(List<String> imageUrls, boolean canLoadMore) {
        mImageUrls = imageUrls;
        mCanLoadMoreImages = canLoadMore;
        notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < mImageUrls.size()) {
            return VIEW_TYPE_IMAGE;
        }
        else {
            return VIEW_TYPE_LOADING;
        }
    }

    @Override
    public int getCount() {
        return mImageUrls.size() + (mCanLoadMoreImages ? 1 : 0);
    }

    @Override
    public String getItem(int position) {
        if (position < mImageUrls.size()) {
            return mImageUrls.get(position);
        }
        else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int viewType = getItemViewType(position);

        if (viewType == VIEW_TYPE_IMAGE) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.row_image, parent, false);
            }

            NetworkImageView networkImageView = (NetworkImageView) convertView;
            networkImageView.setImageUrl(getItem(position), ImagesApplication.sImageLoader);
        }
        else if (viewType == VIEW_TYPE_LOADING) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.row_loading, parent, false);
            }
        }

        return convertView;
    }
}
