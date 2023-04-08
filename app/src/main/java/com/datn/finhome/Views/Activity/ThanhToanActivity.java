package com.datn.finhome.Views.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.datn.finhome.PayHistory.PayHistory;
import com.datn.finhome.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ThanhToanActivity extends AppCompatActivity {
    Button btnthem;
    String SECRET_KEY = "sk_test_51MOdT7HtBcRTAQQiquk5QrD4vHzMTclWcuSSchOJpsevdd65Ukd18Mqvvlc1DYFmCC5VGniAtaBiEBy2LbsQsl3P00W9hi5AQn";
    String PUBLISH_KEY = "pk_test_51MOdT7HtBcRTAQQi0k7jZ3DTbfLG3KAbyrVrDsegjYl4JGiy35EjgkgQqvZGPy31RXyG7wymFI0V6K7TCP21GLM600FbxGDnRH";
    String usd = "1"+"00";
    PaymentSheet paymentSheet;
    String CustomerId;
    String EphericalKey;
    String ClientSecret;
    String roomId;
    String idName;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanh_toan);
        btnthem = findViewById(R.id.btnTn);
        user  = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent = getIntent();
        roomId = intent.getStringExtra("RoomId");
        idName = intent.getStringExtra("title");

        btnthem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentFlow();
            }
        });

        PaymentConfiguration.init(this,PUBLISH_KEY);
        paymentSheet = new PaymentSheet(this, paymentSheetResult -> {
            onPaymetResult(paymentSheetResult);
        });

        StringRequest request = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/customers",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            CustomerId = object.getString("id");
//                            Toast.makeText(ThanhToanActivity.this, CustomerId, Toast.LENGTH_SHORT).show();
                            getEmphericalkey(CustomerId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ThanhToanActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer "+SECRET_KEY);
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);


    }

    private void paymentFlow() {
        paymentSheet.presentWithPaymentIntent(ClientSecret, new PaymentSheet.Configuration("Learn with Arvind", new PaymentSheet.CustomerConfiguration(
                CustomerId,
                EphericalKey
        )));
    }

    private void onPaymetResult(PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            Toast.makeText(this, "Bạn Đã Thanh Toán Thành Công 1 USD", Toast.LENGTH_SHORT).show();
            AddPayHistoryToFirebase();
            Intent intent = new Intent(this,HostActivity.class);
            startActivity(intent);
        }
    }

    private void getEmphericalkey(String customerId) {
        StringRequest request = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/ephemeral_keys",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            EphericalKey = object.getString("id");
//                            Toast.makeText(ThanhToanActivity.this, EphericalKey, Toast.LENGTH_SHORT).show();
                            getClientSecret(CustomerId, EphericalKey);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ThanhToanActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @NonNull
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer "+SECRET_KEY);
                header.put("Stripe-Version", "2022-11-15");
                return header;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("customer", CustomerId);

                return param;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void getClientSecret(String customerId, String ephericalKey) {
        StringRequest request = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/payment_intents",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            ClientSecret = object.getString("client_secret");
//                            Toast.makeText(ThanhToanActivity.this, ClientSecret, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ThanhToanActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer "+SECRET_KEY);
                return header;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("customer", CustomerId);
                param.put("amount", usd);
                param.put("currency", "USD");
                param.put("automatic_payment_methods[enabled]", "true");

                return param;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void AddPayHistoryToFirebase(){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        String key = FirebaseDatabase.getInstance().getReference("Payhistory").push().getKey();
        String pattern = "HH:mm MM/dd/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(new Date());

        String money = "1$";
        PayHistory payHistory = new PayHistory(
                roomId,
                user.getUid(),
                idName,
                money
        );
        payHistory.setIdPay(key);
        payHistory.setPayTime(date);
        mDatabase.child("Payhistory").push().setValue(payHistory, (databaseError, databaseReference) -> {
            if (databaseError != null) {
//                LoaderDialog.dismiss();
                Toast.makeText(ThanhToanActivity.this, "Lỗi: " + databaseError + "", Toast.LENGTH_SHORT).show();
            } else {
//                LoaderDialog.dismiss();
                Toast.makeText(ThanhToanActivity.this, "Đã lưu lại thanh toán" , Toast.LENGTH_SHORT).show();
            }
        });
    }
}