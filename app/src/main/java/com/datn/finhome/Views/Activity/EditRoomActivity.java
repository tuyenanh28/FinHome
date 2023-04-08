package com.datn.finhome.Views.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.datn.finhome.Models.RoomModel;
import com.datn.finhome.Models.UserModel;
import com.datn.finhome.R;
import com.datn.finhome.Utils.OverUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class EditRoomActivity extends AppCompatActivity {
    EditText edTitle, edLocation, edSizeRoom, edPrice, edDescription;
    AppCompatImageButton btnBack;
    AppCompatButton btnPost2;
    RecyclerView recyclerImage;
    ImageView imageView;
    String RoomId;

    Toolbar toolbar;
    StorageReference storageReference;
    FirebaseStorage firebaseStorage;
    FirebaseAuth firebaseAuth;
    private DatabaseReference reference;
    private Uri imgUri;
    String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_room);
        initView();
        setRoom();

    }
    private void initView() {
        edTitle = findViewById(R.id.edit_title);
        edLocation =findViewById(R.id.edit_location);
        imageView = findViewById(R.id.dgAdd_add);
        edSizeRoom = findViewById(R.id.edit_size_room);
        edPrice = findViewById(R.id.edit_price);
        edDescription = findViewById(R.id.edit_description);
        recyclerImage = findViewById(R.id.recyclerImage);
        btnBack = findViewById(R.id.btnBack);
        btnPost2 = findViewById(R.id.btnPost2);
        Intent intent = getIntent();
        RoomId = intent.getStringExtra("RoomId");

        btnPost2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    onClickUpdateRoom();
            }
        });
    }

    private void setRoom(){
        reference = FirebaseDatabase.getInstance().getReference("Room");
        reference.child(RoomId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                RoomModel roomModel = snapshot.getValue(RoomModel.class);
                if(roomModel != null){
                    String fullName = roomModel.getName();
                    String DiaChi = roomModel.getAddress();
                    String Mota = roomModel.getDescription();
                    String img = roomModel.getImg();
                    String Size = roomModel.getSizeRoom();
                    String Price = roomModel.getPrice();

                    edTitle.setText(fullName);
                    edSizeRoom.setText(Size);
                    Glide.with(getApplicationContext()).load(img).into(imageView);
                    edPrice.setText(Price);
                    edDescription.setText(Mota);
                    edLocation.setText(DiaChi);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditRoomActivity.this, "Show", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onClickUpdateRoom() {
        reference = FirebaseDatabase.getInstance().getReference("Room");
        String newName = edTitle.getText().toString().trim();
        String newAdress = edLocation.getText().toString().trim();
        String newSize = edSizeRoom.getText().toString().trim();
        String newMota = edDescription.getText().toString().trim();
        String newPrice = edPrice.getText().toString().trim();

        if(newName.isEmpty() || newAdress.isEmpty() || newMota.isEmpty()){
            Toast.makeText(EditRoomActivity.this,"Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
        } else {
            reference.child(RoomId).child("name").setValue(newName);
            reference.child(RoomId).child("address").setValue(newAdress);
            reference.child(RoomId).child("sizeRoom").setValue(newSize);
            reference.child(RoomId).child("description").setValue(newMota);
            reference.child(RoomId).child("price").setValue(newPrice);
//            uploadToFirebase(imageUri);
            Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }

    }
}