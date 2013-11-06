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

import com.idunnolol.images.R;
import com.idunnolol.images.utils.Ui;

import java.util.List;

public class ImagesFragment extends Fragment {

    public static final String TAG = ImagesFragment.class.getName();

    private ImagesFragmentListener mListener;

    private GridView mGridView;
    private ProgressBar mProgressBar;

    private ImagesAdapter mAdapter;

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

        mAdapter = new ImagesAdapter(getActivity());
        mGridView.setAdapter(mAdapter);
        mGridView.setOnScrollListener(mScrollListener);

        return rootView;
    }

    public void bind(List<String> imageUrls, boolean canLoadMore) {
        mCanLoadMore = canLoadMore;

        if (imageUrls.size() == 0) {
            mGridView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        }
        else {
            mGridView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);

            mAdapter.bind(imageUrls, canLoadMore);
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
