package com.datn.finhome.Utils;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.datn.finhome.R;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;

public class LoaderDialog {
    private static AlertDialog processingDialog = null;
    public static void createDialog(Activity activity) {
        View layout = LayoutInflater.from(activity).inflate(R.layout.loader_dialog, null);
        processingDialog = new AlertDialog.Builder(activity).create();
        processingDialog.setCanceledOnTouchOutside(false);
        processingDialog.setView(layout);
        processingDialog.show();
        processingDialog.setOnKeyListener( (dialog, keyCode, event) ->  {
            activity.finish();
            return true;
        });

        processingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        SpinKitView progressBarLoading = processingDialog.findViewById(R.id.progressCircularLoading);
        assert progressBarLoading != null;
        Sprite circle = new Circle();
        progressBarLoading.setIndeterminateDrawable(circle);
    }

    public static void dismiss(){
        processingDialog.dismiss();
        processingDialog = null;
    }
}
