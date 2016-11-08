package net.mready.workshop;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button btnRequest;
    private TextView tvResponse;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRequest = (Button) findViewById(R.id.btn_request);
        tvResponse = (TextView) findViewById(R.id.tv_result);
        progressDialog = new ProgressDialog(this);

        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleButtonClick();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        super.onDestroy();
    }

    private void handleButtonClick() {
        if (NetworkUtils.hasNetworkConnection(this)) {
            displayProgressDialog(true);
            tvResponse.setVisibility(View.GONE);
            fetchData();
        } else {
            displayResponse(getString(R.string.error_network_unavailable));
        }
    }

    private void fetchData() {
        new GetUserTask("JakeWharton") {
            @Override
            protected void onPostExecute(User user) {
                String responseText;
                if (user != null) {
                    responseText = buildResponseText(user);
                } else {
                    responseText = getString(R.string.error_unknown);
                }
                displayResponse(responseText);
            }
        }.execute();
    }

    private void displayResponse(String response) {
        displayProgressDialog(false);
        tvResponse.setVisibility(View.VISIBLE);
        tvResponse.setText(response);
    }

    private void displayProgressDialog(boolean shouldDisplay) {
        if (progressDialog != null) {
            if (shouldDisplay) {
                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.setCancelable(false);
                progressDialog.show();
            } else {
                progressDialog.dismiss();
            }
        }
    }

    private String buildResponseText(User user) {
        return user.getName() + " works at " + user.getCompanyName()
                + " and you can contact him at " + user.getEmailAddress();
    }

}