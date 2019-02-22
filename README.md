## ArticlesListFragment
### Articles downloading
#### onUserDataLoaded()
##### 1. THREAD LOCAL
All the articles from the DB are retrieved:

- getAllArticles(...)
- getFilteredArticles(feedList, ...)

When completed (sqLiteArticlesLoaded == true):
	1. update the content of the list
	2. call onListFragmentLocalArticlesReady on ArticlesListActivity
		- hide the progressBar
		- show the list
		- start the slider with the updated content

##### 2. THREAD ONLINE
Started when:

- the network is available
- the last update was performed n minutes ago

Articles from the feeds are retrieved:

- perform the download for all the feeds
- get the scores for the articles
- waitAllFeedsLoaded: 
    1. wait until:
        + all feeds are retrieved
        + all scores are retrieved
        + sqlLite has finished
    2. sort the articles:
    3. save all the downloaded articles in the DB (if present update the score)
    4. save the last update value
    5. call the onListFragmentArticlesReady() on ArticlesListActivity
        + it shows the "Update Content" button to allow the user to update the list: when clicked call onUserDataLoaded()
    6. call the onListFragmentAllArticlesReady(feedArticlesNumberMap) on ArticlesListActivity
        + it updates the number of articles
