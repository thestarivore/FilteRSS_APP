package com.company.rss.rss.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.company.rss.rss.ArticleActivity;
import com.company.rss.rss.controllers.ArticleListSwipeController;
import com.company.rss.rss.R;
import com.company.rss.rss.adapters.ArticleRecyclerViewAdapter;
import com.company.rss.rss.models.Article;
import com.company.rss.rss.models.Collection;
import com.company.rss.rss.models.Feed;
import com.company.rss.rss.models.Multifeed;
import com.company.rss.rss.models.RSSFeed;
import com.company.rss.rss.models.UserData;
import com.company.rss.rss.restful_api.interfaces.AsyncRSSFeedResponse;
import com.company.rss.rss.rss_parser.LoadRSSFeed;

import java.util.ArrayList;
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
    private int feedCounter;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArticlesListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
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

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article_list, container, false);

        // Create the RecyclerView and Set it's adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;

            // Set the swipe controller
            ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ArticleListSwipeController(0, ItemTouchHelper.RIGHT, this);
            new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new ArticleRecyclerViewAdapter(articles, mListener));
        }
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
    public void onUserDataLoaded() {
        final List<Feed>        feedList                = new ArrayList<>();
        List<Article>           articleList             = new ArrayList<>();
        final Map<String,Integer>    feedArticlesNumberMap   = new HashMap<>();
        final int numberOfFeeds;

        //Get the Transferred UserData
        this.userData = UserData.getInstance();

        //Prepare the articles list
        this.articles = new ArrayList<>();

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
            Collection collection = userData.getCollectionList().get(userData.getCollectionPosition());
            articleList.addAll(userData.getCollectionMap().get(collection));
        }

        //Total number of feeds
        numberOfFeeds = feedList.size();

        //MODE_ALL_MULTIFEEDS_FEEDS || MODE_MULTIFEED_ARTICLES || MODE_FEED_ARTICLES
        if (userData.getVisualizationMode() != UserData.MODE_COLLECTION_ARTICLES) {
            feedCounter = 0;
            //Start Downloading the Articles, even if the UI hasn't loaded yet
            for (final Feed feed : feedList) {
                new LoadRSSFeed(new AsyncRSSFeedResponse() {
                    @Override
                    public void processFinish(Object output, RSSFeed rssFeed) {
                        for (Article article : rssFeed.getItemList()) {
                            article.setFeed(feed.getId());
                            articles.add(article);
                        }

                        //Save the number of articles for each feed, mapped in a HashMap
                        feedArticlesNumberMap.put(feed.getTitle(),articles.size());

                        //Wait for onCreateView to set RecyclerView's Adapter
                        while (recyclerView == null || recyclerView.getAdapter() == null) ;

                        //Notify a change in the RecyclerView's Article List
                        recyclerView.getAdapter().notifyDataSetChanged();

                        Log.d(ArticleActivity.logTag + ":" + TAG, "LoadRSSFeed finished: " + (feedCounter+1) + "/" + feedList.size());

                        feedCounter++;
                    }
                }, getContext(), feed).execute();
            }

            // Wait until at least one LoadRSSFeed has finished then call onListFragmentArticlesReady
            // to notify that we have something to show
            final Thread waitAllFeedsLoaded = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (feedCounter < 1){ // wait until we have something to show
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if(mListener != null){
                        Log.d(ArticleActivity.logTag + ":" + TAG, "All LoadRSSFeed finished");
                        mListener.onListFragmentArticlesReady();
                    }

                    //Wait for all articles to load, then notify the activity with a callback, and pass the
                    //map of the number of articles for each feed
                    while (feedCounter < numberOfFeeds);
                    if (userData.getVisualizationMode() == UserData.MODE_ALL_MULTIFEEDS_FEEDS) {
                        mListener.onListFragmentAllArticlesReady(feedArticlesNumberMap);
                    }
                }
            });
            waitAllFeedsLoaded.start();

            // call on list fragmentArticlesReady always after a delay of 20 seconds
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    waitAllFeedsLoaded.interrupt();
                    if(feedCounter < feedList.size()){
                        if(mListener != null){
                            Log.d(ArticleActivity.logTag + ":" + TAG, "TIMEOUT LoadRSSFeed, loading not completed");
                            mListener.onListFragmentArticlesReady();
                        }
                    }
                }
            }, 20000);
        }
        //MODE_COLLECTION_ARTICLES
        else {
            articles.addAll(articleList);
            mListener.onListFragmentArticlesReady();
        }

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

        void onListFragmentAllArticlesReady(Map<String, Integer> feedArticlesNumberMap);
    }


}
