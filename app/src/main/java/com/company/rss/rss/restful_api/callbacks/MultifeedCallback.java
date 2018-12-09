package com.company.rss.rss.restful_api.callbacks;

import com.company.rss.rss.models.Multifeed;

import java.util.List;

public interface MultifeedCallback {
    public void onLoad(List<Multifeed> multifeeds);
    public void onFailure();
}
