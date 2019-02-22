package com.filterss.filterssapp.restful_api.callbacks;

import com.filterss.filterssapp.models.Feed;

import java.util.List;

public interface FeedCallback {
    public void onLoad(List<Feed> feeds);
    public void onFailure();
}
