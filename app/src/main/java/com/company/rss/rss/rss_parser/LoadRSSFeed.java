package com.company.rss.rss.rss_parser;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.company.rss.rss.models.Article;
import com.company.rss.rss.models.RSSFeed;

/**
 * Loads an RSS feed from a given URL and writes the object
 * to a file in the application's /data directory. Parses 
 * through the feed and starts the main fragment control
 * upon completion.
 */
public class LoadRSSFeed extends AsyncTask<Void, Void, Void> {
	private static final String TAG = "LoadRSSFeed";

	// The parent context
	private Context parent;

	// The RSSFeed object
	private RSSFeed feed;

	// The URL we're parsing from
	private String RSSFEEDURL;

	public LoadRSSFeed(Context c, String url){
		// Set the parent
		parent = c;
		// Set the feed URL
		RSSFEEDURL = url;
	}

	@Override
	protected Void doInBackground(Void... params) {
		// Parse the RSSFeed and save the object
		feed = new DOMParser().parseXML(RSSFEEDURL);
		return null;
	}

	@Override
	protected void onPreExecute(){

	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);

        //Print all the Articles
		Log.d(TAG, "\n### LIST of Articles for: "+ RSSFEEDURL +" ###");
		for(Article article: feed.getItemList()){
			Log.d(TAG, "\n\n\n---Article: " + article.getTitle() + ", " + article.getLink() + ", " + article.getDescription() + ", " + article.getPubDate());
		}
	}
}