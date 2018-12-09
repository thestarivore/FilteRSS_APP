package com.company.rss.rss.restful_api.callbacks;

import com.company.rss.rss.models.Feed;

import java.util.List;

public interface FeedCallback {
    public void onLoad(List<Feed> feeds);
    public void onFailure();
}
