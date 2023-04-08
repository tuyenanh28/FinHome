package com.datn.finhome.Views.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.finhome.Adapter.RoomAdapter;
import com.datn.finhome.Interfaces.APIProvincesId;
import com.datn.finhome.Interfaces.IClickItemUserListener;
import com.datn.finhome.Models.Address.District;
import com.datn.finhome.Models.Address.State;
import com.datn.finhome.Models.RoomModel;
import com.datn.finhome.R;
import com.datn.finhome.Utils.LoaderDialog;
import com.datn.finhome.Views.Activity.ShowDetailActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
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
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class SearchFragment extends Fragment implements IClickItemUserListener {
    private RoomAdapter adapter;
    private List<RoomModel> list = new ArrayList<>();

    DatabaseReference reference;
    RecyclerView recyclerView;
    Toolbar toolbar;
    SearchView searchView;
    Boolean gender = true;
    Spinner spinnerTinh, spinnerHuyen, spinnerXa;
    private ArrayList<String> getStateName = new ArrayList<String>();
    private ArrayList<String> getDistrictName = new ArrayList<String>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        LoaderDialog.createDialog(requireActivity());
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycle_list_search);
        searchView = view.findViewById(R.id.txt_search);
        toolbar = view.findViewById(R.id.toobar_search);
        spinnerXa = view.findViewById(R.id.spinnerXa);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);


        listRoom();
        searchRoom();
    }


    private void listRoom() {
        reference = FirebaseDatabase.getInstance().getReference().child("Room");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    RoomModel roomModel = dataSnapshot.getValue(RoomModel.class);
                    list.add(roomModel);
                    recyclerView.setHasFixedSize(true);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                    adapter = new RoomAdapter(getContext(), list, roomModel1 -> onClickGoToDetail(roomModel1));
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);
                }
                LoaderDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TAG", error.getMessage());
            }
        });
    }

    private void searchRoom() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                adapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.filter_menu, menu);
        MenuItem menuItemFilter = menu.findItem(R.id.toobar_btnFilter);
        menuItemFilter.setIcon(R.drawable.ic_filter_list);
        menuItemFilter.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.activity_filter_room);

                RangeSeekBar rangeSeekBarPrice = dialog.findViewById(R.id.rangeSeekBarPrice);
                TextView minPriceFilter = dialog.findViewById(R.id.min_price_filter);
                TextView maxPriceFilter = dialog.findViewById(R.id.max_price_filter);
                RadioButton rad_max = dialog.findViewById(R.id.radio_max);
                RadioButton rad_min = dialog.findViewById(R.id.radio_min);

                rangeSeekBarPrice.setTickCount(11);
                rangeSeekBarPrice.setThumbNormalRadius((int) 6f);
                rangeSeekBarPrice.setThumbPressedRadius((int) 8f);
                rangeSeekBarPrice.setRightIndex(0);
                rangeSeekBarPrice.setLeftIndex(10);

                rangeSeekBarPrice.setOnRangeBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangerListener() {
                    @Override
                    public void onIndexChange(RangeSeekBar rangeBar, int leftIndex, int rightIndex) {
                        Number min_value = rangeBar.getLeftIndex();
                        Number max_value = rangeBar.getRightIndex();

                        int minPrice = (int) min_value;
                        int maxPrice = (int) max_value;

                        minPriceFilter.setText(minPrice + " triệu");
                        maxPriceFilter.setText(maxPrice + " triệu");

                        filterPrice(minPrice, maxPrice,rad_min,rad_max);
                    }
                });


                RangeSeekBar rangeSeekBarSize = dialog.findViewById(R.id.rangeSeekBarSize);
                TextView minSizeFilter = dialog.findViewById(R.id.min_size_filter);
                TextView maxSizeFilter = dialog.findViewById(R.id.max_size_filter);

                rangeSeekBarSize.setTickCount(101);
                rangeSeekBarSize.setThumbNormalRadius((int) 6f);
                rangeSeekBarSize.setThumbPressedRadius((int) 8f);
                rangeSeekBarSize.setRightIndex(0);
                rangeSeekBarSize.setLeftIndex(100);

                rangeSeekBarSize.setOnRangeBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangerListener() {
                    @Override
                    public void onIndexChange(RangeSeekBar rangeBar, int leftIndex, int rightIndex) {
                        Number min_value = rangeBar.getLeftIndex();
                        Number max_value = rangeBar.getRightIndex();

                        int minSize = (int) min_value;
                        int maxSize = (int) max_value;

                        minSizeFilter.setText(minSize + " m2");
                        maxSizeFilter.setText(maxSize + " m2");
                       if (rad_min.isChecked()){
                           reference = FirebaseDatabase.getInstance().getReference().child("Room");
                           Query query = reference.orderByChild("price");
                           query.addValueEventListener(new ValueEventListener() {
                               @Override
                               public void onDataChange(@NonNull DataSnapshot snapshot) {
                                   list.clear();
                                   for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                       RoomModel roomModel = dataSnapshot.getValue(RoomModel.class);
                                       if (Float.parseFloat(roomModel.getSizeRoom()) >= minSize
                                               && Float.parseFloat(roomModel.getSizeRoom()) <= maxSize) {
                                           list.add(roomModel);
                                           recyclerView.setHasFixedSize(true);
                                           RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                                           adapter = new RoomAdapter(getContext(), list, roomModel1 -> onClickGoToDetail(roomModel1));
                                           recyclerView.setLayoutManager(layoutManager);
                                           recyclerView.setAdapter(adapter);
                                       } else {
                                           list.clear();
                                       }

                                   }
                               }

                               @Override
                               public void onCancelled(@NonNull DatabaseError error) {
                                   Log.e("TAG", error.getMessage());
                               }
                           });
                       }else if(rad_max.isChecked()){
                           reference = FirebaseDatabase.getInstance().getReference().child("Room");
                           Query query = reference.orderByChild("price");
                           query.addValueEventListener(new ValueEventListener() {
                               @Override
                               public void onDataChange(@NonNull DataSnapshot snapshot) {
                                   list.clear();
                                   for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                       RoomModel roomModel = dataSnapshot.getValue(RoomModel.class);
                                       if (Float.parseFloat(roomModel.getSizeRoom()) >= minSize
                                               && Float.parseFloat(roomModel.getSizeRoom()) <= maxSize) {
                                           list.add(0,roomModel);
                                           recyclerView.setHasFixedSize(true);
                                           RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                                           adapter = new RoomAdapter(getContext(), list, roomModel1 -> onClickGoToDetail(roomModel1));
                                           recyclerView.setLayoutManager(layoutManager);
                                           recyclerView.setAdapter(adapter);
                                       } else {
                                           list.clear();
                                       }

                                   }
                               }

                               @Override
                               public void onCancelled(@NonNull DatabaseError error) {
                                   Log.e("TAG", error.getMessage());
                               }
                           });
                       }

                    }
                });



                selecAddress(dialog); // chọn tỉnh

                Button btnApply = dialog.findViewById(R.id.btn_apply_find_room_filter);
                btnApply.setOnClickListener(v -> {
                    dialog.dismiss();
                });


                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                return false;
            }
        });
    }

    private void onClickGoToDetail(RoomModel roomModel) {
        Intent intent = new Intent(getActivity(), ShowDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("Room", roomModel);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onClickItemRoom(RoomModel roomModel) {
    }

    private void filterPrice(int min, int max,RadioButton rad_min,RadioButton rad_max) {
        if (rad_min.isChecked()){
            reference = FirebaseDatabase.getInstance().getReference().child("Room");
            Query query = reference.orderByChild("price");
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        RoomModel roomModel = dataSnapshot.getValue(RoomModel.class);
                        if (Double.parseDouble(roomModel.getPrice()) >= min * 1000000
                                && Double.parseDouble(roomModel.getPrice()) <= max * 1000000) {
                            list.add(roomModel);
                        }
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                        adapter = new RoomAdapter(getContext(), list, roomModel1 -> onClickGoToDetail(roomModel1));
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapter);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("TAG", error.getMessage());
                }
            });
        }else if(rad_max.isChecked()){
            reference = FirebaseDatabase.getInstance().getReference().child("Room");
            Query query = reference.orderByChild("price");
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        RoomModel roomModel = dataSnapshot.getValue(RoomModel.class);
                        if (Double.parseDouble(roomModel.getPrice()) >= min * 1000000
                                && Double.parseDouble(roomModel.getPrice()) <= max * 1000000) {
                            list.add(0,roomModel);
                        }
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                        adapter = new RoomAdapter(getContext(), list, roomModel1 -> onClickGoToDetail(roomModel1));
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapter);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("TAG", error.getMessage());
                }
            });
        }

    }

    private void selecAddress(Dialog dialog) {
        spinnerTinh = dialog.findViewById(R.id.spinnerTinh);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIProvincesId.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        APIProvincesId provincesId = retrofit.create(APIProvincesId.class);
        Call<String> call = provincesId.getState();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
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

                            ArrayAdapter<String> spinStateAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, getStateName);
                            spinStateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerTinh.setAdapter(spinStateAdapter);
                            spinnerTinh.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    Toast.makeText(getContext(), "" + getStateName.get(position), Toast.LENGTH_SHORT).show();
                                    int getStateId = getStateData.get(position).getStcode();
                                    Log.i("Success", getStateName.get(position));
                                    getDistrict(getStateId, dialog);

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

    private void getDistrict(int getStateId, Dialog dialog) {
        spinnerHuyen = dialog.findViewById(R.id.spinnerHuyen);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIProvincesId.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        APIProvincesId provincesId = retrofit.create(APIProvincesId.class);
        Call<String> call = provincesId.getDistrict();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.i("Success", response.body().toString());
                        try {
                            String getResponse = response.body().toString();
                            List<District> getDistrictData = new ArrayList<District>();
                            JSONArray jsonArray = new JSONArray(getResponse);
                            getDistrictData.add(new District(-1, "Chọn Quận, Huyện"));
                            for (int i = 0; i < jsonArray.length(); i++) {
                                District district = new District();
                                JSONObject object = jsonArray.getJSONObject(i);
                                district.setDiscode(object.getInt("code"));
                                district.setDisName(object.getString("name"));
                                district.setStcode(object.getInt("province_code"));
                                getDistrictData.add(district);
                            }

                            for (int i = 0; i < getDistrictData.size(); i++) {
                                getDistrictName.add(getDistrictData.get(i).getDisName().toString());
                            }

                            ArrayAdapter<String> spinDistrictAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, getDistrictName);
                            spinDistrictAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerHuyen.setAdapter(spinDistrictAdapter);
                            spinnerHuyen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    Toast.makeText(getContext(), "" + getDistrictName.get(position), Toast.LENGTH_SHORT).show();

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
}