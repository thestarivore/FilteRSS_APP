package com.company.rss.rss.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;

import com.company.rss.rss.ArticleActivity;
import com.company.rss.rss.R;
import com.company.rss.rss.adapters.FeedsListAdapter;
import com.company.rss.rss.models.Feed;
import com.company.rss.rss.models.Multifeed;

import java.util.List;

import top.defaults.colorpicker.ColorPickerPopup;

public class MultifeedEditFragment extends Fragment {
    private View view;
    private MultifeedEditInterface saveMultifeedInterface;
    private Multifeed multifeed;

    public static MultifeedEditFragment newInstance(Multifeed multifeed) {
        MultifeedEditFragment multifeedEditFragment = new MultifeedEditFragment();

        Bundle args = new Bundle();
        args.putSerializable("multifeed", multifeed);
        multifeedEditFragment.setArguments(args);

        return multifeedEditFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_multifeed_edit, parent, false);
        this.view = view;

        // MULTIFEED DATA
        EditText name = (EditText) view.findViewById(R.id.editTextMultifeedName);
        View color = (View) view.findViewById(R.id.viewMultifeedEditColor);
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekBarMultifeedEdit);

        multifeed = (Multifeed) getArguments().getSerializable("multifeed");
        name.setText(multifeed.getName());
        color.setBackgroundColor(multifeed.getColor());
        seekBar.setProgress(multifeed.getImportance());


        // multifeed's feeds
        // TODO: retrieve multifeed's feeds from the API
        final List<Feed> feeds = multifeed.getFeeds();

        final ListView listview = (ListView) view.findViewById(R.id.listViewEditMultifeed);
        final FeedsListAdapter adapter = new FeedsListAdapter(getContext(), feeds, true);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                Log.v(ArticleActivity.logTag, "Feed " + id + " clicked");
                final Feed feed = (Feed) parent.getItemAtPosition(position);

                Log.v(ArticleActivity.logTag, "Feed information: " + feed.toString());

                // if feed not in any feed list
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.dialog_remove_feed_title)
                        .setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(ArticleActivity.logTag, "Removing feed " + position + " feed: " + feeds.get(position));
                                feeds.remove(position);
                                adapter.notifyDataSetChanged();
                                saveMultifeedInterface.onSaveMultifeed(multifeed);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(ArticleActivity.logTag, "Dialog closed");
                            }
                        })
                        .show();
            }

        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        final View viewMultifeedEditColor = (View) view.findViewById(R.id.viewMultifeedEditColor);

        viewMultifeedEditColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                new ColorPickerPopup.Builder(getContext())
                        .initialColor(getBackgroundColor(viewMultifeedEditColor))
                        .enableBrightness(false)
                        .enableAlpha(false)
                        .okTitle(getString(R.string.choose))
                        .cancelTitle(getString(R.string.cancel))
                        .showIndicator(false)
                        .showValue(false)
                        .build()
                        .show(new ColorPickerPopup.ColorPickerObserver() {
                            @Override
                            public void onColorPicked(int color) {
                                v.setBackgroundColor(color);
                            }

                            @Override
                            public void onColor(int color, boolean fromUser) {

                            }
                        });
            }
        });

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MultifeedEditInterface) {
            saveMultifeedInterface = (MultifeedEditInterface) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement MultifeedEditInterface");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v(ArticleActivity.logTag, "Multifeed Edit Fragment paused");

        // save data and pass updated multifeed to parent activity
        Multifeed multifeed = (Multifeed) getArguments().getSerializable("multifeed");

        EditText name = (EditText) view.findViewById(R.id.editTextMultifeedName);
        View colorView = (View) view.findViewById(R.id.viewMultifeedEditColor);
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekBarMultifeedEdit);

        multifeed.setName(String.valueOf(name.getText()));
        multifeed.setColor(getBackgroundColor(colorView));
        multifeed.setImportance(seekBar.getProgress());

        Log.v(ArticleActivity.logTag, "Multifeed set " + multifeed.toString());

        // TODO: invoke onSaveMultifeed only if the multifeed has been updated
        saveMultifeedInterface.onSaveMultifeed(multifeed);
    }

    private int getBackgroundColor(View view) {
        Drawable background = view.getBackground();
        if (background instanceof ColorDrawable){
            return ((ColorDrawable) background).getColor();
        }
        return 0;
    }

    public interface MultifeedEditInterface {
        public void onSaveMultifeed(Multifeed multifeed);
    }


}
