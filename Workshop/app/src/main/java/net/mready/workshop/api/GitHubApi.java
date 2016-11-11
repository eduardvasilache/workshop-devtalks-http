package net.mready.workshop.api;

import net.mready.workshop.models.Repository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GitHubApi {

    public interface ResponseCallback<T> {
        void onSuccess(T data);

        void onError(String message);
    }

    private static final String BASE_URL = "https://api.github.com/";

    private final GitHubService gitHubService;

    public GitHubApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        gitHubService = retrofit.create(GitHubService.class);
    }

    public void getRepositories(String searchQuery, final ResponseCallback<List<Repository>> callback) {
        gitHubService.getRepositories(searchQuery).enqueue(new Callback<RepositoriesResponse>() {
            @Override
            public void onResponse(Call<RepositoriesResponse> call, Response<RepositoriesResponse> response) {
                RepositoriesResponse repositoriesResponse = response.body();
                if (repositoriesResponse != null) {
                    callback.onSuccess(repositoriesResponse.getRepositories());
                } else {
                    callback.onSuccess(null);
                }
            }

            @Override
            public void onFailure(Call<RepositoriesResponse> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

}