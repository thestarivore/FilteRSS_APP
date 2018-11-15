// TODO: remove or implement this


package com.company.rss.rss.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.company.rss.rss.R;

public class AddFeedDialogFragment extends DialogFragment {
    private String[] multifeeds;

    public AddFeedDialogFragment() {
        // Use `newInstance` to add arguments
    }

    public static AddFeedDialogFragment newInstance() {
        AddFeedDialogFragment frag = new AddFeedDialogFragment();
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //return getActivity().getLayoutInflater().inflate(R.layout.fragment_dialog_add_feed, container);

        return inflater.inflate(R.layout.fragment_dialog_add_feed, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}
