package net.mready.workshop.tasks;

import android.os.AsyncTask;
import android.util.Log;

import net.mready.workshop.models.Repository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
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
    private final String searchQuery;

    public GetRepositoriesTask(String searchQuery) {
        this.searchQuery = searchQuery;

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS);

        okHttpClient = builder.build();
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
                throw new IOException("Unexpected code " + response);
            }

            String responseText = response.body().string();
            return parse(responseText);
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
            return null;
        }
    }

    private List<Repository> parse(String json) {
        List<Repository> repositories = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("items");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject repositoryJsonObject = jsonArray.getJSONObject(i);

                Repository repository = new Repository();

                final String keyFullName = "full_name";
                if (repositoryJsonObject.has(keyFullName)) {
                    repository.setFullName(repositoryJsonObject.getString(keyFullName));
                }

                final String keyDescription = "description";
                if (repositoryJsonObject.has(keyDescription)) {
                    repository.setDescription(repositoryJsonObject.getString(keyDescription));
                }

                final String keyUrl = "html_url";
                if (repositoryJsonObject.has(keyUrl)) {
                    repository.setUrl(repositoryJsonObject.getString(keyUrl));
                }

                repositories.add(repository);
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
        }

        return repositories;
    }

}