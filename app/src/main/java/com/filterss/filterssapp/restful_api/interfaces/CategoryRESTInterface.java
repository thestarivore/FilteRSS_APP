package com.filterss.filterssapp.restful_api.interfaces;

import com.filterss.filterssapp.models.Category;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface CategoryRESTInterface {
    @GET("/v1/categories")
    public Call<List<Category>> getCategories();
}
