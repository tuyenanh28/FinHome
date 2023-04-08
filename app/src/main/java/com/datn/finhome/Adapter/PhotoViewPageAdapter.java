package com.datn.finhome.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.datn.finhome.Models.photoViewPage;
import com.datn.finhome.R;

import java.util.List;

public class PhotoViewPageAdapter extends PagerAdapter {
    private List<photoViewPage> photoViewPages;

    public PhotoViewPageAdapter(List<photoViewPage> photoViewPages) {
        this.photoViewPages = photoViewPages;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.item_photo_viewpage,container,false);
        ImageView imageView = view.findViewById(R.id.img_photo);
        photoViewPage photoViewPage = photoViewPages.get(position);
        imageView.setImageResource(photoViewPage.getResourceId());
        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        if(photoViewPages != null){
            return photoViewPages.size();
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view ==  object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
        notifyDataSetChanged();
    }
}
