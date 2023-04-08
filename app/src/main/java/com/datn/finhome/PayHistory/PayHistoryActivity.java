package com.datn.finhome.PayHistory;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.finhome.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PayHistoryActivity extends AppCompatActivity {
    private RecyclerView rcv;
    private DatabaseReference reference;
    private PayHistoryAdapter payHistoryAdapter;
    private List<PayHistory> payHistorieModel;
    private String userId;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_history);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = firebaseUser.getUid();
        payHistorieModel = new ArrayList<>();
        rcv = findViewById(R.id.rcvPayHistory);
        initHistoryPay();
//        reference = FirebaseDatabase.getInstance().getReference("Payhistory");
//        reference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                PayHistory payModel = snapshot.getValue(PayHistory.class);
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//
//                    payHistorieModel.add(payModel);
//                    rcv.setAdapter(payHistoryAdapter);
//                }
////                LoaderDialog.dismiss();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e("TAG", error.getMessage());
//            }
//        });
    }


    private void initHistoryPay(){
        payHistorieModel = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Payhistory");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    PayHistory payModel = dataSnapshot.getValue(PayHistory.class);
                    if (Objects.equals(payModel.getIdUser(),userId)) {
                        payHistorieModel.add(payModel);
                        payHistoryAdapter = new PayHistoryAdapter(PayHistoryActivity.this, payHistorieModel);
                        rcv.setAdapter(payHistoryAdapter);
                        rcv.setHasFixedSize(true);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TAG", "onCancelled: " + error.getMessage());
            }
        });
    }
}