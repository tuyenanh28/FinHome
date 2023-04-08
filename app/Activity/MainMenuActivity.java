package com.datn.finhome.Views.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.datn.finhome.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainMenuActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;
    FrameLayout fragmentContainer;

    MainActivity HomeView;
    accountView AccountView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        initControl();
        //Chạy lần đầu tiên sẽ load vào màn hình main
        HomeView = new MainActivity();
        setFragment(HomeView);

    }

    //Khởi tạo control
    private void initControl(){
        fragmentContainer = findViewById(R.id.fragment_container);
        bottomNavigation = findViewById(R.id.bottom_navigation);

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();

                switch (id) {
                    case R.id.nav_home:
                        //Chuyển sang màn hình home
                        //HomeView = new MainActivity();
                        setFragment(HomeView);
                        return true;
                    case R.id.nav_account:
                        //Chuyển sang màn hình quản lý tài khoản
                        AccountView = new accountView();
                        setFragment(AccountView);
                        return true;

                    default:
                        return false;
                }
            }
        });
    }

    //Hàm replace fragment tương ứng khi chọn vào menu
    private void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();
    }
}