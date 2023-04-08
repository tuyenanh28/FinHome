package com.datn.finhome.Views.Activity;

import static android.content.ContentValues.TAG;
import static com.datn.finhome.Utils.OverUtils.MY_REQUEST_CODE;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Magnifier;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.datn.finhome.Interfaces.IAfterGetAllObject;
import com.datn.finhome.Models.Image;
import com.datn.finhome.Models.UserModel;
import com.datn.finhome.R;
import com.datn.finhome.Utils.ImgUri;
import com.datn.finhome.Utils.OverUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;

public class AccountInfoActivity extends AppCompatActivity {
    OverUtils overUtils;

    private AppCompatImageView imageView;
    private EditText txtNameAcc, txtSdtAcc, txtMailAcc, txtAddressAcc;
    private Button btnDelAcc;
    private Toolbar toolbar;
    private Uri imageUri;

    private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private StorageReference sReference;

    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);
        initView();
        setAccInfo();
        initListener();
    }

    private void initView() {
        toolbar = findViewById(R.id.toobar_acc);
        imageView = findViewById(R.id.imgAccInfo);
        txtNameAcc = findViewById(R.id.txt_name_acc);
        txtSdtAcc = findViewById(R.id.txt_sdt_acc);
        txtMailAcc = findViewById(R.id.txt_mail_acc);
        txtAddressAcc = findViewById(R.id.txt_address_acc);
        btnDelAcc = findViewById(R.id.btn_del_acc);

        sReference = FirebaseStorage.getInstance().getReference();

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toobar_menu, menu);

        int i = 0;
        MenuItem item = menu.getItem(i);
        SpannableString s = new SpannableString("Lưu");
        s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, s.length(), 0);
        item.setTitle(s);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.toobar_btnSave:
                onClickUpdateAccInfo();
                break;
            default:break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setAccInfo(){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userId = firebaseUser.getUid();

        reference.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel userModel = snapshot.getValue(UserModel.class);
                if(userModel != null){
                    String fullName = userModel.name;
                    String phone = userModel.phoneNumber;
                    String avatar = userModel.avatar;
                    String email = userModel.email;
                    String address = userModel.address;

                    txtNameAcc.setText(fullName);
                    txtSdtAcc.setText(phone);
                    Glide.with(getApplicationContext()).load(avatar).into(imageView);
                    txtMailAcc.setText(email);
                    txtAddressAcc.setText(address);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AccountInfoActivity.this, "Show", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initListener() {
        imageView.setOnClickListener(view -> onClickSetImage());
        btnDelAcc.setOnClickListener(view -> onClickDelAccInfo());
    }

    private void onClickSetImage() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, 2);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    private void uploadToFirebase(Uri uri) {
        StorageReference fileRef = sReference.child(System.currentTimeMillis() + "." + ImgUri.getExtensionFile(this, imageUri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        reference = FirebaseDatabase.getInstance().getReference("Users");
                        userId = firebaseUser.getUid();
                        String imgLink = String.valueOf(uri);
                        reference.child(userId).child("avatar").setValue(imgLink);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AccountInfoActivity.this, "Upload Image Fail!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onClickUpdateAccInfo() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userId = firebaseUser.getUid();

        String newName = txtNameAcc.getText().toString().trim();
        String newPhone = txtSdtAcc.getText().toString().trim();
        String newMail = txtMailAcc.getText().toString().trim();
        String newAddress = txtAddressAcc.getText().toString().trim();

        if(newName.isEmpty() || newPhone.isEmpty() || newMail.isEmpty()){
            Toast.makeText(AccountInfoActivity.this,"Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
        }else if(newMail.trim().length() == 0) {
            overUtils.makeToast(getApplicationContext(),overUtils.ERROR_EMAIL);
        }else if (newName.length() <= 5) {
            OverUtils.makeToast(getApplicationContext(), OverUtils.VALIDATE_NAME);
        }else if(!newPhone.trim().matches("^(0|\\+84)(\\s|\\.)?((3[2-9])|(5[689])|(7[06-9])|(8[1-689])|(9[0-46-9]))(\\d)(\\s|\\.)?(\\d{3})(\\s|\\.)?(\\d{3})$")){
            overUtils.makeToast(getApplicationContext(),overUtils.VALIDATE_PHONE);
        } else {
            reference.child(userId).child("name").setValue(newName);
            reference.child(userId).child("phoneNumber").setValue(newPhone);
            reference.child(userId).child("email").setValue(newMail);
            reference.child(userId).child("address").setValue(newAddress);
            uploadToFirebase(imageUri);
            Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }

    }


    private void onClickDelAccInfo() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userId = firebaseUser.getUid();
        reference.child(userId).removeValue();
        firebaseUser.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(AccountInfoActivity.this, "Đã xóa tài khoản", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(AccountInfoActivity.this, LoginActivity.class));
                        }
                    }
                });

    }




}