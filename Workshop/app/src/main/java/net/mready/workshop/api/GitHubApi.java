package net.mready.workshop.api;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import net.mready.workshop.models.Repository;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GitHubApi {

    public interface ResponseCallback<T> {
        void onSuccess(T data);

        void onError(String message);
    }

    private static final String BASE_URL = "https://api.github.com/search/repositories";

    private final OkHttpClient okHttpClient;
    private final Gson gson;
    private final Handler handler;

    public GitHubApi() {
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .build();
        gson = new Gson();
        handler = new Handler(Looper.getMainLooper());
    }

    public void getRepositories(String searchQuery, final ResponseCallback<List<Repository>> callback) {
        HttpUrl httpUrl = HttpUrl.parse(BASE_URL)
                .newBuilder()
                .addQueryParameter("q", searchQuery)
                .addQueryParameter("per_page", "30")
                .build();

        Request request = new Request.Builder()
                .url(httpUrl)
                .get()
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onError(e.getMessage());
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                try {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    }

                    String responseText = response.body().string();
                    final List<Repository> repositories = parseRepositories(responseText);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess(repositories);
                        }
                    });
                } catch (IOException | JsonSyntaxException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(e.getMessage());
                        }
                    });
                }
            }
        });
    }

    private List<Repository> parseRepositories(String json) throws JsonSyntaxException {
        RepositoriesResponseWrapper response =
                gson.fromJson(json, RepositoriesResponseWrapper.class);

        if (response == null) {
            throw new JsonSyntaxException("Invalid JSON syntax");
        }

        return response.getRepositories();
    }

}