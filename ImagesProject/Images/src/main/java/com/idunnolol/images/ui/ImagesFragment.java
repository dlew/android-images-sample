package com.idunnolol.images.ui;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.idunnolol.images.R;
import com.idunnolol.images.utils.Ui;

import java.util.List;

public class ImagesFragment extends Fragment {

    public static final String TAG = ImagesFragment.class.getName();

    private ImagesFragmentListener mListener;

    private GridView mGridView;
    private ProgressBar mProgressBar;
    private TextView mErrorTextView;

    private ImagesAdapter mAdapter;

    private List<String> mImageUrls;
    private boolean mCanLoadMore;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mListener = (ImagesFragmentListener) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_images, container, false);

        mGridView = Ui.findView(rootView, R.id.grid_view);
        mProgressBar = Ui.findView(rootView, R.id.progress_bar);
        mErrorTextView = Ui.findView(rootView, R.id.error_text_view);

        mAdapter = new ImagesAdapter(getActivity());
        mGridView.setAdapter(mAdapter);
        mGridView.setOnScrollListener(mScrollListener);

        bind(mImageUrls, mCanLoadMore);

        return rootView;
    }

    public void bind(List<String> imageUrls, boolean canLoadMore) {
        mImageUrls = imageUrls;
        mCanLoadMore = canLoadMore;

        if (mGridView != null && mProgressBar != null) {
            if (imageUrls == null || imageUrls.size() == 0) {
                mGridView.setVisibility(View.GONE);

                if (canLoadMore) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mErrorTextView.setVisibility(View.GONE);
                }
                else {
                    mProgressBar.setVisibility(View.GONE);
                    mErrorTextView.setVisibility(View.VISIBLE);
                }
            }
            else {
                mGridView.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                mErrorTextView.setVisibility(View.GONE);

                mAdapter.bind(imageUrls, canLoadMore);
            }
        }
    }

    private AbsListView.OnScrollListener mScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            // Ignore
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                             int totalItemCount) {
            if (mCanLoadMore && firstVisibleItem + visibleItemCount == totalItemCount) {
                mListener.onRequestMoreImages();
            }
        }
    };

    // Listener

    public interface ImagesFragmentListener {
        public void onRequestMoreImages();
    }
}
