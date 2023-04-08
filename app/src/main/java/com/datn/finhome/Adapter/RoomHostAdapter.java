package com.datn.finhome.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.finhome.Interfaces.IClickItemUserListener;
import com.datn.finhome.Models.ReviewModel;
import com.datn.finhome.Models.RoomModel;
import com.datn.finhome.Models.UserModel;
import com.datn.finhome.R;
import com.datn.finhome.Views.Activity.EditRoomActivity;
import com.datn.finhome.Views.Activity.HostActivity;
import com.datn.finhome.Views.Activity.ThanhToanActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class RoomHostAdapter extends RecyclerView.Adapter<RoomHostAdapter.ViewHolder> {
    private  Context context;
    private List<RoomModel> roomModelList;
    private IClickItemUserListener iClickItemUserListener;

    public RoomHostAdapter(Context context, List<RoomModel> roomModelList,  IClickItemUserListener listener) {
        this.context = context;
        this.roomModelList = roomModelList;
        this.iClickItemUserListener = listener;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RoomModel roomModel = roomModelList.get(position);
        if (roomModel == null) {
            return;
        }
        holder.tvName.setText(roomModel.getName());
        Locale locale = new Locale("vi", "VN");
        NumberFormat currencyFormat = NumberFormat.getNumberInstance(locale);
        if (roomModel.getPrice() != null){

            holder.tvPrice.setText(currencyFormat.format(Integer.parseInt (roomModel.getPrice())) + " VNĐ/Phòng");
        }
        holder.tvAddress.setText(roomModel.getAddress());
        Picasso.get().load(roomModel.getImg()).placeholder(R.mipmap.ic_launcher).into(holder.imgRoom);
        holder.tvDayTin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialog(roomModel);
            }
        });

        if (roomModel.isBrowser() == false){
            holder.tvTrangThai.setText("Phòng đang chờ duyệt");
            holder.tvTrangThai.setTextColor(Color.RED);
        }else {
            holder.tvTrangThai.setText("Phòng đã được duyệt");
            holder.tvTrangThai.setTextColor(Color.GREEN);

        }



    }



    private void deleteRoom(RoomModel roomModel) {
        AlertDialog.Builder  builder =new AlertDialog.Builder(context);
        builder.setTitle("Delete Room")
                .setMessage("Bạn chắc chắn muốn xóa phòng này chứ")
                .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Room");
                        reference.child(roomModel.getId())
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
                })
                .show();
    }

    private void ShowDialog(RoomModel roomModel) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);
        Button buttonThanhToan = dialog.findViewById(R.id.btnThanhToan);

        buttonThanhToan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ThanhToanActivity.class);
                intent.putExtra("RoomId", roomModel.getId());
                intent.putExtra("title", roomModel.getName());
                context.startActivity(intent);
                dialog.dismiss();
            }
        });


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }


    @Override
    public int getItemCount() {
        return roomModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener{
        private LinearLayout container;
        private AppCompatImageView imgRoom;
        private TextView tvName, tvPrice, tvAddress, tvDayTin, tvTrangThai;
        private AppCompatImageButton menu_ctr;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgRoom = itemView.findViewById(R.id.imgRoom);
            tvAddress = itemView.findViewById(R.id.tvAddressRoom);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvName = itemView.findViewById(R.id.tvNameRoom);
            tvDayTin = itemView.findViewById(R.id.tvDayTin);
            tvTrangThai = itemView.findViewById(R.id.tvTrangThai);
            menu_ctr = itemView.findViewById(R.id.menu_ctr);
            container = itemView.findViewById(R.id.containerRoom);

            menu_ctr.setOnClickListener(v -> showMenu(v));
        }

        private void showMenu(View v) {
            PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
            popupMenu.inflate(R.menu.menu_ctr);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            RoomModel roomModel = roomModelList.get(getAdapterPosition());
            switch (item.getItemId()) {
                case R.id.id_edit:
                    Intent intent = new Intent(context, EditRoomActivity.class);
                    intent.putExtra("RoomId", roomModel.getId());
                    context.startActivity(intent);
                    return true;
                case R.id.id_delete:
                    deleteRoom(roomModel);
                    notifyDataSetChanged();
                    return true;
                default:
                    return false;
            }
        }
    }
}
