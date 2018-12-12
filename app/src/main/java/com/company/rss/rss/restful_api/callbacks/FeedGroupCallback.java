package com.company.rss.rss.restful_api.callbacks;

import com.company.rss.rss.models.FeedGrouping;
import java.util.List;

public interface FeedGroupCallback {
    public void onLoad(List<FeedGrouping> feedGroupingList);
    public void onFailure();
}
