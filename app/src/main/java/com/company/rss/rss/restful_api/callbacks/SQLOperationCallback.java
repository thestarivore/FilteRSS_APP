package com.company.rss.rss.restful_api.callbacks;

import com.company.rss.rss.models.SQLOperation;

public interface SQLOperationCallback {
    public void onLoad(SQLOperation sqlOperation);
    public void onFailure();
}
