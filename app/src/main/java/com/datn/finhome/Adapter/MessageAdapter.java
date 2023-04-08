package com.datn.finhome.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.datn.finhome.Models.Chat;
import com.datn.finhome.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;



import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    public static final String KEY_GET_USER = "GET_USER";
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private Context context;
    private List<Chat> list;
    private String imageUrl;

    FirebaseUser firebaseUser;

    public MessageAdapter(Context context, List<Chat> list, String imageUrl) {
        this.context = context;
        this.list = list;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @NotNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_right, parent, false);
            return new MessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_left, parent, false);
            return new MessageViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MessageAdapter.MessageViewHolder holder, int position) {
         Chat chat = list.get(position);
         holder.txtShowMessage.setText(chat.getMessage());
        //check for last message
         holder.item_chat.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final CharSequence[] items = {"Xóa", "Xem trang cá nhân"};

                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Chọn phương thức");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if(item==0){
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                            Chat chat1=list.get(holder.getAdapterPosition());
                            Query applesQuery = ref.child("Chats").orderByChild("id").equalTo(chat1.getId());
                            applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                                        snapshot.getRef().removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        }else if(item==1){

                        }
                    }
                });
                builder.show();
                return true;
            }
        });

        if(position==list.size()-1){
            if(chat.isIsSeen()){
                holder.txtSeen.setText("Đã xem");
            }else {
                holder.txtSeen.setText("Đã gửi");
            }
        }else {
            holder.txtSeen.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (list != null)
            return list.size();
        else
            return 0;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView circleImage;
        private TextView txtShowMessage;
        private TextView txtSeen;
        private RelativeLayout item_chat;
        public MessageViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            circleImage = itemView.findViewById(R.id.circleImage);
            txtShowMessage = itemView.findViewById(R.id.txtShowMessage);
            txtSeen=itemView.findViewById(R.id.txtSeen);
            item_chat=itemView.findViewById(R.id.item_chat);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if(list.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else {
            return MSG_TYPE_LEFT;
        }
    }
}
