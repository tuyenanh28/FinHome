package com.datn.finhome.Views.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.datn.finhome.Adapter.AdapterFavorite;
import com.datn.finhome.Models.RoomModel;
import com.datn.finhome.R;
import com.datn.finhome.databinding.ActivityFavoriteBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FavoriteActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    private ActivityFavoriteBinding binding;
    private ArrayList<RoomModel> roomModels;
    private AdapterFavorite adapterFavorite;
    RecyclerView rvRoomFv;

    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoriteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();
        rvRoomFv = binding.rcvFavorite;

        loadFavorite();

        toolbar = binding.toobarFavorite;
        toolbar = findViewById(R.id.toobar_favorite);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Yêu thích");
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadFavorite() {
        roomModels = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("favorites")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                       roomModels.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            String roomId = "" + dataSnapshot.child("roomId").getValue();

                            RoomModel roomModel = new RoomModel();
                            roomModel.setId(roomId);
                            roomModels.add(roomModel);
                        }
                        adapterFavorite = new AdapterFavorite(FavoriteActivity.this, roomModels);
                        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(FavoriteActivity.this,DividerItemDecoration.VERTICAL);
                        rvRoomFv.addItemDecoration(decoration);
                        rvRoomFv.setAdapter(adapterFavorite);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("", error.getMessage());
                    }
                });
    }
}