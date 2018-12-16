package com.company.rss.rss.restful_api.interfaces;

import com.company.rss.rss.models.Article;
import com.company.rss.rss.models.RSSFeed;
import java.util.List;

public interface AsyncRSSFeedResponse {
    void processFinish(Object output, RSSFeed rssFeed);
}
