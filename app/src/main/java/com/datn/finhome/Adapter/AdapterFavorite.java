package com.datn.finhome.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.datn.finhome.Models.RoomModel;
import com.datn.finhome.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterFavorite extends RecyclerView.Adapter<AdapterFavorite.HolderRoomFavorite> {
    private Context  context;
    private ArrayList<RoomModel> roomModels;

    public AdapterFavorite(Context context, ArrayList<RoomModel> roomModels) {
        this.context = context;
        this.roomModels = roomModels;
    }

    @NonNull
    @Override
    public HolderRoomFavorite onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room_favorite, parent, false);
        return new HolderRoomFavorite(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderRoomFavorite holder, int position) {
      RoomModel roomModel = roomModels.get(position);

      loadRoom(roomModel,holder);
        holder.btnFavorite.setOnClickListener(v -> {
            //add favorite | xóa favorite
            removeFavorite(context,roomModel.getId());
            notifyDataSetChanged();
        });
    }

    private static void removeFavorite(Context context,String roomId){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null){
            Toast.makeText(context, "vui long dang nhap", Toast.LENGTH_SHORT).show();
        }else {

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(firebaseAuth.getUid()).child("favorites").child(roomId).removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(context, "Đã xóa khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "That bai", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

    private void loadRoom(RoomModel roomModel, HolderRoomFavorite holder) {
        String roomId = roomModel.getId();
        Log.d("aaa","id:"+roomId);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Room");
        ref.child(roomId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String roomTitle = ""+snapshot.child("name").getValue();
                        String roomAdr = ""+snapshot.child("address").getValue();
                        String roomPrice = ""+snapshot.child("price").getValue();
                        String roomImg = ""+snapshot.child("img").getValue();
                        String uid = ""+snapshot.child("uid").getValue();


//                        roomModel.setFavorite(true);
                        roomModel.setTitle(roomAdr);
                        roomModel.setName(roomTitle);
                        roomModel.setImg(roomImg);
                        roomModel.setUid(uid);
//
                        holder.tvName.setText(roomTitle);
                        holder.tvPrice.setText(roomPrice);
                        holder.tvAddress.setText(roomAdr);
                        Picasso.get().load(roomImg).into(holder.imgRoom);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("", error.getMessage());
                    }
                });
    }

    @Override
    public int getItemCount() {
        return roomModels.size();
//        Log.d("aa","lisst"+roomModels.size());
    }

    class HolderRoomFavorite extends RecyclerView.ViewHolder{
        private ConstraintLayout container;
        private AppCompatImageView imgRoom;
        private TextView tvName, tvPrice, tvAddress;
        private AppCompatCheckBox btnFavorite;
        public HolderRoomFavorite(@NonNull View itemView) {
            super(itemView);
            imgRoom = itemView.findViewById(R.id.imgRoom);
            tvAddress = itemView.findViewById(R.id.tvAddressRoom);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvName = itemView.findViewById(R.id.tvNameRoom);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
//            container = itemView.findViewById(R.id.containerRoom);
        }
    }
}
