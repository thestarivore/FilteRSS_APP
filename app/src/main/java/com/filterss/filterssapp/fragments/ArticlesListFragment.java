package com.filterss.filterssapp.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.filterss.filterssapp.ArticleActivity;
import com.filterss.filterssapp.controllers.ArticleListSwipeController;
import com.filterss.filterssapp.R;
import com.filterss.filterssapp.adapters.ArticleRecyclerViewAdapter;
import com.filterss.filterssapp.models.Article;
import com.filterss.filterssapp.models.ArticlesScores;
import com.filterss.filterssapp.models.Collection;
import com.filterss.filterssapp.models.Feed;
import com.filterss.filterssapp.models.Multifeed;
import com.filterss.filterssapp.models.RSSFeed;
import com.filterss.filterssapp.models.SQLOperation;
import com.filterss.filterssapp.models.UserData;
import com.filterss.filterssapp.persistence.UserPrefs;
import com.filterss.filterssapp.restful_api.RESTMiddleware;
import com.filterss.filterssapp.restful_api.callbacks.ArticleCallback;
import com.filterss.filterssapp.restful_api.callbacks.ArticlesScoresCallback;
import com.filterss.filterssapp.restful_api.callbacks.SQLOperationCallback;
import com.filterss.filterssapp.restful_api.interfaces.AsyncRSSFeedResponse;
import com.filterss.filterssapp.rss_parser.LoadRSSFeed;
import com.filterss.filterssapp.service.SQLiteService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ArticlesListFragment extends Fragment implements ArticleListSwipeController.RecyclerItemTouchHelperListener {
    private final String TAG = getClass().getName();
    private RecyclerView recyclerView = null;
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    List<Article> articles;// = Article.generateMockupArticles(25);
    private UserData userData;
    private RESTMiddleware api;
    private int feedCounter;
    private int scoreCounter;
    private Context context;
    private ArticleRecyclerViewAdapter adapter;
    private boolean allFeedsLoaded = true;
    private boolean sqLiteArticlesLoaded = true;

    private Thread waitSQLiteLoadedCopy = null;
    private Thread waitAllFeedsLoadedCopy = null;
    private List<LoadRSSFeed> loadRSSFeedList = new ArrayList<>();
    private Map<String, Integer> feedArticlesNumberMap = new HashMap<>();
    private boolean onlineRunning;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArticlesListFragment() {
    }

    public static ArticlesListFragment newInstance(int columnCount) {
        ArticlesListFragment fragment = new ArticlesListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = new RESTMiddleware(getContext());

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view;

        /*if (this.articles==null || this.articles.isEmpty()) {
            // No articles, suggest the user to add a feed
            view = inflater.inflate(R.layout.fragment_article_list_empty, container, false);

            mListener.onListFragmentArticlesReady();

        } else {*/
        view = inflater.inflate(R.layout.fragment_article_list, container, false);

        // Create the RecyclerView and Set it's adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            this.context = context;
            recyclerView = (RecyclerView) view;

            // Set the swipe controller
            ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ArticleListSwipeController(0, ItemTouchHelper.RIGHT, this);
            new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            adapter = new ArticleRecyclerViewAdapter(articles, mListener, context);
            recyclerView.setAdapter(adapter);
        }
        //}

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        mListener.onListFragmentInteractionSwipe(articles.get(position));
    }

    /**
     * Callback launched (on Fragment Attach) from the activity to inform the fragment that the UserData has been loaded
     */
    public synchronized void updateArticles() {
        final List<Feed> feedList = new ArrayList<>();
        final List<Article> collectionArticleList = new ArrayList<>();
        final List<Article> downloadedArticleList = new ArrayList<>();
        final SQLiteService sqLiteService = SQLiteService.getInstance(getContext());
        final long startTimeDownloadArticles = System.nanoTime();

        //Get the Transferred UserData
        this.userData = UserData.getInstance();
        final UserPrefs prefs = new UserPrefs(getContext());

        //Prepare the articles list, and before downloading the real list of articles, retrieve the list
        //of articles stored in the local SQLite Database;
        if (articles == null) {
            this.articles = new ArrayList<>();
        }

        Collection collection = null;
        //Get the list of feeds to show in the RecyclerView
        //All Multifeeds Feeds Articles
        if (userData.getVisualizationMode() == UserData.MODE_ALL_MULTIFEEDS_FEEDS) {
            feedList.addAll(userData.getFeedList());
        }
        //Multifeed Articles
        else if (userData.getVisualizationMode() == UserData.MODE_MULTIFEED_ARTICLES) {
            Multifeed multifeed = userData.getMultifeedList().get(userData.getMultifeedPosition());
            feedList.addAll(userData.getMultifeedMap().get(multifeed));
        }
        //Feed Articles
        else if (userData.getVisualizationMode() == UserData.MODE_FEED_ARTICLES) {
            Multifeed multifeed = userData.getMultifeedList().get(userData.getMultifeedPosition());
            feedList.add(userData.getMultifeedMap().get(multifeed).get(userData.getFeedPosition()));
        }
        //Collection Articles
        else if (userData.getVisualizationMode() == UserData.MODE_COLLECTION_ARTICLES) {
            collection = userData.getCollectionList().get(userData.getCollectionPosition());
            collectionArticleList.addAll(userData.getCollectionMap().get(collection));
        }

        //MODE_ALL_MULTIFEEDS_FEEDS || MODE_MULTIFEED_ARTICLES || MODE_FEED_ARTICLES
        if (userData.getVisualizationMode() != UserData.MODE_COLLECTION_ARTICLES) {
            /*
             * LOCAL (SQLite)
             */
            // Wait until all the articles stored locally in the SQLite database are retrieved
            sqLiteArticlesLoaded = false;
            final Thread waitSQLiteLoaded = new Thread(new Runnable() {
                @Override
                public void run() {

                    //When the local ArticleList has finished loading, get the new articles in the RecyclerView adapter's list
                    articles.clear();
                    Log.d(ArticleActivity.logTag + ":" + TAG, "Getting articles from DB...");
                    if (userData.getVisualizationMode() == UserData.MODE_ALL_MULTIFEEDS_FEEDS) {
                        //List<Article> localArticleList = userData.getLocalArticleList();


                        // get all the articles directly from the database
                        sqLiteService.getFilteredArticles(feedList, userData.getArticleOrder(), new ArticleCallback() {
                            @Override
                            public void onLoad(List<Article> localArticles) {
                                Log.d(ArticleActivity.logTag + ":" + TAG, "Local article list: " + localArticles.size());
                                articles = localArticles;
                                sqLiteArticlesLoaded = true;
                            }

                            @Override
                            public void onFailure() {
                                Log.e(ArticleActivity.logTag + ":" + TAG, "Failed loading local article list");
                            }
                        });

                    } else {
                        //List<Article> localArticleListFiltered = userData.getLocalArticleListFiltered(feedList);

                        // get filtered articles directly from the database
                        sqLiteService.getFilteredArticles(feedList, userData.getArticleOrder(), new ArticleCallback() {
                            @Override
                            public void onLoad(List<Article> localArticles) {
                                Log.d(ArticleActivity.logTag + ":" + TAG, "Filtered local article list: " + localArticles.size());
                                articles = localArticles;
                                sqLiteArticlesLoaded = true;
                            }

                            @Override
                            public void onFailure() {
                                Log.e(ArticleActivity.logTag + ":" + TAG, "Failed loading filtered local article list");
                            }
                        });
                    }

                    while (!sqLiteArticlesLoaded) {
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    if (articles.size() > 0) {
                        // Local DB not empty
                        //Associate the FeedObjects to each Article (since these are not stored in the local SQLite Database
                        for (final Article article : articles) {
                            for (Feed feed : userData.getFeedList()) {
                                if (feed.getId() == article.getFeed()) {
                                    article.setFeedObj(feed);
                                }
                            }
                            // set the article read information
                            article.setRead(sqLiteService.getArticleRead(article.getHashId()));
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (recyclerView != null) {
                                    recyclerView.setAdapter(new ArticleRecyclerViewAdapter(articles, mListener, getContext()));
                                    recyclerView.getAdapter().notifyDataSetChanged();
                                }
                            }
                        });

                        mListener.onListFragmentLocalArticlesReady(articles);
                    } else {
                        Log.d(ArticleActivity.logTag + ":" + TAG, "Local DB is empty");
                    }
                }
            });
            waitSQLiteLoaded.start();
            waitSQLiteLoadedCopy = waitSQLiteLoaded;

            /*
             * ONLINE
             */
            if (isNetworkAvailable() && !onlineRunning) {
                // Wait until all LoadRSSFeeds has finished then call onListFragmentArticlesReady
                // to notify that we have something to show
                Date now = new Date();
                Date lastUpdate = prefs.retrieveLastUpdate();
                long diffMin = (now.getTime() - lastUpdate.getTime()) / (60 * 1000);

                Log.d(ArticleActivity.logTag + ":" + TAG, "Last update: " + lastUpdate + ". Diff: " + diffMin + " mins ago");

                if (diffMin >= 10) {
                    Log.d(ArticleActivity.logTag + ":" + TAG, "Starting online fetch...");

                    feedCounter = 0;
                    scoreCounter = 0;
                    onlineRunning = true;
                    allFeedsLoaded = false;

                    // perform the download from all the feeds
                    final List<Feed> allFeedList = userData.getFeedList();

                    loadRSSFeedList.clear();
                    for (final Feed feed : allFeedList) {
                        loadRSSFeedList.add(
                                new LoadRSSFeed(new AsyncRSSFeedResponse() {
                                    @Override
                                    public void processFinish(Object output, RSSFeed rssFeed) {
                                        final HashMap<Long, Article> articlesHashesMap = new HashMap<>();

                                        for (final Article article : rssFeed.getItemList()) {
                                            article.setFeedId(feed.getId());
                                            article.setFeedObj(feed);

                                            articlesHashesMap.put(article.getHashId(), article);

                                            //articles.add(article);
                                            downloadedArticleList.add(article);
                                        }

                                        // Get the scores for the set of articles
                                        if (!articlesHashesMap.keySet().isEmpty()) {
                                            getArticlesScores(articlesHashesMap);
                                        } else {
                                            scoreCounter++;
                                        }

                                        //Save the number of articles for each feed, mapped in a HashMap
                                        feedArticlesNumberMap.put(feed.getTitle(), rssFeed.getItemCount());

                                        //Wait for onCreateView to set RecyclerView's Adapter
                                        while (recyclerView == null || recyclerView.getAdapter() == null) {
                                            try {
                                                Thread.sleep(50);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        Log.d(ArticleActivity.logTag + ":" + TAG, "LoadRSSFeed finished: " + (feedCounter + 1) + "/" + allFeedList.size() + ", founded: " + rssFeed.getItemCount() + " articles");
                                        feedCounter++;

                                    }
                                }, getContext(), feed));
                    }

                    //Execute all AsyncTasks
                    for (int i = 0; i < loadRSSFeedList.size(); i++) {
                        loadRSSFeedList.get(i).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }

                    final Thread waitAllFeedsLoaded = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (feedCounter < allFeedList.size() || scoreCounter < allFeedList.size() || !sqLiteArticlesLoaded) {
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                            //Time the operation
                            long endTimeLUD = System.nanoTime();
                            long duration = (endTimeLUD - startTimeDownloadArticles);  //divide by 1000000 to get milliseconds.
                            Log.d(ArticleActivity.logTag + ":" + TAG, "#TIME: Downloaded ALL the Feeds Articles: " + (duration / 1000000) + "ms");

                            computeArticlesScore(downloadedArticleList);

                            // save downloaded articles in the db
                            manageArticlesLocalDBPersistence(sqLiteService, downloadedArticleList, new SQLOperationCallback() {
                                @Override
                                public void onLoad(SQLOperation sqlOperation) {
                                    Log.d(ArticleActivity.logTag + ":" + TAG,
                                            "Added " + sqlOperation.getAffectedRows() + " articles into the local SQLite DB, Article Table!");

                                    // save last update value
                                    Log.d(ArticleActivity.logTag + ":" + TAG, "Saving last update: " + new Date());
                                    prefs.storeLastUpdate(new Date());

                                    allFeedsLoaded = true;
                                    onlineRunning = false;

                                    if (articles.size() == 0) {
                                        // local DB empty => reload
                                        Log.d(ArticleActivity.logTag + ":" + TAG, "Local DB empty, reloading...");
                                        updateArticles();
                                    } else {
                                        mListener.onListFragmentArticlesReady();
                                        mListener.onListFragmentAllArticlesReady(feedArticlesNumberMap);
                                    }

                                }

                                @Override
                                public void onFailure() {
                                    Log.d(ArticleActivity.logTag + ":" + TAG, "Failed saving to local DB!");
                                }
                            });


                        }
                    });
                    waitAllFeedsLoaded.start();
                    waitAllFeedsLoadedCopy = waitAllFeedsLoaded;

                    // call on list fragmentArticlesReady always after a delay of 60 seconds
                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            waitAllFeedsLoaded.interrupt();
                            allFeedsLoaded = true;
                            if (feedCounter < feedList.size()) {
                                if (mListener != null) {
                                    Log.d(ArticleActivity.logTag + ":" + TAG, "TIMEOUT LoadRSSFeed, loading not completed");
                                    mListener.onListFragmentArticlesReady();
                                    onlineRunning = false;
                                }
                            }
                        }
                    }, 60000);

                }
            } else {
                if (onlineRunning)
                    Log.d(ArticleActivity.logTag + ":" + TAG, "An online fetch is already running");
                if (!isNetworkAvailable())
                    Log.d(ArticleActivity.logTag + ":" + TAG, "Network not available");
            }
        }
        //MODE_COLLECTION_ARTICLES
        else {
            articles.clear();
            articles.addAll(collectionArticleList);

            //Notify a change in the RecyclerView's Article List
            ArticleRecyclerViewAdapter articleRecyclerViewAdapter = (ArticleRecyclerViewAdapter) recyclerView.getAdapter();
            articleRecyclerViewAdapter.setCollection(collection);

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (recyclerView != null) {
                        recyclerView.getAdapter().notifyDataSetChanged();
                    }
                }
            });

            mListener.onListFragmentLocalArticlesReady(null);
        }
    }

    private synchronized void computeArticlesScore(List<Article> articles) {
        Log.d(ArticleActivity.logTag + ":" + TAG, "# Articles: " + articles.size());

        Log.d(ArticleActivity.logTag + ":" + TAG, "Sorting articles...");

        for (int i = 0; i < articles.size(); i++) {
            Article article = articles.get(i);
            float articleScoreByRating = article.getScore() * article.getFeedObj().getMultifeed().getRating();
            article.setScore(articleScoreByRating);
        }

        Log.d(ArticleActivity.logTag + ":" + TAG, "Sorting DONE");
    }

    /**
     * Gets the scores for a list of articles hashes
     *
     * @param articlesHashes
     */
    private void getArticlesScores(final HashMap<Long, Article> articlesHashes) {
        api.getArticlesScores(articlesHashes.keySet(), new ArticlesScoresCallback() {

            @Override
            public void onLoad(List<ArticlesScores> articlesScores) {
                scoreCounter++;

                for (int i = 0; i < articlesScores.size(); i++) {
                    Article article = articlesHashes.get(articlesScores.get(i).getArticle());
                    Log.d(ArticleActivity.logTag + ":" + TAG, "Received score for article: " + articlesScores.get(i) + " : Multifeed rating: " + article.getFeedObj().getMultifeed().getRating());
                    article.setScore(articlesScores.get(i).getScore());
                }
            }

            @Override
            public void onFailure() {
                scoreCounter++;

                Log.e(ArticleActivity.logTag + ":" + TAG, "Scores for articles " + articlesHashes.keySet() + "NOT received");
                //article.setScore(0);
            }
        });
    }

    /**
     * Query if there is Internet Connection or the device is Offline
     *
     * @return True if there is Internet connection, false if Offline
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    // save everything to the database
    void manageArticlesLocalDBPersistence(final SQLiteService sqLiteService, final List<Article> downloadedArticleList, final SQLOperationCallback callback) {
        sqLiteService.putArticles(downloadedArticleList, callback);
    }

    /**
     * Returns TRUE if everything has loaded (Downloaded Articles + Loaded Local SQLite Articles)
     */
    public boolean isEverythingLoaded() {
        return allFeedsLoaded && sqLiteArticlesLoaded;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteractionClick(Article article);

        void onListFragmentInteractionSwipe(Article article);

        // Notify the ArticleListActivity that articles from the local storage have been loaded
        void onListFragmentLocalArticlesReady(final List<Article> articleList);

        // Notify the ArticleListActivity that new articles from the online feeds have been loaded
        void onListFragmentArticlesReady();

        void onListFragmentAllArticlesReady(Map<String, Integer> feedArticlesNumberMap);
    }

}
