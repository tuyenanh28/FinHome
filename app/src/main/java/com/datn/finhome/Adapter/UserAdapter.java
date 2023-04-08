package com.datn.finhome.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.datn.finhome.Interfaces.OnEventShowUser;
import com.datn.finhome.Models.Chat;
import com.datn.finhome.Models.UserCategory;
import com.datn.finhome.Models.UserModel;
import com.datn.finhome.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private Context context;
    private List<UserModel> list;
    String theLastMessage;
    private OnEventShowUser onEventShowUser;

    public UserAdapter(Context context, List<UserModel> list,OnEventShowUser onEventShowUser) {
        this.context = context;
        this.list = list;
        this.onEventShowUser = onEventShowUser;
    }
//    public void setList(List<UserModel> list){
//        this.list=list;
//        this.notifyDataSetChanged();
//    }


    @NonNull
    @NotNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        UserViewHolder userViewHolder = new UserViewHolder(view);
        return userViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull UserAdapter.UserViewHolder holder, int position) {
        UserModel user = list.get(position);
        String upperString = user.getName().substring(0, 1).toUpperCase() + user.getName().substring(1).toLowerCase();
        holder.txtFullname.setText(upperString);
        Glide.with(context).load(user.getAvatar()).into(holder.profilePicc);
        lastMessage(user.getUserID(),holder.lastMessage);
        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onEventShowUser.onClickShowUser(user);
            }
        });
    }
    @Override
    public int getItemCount() {
        if (list != null)
            return list.size();
        else
            return 0;
    }


    public class UserViewHolder extends RecyclerView.ViewHolder {
        private CardView rootLayout;
        private CircleImageView profilePicc;
        private TextView txtFullname;
        private TextView lastMessage;
        private TextView unseenMassage;

        public UserViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            rootLayout = itemView.findViewById(R.id.rootLayout);
            profilePicc = itemView.findViewById(R.id.profilePicc);
            txtFullname = itemView.findViewById(R.id.txtFullname);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            unseenMassage = itemView.findViewById(R.id.unseenMassage);
        }
    }
    private void lastMessage(final  String userId,final TextView lastMessage){
        theLastMessage="default";
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if( chat.getReceiver().equals(firebaseUser.getUid()) &&
                            chat.getSender().equals(userId) ||
                            chat.getReceiver().equals(userId) &&
                                    chat.getSender().equals(firebaseUser.getUid())){
                        theLastMessage = chat.getMessage();
                    }
                }

                switch (theLastMessage){
                    case "default":
                        lastMessage.setText("No message");
                        break;
                    default:
                        lastMessage.setText(theLastMessage);
                        break;
                }
                theLastMessage="default";
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}
