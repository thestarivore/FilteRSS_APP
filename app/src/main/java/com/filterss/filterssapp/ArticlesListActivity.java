package com.filterss.filterssapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.filterss.filterssapp.adapters.ArticleSlidePagerAdapter;
import com.filterss.filterssapp.adapters.ExpandableListAdapter;
import com.filterss.filterssapp.fragments.ArticlesListFragment;
import com.filterss.filterssapp.fragments.ArticlesSlideFragment;
import com.filterss.filterssapp.models.Article;
import com.filterss.filterssapp.models.Collection;
import com.filterss.filterssapp.models.Feed;
import com.filterss.filterssapp.models.Multifeed;
import com.filterss.filterssapp.models.SQLOperation;
import com.filterss.filterssapp.models.SavedArticle;
import com.filterss.filterssapp.models.UserData;
import com.filterss.filterssapp.persistence.UserPrefs;
import com.filterss.filterssapp.restful_api.LoadUserCollections;
import com.filterss.filterssapp.restful_api.LoadUserMultifeeds;
import com.filterss.filterssapp.restful_api.RESTMiddleware;
import com.filterss.filterssapp.restful_api.callbacks.SQLOperationCallback;
import com.filterss.filterssapp.restful_api.interfaces.AsyncResponse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class ArticlesListActivity extends AppCompatActivity implements ArticlesListFragment.OnListFragmentInteractionListener,
        ArticlesSlideFragment.OnFragmentInteractionListener {
    private final String TAG = getClass().getName();
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
    private HashMap<String, List<String>> multifeedListFeedIcon;
    private HashMap<String, Integer> multifeedColorList;
    //Collections List
    private ExpandableListAdapter collectionListAdapter;
    private ExpandableListView expListViewCollections;
    private List<String> collectionListHeaders;
    private HashMap<String, List<String>> collectionListChild;
    private HashMap<String, List<String>> collectionListFeedIcon;
    private HashMap<String, Integer> collectionColorList;
    //Other
    private TextView textViewMultifeedList;
    private TextView textViewCollectionsList;
    private TextView textViewAccountEmail;
    private TextView textViewAccountName;

    private RESTMiddleware api;
    private Context context;
    private UserData userData;
    private UserPrefs userPrefs;
    private ProgressBar progressBar;
    private LinearLayout contentLinearLayout;
    private Map<String, Integer> feedArticlesNumberMap;
    private List<Article> topArticles;

    //Articles added to Read It Later collection, used to avoid calling the api multiple times if multiple swipe are performed
    private List<Long> articlesAddedToRIL;
    // articles removed from a collection, used to avoid calling the api multiple times if multiple swipe are performed
    private List<Long> articlesRemovedFromColl;
    private ActionBar actionbar;
    private Window window;

    // Autoslider
    int currentSlide = 1;
    boolean autoSliderOn;
    private Handler autoSliderHandler;
    private Runnable autoSliderRunnable;
    private int autoSliderTimeoutMillis = 4000;
    private ArticlesListFragment articlesListFragment;
    private AppBarLayout appBarLayout;
    private Button updateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles_list);

        window = this.getWindow();

        context = this;
        multifeedListHeaders = new ArrayList<String>();
        multifeedListChild = new HashMap<String, List<String>>();
        multifeedListFeedIcon = new HashMap<String, List<String>>();
        multifeedColorList = new HashMap<String, Integer>();
        collectionListHeaders = new ArrayList<String>();
        collectionListChild = new HashMap<String, List<String>>();
        collectionListFeedIcon = new HashMap<String, List<String>>();
        collectionColorList = new HashMap<String, Integer>();

        //Instantiate the Middleware for the RESTful API's
        api = new RESTMiddleware(this);

        //Get a UserData instance
        loadUserData();     // Fragment's onAttachFragment should run first, but this function
        // loads the UserData only if there is no copy retrieved yet
        userPrefs = new UserPrefs(getBaseContext());

        //DRAWER Left Menu
        //Multifeed Expandable List
        initMultifeedListOnDrawer();
        //Collection Expandable List
        initCollectionListOnDrawer();
        //AllMultifeed TextView
        initAllMultifeedTextClick();
        //Collectio TextView
        initCollectionsTextClick();
        // init the logout button
        initLogoutButton();

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
        textViewAccountEmail = findViewById(R.id.textViewAccountEmail);
        textViewAccountEmail.setText(userData.getUser().getEmail());

        //Init User's Name TextView
        textViewAccountName = findViewById(R.id.textViewAccountName);
        textViewAccountName.setText(userData.getUser().getName());

        //TOOLBAR
        Toolbar toolbar = findViewById(R.id.articles_list_toolbar);
        setSupportActionBar(toolbar);
        actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

        initToolbar();

        // TOP ARTICLES - SLIDER
        // Set the slider to third the size of the viewport
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        pager = findViewById(R.id.pagerArticles);
        pager.getLayoutParams().height = size.y / 3;
        pager.setClipToPadding(false);
        pager.setPadding(60, 0, 60, 0);
        pager.setPageMargin(0);
        // Disable autoslider when the slider is touched
        pager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                toggleAutoslider(false);
                return false;
            }
        });

        // Set the progress bar visible and hide other
        progressBar = findViewById(R.id.progressBarArticlesList);
        contentLinearLayout = findViewById(R.id.articleListLinearLayout);
        appBarLayout = findViewById(R.id.articleListAppBarLayout);
        updateButton = findViewById(R.id.buttonUpdate);
        progressBar.setVisibility(View.VISIBLE);
        updateButton.setVisibility(View.GONE);
        contentLinearLayout.setVisibility(View.INVISIBLE);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                articlesListFragment.updateArticles();
                updateButton.setVisibility(View.GONE);
            }
        });

        articlesAddedToRIL = new ArrayList<>();
        articlesRemovedFromColl = new ArrayList<>();

        // AUTOSLIDER
        autoSliderHandler = new Handler();
        autoSliderRunnable = new Runnable() {
            public void run() {
                if (topArticles != null && topArticles.size() != 0) {
                    // Log.d(ArticleActivity.logTag + ":" + TAG, "Autoslider page: " + currentSlide);
                    pager.setCurrentItem(currentSlide, true);
                    currentSlide++;
                    if (currentSlide >= topArticles.size())
                        currentSlide = 0;
                    autoSliderHandler.postDelayed(this, autoSliderTimeoutMillis);
                }
            }
        };

        showArticleListFragment();
    }


    /*+
     * Show the list on 1 or 2 columns based on the screen size
     */
    private void showArticleListFragment() {
        int screenLayout = context.getResources().getConfiguration().screenLayout;
        screenLayout &= Configuration.SCREENLAYOUT_SIZE_MASK;

        int numColumn;
        if (screenLayout == Configuration.SCREENLAYOUT_SIZE_LARGE // large
                || screenLayout == 4) { // x-large
            numColumn = 2;
        } else {
            numColumn = 1;
        }

        articlesListFragment = ArticlesListFragment.newInstance(numColumn);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.articleListFrameLayout, articlesListFragment);
        ft.commit();
    }

    /**
     * The logout button initializer
     */
    private void initLogoutButton() {
        Button logoutAccount = findViewById(R.id.buttonAccountLogout);
        logoutAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show a confirmation dialog
                new AlertDialog.Builder(ArticlesListActivity.this)
                        .setTitle(R.string.dialog_are_you_sure)
                        // The positive delete the user local account
                        .setPositiveButton(R.string.logout, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(ArticleActivity.logTag + ":" + TAG, "User logout confirmed...");
                                userData.deleteAll();
                                finish();
                                startLoginActivity();
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

    /**
     * Initialize the Activity's Toolbar(ActionBar)
     */
    private void initToolbar() {
        //Set the appropriate title
        //All Multifeeds Feeds Articles
        if (userData.getVisualizationMode() == UserData.MODE_ALL_MULTIFEEDS_FEEDS) {
            updatedActionBar((String) getText(R.string.multifeed_title), Color.BLACK);

        }
        //Multifeed Articles
        else if (userData.getVisualizationMode() == UserData.MODE_MULTIFEED_ARTICLES) {
            Multifeed multifeed = userData.getMultifeedList().get(userData.getMultifeedPosition());
            updatedActionBar(multifeed.getTitle(), multifeed.getColor());
        }
        //Feed Articles
        else if (userData.getVisualizationMode() == UserData.MODE_FEED_ARTICLES) {
            Multifeed multifeed = userData.getMultifeedList().get(userData.getMultifeedPosition());
            Feed feed = userData.getMultifeedMap().get(multifeed).get(userData.getFeedPosition());
            try {
                updatedActionBar(feed.getTitle(), feed.getCoverColor().isEmpty() ? multifeed.getColor() : Color.parseColor("#" + feed.getCoverColor()));
            } catch (Exception e) {
                Log.e(ArticleActivity.logTag + ":" + TAG, "Feed color error...");
                updatedActionBar(feed.getTitle(), multifeed.getColor());
            }
        }
        //Collection Articles
        else if (userData.getVisualizationMode() == UserData.MODE_COLLECTION_ARTICLES) {
            Collection collection = userData.getCollectionList().get(userData.getCollectionPosition());
            updatedActionBar(collection.getTitle(), collection.getColor());
        }
    }

    private void updatedActionBar(String title, int color) {
        actionbar.setTitle(title);
        actionbar.setBackgroundDrawable(new ColorDrawable(color));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(color);
        }
    }

    /**
     * Initialize AllMultifeedList TextView and OnClick listener -> Restart the Activity and show all
     * the Multifeeds Feeds Articles.
     */
    private void initAllMultifeedTextClick() {
        textViewMultifeedList = findViewById(R.id.textViewMultifeedList);
        textViewMultifeedList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Prepare the new ListToVisualize and Restart Activity
                userData.setVisualizationMode(UserData.MODE_ALL_MULTIFEEDS_FEEDS);

                contentLinearLayout.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                updateButton.setVisibility(View.GONE);

                // Show the pager
                pager.setVisibility(View.VISIBLE);

                //restartActivity();
                refreshFragmentData();
            }
        });
        textViewMultifeedList.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startMultifeedManagerActivity();
                return true;
            }
        });
    }

    private void initCollectionsTextClick() {
        textViewCollectionsList = findViewById(R.id.textViewCollectionsList);
        textViewCollectionsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        textViewCollectionsList.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startCollectionManagerActivity();
                return true;
            }
        });
    }

    /**
     * Initialize Multifeed Expandable List on the Left Drawer
     */
    private void initMultifeedListOnDrawer() {
        expListViewMultifeeds = (ExpandableListView) findViewById(R.id.exp_list_view_multifeeds);
        prepareMultifeedsListData();
        multifeedListAdapter = new ExpandableListAdapter(this, multifeedListHeaders, multifeedListChild, multifeedListFeedIcon, multifeedColorList);
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

                return true;
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

                contentLinearLayout.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                updateButton.setVisibility(View.GONE);

                // Show the pager
                pager.setVisibility(View.VISIBLE);

                refreshFragmentData();
                return true;
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

                contentLinearLayout.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                updateButton.setVisibility(View.GONE);

                // Show the pager
                pager.setVisibility(View.VISIBLE);

                refreshFragmentData();
                return true; // This way the expander cannot be collapsed
            }
        });
    }

    /**
     * Initialize Collection Expandable List on the Left Drawer
     */
    private void initCollectionListOnDrawer() {
        expListViewCollections = (ExpandableListView) findViewById(R.id.exp_list_view_collections);
        expListViewCollections.setGroupIndicator(null);
        expListViewCollections.setDividerHeight(0);

        prepareCollectionsListData();

        collectionListAdapter = new ExpandableListAdapter(this, collectionListHeaders, collectionListChild, collectionListFeedIcon, collectionColorList);
        expListViewCollections.setAdapter(collectionListAdapter);
        expListViewCollections.requestLayout();
        //Item LongClick
        expListViewCollections.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                long packedPosition = expListViewCollections.getExpandableListPosition(position);
                int itemType = ExpandableListView.getPackedPositionType(packedPosition);
                if (itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                    startCollectionManagerActivity();
                }
                return true;
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

                contentLinearLayout.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                updateButton.setVisibility(View.GONE);

                // Hide the pager
                pager.setVisibility(View.GONE);

                refreshFragmentData();
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
            articlesListFragment.updateArticles();
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
            //Add the associated feed icons list
            multifeedListFeedIcon.put(multifeed.getTitle(), userData.getMapFeedIconLinkListByKey(multifeed));
            //Add the associated color to each multifeed
            multifeedColorList.put(multifeed.getTitle(), multifeed.getColor());
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
            //Add the associated color to each multifeed
            collectionColorList.put(collection.getTitle(), collection.getColor());
        }
    }

    private void toggleAutoslider(boolean start) {
        if (start) {
            if (!autoSliderOn) {
                Log.d(ArticleActivity.logTag + ":" + TAG, "Autoslider started");
                autoSliderHandler.postDelayed(autoSliderRunnable, autoSliderTimeoutMillis);
                autoSliderOn = true;
            }
        } else {
            if (autoSliderOn) {
                Log.d(ArticleActivity.logTag + ":" + TAG, "Autoslider stopped");
                autoSliderHandler.removeCallbacks(autoSliderRunnable);
                autoSliderOn = false;
            }
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
                return true;
            case R.id.itemOrderByArticleList:
                showOrderByDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showOrderByDialog() {
        CharSequence[] dialogValues = { getString(R.string.date), getString(R.string.rating) };
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.order_by);

        int checked = -1;
        if(userData.getArticleOrder() == UserData.ORDER_BY_DATE) checked = 0;
        else if(userData.getArticleOrder() == UserData.ORDER_BY_RATING) checked = 1;

        dialog.setSingleChoiceItems(dialogValues, checked, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                contentLinearLayout.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);

                switch (item) {
                    case 0:
                        userData.setArticleOrder(UserData.ORDER_BY_DATE);
                        break;
                    case 1:
                        userData.setArticleOrder(UserData.ORDER_BY_RATING);
                        break;
                }

                articlesListFragment.updateArticles();
                dialog.dismiss();
            }
        });
        AlertDialog alert = dialog.create();
        alert.show();
    }

    /**
     * Handles the interactions with the list, click
     *
     * @param article the clicked article
     */
    @Override
    public void onListFragmentInteractionClick(Article article) {
        Log.d(ArticleActivity.logTag + ":" + TAG, article.toString());
        startArticleActivity(article);
    }

    /**
     * Handles the interactions with the list, swipe.
     * If the visualization is Collection then on swipe remove the article from the collection
     * if the visualization is Multifeed then on swipe add the article to the Read It Later collection
     *
     * @param article the swiped article
     */
    @Override
    public void onListFragmentInteractionSwipe(final Article article) {
        if (userData.getVisualizationMode() == UserData.MODE_COLLECTION_ARTICLES) {
            // Remove swiped article from collection

            // Check if not already removed to avoid multiple deletion
            if (!articlesRemovedFromColl.contains(article.getHashId())) {
                Log.d(ArticleActivity.logTag + ":" + TAG, "Removing article " + article.getTitle() + " from saved articles");

                // Find the id of the collection where the article is saved
                List<SavedArticle> userSavedArticles = userData.getSavedArticleList();
                int coll = 0;
                for (SavedArticle savedArticle : userSavedArticles) {
                    long articleHash = savedArticle.getArticle();
                    if (articleHash == article.getHashId()) {
                        coll = savedArticle.getCollection();
                        break;
                    }
                }
                if (coll == 0) {
                    Snackbar.make(findViewById(android.R.id.content), R.string.error_collection_not_found, Snackbar.LENGTH_LONG).show();
                    return;
                }

                final int finalColl = coll;
                api.deleteUserSavedArticle(article.getHashId(), coll, new SQLOperationCallback() {
                    @Override
                    public void onLoad(SQLOperation sqlOperation) {
                        Log.d(ArticleActivity.logTag + ":" + TAG, "Article " + article.getTitle() + " from collection " + finalColl + " removed");
                        Snackbar.make(findViewById(android.R.id.content), R.string.article_removed_from_collection, Snackbar.LENGTH_LONG).show();
                        articlesRemovedFromColl.add(article.getHashId());

                        // refresh the user's collections
                        loadUserCollections(false);
                    }

                    @Override
                    public void onFailure() {
                        Log.d(ArticleActivity.logTag + ":" + TAG, "Article " + article.getTitle() + " from collection " + finalColl + " NOT removed");
                        Snackbar.make(findViewById(android.R.id.content), R.string.error_connection, Snackbar.LENGTH_LONG).show();

                    }
                });
            }


        } else {
            // Add swiped article to Read It Later collection

            // If the read it later collection exists and the article was not already added to id
            if (!articlesAddedToRIL.contains(article.getHashId())) {
                Log.d(ArticleActivity.logTag + ":" + TAG, "Saving article " + article.getTitle() + " into Read It Later");

                // Find the id of the Read It Later collection
                List<Collection> userCollections = userData.getCollectionList();
                Collection readItLaterCollection = null;
                for (Collection collection : userCollections) {
                    if (collection.getTitle().equals(getText(R.string.read_it_later))) {
                        readItLaterCollection = collection;
                        break;
                    }
                }
                if (readItLaterCollection == null) {
                    Snackbar.make(findViewById(android.R.id.content), R.string.error_collection_not_found, Snackbar.LENGTH_LONG).show();
                    return;
                }


                Log.d(ArticleActivity.logTag + ":" + TAG, "Article " +
                        article.getTitle() + " " +
                        article.getComment() + " " +
                        article.getLink() + " " +
                        article.getImgLink() + " " +
                        article.getPubDateString("yyyy-MM-dd hh:mm:ss") + " " +
                        userData.getUser().getId() + " " +
                        article.getFeedId() + " " +
                        readItLaterCollection.getId() + " to collection " + readItLaterCollection.getTitle());

                api.addUserArticleAssociatedToCollection(
                        article.getTitle(),
                        article.getDescription(),
                        article.getComment(),
                        article.getLink(),
                        article.getImgLink(),
                        article.getPubDateString("yyyy-MM-dd hh:mm:ss"),
                        userData.getUser().getId(),
                        article.getFeedId(),
                        readItLaterCollection.getId(),
                        new SQLOperationCallback() {
                            @Override
                            public void onLoad(SQLOperation sqlOperation) {
                                Log.d(ArticleActivity.logTag + ":" + TAG, "Article " + article.getTitle() + " to collection Read It Later saved");
                                Snackbar.make(findViewById(android.R.id.content), R.string.article_added_to_collection, Snackbar.LENGTH_LONG).show();
                                articlesAddedToRIL.add(article.getHashId());

                                // refresh the user's collections
                                loadUserCollections(false);
                            }

                            @Override
                            public void onFailure() {
                                Log.e(ArticleActivity.logTag + ":" + TAG, "Article " + article.getTitle() + " to collection Read It Later NOT saved");
                                Snackbar.make(findViewById(android.R.id.content), R.string.error_adding_article, Snackbar.LENGTH_LONG).show();
                            }
                        }
                );
            }
        }
    }

    @Override
    public void onListFragmentLocalArticlesReady(final List<Article> articleList) {
        Log.d(ArticleActivity.logTag + ":" + TAG, "Local articles are ready to be displayed...");

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (progressBar == null || contentLinearLayout == null) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // show the list
                        Log.d(ArticleActivity.logTag + ":" + TAG, "Showing articles list");
                        progressBar.setVisibility(View.GONE);
                        contentLinearLayout.setVisibility(View.VISIBLE);

                        if(articleList != null){
                            // start the slider
                            currentSlide = 1;
                            toggleAutoslider(true);
                            updateArticlesSlidePager(articleList);
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    public void onListFragmentArticlesReady() {
        Log.d(ArticleActivity.logTag + ":" + TAG, "New articles are ready to be displayed, show the update button...");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // show the list
                Log.d(ArticleActivity.logTag + ":" + TAG, "Showing update button");
                updateButton.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onListFragmentAllArticlesReady(Map<String, Integer> feedArticlesNumberMap) {
        //Update the Number of Articles per Feed in the ExpandableList in the Drawer and notify the change
        multifeedListAdapter.updateFeedArticlesNumbers(feedArticlesNumberMap);
        this.feedArticlesNumberMap = feedArticlesNumberMap;
        runOnUiThread(new Runnable() {
            public void run() {
                multifeedListAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Update the ArticlesSlidePager with a new List of Articles to visualize as TopArticles
     *
     * @param articleList List of Articles from which to choose from
     */
    private void updateArticlesSlidePager(List<Article> articleList) {
        //Add 10 random Articles to the TopList Slider
        //TODO: add the most important instead of a random list
        if (articleList.size() >= ArticleSlidePagerAdapter.NUM_ARTICLES) {
            int i = 0, j;
            topArticles = new ArrayList<>();
            Random rand = new Random();
            int maxIndex = articleList.size() - 1;

            for (i = 0; i < ArticleSlidePagerAdapter.NUM_ARTICLES; i++) {
                int attempts = 0;
                do {
                    j = rand.nextInt(maxIndex);
                }
                while (!Article.checkUrlIsValid(articleList.get(j).getImgLink()) && attempts++ < 20);
                topArticles.add(articleList.get(j));
            }
        } else
            topArticles = articleList;
        pager = findViewById(R.id.pagerArticles);
        pagerAdapter = new ArticleSlidePagerAdapter(getSupportFragmentManager(), topArticles);
        pager.setAdapter(pagerAdapter);
        pager.setCurrentItem(1, true);
    }

    /**
     * Handles the interactions with the top slider
     *
     * @param article the article clicked on the slider
     */
    @Override
    public void onFragmentInteraction(Article article) {
        Log.d(ArticleActivity.logTag + ":" + TAG, article.toString());
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

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * Restart this Activity and also pass some data within the intent, so that it doesn't get lost
     */
    private void restartActivity() {
        Intent intent = new Intent(ArticlesListActivity.this, ArticlesListActivity.class);
        //Sent the Map of the NumberOfArticles per Feed, so that it doesn't get lost
        intent.putExtra("feedArticlesNumberMap", (Serializable) feedArticlesNumberMap);
        intent.putExtra("topArticles", (Serializable) topArticles);
        finish();
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable("feedArticlesNumberMap", (Serializable) feedArticlesNumberMap);
        savedInstanceState.putSerializable("topArticles", (Serializable) topArticles);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Refresh ArticlesListFragment's Data without the need for a activity reboot
     */
    private void refreshFragmentData() {
        drawerLayout.closeDrawers();
        //articlesListFragment.refreshRecyclerViewData();
        initToolbar();

        //Update the fragment with a bit of delay, so that the drawer doesn't lag on closing
        /*Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (drawerLayout.isDrawerOpen(GravityCompat.START)){
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                //Refresh the RecyclerView List
                articlesListFragment.refreshRecyclerViewData();
            }
        });
        thread.start();*/

        // Expand the toolbar
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                appBarLayout.setExpanded(true);
            }
        });

        //TODO: unfortunately the above solution for drawer lag is causing Concurrency problems, find another solution!
        //Refresh the RecyclerView List
        articlesListFragment.updateArticles();

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        //Get Bundle saved data
        feedArticlesNumberMap = (HashMap<String, Integer>) savedInstanceState.getSerializable("feedArticlesNumberMap");
        topArticles = (List<Article>) savedInstanceState.getSerializable("topArticles");
        multifeedListAdapter.updateFeedArticlesNumbers(feedArticlesNumberMap);
        multifeedListAdapter.notifyDataSetChanged();
        pager = findViewById(R.id.pagerArticles);
        pagerAdapter = new ArticleSlidePagerAdapter(getSupportFragmentManager(), topArticles);
        pager.setAdapter(pagerAdapter);
    }

    /**
     * When returning from an activity that performs changes on the data we need to refresh
     * the drawer with the new data.
     * REQUEST_CODE_MULTIFEED_EDIT is used for changes to Multifeed
     * REQUEST_CODE_COLLECTION_EDIT is used for changes to Collections
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
            // A multifeed was edited or created so refresh the local data
            Log.d(ArticleActivity.logTag + ":" + TAG, "Refreshing User's Multifeeds... ");

            Snackbar.make(findViewById(android.R.id.content), R.string.updating_user_information, Snackbar.LENGTH_LONG).show();

            loadUserMultifeeds(true);

            // if a new feed was added reset the last update value
            userPrefs.storeLastUpdate(new Date(0L));
        } else if (requestCode == REQUEST_CODE_COLLECTION_EDIT && resultCode == RESULT_OK) {
            // A collection was edited or created so refresh the local data
            Log.d(ArticleActivity.logTag + ":" + TAG, "Refreshing User's Collections... ");

            Snackbar.make(findViewById(android.R.id.content), R.string.updating_user_information, Snackbar.LENGTH_LONG).show();

            loadUserCollections(true);
        }
    }

    /**
     * Refresh the user's multifeeds saved locally
     *
     * @param showSnackBar boolean to show the SnackBar or not
     */
    private void loadUserMultifeeds(final Boolean showSnackBar) {
        // Refresh the user's multifeed saved locally
        new LoadUserMultifeeds(new AsyncResponse() {
            @Override
            public void processFinish(Integer output) {
                Log.d(ArticleActivity.logTag + ":" + TAG, "User's Multifeeds refreshed... ");

                userData.loadPersistedData(context);
                userData.processUserData();

                // refresh the multifeed list
                multifeedListHeaders.clear();
                multifeedListChild.clear();
                multifeedListFeedIcon.clear();
                multifeedColorList.clear();

                prepareMultifeedsListData();
                multifeedListAdapter.notifyDataSetChanged();

                //Expand all the groups
                for (int i = 0; i < multifeedListAdapter.getGroupCount(); i++) {
                    expListViewMultifeeds.expandGroup(i);
                }

                if (showSnackBar)
                    Snackbar.make(findViewById(android.R.id.content), R.string.user_information_updated, Snackbar.LENGTH_LONG).show();

                // All multifeed mode
                textViewMultifeedList.performClick();

            }
        }, this, userData.getUser()).execute();
    }

    /**
     * Refresh the user's collection saved locally
     *
     * @param showSnackBar boolean to show the SnackBar or not
     */
    private void loadUserCollections(final Boolean showSnackBar) {
        new LoadUserCollections(new AsyncResponse() {
            @Override
            public void processFinish(Integer output) {
                Log.d(ArticleActivity.logTag + ":" + TAG, "User's Collections refreshed... ");

                userData.loadPersistedData(context);
                userData.processUserData();

                collectionListHeaders.clear();
                prepareCollectionsListData();
                collectionListAdapter.notifyDataSetChanged();

                if (showSnackBar)
                    Snackbar.make(findViewById(android.R.id.content), R.string.user_information_updated, Snackbar.LENGTH_LONG).show();
            }
        }, this, userData.getUser()).execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        toggleAutoslider(false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Get intent passed data
        Intent intent = getIntent();
        if (intent.hasExtra("feedArticlesNumberMap")) {
            feedArticlesNumberMap = (HashMap<String, Integer>) intent.getSerializableExtra("feedArticlesNumberMap");
            multifeedListAdapter.updateFeedArticlesNumbers(feedArticlesNumberMap);
            multifeedListAdapter.notifyDataSetChanged();
        }
        if (intent.hasExtra("topArticles")) {
            topArticles = (List<Article>) intent.getSerializableExtra("topArticles");
            pager = findViewById(R.id.pagerArticles);
            pagerAdapter = new ArticleSlidePagerAdapter(getSupportFragmentManager(), topArticles);
            pager.setAdapter(pagerAdapter);
        }
    }
}
