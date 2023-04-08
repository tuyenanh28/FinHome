package com.datn.finhome.Views.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.datn.finhome.Adapter.RoomAdapter;
import com.datn.finhome.Interfaces.APIProvincesId;
import com.datn.finhome.Models.Address.State;
import com.datn.finhome.Models.RoomModel;
import com.datn.finhome.R;
import com.google.firebase.database.DatabaseReference;
import com.lpphan.rangeseekbar.RangeSeekBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class FilterRoomActivity extends AppCompatActivity {

    Toolbar toolbar;

    Button btnApply;
    Boolean gender = true;
    TextView minPriceFilter, maxPriceFilter;
    RadioButton rad_min, rad_max;

    RangeSeekBar rangeSeekBarPrice;

    DatabaseReference reference;
    RoomAdapter adapter;
    private List<RoomModel> list = new ArrayList<>();

    Spinner spinnerTinh, spinnerHuyen, spinnerXa;
    private ArrayList<String> getStateName = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_room);
        toolbar = findViewById(R.id.toobar_filter);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Bộ lọc tìm phòng");
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        actionBar.setDisplayHomeAsUpEnabled(true);

        spinnerTinh = findViewById(R.id.spinnerTinh);
        spinnerHuyen = findViewById(R.id.spinnerHuyen);
        spinnerXa = findViewById(R.id.spinnerXa);


        rangeSeekBarPrice = findViewById(R.id.rangeSeekBarPrice);
        minPriceFilter = findViewById(R.id.min_price_filter);
        maxPriceFilter = findViewById(R.id.max_price_filter);
        rad_min = findViewById(R.id.radio_min);
        rad_max = findViewById(R.id.radio_max);
        rad_min.setOnClickListener((View.OnClickListener) this);
        rad_max.setOnClickListener((View.OnClickListener) this);

        if(rad_min.isChecked()) {
            gender = false;
        }
        selecAddress();

        btnApply = findViewById(R.id.btn_apply_find_room_filter);
    }

    private void selecAddress() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIProvincesId.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        APIProvincesId provincesId = retrofit.create(APIProvincesId.class);
        Call<String> call = provincesId.getState();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.i("Phạm Lâm Vũ", response.body().toString());
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.i("Success", response.body().toString());
                        try {
                            String getResponse = response.body().toString();
                            List<State> getStateData = new ArrayList<State>();
                            JSONArray jsonArray = new JSONArray(getResponse);
                            getStateData.add(new State(-1, "Chọn tỉnh, thành phố"));
                            for (int i = 0; i < jsonArray.length(); i++) {
                                State state = new State();
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                state.setStcode(jsonObject.getInt("code"));
                                state.setStateName(jsonObject.getString("name"));
                                getStateData.add(state);
                            }

                            for (int i = 0; i < getStateData.size(); i++) {
                                getStateName.add(getStateData.get(i).getStateName().toString());
                            }

                            ArrayAdapter<String> spinStateAdapter = new ArrayAdapter<String>(FilterRoomActivity.this, android.R.layout.simple_spinner_item, getStateName);
                            spinStateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerTinh.setAdapter(spinStateAdapter);
                            spinnerTinh.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:break;
        }

        return super.onOptionsItemSelected(item);
    }



}