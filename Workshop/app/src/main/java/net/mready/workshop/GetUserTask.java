package net.mready.workshop;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class GetUserTask extends AsyncTask<Void, Void, User> {

    private static final String LOG_TAG = GetUserTask.class.getName();

    private static final String BASE_URL = "https://api.github.com/users/";

    private final String url;

    public GetUserTask(String username) {
        this.url = BASE_URL + username;
    }

    @Override
    protected User doInBackground(Void... voids) {
        InputStream inputStream = null;
        try {
            URL url = new URL(this.url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3 * 1000);
            connection.setReadTimeout(3 * 1000);
            connection.setDoInput(true);

            connection.connect();
            inputStream = connection.getInputStream();

            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            String response = stringBuilder.toString();
            return parseUser(response);
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