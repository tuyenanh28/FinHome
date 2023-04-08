package com.datn.finhome.Views.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.datn.finhome.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.intellij.lang.annotations.Pattern;

public class ForgotPassActivity extends AppCompatActivity {
    EditText edtForgot;
    Button btnForgot;
    ProgressBar progressBar;

    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);
        edtForgot = findViewById(R.id.edtForgot_Email);
        btnForgot = findViewById(R.id.btn_ForgorPass);
        firebaseAuth = FirebaseAuth.getInstance();
        btnForgot.setOnClickListener(v -> resetPass());
    }

    private void resetPass() {
        String Email = edtForgot.getText().toString().trim();
        if (Email.isEmpty()){
            Toast.makeText(this, "Nhâp email của bạn", Toast.LENGTH_SHORT).show();
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()){
            edtForgot.setError("vui lòng nhập email!");
            edtForgot.requestFocus();
            return;
        }
        firebaseAuth.sendPasswordResetEmail(Email).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
//                        if(firebaseAuth.getCurrentUser().isEmailVerified()) {
                        Toast.makeText(ForgotPassActivity.this, "Vui lòng kiểm tra emali của bạn!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ForgotPassActivity.this,LoginActivity.class);
                        startActivity(intent);
//                        }else {
//                            Toast.makeText(ForgotPassActivity.this, "Vui lòng Xác thực email của bạn", Toast.LENGTH_SHORT).show();
//                        }
            }else {
                Toast.makeText(ForgotPassActivity.this, "Email không tồn tại", Toast.LENGTH_SHORT).show();
            }
        });
    }
}