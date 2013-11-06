package com.idunnolol.images.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.idunnolol.images.R;
import com.idunnolol.images.utils.Ui;

import java.util.List;

public class ImagesFragment extends Fragment {

    public static final String TAG = ImagesFragment.class.getName();

    private GridView mGridView;
    private ProgressBar mProgressBar;

    private ImagesAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_images, container, false);

        mGridView = Ui.findView(rootView, R.id.grid_view);
        mProgressBar = Ui.findView(rootView, R.id.progress_bar);

        mAdapter = new ImagesAdapter(getActivity());
        mGridView.setAdapter(mAdapter);

        return rootView;
    }

    public void bind(List<String> imageUrls, boolean canLoadMore) {
        mAdapter.bind(imageUrls, canLoadMore);
    }
}
