package com.datn.finhome.chat;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.datn.finhome.Adapter.UserAdapter;
import com.datn.finhome.Interfaces.OnEventShowUser;
import com.datn.finhome.Models.Chat;
import com.datn.finhome.Models.UserModel;
import com.datn.finhome.R;
import com.datn.finhome.Views.Activity.MessageActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class Chat2Fragment extends Fragment {
    private RecyclerView rvChat;
    private UserAdapter adapter;
    private List<UserModel> list;
    UserModel userModel3;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    private List<String> listUser;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view  = inflater.inflate(R.layout.fragment_chat2, container, false);
        rvChat = view.findViewById(R.id.rvChat2);
        rvChat.setHasFixedSize(true);
        rvChat.setLayoutManager(new LinearLayoutManager(getContext()));
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        listUser = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listUser.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Chat  chat = dataSnapshot.getValue(Chat.class);
                    if (chat.getSender() != null && chat.getSender().equals(firebaseUser.getUid())){
                        listUser.add(chat.getReceiver());
                    }
                    if(chat.getReceiver() != null && chat.getReceiver().equals(firebaseUser.getUid())){
                        listUser.add(chat.getSender());
                    }
                }
                readChat();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }

    private void readChat() {
        list = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UserModel user = dataSnapshot.getValue(UserModel.class);
                    ListIterator<UserModel> listIteratorUser = list.listIterator();
                    for (String id : listUser) {
                        Log.d("",""+id);
                        if(user.getUserID() != null && user.getUserID().equals(id)){
                            if (list.size() != 0) {
                                while(listIteratorUser.hasNext()){
                                    UserModel user1 = listIteratorUser.next();
                                    if (!user.getUserID().equals(user1.getUserID())){
                                        listIteratorUser.add(user);
                                    }
                                }


                            }else {
                                listIteratorUser.add(user);

                            }

                        }
                    }

                }

                adapter = new UserAdapter(getContext(),list, new OnEventShowUser() {
                    @Override
                    public void onClickShowUser(UserModel user) {
                        Intent intent = new Intent(getContext(), MessageActivity.class);
                        intent.putExtra(MessageActivity.KEY, user.getUserID());
//                intent.putExtra(MessageActivity.KEY_URL, user.getImages().get(0).getUrl());
                        getActivity().startActivity(intent);
                    }
                });
                rvChat.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}