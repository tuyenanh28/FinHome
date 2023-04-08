package com.datn.finhome.Views.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.datn.finhome.Controllers.UserController;
import com.datn.finhome.Models.UserModel;
import com.datn.finhome.R;
import com.datn.finhome.Utils.OverUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{
    OverUtils overUtils;
    Button btn_signup;
    FirebaseAuth firebaseAuth;
    EditText edt_email_signUp, edt_password_signUp, edt_retype_password_signUp, edt_name_signUp, edt_phone_signUp;
    RadioButton rad_gender_female_signUp, rad_gender_male_signUp;
    ProgressDialog progressDialog;
    UserController userController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        firebaseAuth = FirebaseAuth.getInstance();
        initView();
    }

    private void initView() {
        btn_signup = findViewById(R.id.btn_signUp);
        edt_email_signUp = findViewById(R.id.edt_email_signUp);
        edt_password_signUp = findViewById(R.id.edt_password_signUp);
        edt_retype_password_signUp = findViewById(R.id.edt_retype_password_signUp);
        edt_name_signUp = findViewById(R.id.edt_name_signUp);
        edt_phone_signUp = findViewById(R.id.edt_phone_signUp);
        rad_gender_female_signUp = findViewById(R.id.rad_gender_female_signUp);
        rad_gender_male_signUp = findViewById(R.id.rad_gender_male_signUp);
        progressDialog = new ProgressDialog(RegisterActivity.this, R.style.MyProgessDialogStyle);
        userController = new UserController(this);
        btn_signup.setOnClickListener(this);
        rad_gender_female_signUp.setOnClickListener(this);
        rad_gender_male_signUp.setOnClickListener(this);
    }

    private void signUp() {
        String email = edt_email_signUp.getText().toString();
        String password = edt_password_signUp.getText().toString();
        String passwordRetype = edt_retype_password_signUp.getText().toString();
        final String name = edt_name_signUp.getText().toString();
        final String avatar = "https://cdn-icons-png.flaticon.com/512/149/149071.png";
        final Boolean owner = false;
        final String phone =  edt_phone_signUp.getText().toString();
        Boolean gender = true;
        if(rad_gender_female_signUp.isChecked()) {
            gender = false;
        }

        final Boolean genderUser = gender;
        if(email.trim().length() == 0) {
            overUtils.makeToast(getApplicationContext(),overUtils.ERROR_EMAIL);
        }else if (name.length() <= 5) {
            OverUtils.makeToast(getApplicationContext(), OverUtils.VALIDATE_NAME);
        }
        else if (password.trim().length() == 0) {
            overUtils.makeToast(getApplicationContext(),overUtils.ERROR_PASS);
        } else if (!passwordRetype.equals(password)) {
            overUtils.makeToast(getApplicationContext(),overUtils.CHECK_PASS);
        } else if  (password.matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-])")) {
            overUtils.makeToast(getApplicationContext(),overUtils.ERROR_PASS1);
        }else {
            progressDialog.setMessage("Đang tạo tài khoản...");
            progressDialog.setIndeterminate(true);
            progressDialog.show();

            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(RegisterActivity.this, "Chúng tôi đã gửi tin nhắn xác thực về email vui lòng xác thực để tiếp tục đăng nhập", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                                String uid = task.getResult().getUser().getUid();
                                UserModel userModel = new UserModel();
                                userModel.setUserID(uid);
                                userModel.setName(name);
                                userModel.setEmail(email);
                                userModel.setAvatar(avatar);
                                userModel.setGender(genderUser);
                                userModel.setOwner(owner);
                                userModel.setPhoneNumber(phone);
                                userModel.setEnable(true);
                                userController.addUser(userModel, uid);
                                progressDialog.dismiss();
                                overUtils.makeToast(getApplicationContext(),"Đăng ký thành công");
                                Intent iSignin = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(iSignin);
                    } else {
                        progressDialog.dismiss();
                        overUtils.makeToast(getApplicationContext(),overUtils.ERROR_SIGNIN);
                    }
                }
            });
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_signUp:
                signUp();
                break;
        }
    }
}