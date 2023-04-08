package com.datn.finhome.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.datn.finhome.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder>{
    private ArrayList<Uri> uriArrayList;
    private Context context;
    CountOfImageWhenRemove countOfImageWhenRemove;

    public PhotoAdapter(ArrayList<Uri> uriArrayList, Context context, CountOfImageWhenRemove countOfImageWhenRemove) {
        this.uriArrayList = uriArrayList;
        this.context = context;
        this.countOfImageWhenRemove = countOfImageWhenRemove;
    }

    @NonNull
    @Override
    public PhotoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_photo, parent, false);

        return new ViewHolder(view, countOfImageWhenRemove);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
//        holder.imageView.setImageURI(uriArrayList.get(position));
        Glide.with(context)
                .load(uriArrayList.get(holder.getAdapterPosition()))
                .into(holder.imageView);

        holder.deleteImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uriArrayList.remove(uriArrayList.get(holder.getAdapterPosition()));
                notifyItemRemoved(holder.getAdapterPosition());
                notifyItemRangeChanged(holder.getAdapterPosition(), getItemCount());
                countOfImageWhenRemove.clicked(uriArrayList.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return uriArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView, deleteImages;
        CountOfImageWhenRemove countOfImageWhenRemove;
        public ViewHolder(@NonNull View itemView, CountOfImageWhenRemove countOfImageWhenRemove) {
            super(itemView);
            this.countOfImageWhenRemove = countOfImageWhenRemove;
            imageView = itemView.findViewById(R.id.imgAdd);
            deleteImages = itemView.findViewById(R.id.deleteImage);
        }
    }

    public interface CountOfImageWhenRemove{
        void clicked(int getSize);
    }
}
