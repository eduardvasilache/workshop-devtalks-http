package net.mready.workshop.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.mready.workshop.R;
import net.mready.workshop.models.Repository;
import net.mready.workshop.api.GetRepositoriesTask;
import net.mready.workshop.utils.NetworkUtils;

import java.util.List;

public class RepositoriesActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private RepositoriesAdapter repositoriesAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText etSearchQuery = (EditText) findViewById(R.id.et_search_query);
        final Button btnSearch = (Button) findViewById(R.id.btn_search);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        progressDialog = new ProgressDialog(this);
        repositoriesAdapter = new RepositoriesAdapter();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(repositoriesAdapter);

        etSearchQuery.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    onSearchClicked(etSearchQuery.getText().toString());
                    return true;
                }
                return false;
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearchClicked(etSearchQuery.getText().toString());
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

    private void onSearchClicked(String query) {
        closeKeyboard();

        if (NetworkUtils.hasNetworkConnection(this)) {
            search(query);
        } else {
            displayToast(getString(R.string.error_network_unavailable));
        }
    }

    private void setLoading(boolean loading) {
        if (progressDialog != null) {
            if (loading) {
                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.setCancelable(false);
                progressDialog.show();
            } else {
                progressDialog.dismiss();
            }
        }
    }

    private void displayToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    private void search(String query) {
        setLoading(true);
        new GetRepositoriesTask(query) {
            @Override
            protected void onPostExecute(List<Repository> repositories) {
                setLoading(false);
                if (repositories != null) {
                    if (repositoriesAdapter != null) {
                        repositoriesAdapter.setRepositories(repositories);
                    }
                } else {
                    displayToast(getString(R.string.error_unknown));
                }
            }
        }.execute();
    }

}