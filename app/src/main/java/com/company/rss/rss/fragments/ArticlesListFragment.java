package com.company.rss.rss.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.view.ViewTreeObserver;

import com.company.rss.rss.ArticleActivity;
import com.company.rss.rss.controllers.ArticleListSwipeController;
import com.company.rss.rss.R;
import com.company.rss.rss.adapters.ArticleRecyclerViewAdapter;
import com.company.rss.rss.models.Article;
import com.company.rss.rss.models.ArticlesScores;
import com.company.rss.rss.models.Collection;
import com.company.rss.rss.models.Feed;
import com.company.rss.rss.models.Multifeed;
import com.company.rss.rss.models.RSSFeed;
import com.company.rss.rss.models.SQLOperation;
import com.company.rss.rss.models.UserData;
import com.company.rss.rss.restful_api.RESTMiddleware;
import com.company.rss.rss.restful_api.callbacks.ArticleCallback;
import com.company.rss.rss.restful_api.callbacks.ArticlesScoresCallback;
import com.company.rss.rss.restful_api.callbacks.SQLOperationCallback;
import com.company.rss.rss.restful_api.interfaces.AsyncRSSFeedResponse;
import com.company.rss.rss.rss_parser.LoadRSSFeed;
import com.company.rss.rss.service.SQLiteService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private boolean sqlDone;
    private boolean allFeedsLoaded = true;
    private boolean sqLiteArticlesLoaded = true;

    private Thread waitSQLiteLoadedCopy = null;
    private Thread waitAllFeedsLoadedCopy = null;
    private List<LoadRSSFeed> loadRSSFeedList = new ArrayList<>();

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

    public synchronized void refreshRecyclerViewData() {
        //If are still things runnig, stop everything
        if (isEverythingLoaded() == false) {
            //Interrupt all AsyncTasks
            for (int i=0;i<loadRSSFeedList.size();i++){
                loadRSSFeedList.get(i).cancel(true);
            }

            waitSQLiteLoadedCopy.interrupt();
            waitAllFeedsLoadedCopy.interrupt();
        }

        this.articles.clear();
        onUserDataLoaded();

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (recyclerView != null) {
                    recyclerView.scrollToPosition(0);
                }
            }
        });
    }


    /**
     * Callback launched (on Fragment Attach) from the activity to inform the fragment that the UserData has been loaded
     */
    public synchronized void onUserDataLoaded() {
        final List<Feed> feedList = new ArrayList<>();
        final List<Article> collectionArticleList = new ArrayList<>();
        final List<Article> downloadedArticleList = new ArrayList<>();
        final Map<String, Integer> feedArticlesNumberMap = new HashMap<>();
        final int numberOfFeeds;
        final SQLiteService sqLiteService = SQLiteService.getInstance(getContext());
        final long startTimeDownloadArticles = System.nanoTime();
        //final boolean[] articlesSQLiteLoaded = {false};

        //Get the Transferred UserData
        this.userData = UserData.getInstance();

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

        //Total number of feeds
        numberOfFeeds = feedList.size();

        //MODE_ALL_MULTIFEEDS_FEEDS || MODE_MULTIFEED_ARTICLES || MODE_FEED_ARTICLES
        if (userData.getVisualizationMode() != UserData.MODE_COLLECTION_ARTICLES) {
            feedCounter = 0;
            scoreCounter = 0;

            //Start Downloading the Articles, even if the UI hasn't loaded yet
            if (isNetworkAvailable()) {
                loadRSSFeedList.clear();
                for (final Feed feed : feedList) {
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
                            while (recyclerView == null || recyclerView.getAdapter() == null){
                                try {
                                    Thread.sleep(50);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                            Log.d(ArticleActivity.logTag + ":" + TAG, "LoadRSSFeed finished: " + (feedCounter + 1) + "/" + feedList.size() + ", founded: " + rssFeed.getItemCount() + " articles");
                            feedCounter++;

                        }
                    }, getContext(), feed));
                }

                //Execute all AsyncTasks
                for (int i=0;i<loadRSSFeedList.size();i++){
                    loadRSSFeedList.get(i).execute();
                }
            }

            sqlDone = false;
            // Wait until all the articles stored locally in the SQLite database are retrieved
            sqLiteArticlesLoaded = false;
            final Thread waitSQLiteLoaded = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!userData.isLocalArticleListLoaded()) {
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    //When the local ArticleList has finished loading, get the new articles in the RecyclerView adapter's list
                    articles.clear();
                    if (userData.getVisualizationMode() == UserData.MODE_ALL_MULTIFEEDS_FEEDS) {
                        List<Article> localArticleList = userData.getLocalArticleList();
                        Log.d(ArticleActivity.logTag + ":" + TAG, "Local article list: " + localArticleList.size());
                        articles.addAll(localArticleList);
                    } else {
                        List<Article> localArticleListFiltered = userData.getLocalArticleListFiltered(feedList);
                        Log.d(ArticleActivity.logTag + ":" + TAG, "Local article list filtered: " + localArticleListFiltered.size());
                        articles.addAll(userData.getLocalArticleListFiltered(feedList));
                    }

                    sqlDone = true;

                    //Associate the FeedObjects to each Article (since these are not stored in the local SQLite Database
                    for (Article article : articles) {
                        for (Feed feed : userData.getFeedList()) {
                            if (feed.getId() == article.getFeed()) {
                                article.setFeedObj(feed);
                            }
                        }
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
                    mListener.onListFragmentAllArticlesReady(feedArticlesNumberMap, articles);
                    mListener.onListFragmentArticlesReady();
                    sqLiteArticlesLoaded = true;
                }
            });
            waitSQLiteLoaded.start();
            waitSQLiteLoadedCopy = waitSQLiteLoaded;

            // Wait until all LoadRSSFeeds has finished then call onListFragmentArticlesReady
            // to notify that we have something to show
            allFeedsLoaded = false;
            final Thread waitAllFeedsLoaded = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (feedCounter < numberOfFeeds || scoreCounter < numberOfFeeds || !sqlDone) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    //Time the operation
                    long endTimeLUD = System.nanoTime();
                    long duration = (endTimeLUD - startTimeDownloadArticles);  //divide by 1000000 to get milliseconds.
                    Log.d(ArticleActivity.logTag + ":" + TAG, "#TIME: Downloaded ALL the Feeds Articles: "+ (duration/1000000) +"ms");

                    /**
                     * All articles have been downloaded -->
                     *      Control if there are any changes, between the copy in the local SQLite Database and
                     *      the list of articles just downloaded;
                     *
                     * If we find even one downloaded article missing in the local list --> substitute all
                     */
                    boolean articleMismatch = false;
                    if (articles.size() != downloadedArticleList.size()) { // if different sizes -> mismatch
                        articleMismatch = true;

                        //Substitute all the articles in the recycler view with those downloaded
                        articles = downloadedArticleList;
                    } else {
                        for (Article article : downloadedArticleList) {
                            if (article.isArticleInTheList(articles) == false) {
                                articleMismatch = true;

                                //Substitute all the articles in the recycler view with those downloaded
                                articles = downloadedArticleList;

                                //Exit for loop prematurely
                                break;
                            }
                        }
                    }

                    //Wait for all articles to load, then notify the activity with a callback, and pass the
                    //map of the number of articles for each feed
                    if (articleMismatch) {
                        if (userData.getVisualizationMode() == UserData.MODE_ALL_MULTIFEEDS_FEEDS) {
                            mListener.onListFragmentAllArticlesReady(feedArticlesNumberMap, articles);
                        }
                    }

                    if (mListener != null) {
                        Log.d(ArticleActivity.logTag + ":" + TAG, "All LoadRSSFeed and getScores finished");

                        sortArticles();

                        //Reload RecyclerView only if the downloaded articles are different that those stored locally
                        if (articleMismatch) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (recyclerView != null) {
                                        recyclerView.setAdapter(new ArticleRecyclerViewAdapter(articles, mListener, getContext()));
                                        recyclerView.getAdapter().notifyDataSetChanged();
                                    }
                                }
                            });

                            mListener.onListFragmentArticlesReady();
                        }

                        //Wait for the SQLite Article to load
                        while (!userData.isLocalArticleListLoaded()) {
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        /**
                         * If the LocalCopy of the articles is different from the one just downloaded, we also
                         * need to persist the new list of articles downloaded. It is important to do it here,
                         * after the list has been sorted, otherwise if we change the list during the persistence
                         * we might get a concurrency problem (ex ConcurrentModificationException).
                         */
                        manageArticlesLocalDBPersistence(articleMismatch, sqLiteService, downloadedArticleList);
                    }
                    allFeedsLoaded = true;
                }
            });
            waitAllFeedsLoaded.start();
            waitAllFeedsLoadedCopy = waitAllFeedsLoaded;

            // call on list fragmentArticlesReady always after a delay of 20 seconds
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
                        }
                    }
                }
            }, 20000);
        }
        //MODE_COLLECTION_ARTICLES
        else {
            articles.addAll(collectionArticleList);
            mListener.onListFragmentArticlesReady();

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
        }

    }

    private synchronized void sortArticles() {
        Log.d(ArticleActivity.logTag + ":" + TAG, "# Articles: " + articles.size());

        Log.d(ArticleActivity.logTag + ":" + TAG, "Sorting articles...");

        for (int i = 0; i < articles.size(); i++) {
            Article article = articles.get(i);
            //Log.d(ArticleActivity.logTag + ":" + TAG, articles.get(i).toString());
            float articleScoreByRating = article.getScore() * article.getFeedObj().getMultifeed().getRating();
            article.setScore(articleScoreByRating);

        }

        Collections.sort(articles, new Comparator<Article>() {
            @Override
            public int compare(Article a1, Article a2) {
                if (a1.getScore() == a2.getScore()){
                    if(a1.getPubDate() != null && a2.getPubDate() != null)
                        return a2.getPubDate().compareTo(a1.getPubDate());
                    else
                        return 0;
                } else {
                    return a1.getScore() > a2.getScore() ? -1 : 1;
                }
            }
        });

        Log.d(ArticleActivity.logTag + ":" + TAG, "Sorting DONE");
        /*for (int i = 0; i < articles.size(); i++) {
            Log.d(ArticleActivity.logTag + ":" + TAG, articles.get(i).toString());
        }*/
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

        void onListFragmentArticlesReady();

        void onListFragmentAllArticlesReady(Map<String, Integer> feedArticlesNumberMap, List<Article> articleList);
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


    /**
     * Manage the local SQLite Database persistence of the Articles. The persistence differs based on the Selection type:
     * 1. In the ALL_MULTIFEEDS selection, we resynchronize everything by delegating everything in the Article Table of the db,
     *      saving the new downloaded articles list and finally resynchronize by getting the article list back from the database;
     * 2. In the PARTIAL selections, we don't currently delete articles on the database, we just add the new ones. This may
     *      accumulate some old articles, but they will be dropped as soon as a full selection is done(ALL_MULTIFEEDS);
     * @param articleMismatch
     * @param sqLiteService
     * @param downloadedArticleList
     */
    void manageArticlesLocalDBPersistence(boolean articleMismatch, final SQLiteService sqLiteService, final List<Article> downloadedArticleList){
        if (articleMismatch) {
            if (userData.getVisualizationMode() == UserData.MODE_ALL_MULTIFEEDS_FEEDS) {
                //Once all the articles have been downloaded for each feed, store them in the local SQLite Database
                //(but first clear the table of the old rows)
                sqLiteService.deleteAllArticles(new SQLOperationCallback() {
                    @Override
                    public void onLoad(SQLOperation sqlOperation) {
                        Log.d(ArticleActivity.logTag + ":" + TAG,
                                "All the articles have been deleted from the local SQLite DB, Article Table!");

                        //Once the Table have been cleaned, add/store the list of articles
                        sqLiteService.putArticles(downloadedArticleList, new SQLOperationCallback() {
                            @Override
                            public void onLoad(SQLOperation sqlOperation) {
                                Log.d(ArticleActivity.logTag + ":" + TAG,
                                        "Added " + sqlOperation.getAffectedRows() + " articles into the local SQLite DB, Article Table!");

                                //UserData ArticlesList is now obsolete
                                userData.setLocalArticleListLoaded(false);

                                //Get all the articles stored locally in the SQLite Database
                                sqLiteService.getAllArticles(new ArticleCallback() {
                                    @Override
                                    public void onLoad(List<Article> localArticles) {
                                        if (localArticles != null) {
                                            userData.setLocalArticleList(localArticles);
                                        }
                                    }

                                    @Override
                                    public void onFailure() {
                                        Log.d(ArticleActivity.logTag + ":" + TAG, "Failed!");
                                    }
                                });
                            }

                            @Override
                            public void onFailure() {
                                Log.d(ArticleActivity.logTag + ":" + TAG, "Failed!");
                            }
                        });
                    }

                    @Override
                    public void onFailure() {
                        Log.d(ArticleActivity.logTag + ":" + TAG, "Failed!");
                    }
                });
            }
            //On Partial Selections
            else{
                //Add/store the list of articles for this selection, without deleting the old ones
                //This may add some additional articles, but they will be deleted on the ALL_MULTIFEEDS Selections
                sqLiteService.putArticles(downloadedArticleList, new SQLOperationCallback() {
                    @Override
                    public void onLoad(SQLOperation sqlOperation) {
                        Log.d(ArticleActivity.logTag + ":" + TAG,
                                "Added " + sqlOperation.getAffectedRows() + " articles into the local SQLite DB, Article Table!");

                        //UserData ArticlesList is now obsolete
                        userData.setLocalArticleListLoaded(false);

                        //Get all the articles stored locally in the SQLite Database
                        sqLiteService.getAllArticles(new ArticleCallback() {
                            @Override
                            public void onLoad(List<Article> localArticles) {
                                if (localArticles != null) {
                                    userData.setLocalArticleList(localArticles);
                                }
                            }

                            @Override
                            public void onFailure() {
                                Log.d(ArticleActivity.logTag + ":" + TAG, "Failed!");
                            }
                        });
                    }

                    @Override
                    public void onFailure() {
                        Log.d(ArticleActivity.logTag + ":" + TAG, "Failed!");
                    }
                });
            }
        }
    }

    /**
     * Returns TRUE if everything has loaded (Downloaded Articles + Loaded Local SQLite Articles)
     */
    public boolean isEverythingLoaded(){
        return allFeedsLoaded && sqLiteArticlesLoaded;
    }
}
