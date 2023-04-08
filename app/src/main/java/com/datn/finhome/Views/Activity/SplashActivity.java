
package com.datn.finhome.Views.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.datn.finhome.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {
    int SPLASH_TIME_OUT =3000;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        firebaseAuth = FirebaseAuth.getInstance();

    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            startActivity(new Intent(SplashActivity.this, MainMenuActivity.class));
        }else {
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
                startActivity(intent);

                finish();
            },SPLASH_TIME_OUT);
        }
    }
}