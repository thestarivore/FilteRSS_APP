package com.company.rss.rss;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.company.rss.rss.adapters.FeedsListAdapter;
import com.company.rss.rss.models.Feed;
import com.company.rss.rss.models.Multifeed;

import java.util.List;

public class FeedsSearchActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;

    // TODO: view https://developer.android.com/training/improving-layouts/smooth-scrolling#java

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeds_search);

        Toolbar toolbar = findViewById(R.id.feeds_search_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

        // TODO: get feeds and multifeeds from the API
        final List<Feed> feeds = Feed.generateMockupFeeds(10);
        final List<Multifeed> multifeeds = Multifeed.generateMockupMultifeeds(4);

        drawerLayout = findViewById(R.id.drawer_layout_feeds_search);
        drawerLayout.addDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        // Respond when the drawer's position changes
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Respond when the drawer is opened
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        // Respond when the drawer is closed
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                        // Respond when the drawer motion state changes
                    }
                }
        );

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        drawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        return true;
                    }
                });

        final ListView listview = (ListView) findViewById(R.id.listViewFeedsList);
        final FeedsListAdapter adapter = new FeedsListAdapter(this, feeds, false);
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
                                        Log.d(ArticleActivity.logTag, "Multifeed information: " + multifeeds.get(selectedIndex).toString());

                                        boolean added = addFeedToMultifeed(feed, multifeeds.get(selectedIndex));

                                        if (added) {
                                            // Animate add (+) button that becomes remove (x) button
                                            ImageView imageViewAdd = (ImageView) view.findViewById(R.id.imageViewFeedsSearchActionIcon);
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
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.itemLanguageSelectorEN:
                // TODO: retrieve EN language feed from API and save to preferences

                item.setChecked(!item.isChecked());
                Toast.makeText(getBaseContext(), "English lang selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.itemLanguageSelectorIT:
                // TODO: retrieve IT language feed from API and save to preferences

                item.setChecked(!item.isChecked());
                Toast.makeText(getBaseContext(), "Italian lang selected", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_feeds_search, menu);


        // TODO: retrieve values for feeds language from preferences
        CheckBox checkBox = (CheckBox) menu.findItem(R.id.itemLanguageSelectorEN).getActionView();
        checkBox.setChecked(true);

        return true;
    }
}