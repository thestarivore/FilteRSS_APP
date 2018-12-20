package com.company.rss.rss.restful_api.callbacks;

import com.company.rss.rss.models.SQLOperation;
import java.util.List;

public interface SQLOperationListCallback {
    public void onLoad(List<SQLOperation> sqlOperationList);
    public void onFailure();
}
