package com.company.rss.rss;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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
import com.company.rss.rss.models.SQLOperation;
import com.company.rss.rss.models.User;
import com.company.rss.rss.persistence.UserPrefs;
import com.company.rss.rss.restful_api.RESTMiddleware;
import com.company.rss.rss.restful_api.callbacks.FeedCallback;
import com.company.rss.rss.restful_api.callbacks.MultifeedCallback;
import com.company.rss.rss.restful_api.callbacks.SQLOperationCallback;

import java.util.List;

public class FeedsSearchActivity extends AppCompatActivity {
    private final String TAG = getClass().getName();
    private RESTMiddleware api;
    private User loggedUser;

    private static final int REQUEST_CREATE_MULTIFEED = 0;

    private DrawerLayout drawerLayout;
    private FeedsListAdapter adapter;
    private NavigationView navigationView;

    private List<Multifeed> multifeeds;
    private List<Feed> feeds;
    private ListView feedsListview;

    // TODO: view https://developer.android.com/training/improving-layouts/smooth-scrolling#java

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeds_search);

        UserPrefs prefs = new UserPrefs(this);
        loggedUser = prefs.retrieveUser();

        api = new RESTMiddleware(this);

        Toolbar toolbar = findViewById(R.id.feeds_search_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null){
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        }

        // TODO: substitute this with getTop10Feeds
        api.getAllFeeds(new FeedCallback() {
            @Override
            public void onLoad(List<Feed> feedsReply) {
                Log.d(ArticleActivity.logTag + ":" + TAG, "All Feeds Loaded: " + feedsReply.size());
                feeds = feedsReply;
                setFeedsList();
            }

            @Override
            public void onFailure() {
                Log.e(ArticleActivity.logTag + ":" + TAG, "All Feeds Error");
            }
        });

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

        
        // Navigation view with list of categories
        navigationView = findViewById(R.id.nav_view_categories);
        // Set the first item ("All") as checked
        navigationView.getMenu().getItem(0).setChecked(true);
        // Set click listener, when new cat is clicked change filter the dataset
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        drawerLayout.closeDrawers();

                        // Update the UI based on the item selected
                        final String category = (String) menuItem.getTitle();
                        Log.d(ArticleActivity.logTag + ":" + TAG, "Looking for " + category + " feeds");

                        // If the category is All get all the feeds otherwise get the feed by category
                        if(category.equals("All")){
                            api.getAllFeeds(new FeedCallback() {
                                @Override
                                public void onLoad(List<Feed> feedsReply) {
                                    updateFeedsList(feedsReply);
                                }

                                @Override
                                public void onFailure() {
                                    Log.e(ArticleActivity.logTag + ":" + TAG, "All Feeds Error");
                                }
                            });
                        } else {
                            api.getFilteredFeeds(null, category, new FeedCallback() {
                                @Override
                                public void onLoad(List<Feed> feedsReply) {
                                    updateFeedsList(feedsReply);
                                }

                                @Override
                                public void onFailure() {
                                    Log.e(ArticleActivity.logTag + ":" + TAG, "Feeds for " + category + " error");
                                }
                            });
                        }

                        return true;
                    }
                });

    }

    /*
    Initializes the feed list
     */
    private void setFeedsList() {
        // Get the feed list
        feedsListview = findViewById(R.id.listViewFeedsList);
        // Set the adapter
        adapter = new FeedsListAdapter(this, feeds, false);
        feedsListview.setAdapter(adapter);
        // On item click open the dialog for adding the feed to a multifeed
        feedsListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final Feed feed = (Feed) parent.getItemAtPosition(position);
                Log.d(ArticleActivity.logTag + ":" + TAG, "Feed " + id + " clicked. Info: " + feed.toString());

                // This dialog allows the user to add a feed into a multifeed display a SingleChoiceItems list
                new AlertDialog.Builder(FeedsSearchActivity.this)
                        .setTitle(R.string.dialog_add_feed_title)
                        .setSingleChoiceItems(Multifeed.toStrings(multifeeds), -1,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int selectedIndex) {
                                        Log.d(ArticleActivity.logTag + ":" + TAG, "Multifeed " + selectedIndex + " clicked, info: " + multifeeds.get(selectedIndex).toString());

                                        addFeedToMultifeed(feed, multifeeds.get(selectedIndex), dialog, view);
                                    }
                                })
                        // The positive button allows the creation of a new multifeed
                        .setPositiveButton(R.string.dialog_add_feed_positive_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startMultifeedCreationActivity();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(ArticleActivity.logTag + ":" + TAG, "Dialog closed");
                            }
                        })
                        .show();
            }

        });
    }

    /*
    Update the list with the new feedsReply
    */
    private void updateFeedsList(List<Feed> feedsReply) {
        Log.d(ArticleActivity.logTag + ":" + TAG, "Feeds updated: " + feedsReply.size());
        feeds = feedsReply;
        adapter.updateFeeds(feeds);    // update the feeds in the adapter
        feedsListview.setSelection(0); // set scroll position to 0
    }

    /*
    onResume update the user's multifeed
     */
    @Override
    protected void onResume() {
        super.onResume();

        api.getUserMultifeeds(loggedUser.getEmail(), new MultifeedCallback() {
            @Override
            public void onLoad(List<Multifeed> multifeedsReply) {
                Log.d(ArticleActivity.logTag + ":" + TAG, "User's Multifeed Loaded" + multifeedsReply.toString());
                multifeeds = multifeedsReply;
            }

            @Override
            public void onFailure() {
                Log.e(ArticleActivity.logTag + ":" + TAG, "User's Multifeed Error");

            }
        });


    }

    /*
    Add the selected feed to the user's multifeed
     */
    private void addFeedToMultifeed(final Feed feed, final Multifeed multifeed, final DialogInterface dialog, final View view) {
        api.addUserFeed(feed.getId(), multifeed.getId(), new SQLOperationCallback() {
            @Override
            public void onLoad(SQLOperation sqlOperation) {
                Log.d(ArticleActivity.logTag + ":" + TAG, "Feed " + feed.getId() + " added to multifeed " + multifeed.getId());

                ImageView imageViewAdd = view.findViewById(R.id.imageViewFeedsSearchActionIcon);
                imageViewAdd.animate().setDuration(500).rotation(45);
                dialog.dismiss();

                Snackbar.make(findViewById(android.R.id.content), feed.getTitle() + " " +
                        getText(R.string.saved_in) + " " + multifeed.getTitle(), Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFailure() {
                Log.e(ArticleActivity.logTag + ":" + TAG, "Feed " + feed.getId() + " NOT added to multifeed " + multifeed.getId());

                Snackbar.make(findViewById(android.R.id.content), R.string.error_adding_feed, Snackbar.LENGTH_LONG).show();
            }
        });


    }


    /*
    Start the activity for the creation of a new Multifeed
     */
    private void startMultifeedCreationActivity() {
        Log.d(ArticleActivity.logTag + ":" + TAG, "Starting multifeed creation...");
        Intent intent = new Intent(this, MultifeedCreationActivity.class);
        startActivityForResult(intent, REQUEST_CREATE_MULTIFEED);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CREATE_MULTIFEED) {
            if (resultCode == RESULT_OK) {
                Log.d(ArticleActivity.logTag + ":" + TAG, "Returned from MultifeedCreationActivity with RESULT_OK");
                Snackbar.make(findViewById(android.R.id.content), R.string.multifeed_saved, Snackbar.LENGTH_LONG).show();
            } else {
                Log.d(ArticleActivity.logTag + ":" + TAG, "Returned from MultifeedCreationActivity without RESULT_OK");
                Snackbar.make(findViewById(android.R.id.content), R.string.multifeed_not_saved, Snackbar.LENGTH_LONG).show();

            }
        }
    }
}