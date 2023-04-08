package com.datn.finhome.Views.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.datn.finhome.Adapter.RoomHostAdapter;
import com.datn.finhome.Controllers.UserDao;
import com.datn.finhome.Interfaces.IAfterGetAllObject;
import com.datn.finhome.Models.RoomModel;
import com.datn.finhome.Models.UserModel;
import com.datn.finhome.R;
import com.datn.finhome.Views.Fragment.AccountViewFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HostActivity extends AppCompatActivity {
    private RecyclerView rv;
    private RoomHostAdapter roomAdapter;
    private List<RoomModel> roomModelList;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private String userId;

    AccountViewFragment AccountView;
    RoomModel roomModel;
    TextView  tvName,tvPhone,tvAdress;
    ImageView imgUser;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);
        tvName = findViewById(R.id.tvNameUser);
        tvPhone = findViewById(R.id.tvSdtUser);
        imgUser = findViewById(R.id.imgUser);
        tvAdress = findViewById(R.id.tvAddressUser);

        rv = findViewById(R.id.rvRoom);
        roomModel = new RoomModel();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        userId = firebaseUser.getUid();
        databaseReference.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel userModel = snapshot.getValue(UserModel.class);
                if(userModel != null){
                    String fullName = userModel.name;
                    String Phone = userModel.phoneNumber;
                    String avatar = userModel.avatar;
                    String adress = userModel.address;
                    tvName.setText(fullName);
                    tvPhone.setText(Phone);
                    tvAdress.setText(adress);
                    Picasso.get().load(avatar).placeholder(R.mipmap.ic_launcher).into(imgUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HostActivity.this, "That bai", Toast.LENGTH_SHORT).show();
            }
        });

        toolbar = findViewById(R.id.toobar_host);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        actionBar.setDisplayHomeAsUpEnabled(true);

        initRoom();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                onBackPressed();
                onBackPressed();
                return true;

            default:break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getMenuInflater().inflate(R.menu.menu_ctr, menu);
    }
    private void initRoom() {

        roomModelList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Room");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    RoomModel roomModel = dataSnapshot.getValue(RoomModel.class);
                    assert roomModel != null;
                    if (Objects.equals(roomModel.getUid(),userId)) {
                        roomModelList.add(roomModel);
                        roomAdapter = new RoomHostAdapter(HostActivity.this, roomModelList, roomModel1 -> {
                            onClickGoToDetail(roomModel1);
                        });
                        roomAdapter.notifyDataSetChanged();
                        rv.setAdapter(roomAdapter);
                        rv.setHasFixedSize(true);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TAG", "onCancelled: " + error.getMessage());
            }
        });
    }

    private  void onClickGoToDetail(RoomModel roomModel){
        Intent intent = new Intent(this, ShowDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("Room", roomModel);
        intent.putExtras(bundle);
        startActivity(intent);
    }


}
