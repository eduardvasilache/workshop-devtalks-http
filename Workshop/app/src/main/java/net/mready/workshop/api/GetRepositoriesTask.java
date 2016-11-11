package net.mready.workshop.api;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import net.mready.workshop.models.Repository;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetRepositoriesTask extends AsyncTask<Void, Void, List<Repository>> {

    private static final String LOG_TAG = GetRepositoriesTask.class.getName();

    private static final String BASE_URL = "https://api.github.com/search/repositories";

    private final OkHttpClient okHttpClient;
    private final Gson gson;
    private final String searchQuery;

    public GetRepositoriesTask(String searchQuery) {
        this.searchQuery = searchQuery;

        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS);
        okHttpClient = okHttpBuilder.build();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
    }

    @Override
    protected List<Repository> doInBackground(Void... voids) {
        HttpUrl httpUrl = HttpUrl.parse(BASE_URL)
                .newBuilder()
                .addQueryParameter("q", searchQuery)
                .addQueryParameter("per_page", "30")
                .build();

        Request request = new Request.Builder()
                .url(httpUrl)
                .get()
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response.code());
            }

            String responseText = response.body().string();
            return parse(responseText);
        } catch (IOException | JsonSyntaxException e) {
            Log.e(LOG_TAG, e.getMessage());
            return null;
        }
    }

    private List<Repository> parse(String json) throws JsonSyntaxException {
        RepositoriesResponseWrapper response =
                gson.fromJson(json, RepositoriesResponseWrapper.class);

        if (response == null) {
            throw new JsonSyntaxException("Invalid JSON syntax");
        }

        return response.getRepositories();
    }

}