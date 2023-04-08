package com.datn.finhome.Views.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.datn.finhome.R;

public class HistoryActivity extends AppCompatActivity {

   TextView tvNameRoom,tvPrice,tvAddressRoom;
    Button btnBack,btnFavorite;
    String title,address,price,favorite;
    SharedPreferences preferences = getSharedPreferences("MyViewHistory", MODE_PRIVATE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_viewhistory);
       btnBack =findViewById(R.id.btnBack);
       btnFavorite= findViewById(R.id.btnFavorite);
       tvNameRoom = findViewById(R.id.tvNameRoom);
       tvAddressRoom=findViewById(R.id.tvAddressRoom);
       tvPrice=findViewById(R.id.tvPrice);
        title = preferences.getString("title","");
        address =preferences.getString("address","");
        favorite =preferences.getString("favorite","");
        price = preferences.getString("price","");

       getHistory();
    }
 void getHistory(){
     tvNameRoom.setText(title);
     tvPrice.setText(price);
     tvAddressRoom.setText(address);
    }
}
