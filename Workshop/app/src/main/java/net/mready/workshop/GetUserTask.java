package net.mready.workshop;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

class GetUserTask extends AsyncTask<Void, Void, User> {

    private static final String LOG_TAG = GetUserTask.class.getName();

    private static final String BASE_URL = "https://api.github.com/users/";

    private final OkHttpClient okHttpClient;
    private final String url;

    public GetUserTask(String username) {
        this.url = BASE_URL + username;

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS);

        okHttpClient = builder.build();
    }

    @Override
    protected User doInBackground(Void... voids) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            String responseText = response.body().string();
            return parseUser(responseText);
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
            return null;
        }
    }

    private User parseUser(String userJson) {
        User user = new User();
        try {
            JSONObject jsonObject = new JSONObject(userJson);

            final String keyName = "name";
            if (jsonObject.has(keyName)) {
                user.setName(jsonObject.getString(keyName));
            }

            final String keyEmail = "email";
            if (jsonObject.has(keyEmail)) {
                user.setEmailAddress(jsonObject.getString(keyEmail));
            }

            final String keyCompany = "company";
            if (jsonObject.has(keyCompany)) {
                user.setCompanyName(jsonObject.getString(keyCompany));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return user;
    }

}