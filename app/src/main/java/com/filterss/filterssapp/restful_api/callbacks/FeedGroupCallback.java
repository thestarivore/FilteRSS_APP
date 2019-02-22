package com.filterss.filterssapp.restful_api.callbacks;

import com.filterss.filterssapp.models.FeedGrouping;
import java.util.List;

public interface FeedGroupCallback {
    public void onLoad(List<FeedGrouping> feedGroupingList);
    public void onFailure();
}
