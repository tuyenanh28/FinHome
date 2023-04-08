package com.datn.finhome.Controllers;

import android.content.Context;

import androidx.annotation.NonNull;

import com.datn.finhome.Interfaces.IAfterGetAllObject;
import com.datn.finhome.Models.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserController {
    Context context;
    UserModel userModel;

    public UserController(Context context) {
        this.context = context;
        this.userModel = new UserModel();
    }

    public void addUser(UserModel newUserModel, String uid) {
        userModel.addUser(newUserModel, uid);
    }




}
