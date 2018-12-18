package com.company.rss.rss;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.company.rss.rss.adapters.ArticleSlidePagerAdapter;
import com.company.rss.rss.adapters.ExpandableListAdapter;
import com.company.rss.rss.fragments.ArticlesListFragment;
import com.company.rss.rss.fragments.ArticlesSlideFragment;
import com.company.rss.rss.models.Article;
import com.company.rss.rss.models.Category;
import com.company.rss.rss.models.Collection;
import com.company.rss.rss.models.Feed;
import com.company.rss.rss.models.FeedGrouping;
import com.company.rss.rss.models.Multifeed;
import com.company.rss.rss.models.RSSFeed;
import com.company.rss.rss.models.ReadArticle;
import com.company.rss.rss.models.SQLOperation;
import com.company.rss.rss.models.SavedArticle;
import com.company.rss.rss.models.User;
import com.company.rss.rss.models.UserData;
import com.company.rss.rss.restful_api.LoadUserCollections;
import com.company.rss.rss.restful_api.LoadUserData;
import com.company.rss.rss.restful_api.LoadUserMultifeeds;
import com.company.rss.rss.restful_api.RESTMiddleware;
import com.company.rss.rss.restful_api.callbacks.ArticleCallback;
import com.company.rss.rss.restful_api.callbacks.CategoryCallback;
import com.company.rss.rss.restful_api.callbacks.CollectionCallback;
import com.company.rss.rss.restful_api.callbacks.FeedCallback;
import com.company.rss.rss.restful_api.callbacks.FeedGroupCallback;
import com.company.rss.rss.restful_api.callbacks.MultifeedCallback;
import com.company.rss.rss.restful_api.callbacks.ReadArticleCallback;
import com.company.rss.rss.restful_api.callbacks.SQLOperationCallback;
import com.company.rss.rss.restful_api.callbacks.SavedArticleCallback;
import com.company.rss.rss.restful_api.callbacks.UserCallback;
import com.company.rss.rss.restful_api.interfaces.AsyncRSSFeedResponse;
import com.company.rss.rss.restful_api.interfaces.AsyncResponse;
import com.company.rss.rss.rss_parser.LoadRSSFeed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ArticlesListActivity extends AppCompatActivity implements ArticlesListFragment.OnListFragmentInteractionListener,
        ArticlesSlideFragment.OnFragmentInteractionListener {
    private static final String TAG = "LoadingActivity";
    private static final int REQUEST_CODE_MULTIFEED_EDIT = 1; // used for activities that can edit multifeed
    private static final int REQUEST_CODE_COLLECTION_EDIT = 2; // used for activities that can edit collections

    // TODO: refactor this
    private DrawerLayout drawerLayout;
    public static final String EXTRA_ARTICLE = "com.rss.rss.ARTICLE";
    private ViewPager pager;
    private PagerAdapter pagerAdapter;

    //Expandable List View on the Left Drawer
    //Multifeeds List
    private ExpandableListAdapter multifeedListAdapter;
    private ExpandableListView expListViewMultifeeds;
    private List<String> multifeedListHeaders;
    private HashMap<String, List<String>> multifeedListChild;
    //Collections List
    private ExpandableListAdapter collectionListAdapter;
    private ExpandableListView expListViewCollections;
    private List<String> collectionListHeaders;
    private HashMap<String, List<String>> collectionListChild;
    //Other
    private TextView textViewAllMultifeedList;
    private TextView textViewAccountEmail;


    private RESTMiddleware api;
    private List<Feed> feedList = new ArrayList<>();
    private Context context;
    private UserData userData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles_list);
        context = this;
        multifeedListHeaders = new ArrayList<String>();
        multifeedListChild = new HashMap<String, List<String>>();
        collectionListHeaders = new ArrayList<String>();
        collectionListChild = new HashMap<String, List<String>>();


        //Instantiate the Middleware for the RESTful API's
        api = new RESTMiddleware(this);

        //Get a UserData instance
        loadUserData();     // Fragment's onAttachFragment should run first, but this function
        // loads the UserData only if there is no copy retrieved yet

        //DRAWER Left Menu
        //Multifeed Expandable List
        initMultifeedListOnDrawer();
        //Collection Expandable List
        initCollectionListOnDrawer();
        //AllMultifeed TextView
        initAllMultifeedTextClick();

        // Drawer
        drawerLayout = findViewById(R.id.drawer_layout_articles_list);
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
        //Init User's Email TextView
        textViewAccountEmail = (TextView) findViewById(R.id.textViewAccountEmail);
        textViewAccountEmail.setText(userData.getUser().getEmail());

        //TOOLBAR
        initToolbar();

        // TOP ARTICLES - SLIDER
        // Set the slider to half the size of the viewport
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        ViewPager viewPager = (ViewPager) findViewById(R.id.pagerArticles);
        viewPager.getLayoutParams().height = size.y / 3;

        // Create mock articles for top articles
        List<Article> topArticles = Article.generateMockupArticles(6);

        pager = (ViewPager) findViewById(R.id.pagerArticles);
        pagerAdapter = new ArticleSlidePagerAdapter(getSupportFragmentManager(), topArticles);
        pager.setAdapter(pagerAdapter);
        pager.setCurrentItem(1, true);
        pager.setClipToPadding(false);
        pager.setPadding(0, 0, 60, 0);
        pager.setPageMargin(0);

    }


    /**
     * Initialize the Activity's Toolbar(ActionBar)
     */
    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.articles_list_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

        //Set the appropriate title
        //All Multifeeds Feeds Articles
        if (userData.getVisualizationMode() == UserData.MODE_ALL_MULTIFEEDS_FEEDS) {
            actionbar.setTitle("All");
        }
        //Multifeed Articles
        else if (userData.getVisualizationMode() == UserData.MODE_MULTIFEED_ARTICLES) {
            Multifeed multifeed = userData.getMultifeedList().get(userData.getMultifeedPosition());
            actionbar.setTitle(multifeed.getTitle());
        }
        //Feed Articles
        else if (userData.getVisualizationMode() == UserData.MODE_FEED_ARTICLES) {
            Multifeed multifeed = userData.getMultifeedList().get(userData.getMultifeedPosition());
            Feed feed = userData.getMultifeedMap().get(multifeed).get(userData.getFeedPosition());
            actionbar.setTitle(feed.getTitle());
        }
        //Collection Articles
        else if (userData.getVisualizationMode() == UserData.MODE_COLLECTION_ARTICLES) {
            Collection collection = userData.getCollectionList().get(userData.getCollectionPosition());
            actionbar.setTitle(collection.getTitle());
        }
    }

    /**
     * Initialize AllMultifeedList TextView and OnClick listener -> Restart the Activity and show all
     * the Multifeeds Feeds Articles.
     */
    private void initAllMultifeedTextClick() {
        textViewAllMultifeedList = (TextView) findViewById(R.id.textViewMultifeedList);
        textViewAllMultifeedList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Prepare the new ListToVisualize and Restart Activity
                userData.setVisualizationMode(UserData.MODE_ALL_MULTIFEEDS_FEEDS);
                restartActivity();
            }
        });
    }

    /**
     * Initialize Multifeed Expandable List on the Left Drawer
     */
    private void initMultifeedListOnDrawer() {
        expListViewMultifeeds = (ExpandableListView) findViewById(R.id.exp_list_view_multifeeds);
        prepareMultifeedsListData();
        multifeedListAdapter = new ExpandableListAdapter(this, multifeedListHeaders, multifeedListChild);
        expListViewMultifeeds.setAdapter(multifeedListAdapter);
        //Expand all the groups
        for (int i = 0; i < multifeedListAdapter.getGroupCount(); i++) {
            expListViewMultifeeds.expandGroup(i);
        }
        //Item LongClick
        expListViewMultifeeds.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                long packedPosition = expListViewMultifeeds.getExpandableListPosition(position);
                int itemType = ExpandableListView.getPackedPositionType(packedPosition);
                if (itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                    startMultifeedManagerActivity();
                }

                return false;
            }
        });
        //Child Click
        expListViewMultifeeds.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Log.d(TAG, "Feed click:" + groupPosition + "," + childPosition + ":"
                        + userData.getMultifeedList().get(groupPosition).getTitle() + "-"
                        + userData.getMultifeedMap().get(userData.getMultifeedList().get(groupPosition)).get(childPosition).getTitle());

                //Prepare the new ListToVisualize and Restart Activity
                userData.setVisualizationMode(UserData.MODE_FEED_ARTICLES);
                userData.setMultifeedPosition(groupPosition);
                userData.setFeedPosition(childPosition);
                restartActivity();
                return false;
            }
        });
        //Group Click
        expListViewMultifeeds.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                Log.d(TAG, "Multifeed click:" + groupPosition + ":" + userData.getMultifeedList().get(groupPosition).getTitle());

                //Prepare the new ListToVisualize and Restart Activity
                userData.setVisualizationMode(UserData.MODE_MULTIFEED_ARTICLES);
                userData.setMultifeedPosition(groupPosition);
                restartActivity();
                return true; // This way the expander cannot be collapsed
            }
        });
    }

    /**
     * Initialize Collection Expandable List on the Left Drawer
     */
    private void initCollectionListOnDrawer() {
        expListViewCollections = (ExpandableListView) findViewById(R.id.exp_list_view_collections);
        prepareCollectionsListData();
        collectionListAdapter = new ExpandableListAdapter(this, collectionListHeaders, collectionListChild);
        expListViewCollections.setAdapter(collectionListAdapter);
        //Item LongClick
        expListViewCollections.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                long packedPosition = expListViewCollections.getExpandableListPosition(position);
                int itemType = ExpandableListView.getPackedPositionType(packedPosition);
                if (itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                    startCollectionManagerActivity();
                }
                return false;
            }
        });
        //Group Click
        expListViewCollections.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                Log.d(TAG, "Collection click:" + groupPosition + ":" + userData.getCollectionList().get(groupPosition).getTitle());

                //Prepare the new ListToVisualize and Restart Activity
                userData.setVisualizationMode(UserData.MODE_COLLECTION_ARTICLES);
                userData.setCollectionPosition(groupPosition);
                restartActivity();
                return true; // This way the expander cannot be collapsed
            }
        });
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof ArticlesListFragment) {
            ArticlesListFragment articlesListFragment = (ArticlesListFragment) fragment;

            //Get a UserData instance
            loadUserData();

            //Notify the fragment
            articlesListFragment.onUserDataLoaded();
        }

    }

    /**
     * Load the UserData object persisted in the SharedMemory
     */
    private void loadUserData() {
        if (userData == null) {
            //Get a UserData instance
            userData = UserData.getInstance();
            userData.loadPersistedData(context);
            userData.processUserData();
        }
    }

    /**
     * Prepare the Multifeeds Expandable List View on the Drawer
     */
    private void prepareMultifeedsListData() {
        //For each user's multifeed
        for (Multifeed multifeed : userData.getMultifeedList()) {
            //Add the multifeed header
            multifeedListHeaders.add(multifeed.getTitle());
            //Add the associated feed list
            multifeedListChild.put(multifeed.getTitle(), userData.getMapFeedTitlesListByKey(multifeed));
        }
    }

    /**
     * Prepare the Collection Expandable List View on the Drawer
     */
    private void prepareCollectionsListData() {
        //For each user's collection
        for (Collection collection : userData.getCollectionList()) {
            //Add the multifeed header
            collectionListHeaders.add(collection.getTitle());
            //Add the associated feed list --> don't show the articles here
            //collectionListChild.put(collection.getTitle(), userData.getMapArticleTitlesListByKey(collection));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_articles_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.itemSearchArticleList:
                startFeedsSearchActivity();
                return (true);
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    Handles the interactions with the list, click
     */

    @Override
    public void onListFragmentInteractionClick(Article article) {
        Log.v(ArticleActivity.logTag, article.toString());
        startArticleActivity(article);
    }
    /*
    Handles the interactions with the list, swipe
     */

    @Override
    public void onListFragmentInteractionSwipe(Article article) {
        Log.v(ArticleActivity.logTag, article.toString());
        /* TODO: save article in the read it later collection if not already present
        otherwise remove it from read it later */
        boolean alreadyPresent = false;
        if (alreadyPresent) {
            Toast.makeText(this, R.string.unswipe_to_remove, Toast.LENGTH_LONG).show();
        }
    }

    /*
    Handles the interactions with the top slider
     */
    @Override
    public void onFragmentInteraction(Article article) {
        Log.v(ArticleActivity.logTag, article.toString());
        startArticleActivity(article);
    }

    private void startMultifeedManagerActivity() {
        Intent intent = new Intent(this, MultifeedManagerActivity.class);
        startActivityForResult(intent, REQUEST_CODE_MULTIFEED_EDIT);
    }

    private void startFeedsSearchActivity() {
        Intent intent = new Intent(this, FeedsSearchActivity.class);
        startActivityForResult(intent, REQUEST_CODE_MULTIFEED_EDIT);
    }

    private void startCollectionManagerActivity() {
        Intent intent = new Intent(this, CollectionManagerActivity.class);
        startActivityForResult(intent, REQUEST_CODE_COLLECTION_EDIT);
    }

    private void startArticleActivity(Article article) {
        Intent intent = new Intent(this, ArticleActivity.class);
        intent.putExtra(EXTRA_ARTICLE, article);
        startActivityForResult(intent, REQUEST_CODE_COLLECTION_EDIT);
    }

    private void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }


    /**
     * When returning from an activity that performs changes on the data we need to refresh
     * the drawer with the new data
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(ArticleActivity.logTag + ":" + TAG, "Returning with " + REQUEST_CODE_MULTIFEED_EDIT);

        if (requestCode == REQUEST_CODE_MULTIFEED_EDIT && resultCode == RESULT_OK) {
            // A multifeed may be edited or created so refresh the local data

            Log.d(ArticleActivity.logTag + ":" + TAG, "Refreshing User's Multifeeds... ");
            Snackbar.make(findViewById(android.R.id.content), R.string.updating_user_information, Snackbar.LENGTH_LONG).show();


            // Refresh the user's multifeed saved locally
            new LoadUserMultifeeds(new AsyncResponse() {
                @Override
                public void processFinish(Object output) {
                    Log.d(ArticleActivity.logTag + ":" + TAG, "User's Multifeeds refreshed... ");

                    userData.loadPersistedData(context);
                    userData.processUserData();

                    multifeedListHeaders.clear();
                    prepareMultifeedsListData();
                    multifeedListAdapter.notifyDataSetChanged();

                    Snackbar.make(findViewById(android.R.id.content), R.string.user_information_updated, Snackbar.LENGTH_LONG).show();


                }
            }, this, userData.getUser()).execute();

        } else if (requestCode == REQUEST_CODE_COLLECTION_EDIT && resultCode == RESULT_OK) {
            // A collection may be edited or created so refresh the local data

            Log.d(ArticleActivity.logTag + ":" + TAG, "Refreshing User's Collections... ");
            Snackbar.make(findViewById(android.R.id.content), R.string.updating_user_information, Snackbar.LENGTH_LONG).show();


            // Refresh the user's collection saved locally
            new LoadUserCollections(new AsyncResponse() {
                @Override
                public void processFinish(Object output) {
                    Log.d(ArticleActivity.logTag + ":" + TAG, "User's Collections refreshed... ");

                    userData.loadPersistedData(context);
                    userData.processUserData();

                    collectionListHeaders.clear();
                    prepareCollectionsListData();
                    collectionListAdapter.notifyDataSetChanged();

                    Snackbar.make(findViewById(android.R.id.content), R.string.user_information_updated, Snackbar.LENGTH_LONG).show();


                }
            }, this, userData.getUser()).execute();

        }
    }

    /**
     * Calls all the RESTful APIs to test them
     */
    private void restApiTestCalls() {
        final ProgressDialog progressDialog =
                ProgressDialog.show(this, "Wait", "Executing RESTful APIs...", true, false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        Thread thread = new Thread() {
            @Override
            public void run() {
                /*********************** Categories *********************************/
                api.getAllCategories(new CategoryCallback() {
                    @Override
                    public void onLoad(List<Category> categories) {
                        for (Category category : categories) {
                            Log.d(TAG, "\nCategory: " + category.getName() + ", " + category.getLang());
                        }
                    }

                    @Override
                    public void onFailure() {
                        Log.d(TAG, "\nFailure on: getAllCategories");
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }

                /*********************** Auth *********************************/
                //Registration
                api.registerNewUser("pino", "dino", "pinodino", "qwerty", new SQLOperationCallback() {
                    @Override
                    public void onLoad(SQLOperation sqlOperation) {
                        if (sqlOperation.getAffectedRows() == 1) {
                            Log.d(TAG, "\nRegistered User with id: " + sqlOperation.getInsertId());
                        } else {
                            Log.d(TAG, "\nNO User Registered");
                        }
                    }

                    @Override
                    public void onFailure() {
                        Log.d(TAG, "\nFailure on: registerNewUser");
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }

                //Authentication
                api.getUserAuthentication("pinodino", "qwerty", new UserCallback() {
                    @Override
                    public void onLoad(List<User> users) {
                        for (User user : users) {
                            Log.d(TAG, "\nUser authentication");
                            Log.d(TAG, "\nUser: " + user.getId() + ", " + user.getName() + ", " + user.getSurname() + ", " + user.getEmail() + ", " + user.getPassword());
                        }
                    }

                    @Override
                    public void onFailure() {
                        Log.d(TAG, "\nFailure on: getUserAuthentication");
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }

                //Change password
                api.changeUsersPassword("pinodino", "qwerty12345", new SQLOperationCallback() {
                    @Override
                    public void onLoad(SQLOperation sqlOperation) {
                        if (sqlOperation.getAffectedRows() == 1) {
                            Log.d(TAG, "\nChanged the password of the User");
                        } else {
                            Log.d(TAG, "\nNO change!");
                        }
                    }

                    @Override
                    public void onFailure() {
                        Log.d(TAG, "\nFailure on: changeUsersPassword");
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }

                //Authentication
                api.getUserAuthentication("pinodino", "qwerty12345", new UserCallback() {
                    @Override
                    public void onLoad(List<User> users) {
                        for (User user : users) {
                            Log.d(TAG, "\nUser authentication");
                            Log.d(TAG, "\nUser: " + user.getId() + ", " + user.getName() + ", " + user.getSurname() + ", " + user.getEmail() + ", " + user.getPassword());
                        }
                    }

                    @Override
                    public void onFailure() {
                        Log.d(TAG, "\nFailure on: getUserAuthentication");
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }

                /*********************** Feeds *********************************/
                //Get all the Feeds
                api.getAllFeeds(new FeedCallback() {
                    @Override
                    public void onLoad(List<Feed> feeds) {
                        Log.d(TAG, "\nNumber of Feeds: " + feeds.size());

                        //Get a copy of the feeds
                        feedList.addAll(feeds);

                        // Start parsing the first 20 feeds
                        for (int i = 0; i < 20; i++) {
                            //String feedURL = feeds.get(i).getLink();
                            new LoadRSSFeed(new AsyncRSSFeedResponse() {
                                @Override
                                public void processFinish(Object output, RSSFeed rssFeed) {
                                }
                            }, context, feeds.get(i)).execute();
                        }
                    }

                    @Override
                    public void onFailure() {
                        Log.d(TAG, "\nFailure on: getAllFeeds");
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }

                //Add a new Feed
                api.addFeed("UniqueTitle", "www.anything.rss.net", "Tech", "EN", new SQLOperationCallback() {
                    @Override
                    public void onLoad(SQLOperation sqlOperation) {
                        if (sqlOperation.getAffectedRows() == 1) {
                            Log.d(TAG, "\nAdded a new feed!");
                        } else {
                            Log.d(TAG, "\nFeed NOT added!");
                        }
                    }

                    @Override
                    public void onFailure() {
                        Log.d(TAG, "\nFailure on: addFeed");
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }

                //Get all the Feeds Filtered by a string
                api.getFilteredFeeds("UniqueTitle", null, new FeedCallback() {
                    @Override
                    public void onLoad(List<Feed> feeds) {
                        for (Feed feed : feeds) {
                            Log.d(TAG, "\nFeed: " + feed.getTitle() + "," + feed.getLink() + "," + feed.getCategory() + "," + feed.getLang());
                        }
                    }

                    @Override
                    public void onFailure() {
                        Log.d(TAG, "\nFailure on: getFilteredFeeds");
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }

                /*********************** User - Feeds *********************************/
                //Add a Feed to the User's Multifeed
                Log.d(TAG, "\naddUserFeed:");
                api.addUserFeed(1032, 7, new SQLOperationCallback() {
                    @Override
                    public void onLoad(SQLOperation sqlOperation) {
                        if (sqlOperation.getAffectedRows() == 1) {
                            Log.d(TAG, "\nAssociated feed to user's multifeed");
                        } else {
                            Log.d(TAG, "\nFeed NOT associated!");
                        }
                    }

                    @Override
                    public void onFailure() {
                        Log.d(TAG, "\nFailure on: addUserFeed");
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }

                //Get all the User's Feeds
                Log.d(TAG, "\ngetUserFeeds:");
                api.getUserFeeds("test", new FeedCallback() {
                    @Override
                    public void onLoad(List<Feed> feeds) {
                        for (Feed feed : feeds) {
                            Log.d(TAG, "\nFeed: " + feed.getTitle() + "," + feed.getLink() + "," + feed.getCategory() + "," + feed.getLang());
                        }
                    }

                    @Override
                    public void onFailure() {
                        Log.d(TAG, "\nFailure on: getUserFeeds");
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }

                //Get all the User's FeedGroups
                Log.d(TAG, "\ngetUserFeedGroups:");
                api.getUserFeedGroups(5, new FeedGroupCallback() {
                    @Override
                    public void onLoad(List<FeedGrouping> feedGroups) {
                        for (FeedGrouping feedGroup : feedGroups) {
                            Log.d(TAG, "\nFeed: " + feedGroup.getFeed() + "," + feedGroup.getMultifeed() + "," + feedGroup.getArticleCheckpoint());
                        }
                    }

                    @Override
                    public void onFailure() {
                        Log.d(TAG, "\nFailure on: getUserFeedGroups");
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }

                //Add an article checkpoint to the Feed associated with the User's Multifeed
                Log.d(TAG, "\naddUserFeedCheckpoint:");
                api.addUserFeedCheckpoint(1032, 7, 7266917625629689308L, new SQLOperationCallback() {
                    @Override
                    public void onLoad(SQLOperation sqlOperation) {
                        if (sqlOperation.getAffectedRows() == 1) {
                            Log.d(TAG, "\nAdded a checpoint article to the feed related to a user!");
                        } else {
                            Log.d(TAG, "\nChecpoint NOT associated!");
                        }
                    }

                    @Override
                    public void onFailure() {
                        Log.d(TAG, "\nFailure on: addUserFeedCheckpoint");
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }

                //Get the checkpoint of the User's Feed-Multifeed Group
                Log.d(TAG, "\ngetUserFeedCheckpoint:");
                api.getUserFeedCheckpoint(1032, 7, new ArticleCallback() {
                    @Override
                    public void onLoad(List<Article> articles) {
                        for (Article article : articles) {
                            Log.d(TAG, "\nArticle: " + article.getHashId() + "," + article.getTitle() + "," + article.getLink() + "," + article.getImgLink());
                        }
                    }

                    @Override
                    public void onFailure() {
                        Log.d(TAG, "\nFailure on: getUserFeedCheckpoint");
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }

                //Delete a Feed related to a User's Multifeed
                Log.d(TAG, "\ndeleteUserFeed:");
                api.deleteUserFeed(1032, 7, new SQLOperationCallback() {
                    @Override
                    public void onLoad(SQLOperation sqlOperation) {
                        if (sqlOperation.getAffectedRows() == 1) {
                            Log.d(TAG, "\nDeleted a feed-multifeed-user association!");
                        } else {
                            Log.d(TAG, "\nDelete NOT done!");
                        }
                    }

                    @Override
                    public void onFailure() {
                        Log.d(TAG, "\nFailure on: deleteUserFeed");
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }

                /*********************** User - Multifeed *********************************/
                //Add a Multifeed to a User
                Log.d(TAG, "\naddUserMultifeed:");
                api.addUserMultifeed("TestMultifeed", 1, 7, new SQLOperationCallback() {
                    @Override
                    public void onLoad(SQLOperation sqlOperation) {
                        if (sqlOperation.getAffectedRows() == 1) {
                            Log.d(TAG, "\nAdded a Multifeed!");
                        } else {
                            Log.d(TAG, "\nMultifeed NOT added!");
                        }
                    }

                    @Override
                    public void onFailure() {
                        Log.d(TAG, "\nFailure on: addUserMultifeed");
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }

                //Gets the list of all the User's Multifeeds
                Log.d(TAG, "\ngetUserMultifeeds:");
                api.getUserMultifeeds("pino", new MultifeedCallback() {
                    @Override
                    public void onLoad(List<Multifeed> multifeeds) {
                        for (Multifeed multifeed : multifeeds) {
                            Log.d(TAG, "\nrMultifeed: " + multifeed.getTitle() + "," + multifeed.getUser() + "," + multifeed.getColor());
                        }
                    }

                    @Override
                    public void onFailure() {
                        Log.d(TAG, "\nFailure on: getUserMultifeeds");
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }

                //Delete a Multifeed related to a User (manually select one)
                Log.d(TAG, "\ndeleteUserMultifeed:");
                api.deleteUserMultifeed(16, new SQLOperationCallback() {
                    @Override
                    public void onLoad(SQLOperation sqlOperation) {
                        if (sqlOperation.getAffectedRows() == 1) {
                            Log.d(TAG, "\nDeleted a multifeed!");
                        } else {
                            Log.d(TAG, "\nDelete NOT done!");
                        }
                    }

                    @Override
                    public void onFailure() {
                        Log.d(TAG, "\nFailure on: deleteUserMultifeed");
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }


                /*********************** User - Collection *********************************/
                //Add a Collection to a User
                Log.d(TAG, "\naddUserCollection:");
                api.addUserCollection("TestCollection", 1, 7, new SQLOperationCallback() {
                    @Override
                    public void onLoad(SQLOperation sqlOperation) {
                        if (sqlOperation.getAffectedRows() == 1) {
                            Log.d(TAG, "\nAdded a Collection!");
                        } else {
                            Log.d(TAG, "\nCollection NOT added!");
                        }
                    }

                    @Override
                    public void onFailure() {
                        Log.d(TAG, "\nFailure on: addUserCollection");
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }

                //Gets the list of all the User's Collections
                Log.d(TAG, "\ngetUserCollections:");
                api.getUserCollections("pino", new CollectionCallback() {
                    @Override
                    public void onLoad(List<Collection> collections) {
                        for (Collection collection : collections) {
                            Log.d(TAG, "\nCollection: " + collection.getTitle() + "," + collection.getUser() + "," + collection.getColor());
                        }
                    }

                    @Override
                    public void onFailure() {
                        Log.d(TAG, "\nFailure on: getUserCollections");
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }

                //Delete a Collection related to a User (manually select one)
                Log.d(TAG, "\ndeleteUserCollection:");
                api.deleteUserCollection(4, new SQLOperationCallback() {
                    @Override
                    public void onLoad(SQLOperation sqlOperation) {
                        if (sqlOperation.getAffectedRows() == 1) {
                            Log.d(TAG, "\nDeleted a Collection!");
                        } else {
                            Log.d(TAG, "\nDelete NOT done!");
                        }
                    }

                    @Override
                    public void onFailure() {
                        Log.d(TAG, "\nFailure on: deleteUserCollection");
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }


                /*********************** User - Articles *********************************/
                //Add a Article to a User
                Log.d(TAG, "\naddUserArticle:");
                api.addUserArticle("TestArticle", "-", "-", "www.test.com", "www.img.com",
                        "2018-10-21 00:00:00", 8, 1032, new SQLOperationCallback() {
                            @Override
                            public void onLoad(SQLOperation sqlOperation) {
                                if (sqlOperation.getAffectedRows() == 1) {
                                    Log.d(TAG, "\nAdded an Article!");
                                } else {
                                    Log.d(TAG, "\nArticle NOT added!");
                                }
                            }

                            @Override
                            public void onFailure() {
                                Log.d(TAG, "\nFailure on: addUserArticle");
                            }
                        });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }

                //Gets the list of all the User's Articles by feed
                Log.d(TAG, "\ngetUserArticlesByFeed:");
                api.getUserArticlesByFeed(1032, new ArticleCallback() {
                    @Override
                    public void onLoad(List<Article> articles) {
                        for (Article article : articles) {
                            Log.d(TAG, "\nArticle: " + article.getHashId() + "," + article.getTitle() + "," + article.getLink());
                        }
                    }

                    @Override
                    public void onFailure() {
                        Log.d(TAG, "\nFailure on: getUserArticlesByFeed");
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }

                //Gets the list of all the User's Articles by user id
                Log.d(TAG, "\ngetUserArticles:");
                api.getUserArticles(16, new ArticleCallback() {
                    @Override
                    public void onLoad(List<Article> articles) {
                        for (Article article : articles) {
                            Log.d(TAG, "\nArticle: " + article.getHashId() + "," + article.getTitle() + "," + article.getLink());
                        }
                    }

                    @Override
                    public void onFailure() {
                        Log.d(TAG, "\nFailure on: getUserArticles");
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }

                //Delete a Article related to a User (manually select one)
                Log.d(TAG, "\ndeleteUserArticle:");
                api.deleteUserArticle(-4948164086017713000L, new SQLOperationCallback() {
                    @Override
                    public void onLoad(SQLOperation sqlOperation) {
                        if (sqlOperation.getAffectedRows() == 1) {
                            Log.d(TAG, "\nDeleted a Article!");
                        } else {
                            Log.d(TAG, "\nDelete NOT done!");
                        }
                    }

                    @Override
                    public void onFailure() {
                        Log.d(TAG, "\nFailure on: deleteUserArticle");
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }


                /*********************** User - SavedArticles *********************************/
                //Add a SavedArticle to a User
                Log.d(TAG, "\naddUserSavedArticle:");
                api.addUserSavedArticle(7266917625629689308L, 3, new SQLOperationCallback() {
                    @Override
                    public void onLoad(SQLOperation sqlOperation) {
                        if (sqlOperation.getAffectedRows() == 1) {
                            Log.d(TAG, "\nAdded an SavedArticle!");
                        } else {
                            Log.d(TAG, "\nSavedArticle NOT added!");
                        }
                    }

                    @Override
                    public void onFailure() {
                        Log.d(TAG, "\nFailure on: addUserSavedArticle");
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }

                //Gets the list of all the User's Articles saved in a Collection
                Log.d(TAG, "\ngetUserArticlesSavedInCollection:");
                api.getUserArticlesSavedInCollection(3, new ArticleCallback() {
                    @Override
                    public void onLoad(List<Article> articles) {
                        for (Article article : articles) {
                            Log.d(TAG, "\nSavedArticle: " + article.getHashId() + "," + article.getTitle() + "," + article.getLink());
                        }
                    }

                    @Override
                    public void onFailure() {
                        Log.d(TAG, "\nFailure on: getUserArticlesSavedInCollection");
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }

                //Gets the list of all the User's SavedArticles
                Log.d(TAG, "\ngetUserSavedArticles:");
                api.getUserSavedArticles(16, new SavedArticleCallback() {
                    @Override
                    public void onLoad(List<SavedArticle> savedArticles) {
                        for (SavedArticle savedArticle : savedArticles) {
                            Log.d(TAG, "\nSavedArticle: " + savedArticle.getArticle() + "," + savedArticle.getCollection());
                        }
                    }

                    @Override
                    public void onFailure() {
                        Log.d(TAG, "\nFailure on: getUserSavedArticles");
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }

                //Delete a SavedArticle related to a User (manually select one)
                Log.d(TAG, "\ndeleteUserSavedArticle:");
                api.deleteUserSavedArticle(7266917625629689308L, 3, new SQLOperationCallback() {
                    @Override
                    public void onLoad(SQLOperation sqlOperation) {
                        if (sqlOperation.getAffectedRows() == 1) {
                            Log.d(TAG, "\nDeleted a SavedArticle!");
                        } else {
                            Log.d(TAG, "\nDelete NOT done!");
                        }
                    }

                    @Override
                    public void onFailure() {
                        Log.d(TAG, "\nFailure on: deleteUserSavedArticle");
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }


                /*********************** User - ReadArticles *********************************/
                //Add a ReadArticle to a User
                Log.d(TAG, "\naddUserReadArticle:");
                api.addUserReadArticle(2, 8375590935598658000L, 2, new SQLOperationCallback() {
                    @Override
                    public void onLoad(SQLOperation sqlOperation) {
                        if (sqlOperation.getAffectedRows() == 1) {
                            Log.d(TAG, "\nAdded an ReadArticle!");
                        } else {
                            Log.d(TAG, "\nReadArticle NOT added!");
                        }
                    }

                    @Override
                    public void onFailure() {
                        Log.d(TAG, "\nFailure on: addUserReadArticle");
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }

                //Gets the list of all the User's ReadArticle
                Log.d(TAG, "\ngetUserReadArticles:");
                api.getUserReadArticles(3, new ReadArticleCallback() {
                    @Override
                    public void onLoad(List<ReadArticle> readArticles) {
                        for (ReadArticle readArticle : readArticles) {
                            Log.d(TAG, "\nReadArticle: " + readArticle.getArticle() + "," + readArticle.getUser() + "," + readArticle.getVote());
                        }
                    }

                    @Override
                    public void onFailure() {
                        Log.d(TAG, "\nFailure on: getUserReadArticles");
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }

                //Delete a ReadArticle related to a User (manually select one)
                Log.d(TAG, "\ndeleteUserReadArticle:");
                api.deleteUserReadArticle(2, 8375590935598658000L, new SQLOperationCallback() {
                    @Override
                    public void onLoad(SQLOperation sqlOperation) {
                        if (sqlOperation.getAffectedRows() == 1) {
                            Log.d(TAG, "\nDeleted a ReadArticle!");
                        } else {
                            Log.d(TAG, "\nDelete NOT done!");
                        }
                    }

                    @Override
                    public void onFailure() {
                        Log.d(TAG, "\nFailure on: deleteUserReadArticle");
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }

                //Dismiss dialog
                progressDialog.dismiss();
            }
        };
        thread.start();
    }

}
