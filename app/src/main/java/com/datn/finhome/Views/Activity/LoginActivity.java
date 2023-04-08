package com.datn.finhome.Views.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.datn.finhome.Base.BaseActivity;
import com.datn.finhome.R;
import com.datn.finhome.Utils.OverUtils;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
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
import com.google.firebase.auth.FacebookAuthProvider;
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
    SignInButton btnLoginWithGoogle;
    LoginButton btnLoginWithFacebook;
    private CallbackManager callbackManager;
    GoogleApiClient apiClient;
    FirebaseAuth firebaseAuth;
    Button btn_signUp;
    Button btn_login;
    EditText edt_username_login;
    EditText edt_password_login;
    TextView tvForgot,tvDk;
    ImageView btnCheckPass;
    ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;
    DatabaseReference nodeRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        nodeRoot = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(this);
    }


    private void LoginFacebook() {
        btnLoginWithFacebook.setReadPermissions("email", "public_profile");
        btnLoginWithFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(overUtils.TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(overUtils.TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(overUtils.TAG, "facebook:onError", error);
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();

                }
            }
        });
    }

    private void CreateClientLoginWithGoogle() {
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions)
                .build();
    }

    private void LoginGoogle(GoogleApiClient apiClient) {
        //set code
        overUtils.CHECK_TYPE_PROVIDER_LOGIN = overUtils.CODE_PROVIDER_LOGIN_WITH_GOOGLE;
        Intent ILoginGoogle = Auth.GoogleSignInApi.getSignInIntent(apiClient);
        startActivityForResult(ILoginGoogle, overUtils.REQUEST_CODE_LOGIN_WITH_GOOGLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == overUtils.REQUEST_CODE_LOGIN_WITH_GOOGLE) {
            if (resultCode == RESULT_OK) {
                GoogleSignInResult signInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                GoogleSignInAccount account = signInResult.getSignInAccount();
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

    private void CheckLoginFirebase(String tokenID) {
        if (OverUtils.CHECK_TYPE_PROVIDER_LOGIN == OverUtils.CODE_PROVIDER_LOGIN_WITH_GOOGLE) {
            AuthCredential authCredential = GoogleAuthProvider.getCredential(tokenID, null);
            firebaseAuth.signInWithCredential(authCredential);
        }
    }

    @Override
    public void onConnectionFailed(@android.support.annotation.NonNull ConnectionResult connectionResult) {

    }

    private void login() {
        String username = edt_username_login.getText().toString();
        String password = edt_password_login.getText().toString();

        if (username.trim().length() == 0 || password.trim().length() == 0) {
            overUtils.makeToast(getApplicationContext(), overUtils.VALIDATE_TK_MK);
        } else {
            progressDialog.setMessage("Đang đăng nhập...");
            progressDialog.setIndeterminate(true);
            progressDialog.show();
            SharedPreferences.Editor editor = getApplicationContext()
                    .getSharedPreferences("MyPrefs", MODE_PRIVATE)
                    .edit();
            editor.putString("pass", password);
            editor.commit();
            Log.d("asssss", password);

                firebaseAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@android.support.annotation.NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (firebaseAuth.getCurrentUser().isEmailVerified()){
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                if (user != null) {
                                    checkLogin(user.getUid());
                                    progressDialog.dismiss();
                                    overUtils.makeToast(getApplicationContext(), overUtils.LOGIN_successfully);
                                }
                            }else {
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Vui lòng xác thực email của bạn", Toast.LENGTH_SHORT).show();
                            }

                        }else {
                            progressDialog.dismiss();
                            overUtils.makeToast(getApplicationContext(), overUtils.ERROR_MESSAGE_LOGIN);
                        }
                    }
                });
        }
    }

    @Override
    public void onAuthStateChanged(@android.support.annotation.NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null && user.isEmailVerified()) {
                    checkLogin(user.getUid());
                    progressDialog.dismiss();
                    overUtils.makeToast(getApplicationContext(), overUtils.LOGIN_successfully);
                }
    }

    private void checkLogin(String UID) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@android.support.annotation.NonNull DataSnapshot dataSnapshot) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(OverUtils.SHARE_UID, UID);

                Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
                startActivity(intent);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        nodeRoot.child("Users").addListenerForSingleValueEvent(valueEventListener);
    }


    private void initView() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        sharedPreferences = getSharedPreferences(OverUtils.PREFS_DATA_NAME, MODE_PRIVATE);

        btnLoginWithGoogle = findViewById(R.id.btnImg_google_login);
        btnLoginWithFacebook = findViewById(R.id.login_button);

        btn_signUp = findViewById(R.id.btn_signUp);
        btn_login = findViewById(R.id.btn_login);
        FacebookSdk.sdkInitialize(getApplicationContext());
        FacebookSdk.setAutoInitEnabled(true);
        FacebookSdk.fullyInitialize();
        callbackManager = CallbackManager.Factory.create();
        tvForgot = findViewById(R.id.tvForgot);
        tvForgot.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this,ForgotPassActivity.class);
            startActivity(intent);
        });

        edt_username_login = findViewById(R.id.edt_username_login);
        edt_password_login = findViewById(R.id.edt_password_login);
        tvDk = findViewById(R.id.tvDk);
        tvDk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                   startActivity(intent);
            }
        });

        btnCheckPass = findViewById(R.id.checkPass);

        progressDialog = new ProgressDialog(LoginActivity.this, R.style.MyProgessDialogStyle);
        btn_login.setOnClickListener(this);
//        btn_signUp.setOnClickListener(this);
        btnCheckPass.setOnClickListener(this);
        btnLoginWithFacebook.setOnClickListener(this);
        btnLoginWithGoogle.setOnClickListener(this);
        CreateClientLoginWithGoogle();
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnImg_google_login:
                LoginGoogle(apiClient);
                break;
            case R.id.login_button:
                LoginFacebook();
                break;
            case R.id.checkPass:
                setBtnCheckPass(v);
                break;
            case R.id.btn_login:
                login();
                break;
        }
    }


//    public void register(View view) {
//        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
//        startActivity(intent);
//    }

    private void setBtnCheckPass(View view) {
        if(edt_password_login.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
            ((ImageView)(view)).setImageResource(R.drawable.ic_visibility_off);
            edt_password_login.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }
        else{
            ((ImageView)(view)).setImageResource(R.drawable.ic_visibility);
            edt_password_login.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }
}