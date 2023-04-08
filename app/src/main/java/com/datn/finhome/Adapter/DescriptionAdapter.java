package com.datn.finhome.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.datn.finhome.Models.ReviewModel;
import com.datn.finhome.Models.UserModel;
import com.datn.finhome.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class DescriptionAdapter extends RecyclerView.Adapter<DescriptionAdapter.ViewHolder> {
    Context context;
    List<ReviewModel> list;
    List<UserModel> listUser;
    FirebaseAuth firebaseAuth;

    public DescriptionAdapter(Context context, List<ReviewModel> list) {
        this.context = context;
        this.list = list;
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public DescriptionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reviews, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DescriptionAdapter.ViewHolder holder, int position) {
        ReviewModel description = list.get(position);
       String id = description.getIdRoom();
       String RoomId = description.getIdRoom();
//       String comment = description.getReviews();
       String uid = description.getIdUser();
        String time = description.getTime();


//       holder.tvDescription.setText(comment);
       loaduser(description,holder);
//        if (Objects.equals(userModel.getUserID(), description.getIdUser())){
//            holder.tvNameUser.setText(userModel.getName());
//            Glide.with(context).load(userModel.getAvatar()).into(holder.imgUser);
//        }

        holder.tvDescription.setText(description.getReviews());
        holder.tvTime.setText("Bình luận lúc: "+time);

    }

    private void deleteComment(ReviewModel description) {
        AlertDialog.Builder  builder =new AlertDialog.Builder(context);
        builder.setTitle("Delete comment")
                .setMessage("Bạn có muốn xóa binhb luận này không")
                .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Room");
                        reference.child(description.getIdRoom()).child("Reviews").child(description.getIdComment())
                                .removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(context, "xóa thành công", Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                }).
                setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private void loaduser(ReviewModel description, ViewHolder holder) {
        String uid = description.getIdUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = ""+snapshot.child("name").getValue();
                String img = ""+snapshot.child("avatar").getValue();
                holder.tvNameUser.setText(name);
//                Glide.with(context).load(img).into(holder.imgUser);
                Picasso.get().load(img).into(holder.imgUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvNameUser, tvDescription,tvTime;
        AppCompatImageView imgUser,imgdlt;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNameUser = itemView.findViewById(R.id.tvUserNameReview);
            tvDescription = itemView.findViewById(R.id.tvAddressRoom);
            tvTime = itemView.findViewById(R.id.tvTime);
            imgUser = itemView.findViewById(R.id.imgUserReview);
        }
    }
}
