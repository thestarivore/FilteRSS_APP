package com.filterss.filterssapp.restful_api.callbacks;

import com.filterss.filterssapp.models.SQLOperation;
import java.util.List;

public interface SQLOperationListCallback {
    public void onLoad(List<SQLOperation> sqlOperationList);
    public void onFailure();
}
