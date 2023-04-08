package com.datn.finhome.Views.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.datn.finhome.Adapter.MessageAdapter;
import com.datn.finhome.Models.Chat;
import com.datn.finhome.Models.UserModel;
import com.datn.finhome.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    private AppBarLayout appBarLayout;
    private Toolbar toobar;
    private CircleImageView circleImage;
    private TextView txtUsername;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private Intent intent;
    private RecyclerView rvSend;
    private RelativeLayout bottom;
    private EditText textSend;
    private ImageButton btnSend;
    private ImageButton btnImage;
    private List<Chat> list;
    private MessageAdapter adapter;
    private ValueEventListener seenListener;
    private String userId;
    public static final String KEY = "id";
    public static final String KEY_URL = "KEY_URL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        appBarLayout = findViewById(R.id.appBarLayout);
        toobar = findViewById(R.id.toobar);
        circleImage = findViewById(R.id.circleImage);
        txtUsername = findViewById(R.id.txtUsername);
        rvSend = findViewById(R.id.rv_send);
        bottom = findViewById(R.id.bottom);
        textSend = findViewById(R.id.text_send);
        btnSend = findViewById(R.id.btn_send);
        btnImage = findViewById(R.id.btn_image);
        setSupportActionBar(toobar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toobar.setNavigationOnClickListener(view -> {
            finish();
        });

        rvSend.setHasFixedSize(true);
        intent = getIntent();
        userId = intent.getStringExtra("id");
        userId = intent.getStringExtra(KEY);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();



        btnSend.setOnClickListener(view -> {
            String msg = textSend.getText().toString();
            if (msg.trim().length() == 0) {
                Toast.makeText(MessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
            } else {
                sendMessage(firebaseUser.getUid(), userId, msg);
            }

            textSend.setText("");
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                UserModel user = snapshot.getValue(UserModel.class);
                txtUsername.setText(user.getName());
                Glide.with(MessageActivity.this).load(user.getAvatar()).into(circleImage);
                readMessage(firebaseUser.getUid(), userId,user.getAvatar());
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        seenMessage(userId);
    }

    private void seenMessage(String userid) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid)) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        snapshot.getRef().updateChildren(hashMap);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void sendMessage(String sender, String receiver, String message) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference refKey = reference.push();

        String key = refKey.getKey();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", key);
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isseen", false);
        hashMap.put("checkMess", true);
        reference.child("Chats").push().setValue(hashMap);

        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(firebaseUser.getUid())
                .child(userId);
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef.child("id").setValue(userId);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void readMessage(String myId, String userId,String imageUrl) {
        list = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(myId) && chat.getSender().equals(userId) || chat.getReceiver().equals(userId) && chat.getSender().equals(myId)) {
                        list.add(chat);
                    }
                    adapter = new MessageAdapter(MessageActivity.this, list,imageUrl);
                    rvSend.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }


//    @Override
//    protected void onPause() {
//        super.onPause();
//        databaseReference.removeEventListener(seenListener);
//    }
}