package net.mready.workshop.api;

import com.google.gson.annotations.SerializedName;

import net.mready.workshop.models.Repository;

import java.util.ArrayList;
import java.util.List;

public class RepositoriesResponseWrapper {

    @SerializedName("total_count")
    private int totalItemCount;

    @SerializedName("items")
    private List<Repository> repositories;

    public RepositoriesResponseWrapper() {
        repositories = new ArrayList<>();
    }

    public int getTotalItemCount() {
        return totalItemCount;
    }

    public List<Repository> getRepositories() {
        return repositories;
    }

}