
package com.datn.finhome.Views.Activity;

import android.os.Bundle;
import android.os.Debug;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.datn.finhome.R;

public class UserActivity extends AppCompatActivity {
AppCompatButton btnFavorite;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        btnFavorite = findViewById(R.id.btnFavourite);

        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(UserActivity.this, "ok", Toast.LENGTH_SHORT).show();

            }
        });
    }
}