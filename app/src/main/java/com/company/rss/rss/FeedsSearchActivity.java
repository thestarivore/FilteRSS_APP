package com.company.rss.rss;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.company.rss.rss.models.Feed;
import com.company.rss.rss.models.Multifeed;

import java.util.Map;

public class FeedsSearchActivity extends AppCompatActivity {
    // TODO: view https://developer.android.com/training/improving-layouts/smooth-scrolling#java

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feeds_search_activity);

        // TODO: get feeds and multifeeds from the API
        final Feed[] feeds = Feed.generateMockupFeeds(10);
        final Multifeed[] multifeeds = Multifeed.generateMockupMultifeeds(4);

        final ListView listview = (ListView) findViewById(R.id.listViewFeedsList);
        final FeedsListAdapter adapter = new FeedsListAdapter(this, feeds);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                Log.v(ArticleActivity.logTag, "Feed " + id + " clicked");
                final Feed feed = (Feed) parent.getItemAtPosition(position);

                Log.v(ArticleActivity.logTag, "Feed information: " + feed.toString());

                // if feed not in any feed list
                new AlertDialog.Builder(FeedsSearchActivity.this)
                        .setTitle(R.string.dialog_add_feed_title)
                        .setSingleChoiceItems(Multifeed.toStrings(multifeeds), -1,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int selectedIndex) {
                                        Log.d(ArticleActivity.logTag, "Multifeed " + selectedIndex + " clicked");
                                        Log.d(ArticleActivity.logTag, "Multifeed information: " + multifeeds[selectedIndex].toString());

                                        boolean added = addFeedToMultifeed(feed, multifeeds[selectedIndex]);

                                        if (added) {
                                            // Animate add button
                                            ImageView imageViewAdd = (ImageView) view.findViewById(R.id.imageViewFeedsSearchAdd);
                                            imageViewAdd.animate().setDuration(500).rotation(45);
                                            dialog.dismiss();
                                        } else {
                                            // TODO: show error
                                            Toast.makeText(getApplicationContext(), R.string.feed_add_error , Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                })
                        .setPositiveButton(R.string.dialog_add_feed_positive_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(ArticleActivity.logTag, "Creating new multifeed");
                            }
                        })
                        .setNegativeButton(R.string.dialog_add_feed_negative_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(ArticleActivity.logTag, "Dialog closed");
                            }
                        })
                        .show();

                /*view.animate().setDuration(2000).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                *//*listview.remove(item);
                                adapter.notifyDataSetChanged();
                                view.setAlpha(1);*//*
                            }
                        });*/
            }

        });
    }

    private boolean addFeedToMultifeed(Feed feed, Multifeed multifeed) {
        // TODO: call the API and add the feed to the multifeed

        // Feed added
        Boolean feedAdded = true;
        if(feedAdded)
            return true;
        else
            return false;
    }

    private class ViewHolderFeed {
        TextView feedName;
        TextView feedCategory;
        ImageView feedIcon;
    }

    private class FeedsListAdapter extends ArrayAdapter<Feed> {
        private final Context context;
        private final Feed[] feeds;

        public FeedsListAdapter(Context context, Feed[] feeds) {
            super(context, -1, feeds);
            this.context = context;
            this.feeds = feeds;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolderFeed viewHolder;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.feeds_search_item, parent, false);

                viewHolder = new ViewHolderFeed();
                viewHolder.feedName = (TextView) convertView.findViewById(R.id.textViewFeedsSearchName);
                viewHolder.feedCategory = (TextView) convertView.findViewById(R.id.textViewFeedsSearchCategory);
                viewHolder.feedIcon = (ImageView) convertView.findViewById(R.id.imageViewFeedsSearchIcon);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolderFeed) convertView.getTag();
            }

            Feed feed = feeds[position];
            if (feed != null) {
                viewHolder.feedName.setText(feed.getName());
                viewHolder.feedCategory.setText(feed.getCategory());
            }
            return convertView;

        }
    }
}