package com.filterss.filterssapp.restful_api.callbacks;

import com.filterss.filterssapp.models.SQLOperation;

public interface SQLOperationCallback {
    public void onLoad(SQLOperation sqlOperation);
    public void onFailure();
}
