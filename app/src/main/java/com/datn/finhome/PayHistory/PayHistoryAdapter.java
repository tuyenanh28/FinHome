package com.datn.finhome.PayHistory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.finhome.R;

import java.util.List;

public class PayHistoryAdapter extends RecyclerView.Adapter<PayHistoryAdapter.PayViewholder>{
    Context context;
    private List<PayHistory> list;

    public PayHistoryAdapter(Context context, List<PayHistory> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public PayViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payhistory, parent, false);
        PayViewholder roomViewholder = new PayViewholder(view);
        return roomViewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull PayViewholder holder, int position) {
        PayHistory payHistory = list.get(position);
        if (payHistory == null) {
            return;
        }
        holder.userName.setText("Phí đẩy tin: "+payHistory.getTitle());
        holder.payTime.setText(payHistory.getPayTime());
        holder.money.setText("-"+payHistory.getMoney());
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        }
        else
            return 0;
    }


    public class PayViewholder extends RecyclerView.ViewHolder {
        private TextView  userName, payTime, money;
        public PayViewholder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.tvUsername);
            payTime = itemView.findViewById(R.id.tvTimepay);
            money = itemView.findViewById(R.id.tvPricePay);

        }
    }
}
