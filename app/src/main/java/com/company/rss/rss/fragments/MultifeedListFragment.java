package com.company.rss.rss.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.company.rss.rss.ArticleActivity;
import com.company.rss.rss.MultifeedManagerActivity;
import com.company.rss.rss.R;
import com.company.rss.rss.adapters.MultifeedListAdapter;
import com.company.rss.rss.models.Multifeed;

import java.util.ArrayList;

/**
 * The fragment responsible of showing the list of multifeeds
 */
public class MultifeedListFragment extends Fragment {
    private ArrayList<Multifeed> multifeeds;
    private OnMultifeedListListener listener;
    private MultifeedListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static MultifeedListFragment newInstance(ArrayList<Multifeed> multifeeds) {
        MultifeedListFragment fragment = new MultifeedListFragment();
        Bundle args = new Bundle();
        args.putSerializable("multifeeds", multifeeds);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMultifeedListListener) {
            listener = (OnMultifeedListListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement ItemsListFragment.OnMultifeedListListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_multifeed_list, container, false);

        multifeeds = (ArrayList<Multifeed>) getArguments().getSerializable("multifeeds");
        adapter = new MultifeedListAdapter(getContext(), multifeeds);

        final ListView listview = (ListView) view.findViewById(R.id.listViewMultifeedList);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final Multifeed multifeed = (Multifeed) parent.getItemAtPosition(position);
                Log.v(ArticleActivity.logTag, "Multifeed " + id + " clicked" + multifeed.toString());

                listener.onMultifeedSelected(position);
            }

        });

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
                final Multifeed multifeed = (Multifeed) parent.getItemAtPosition(position);
                Log.v(ArticleActivity.logTag, "Multifeed " + id + " long clicked " + multifeed.toString());

                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.dialog_remove_multifeed_title)
                        .setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(ArticleActivity.logTag, "Removing multifeed " + position + " multifeed: " + parent.getItemAtPosition(position));
                                adapter.notifyDataSetChanged();
                                multifeeds.remove(position);
                                // TODO: call the API and remove the multifeed
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(ArticleActivity.logTag, "Dialog closed");
                            }
                        })
                        .show();

                return true;
            }
        });

        /*lvItems.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View item, int position,
                                    long rowId) {
                // Retrieve item based on position
                Item i = adapterItems.getItem(position);
                // Fire selected event for item
                listener.onMultifeedSelected(i);
            }
        });*/
        return view;
    }


    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     * <p>
     * public void setActivateOnItemClick(boolean activateOnItemClick) {
     * // When setting CHOICE_MODE_SINGLE, ListView will automatically
     * // give items the 'activated' state when touched.
     * lvItems.setChoiceMode(
     * activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
     * : ListView.CHOICE_MODE_NONE);
     * }
     */


    public interface OnMultifeedListListener {
        public void onMultifeedSelected(int position);
    }


}
