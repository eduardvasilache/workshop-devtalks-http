package net.mready.workshop.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

interface GitHubService {

    @GET("search/repositories")
    Call<RepositoriesResponse> getRepositories(@Query("q") String query);

}