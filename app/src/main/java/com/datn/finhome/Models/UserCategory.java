package com.datn.finhome.Models;


import java.util.List;

public class UserCategory {
    private UserModel user;
    private List<Image> images;

    public UserCategory(UserModel user) {
        this.user = user;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }
}
