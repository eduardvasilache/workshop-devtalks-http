package net.mready.workshop.api;

import android.os.AsyncTask;
import android.util.Log;

import net.mready.workshop.models.Repository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GetRepositoriesTask extends AsyncTask<Void, Void, List<Repository>> {

    private static final String LOG_TAG = GetRepositoriesTask.class.getName();

    private static final String BASE_URL = "https://api.github.com/search/repositories";

    private final String url;

    public GetRepositoriesTask(String searchQuery) {
        this.url = BASE_URL + "?q=" + searchQuery + "&per_page=30";
    }

    @Override
    protected List<Repository> doInBackground(Void... voids) {
        InputStream inputStream = null;
        try {
            URL url = new URL(this.url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3 * 1000);
            connection.setReadTimeout(3 * 1000);
            connection.setDoInput(true);

            connection.connect();

            int responseCode = connection.getResponseCode();
            boolean successful = responseCode >= 200 && responseCode < 300;
            if (!successful) {
                throw new IOException("Unexpected code " + responseCode);
            }

            inputStream = connection.getInputStream();

            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            String response = stringBuilder.toString();
            return parse(response);
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
            return null;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage());
            }
        }
    }

    private List<Repository> parse(String json) {
        List<Repository> repositories = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);

            final String keyItems = "items";
            if (jsonObject.has(keyItems)) {
                JSONArray jsonArray = jsonObject.getJSONArray(keyItems);

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
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
        }

        return repositories;
    }

}