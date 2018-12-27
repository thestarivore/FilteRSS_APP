package com.company.rss.rss.rss_parser;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.company.rss.rss.ArticleActivity;
import com.company.rss.rss.models.Article;
import com.company.rss.rss.models.Feed;
import com.company.rss.rss.models.RSSFeed;
import com.company.rss.rss.restful_api.interfaces.AsyncRSSFeedResponse;

/**
 * Loads an RSS rssFeed from a given URL and writes the object
 * to a file in the application's /data directory. Parses
 * through the rssFeed and starts the main fragment control
 * upon completion.
 */
public class LoadRSSFeed extends AsyncTask<Void, Void, Object> {
    private final String TAG = getClass().getName();
    private final Feed feed;

    //Call back interface
    public AsyncRSSFeedResponse delegate = null;

    // The parent context
    private Context parent;

    // The RSSFeed object
    private RSSFeed rssFeed;

    // The URL we're parsing from
    private String RSSFEEDURL;

    /**
     * AsyncTask LoadRSSFeed Constructor
     *
     * @param asyncRSSFeedResponse AsyncTask response listener Callback
     * @param c                    Activity Context
     * @param feed                 Feed Object, whose articles to download
     */
    public LoadRSSFeed(AsyncRSSFeedResponse asyncRSSFeedResponse, Context c, Feed feed) {
        //Assigning call back interface through constructor
        delegate = asyncRSSFeedResponse;
        // Set the parent
        parent = c;
        // Set the rssFeed URL
        RSSFEEDURL = feed.getLink();

        this.feed = feed;
    }

    @Override
    protected Void doInBackground(Void... params) {
        // Parse the RSSFeed and save the object
        rssFeed = new DOMParser().parseXML(RSSFEEDURL);
        return null;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(Object result) {
        super.onPostExecute(result);
        delegate.processFinish(result, rssFeed);

        //Print all the Articles
        Log.d(ArticleActivity.logTag + ":" + TAG, "\n### LIST of Articles for: " + RSSFEEDURL + " ###");
        for (Article article : rssFeed.getItemList()) {
            article.setFeedName(feed.getTitle());
            article.setFeedIcon(feed.getIconURL());
            //Log.d(ArticleActivity.logTag + ":" + TAG, "\n\n\n---Article: " + article.getTitle() + ", " + article.getLink() + ", " + article.getDescription() + ", " + article.getPubDate());
        }
    }
}