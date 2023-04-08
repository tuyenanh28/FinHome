package com.datn.finhome.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.datn.finhome.Interfaces.IClickItemUserListener;
import com.datn.finhome.Models.RoomModel;
import com.datn.finhome.R;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewholder> implements Filterable {
    Context context;
    private List<RoomModel> listFull;
    private List<RoomModel> list;
    private IClickItemUserListener iClickItemUserListener;

    public RoomAdapter(Context context, List<RoomModel> list, IClickItemUserListener listener) {
        this.context = context;
        this.list = list;
        this.iClickItemUserListener = listener;
        listFull = new ArrayList<>(list);
    }



    @NonNull
    @Override
    public RoomViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room, parent, false);
        RoomAdapter.RoomViewholder roomViewholder = new RoomAdapter.RoomViewholder(view);
        return roomViewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewholder holder, int position) {
        RoomModel roomModel = list.get(position);
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
        Picasso.get().load(roomModel.getImg()).into(holder.imgRoom);
        holder.container.setOnClickListener(v -> {
            iClickItemUserListener.onClickItemRoom(roomModel);
        });
    }

    @Override
    public int getItemCount() {
        if (list != null)
            return list.size();
        else
            return 0;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<RoomModel> filterlist =new ArrayList<>();
            String pattrn = constraint.toString();
            if(pattrn == null || pattrn.isEmpty()){
                filterlist.addAll(listFull);
            }
            else{
                for(RoomModel roomModel : listFull){
                    if(roomModel.getAddress().toLowerCase().contains(pattrn.toLowerCase())){
                        filterlist.add(roomModel);
                    }
                }
                list = filterlist;
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values= filterlist;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list = (List<RoomModel>) results.values;
            notifyDataSetChanged();
        }
    };


    public class RoomViewholder extends RecyclerView.ViewHolder {
        private LinearLayout container;
        private AppCompatImageView imgRoom;
        private TextView tvName, tvPrice, tvAddress;
        public RoomViewholder(@NonNull View itemView) {
            super(itemView);
            imgRoom = itemView.findViewById(R.id.imgRoom);
            tvAddress = itemView.findViewById(R.id.tvAddressRoom);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvName = itemView.findViewById(R.id.tvNameRoom);
            container = itemView.findViewById(R.id.containerRoom);
        }
    }
}
