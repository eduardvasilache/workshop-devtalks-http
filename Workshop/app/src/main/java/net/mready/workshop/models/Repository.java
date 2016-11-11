package net.mready.workshop.models;

import com.google.gson.annotations.SerializedName;

public class Repository {

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("description")
    private String description;

    @SerializedName("html_url")
    private String url;

    public String getFullName() {
        return fullName;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

}