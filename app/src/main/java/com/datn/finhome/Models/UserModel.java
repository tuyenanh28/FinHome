package com.datn.finhome.Models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.RequestCreator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserModel implements Parcelable {
   public String avatar;
   public String name;
   public String email;
   public String phoneNumber;
   public String address;
    private boolean enable;
    boolean owner, gender;

    private List<Image> mListImage;

    //Id người dùng ở đây là uid trong firebaseauthen
    String userID;

    RequestCreator compressionImageFit;

    public UserModel(String avatar, String name, String email, String phoneNumber, String address, boolean enable, boolean owner, boolean gender, List<Image> mListImage, String userID, RequestCreator compressionImageFit, DatabaseReference nodeRoot) {
        this.avatar = avatar;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.enable = enable;
        this.owner = owner;
        this.gender = gender;
        this.mListImage = mListImage;
        this.userID = userID;
        this.compressionImageFit = compressionImageFit;
        this.nodeRoot = nodeRoot;
    }


    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    protected UserModel(Parcel in) {
        avatar = in.readString();
        name = in.readString();
        email = in.readString();
        phoneNumber = in.readString();
        address = in.readString();
        owner = in.readByte() != 0;
        gender = in.readByte() != 0;
        userID = in.readString();
    }

    public static final Creator<UserModel> CREATOR = new Creator<UserModel>() {
        @Override
        public UserModel createFromParcel(Parcel in) {
            return new UserModel(in);
        }

        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isOwner() {
        return owner;
    }

    public void setOwner(boolean owner) {
        owner = owner;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }


    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public RequestCreator getCompressionImageFit() {
        return compressionImageFit;
    }

    public void setCompressionImageFit(RequestCreator compressionImageFit) {
        this.compressionImageFit = compressionImageFit;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    //Biến lưu root của firebase, lưu ý để biến là private
    private DatabaseReference nodeRoot;

    //hàm khởi tạo rỗng
    public UserModel() {
        nodeRoot = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(avatar);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(phoneNumber);
        dest.writeString(address);
        dest.writeByte((byte) (owner ? 1 : 0));
        dest.writeByte((byte) (gender ? 1 : 0));
        dest.writeString(userID);
    }

    public void addUser(UserModel newUserModel, String uid) {
        DatabaseReference nodeUser = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        nodeUser.setValue(newUserModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                }
            }
        });
    }

//    public Map<String, Object> toMapListRoom() {
//        HashMap<String, Object> map = new HashMap<String, Object>();
//        map.put("list_room", list_room);
//        return map;
//    }

}
