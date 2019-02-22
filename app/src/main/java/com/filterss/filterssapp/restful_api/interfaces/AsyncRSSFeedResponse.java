package com.filterss.filterssapp.restful_api.interfaces;

import com.filterss.filterssapp.models.RSSFeed;

public interface AsyncRSSFeedResponse {
    void processFinish(Object output, RSSFeed rssFeed);
}
