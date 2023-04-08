package com.datn.finhome.Models;

import android.net.Uri;

public class Image {
    private String id;
    private String url;
    private Uri uri;

    public Image() {
    }

    public Image(String id, String url) {
        this.id = id;
        this.url = url;
    }

    public Image(String url) {
        this.url = url;
    }

    public Image(Uri uri) {
        this.uri = uri;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
