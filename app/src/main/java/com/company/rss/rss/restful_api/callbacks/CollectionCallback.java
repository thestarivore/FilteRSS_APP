package com.company.rss.rss.restful_api.callbacks;

import com.company.rss.rss.models.Collection;

import java.util.List;

public interface CollectionCallback {
    public void onLoad(List<Collection> collections);
    public void onFailure();
}
