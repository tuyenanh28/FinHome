package com.datn.finhome.Models;

public class ImageModel {
    String urlImage;

    public ImageModel(){

    }

    public ImageModel(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }
}
