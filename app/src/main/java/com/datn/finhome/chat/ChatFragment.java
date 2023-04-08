package com.datn.finhome.chat;

import android.content.Intent;

import com.datn.finhome.Interfaces.OnEventShowUser;
import com.datn.finhome.Models.Image;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.datn.finhome.Adapter.UserAdapter;
import com.datn.finhome.Models.ChatList;
import com.datn.finhome.Models.UserCategory;
import com.datn.finhome.Models.UserModel;
import com.datn.finhome.Views.Activity.MessageActivity;
import com.datn.finhome.databinding.FragmentChatBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatFragment extends Fragment {

    private ChatViewModel chatViewModel;
    private FragmentChatBinding binding;
    private UserAdapter adapter;
    private List<UserCategory> mUser;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private List<ChatList> userList;
    private List<Image> mListImage;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        chatViewModel =
                new ViewModelProvider(this).get(ChatViewModel.class);

        binding = FragmentChatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        chatViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userList = new ArrayList<>();
        mListImage = new ArrayList<>();
//        adapter = new UserAdapter(getContext(), mUser, new OnEventShowUser() {
//            @Override
//            public void onClickShowUser(UserCategory user) {
//                Intent intent = new Intent(getContext(), MessageActivity.class);
//                intent.putExtra(MessageActivity.KEY, user.getUser().getUserID());
////                intent.putExtra(MessageActivity.KEY_URL, user.getImages().get(0).getUrl());
//                getActivity().startActivity(intent);
//            }
//        });

        binding.rvChat.setAdapter(adapter);

        reference = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatList chatList = snapshot.getValue(ChatList.class);
                    userList.add(chatList);
                }
            readChats();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        binding.edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (i == 0 && i2 == 0) {

                } else {
//                    searchUser(charSequence.toString().toLowerCase());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return root;
    }

    private void readChats() {
        mUser = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                mUser.clear();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    UserModel user = snapshot.getValue(UserModel.class);
//                    mUser.add(new UserCategory(user));
//                    adapter.setList(mUser);
//                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

//    private void searchUser(String s) {
//        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("search")
//                .startAt(s).endAt(s + "\uf8ff");
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
//                mUser.clear();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    UserModel user = snapshot.getValue(UserModel.class);
//                    assert firebaseUser != null;
//                    assert user != null;
//                    if (!user.getUserID().equals(firebaseUser.getUid())) {
//                        reference.child(user.getUserID()).child("images").addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot snapshot) {
//                                mListImage = new ArrayList<>();
//                                for (DataSnapshot dsChild : snapshot.getChildren()) {
//                                    HashMap<String,Object> map = (HashMap<String, Object>) dsChild.getValue();
//                                    String idImg = (String) map.get("id");
//                                    String urlImg = (String) map.get("imageURL");
//                                    mListImage.add(new Image(idImg, urlImg));
//                                }
//                                mUser.add(new UserCategory(user, mListImage));
//                                adapter.setList(mUser);
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull @NotNull DatabaseError error) {
//
//                            }
//                        });
//                    }
//                }
//                adapter.setList(mUser);
//            }
//
//            @Override
//            public void onCancelled(@NonNull @NotNull DatabaseError error) {
//
//            }
//        });
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}