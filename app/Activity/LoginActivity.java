package com.datn.finhome.Views.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.datn.finhome.Base.BaseActivity;
import com.datn.finhome.R;
import com.datn.finhome.Utils.OverUtils;
import com.datn.finhome.databinding.ActivityLoginBinding;

import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener, FirebaseAuth.AuthStateListener {
    OverUtils overUtils;
    ImageButton btnLoginWithGoogle, btnLoginWithFacebook;
    private CallbackManager callbackManager;
    GoogleApiClient apiClient;
    FirebaseAuth firebaseAuth;

    Button btn_signUp;
    Button btn_login;

    EditText edt_username_login;
    EditText edt_password_login;

    ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;
    DatabaseReference nodeRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Khởi tạo firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();
        //Text Đăng xuất
        firebaseAuth.signOut();
        // Lưu mã user đăng nhập vào app
        sharedPreferences = getSharedPreferences(OverUtils.PREFS_DATA_NAME, MODE_PRIVATE);

        btnLoginWithGoogle = (ImageButton) findViewById(R.id.btnImg_google_login);
        btnLoginWithFacebook = (ImageButton) findViewById(R.id.btnImg_facebook_login);

        btn_signUp = (Button) findViewById(R.id.btn_signUp);
        btn_login = (Button) findViewById(R.id.btn_login);

        edt_username_login = (EditText) findViewById(R.id.edt_username_login);
        edt_password_login = (EditText) findViewById(R.id.edt_password_login);
        progressDialog = new ProgressDialog(LoginActivity.this, R.style.MyProgessDialogStyle);
        btn_login.setOnClickListener(this);
        btnLoginWithGoogle.setOnClickListener(this);
        btnLoginWithFacebook.setOnClickListener(this);
        CreateClientLoginWithGoogle();
//        FacebookSdk.sdkInitialize(getApplicationContext());
//        callbackManager = CallbackManager.Factory.create();
//        btnLoginFace = (Button) findViewById(R.id.login_button);
//        btnLoginFace.setReadPermissions("email", "public_profile");
//        btnLoginFace.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                Log.d(TAG, "facebook:onSuccess:" + loginResult);
//                handleFacebookAccessToken(loginResult.getAccessToken());
//            }
//
//            @Override
//            public void onCancel() {
//                Log.d(TAG, "facebook:onCancel");
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//                Log.d(TAG, "facebook:onError", error);
//            }
//        });
//// ...
//        @Override
//        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//            super.onActivityResult(requestCode, resultCode, data);
//
//            // Pass the activity result back to the Facebook SDK
//            mCallbackManager.onActivityResult(requestCode, resultCode, data);
//        }

        /*ClickForgotPassword();*/
        nodeRoot = FirebaseDatabase.getInstance().getReference();
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            startActivity(new Intent(LoginActivity.this, MainMenuActivity.class));
        }
        // Thêm sự kiện listenerStateChange
        firebaseAuth.addAuthStateListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Xóa sự kiện ListenerStateChange
        firebaseAuth.removeAuthStateListener(this);
    }

    //Tạo client đăng nhập bằng google
    private void CreateClientLoginWithGoogle() {
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        //Tạo ra sign client
        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions)
                .build();
    }
    //end Tạo client đăng nhập bằng google

    //Đăng nhập vào tài khoản google
    private void LoginGoogle(GoogleApiClient apiClient) {
        //set code
        overUtils.CHECK_TYPE_PROVIDER_LOGIN = overUtils.CODE_PROVIDER_LOGIN_WITH_GOOGLE;
        Intent ILoginGoogle = Auth.GoogleSignInApi.getSignInIntent(apiClient);
        //Hiển thị client google để đăng nhập
        startActivityForResult(ILoginGoogle, overUtils.REQUEST_CODE_LOGIN_WITH_GOOGLE);
    }

    //end Đăng nhập vào tài khoản google
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Kiểm tra nếu resultcode trả về là của client Login with google
        if (requestCode == overUtils.REQUEST_CODE_LOGIN_WITH_GOOGLE) {
            if (resultCode == RESULT_OK) {

                GoogleSignInResult signInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                //Lấy ra account google được đăng nhập
                GoogleSignInAccount account = signInResult.getSignInAccount();
                //Lấy ra token của account google
                String tokenID = account.getIdToken();
                SharedPreferences.Editor editor = getApplicationContext()
                        .getSharedPreferences("MyPrefs", MODE_PRIVATE)
                        .edit();
                editor.putString("username", account.getDisplayName());
                editor.putString("useremail", account.getFamilyName());
                editor.putString("userAvatar", account.getPhotoUrl().toString());
                editor.apply();
                CheckLoginFirebase(tokenID);
            }

        }
    }

    //Lấy token id và đăng nhập vào firebase
    private void CheckLoginFirebase(String tokenID) {
        if (OverUtils.CHECK_TYPE_PROVIDER_LOGIN ==  OverUtils.CODE_PROVIDER_LOGIN_WITH_GOOGLE) {
            AuthCredential authCredential = GoogleAuthProvider.getCredential(tokenID, null);
            //SignIn to firebase
            firebaseAuth.signInWithCredential(authCredential);
        }
    }
    //end Lấy token id và đăng nhập vào firebase


    @Override
    public void onConnectionFailed(@android.support.annotation.NonNull ConnectionResult connectionResult) {

    }

    private void login() {
        String username = edt_username_login.getText().toString();
        String password = edt_password_login.getText().toString();

        if (username.trim().length() == 0 || password.trim().length() == 0) {
            overUtils.makeToast(getApplicationContext(),"Tài khoản mật khẩu không hợp lệ");
        } else {
            progressDialog.setMessage("Đang đăng nhập...");
            progressDialog.setIndeterminate(true);
            progressDialog.show();

            firebaseAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@android.support.annotation.NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        progressDialog.dismiss();
                        overUtils.makeToast(getApplicationContext(),overUtils.ERROR_MESSAGE_LOGIN);
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnImg_google_login:
//                signIn();
                LoginGoogle(apiClient);
                break;
            case R.id.btnImg_facebook_login:
                break;
            case R.id.btn_signUp:
                Intent iSignup = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(iSignup);
                break;
            case R.id.btn_login:
                login();
                break;
        }
    }

    @Override
    public void onAuthStateChanged(@android.support.annotation.NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            checkLogin(user.getUid());

            progressDialog.dismiss();
            overUtils.makeToast(getApplicationContext(),overUtils.LOGIN_successfully);
        } else {

        }
    }

    private void checkLogin(String UID) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@android.support.annotation.NonNull DataSnapshot dataSnapshot) {

                // Lưu lại mã user đăng nhập vào app
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(OverUtils.SHARE_UID, UID);


                //Load trang chủ
                Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
                startActivity(intent);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        nodeRoot.child("user").addListenerForSingleValueEvent(valueEventListener);
    }

    public void register(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }


}