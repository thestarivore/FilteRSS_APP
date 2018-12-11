package com.company.rss.rss;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.company.rss.rss.adapters.FeedsListAdapter;
import com.company.rss.rss.models.Feed;
import com.company.rss.rss.models.Multifeed;

import java.util.List;

public class FeedsSearchActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private FeedsListAdapter adapter;
    private NavigationView navigationView;

    // TODO: view https://developer.android.com/training/improving-layouts/smooth-scrolling#java

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeds_search);

        Toolbar toolbar = findViewById(R.id.feeds_search_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null){
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        }
        // TODO: get feeds and multifeeds from the API
        final List<Feed> feeds = Feed.generateMockupFeeds(10);
        final List<Multifeed> multifeeds = Multifeed.generateMockupMultifeeds(4);

        drawerLayout = findViewById(R.id.drawer_layout_feeds_search);
        drawerLayout.addDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                        // Respond when the drawer's position changes
                    }

                    @Override
                    public void onDrawerOpened(@NonNull View drawerView) {
                        // Respond when the drawer is opened
                    }

                    @Override
                    public void onDrawerClosed(@NonNull View drawerView) {
                        // Respond when the drawer is closed
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                        // Respond when the drawer motion state changes
                    }
                }
        );

        navigationView = findViewById(R.id.nav_view_categories);
        navigationView.getMenu().getItem(0).setChecked(true);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        drawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        // TODO: call the API and update the feed
                        String category = (String) menuItem.getTitle();
                        Log.v(ArticleActivity.logTag + ":" + getClass().getName(), "Looking for " + category + " feeds");

                        return true;
                    }
                });

        final ListView listview = findViewById(R.id.listViewFeedsList);
        adapter = new FeedsListAdapter(this, feeds, false);
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
                                        Log.v(ArticleActivity.logTag + ":" + getClass().getName(), "Multifeed " + selectedIndex + " clicked, info: " + multifeeds.get(selectedIndex).toString());

                                        boolean added = addFeedToMultifeed(feed, multifeeds.get(selectedIndex));

                                        if (added) {
                                            // Animate add (+) button that becomes remove (x) button
                                            ImageView imageViewAdd = view.findViewById(R.id.imageViewFeedsSearchActionIcon);
                                            imageViewAdd.animate().setDuration(500).rotation(45);
                                            dialog.dismiss();
                                        } else {
                                            // TODO: show error
                                            Toast.makeText(getApplicationContext(), R.string.feed_add_error, Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                })
                        .setPositiveButton(R.string.dialog_add_feed_positive_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.v(ArticleActivity.logTag + ":" + getClass().getName(), "Creating new multifeed");
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.v(ArticleActivity.logTag + ":" + getClass().getName(), "Dialog closed");
                            }
                        })
                        .show();
            }

        });
    }

    private boolean addFeedToMultifeed(Feed feed, Multifeed multifeed) {
        // TODO: call the API and add the feed to the multifeed

        // Feed added
        Boolean feedAdded = true;
        if (feedAdded)
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

                setNavigationViewLang(R.menu.drawer_view_categories_en);

                item.setChecked(!item.isChecked());
                Toast.makeText(getBaseContext(), "English lang selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.itemLanguageSelectorIT:
                // TODO: retrieve IT language feed from API and save to preferences

                setNavigationViewLang(R.menu.drawer_view_categories_it);

                item.setChecked(!item.isChecked());
                Toast.makeText(getBaseContext(), "Italian lang selected", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setNavigationViewLang(int menu) {
        navigationView.getMenu().clear();
        navigationView.inflateMenu(menu);
        navigationView.getMenu().getItem(0).setChecked(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_feeds_search, menu);

        // TODO: retrieve values for feeds language from preferences
        String preferredLanguage = "EN";

        if(preferredLanguage.equals("EN")){
            menu.findItem(R.id.itemLanguageSelectorEN).setChecked(true);
            setNavigationViewLang(R.menu.drawer_view_categories_en);
        } else if(preferredLanguage.equals("IT")){
            menu.findItem(R.id.itemLanguageSelectorEN).setChecked(true);
            setNavigationViewLang(R.menu.drawer_view_categories_it);
        }

        MenuItem item = menu.findItem(R.id.itemSearchFeeds);
        SearchView searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return onQueryTextSubmit(newText);
            }
        });

        return true;
    }
}