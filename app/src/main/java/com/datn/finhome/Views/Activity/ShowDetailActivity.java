package com.datn.finhome.Views.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.datn.finhome.Adapter.DescriptionAdapter;
import com.datn.finhome.Adapter.RoomAdapterHome;
import com.datn.finhome.Models.Report;
import com.datn.finhome.Models.ReviewModel;
import com.datn.finhome.Models.RoomModel;
import com.datn.finhome.R;
import com.datn.finhome.databinding.ActivityShowDetailsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ShowDetailActivity extends AppCompatActivity {

    private ActivityShowDetailsBinding binding;
    private DatabaseReference referenceHost;
    boolean isInMyFavorite = false;
    FirebaseAuth firebaseAuth;
    String uid;
    ImageView imageView;
    ImageButton imageButton;
    TextView tvBaoCao, tvChiDuong;
    RecyclerView recyclerView;
    private FirebaseUser user;
    RoomModel roomModel;
    private DescriptionAdapter descriptionAdapter;
    private List<ReviewModel> mListDescription;
    private RecyclerView rcv;
    private DatabaseReference reference;
    private List<RoomModel> mRoomModel;
    private RoomAdapterHome roomAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        baocao();
        mRoomModel = new ArrayList<>();
        rcv = findViewById(R.id.rcvXemThem);
        reference = FirebaseDatabase.getInstance().getReference("Room");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mRoomModel.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    RoomModel roomModel = dataSnapshot.getValue(RoomModel.class);
                    if (roomModel.isBrowser() == false){

                    }else {
                        mRoomModel.add(roomModel);
                        roomAdapter = new RoomAdapterHome(getApplication(), mRoomModel, roomModel1 -> onClickGoToDetail(roomModel1));
                        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplication(), 2);
                        rcv.setLayoutManager(mLayoutManager);
                        rcv.setAdapter(roomAdapter);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TAG", error.getMessage());
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }
        roomModel = (RoomModel) bundle.get("Room");

        recyclerView = binding.rcvBinhLuan;
        imageView = binding.btnFavoriteReview;
        imageButton = binding.share;
        tvChiDuong = binding.tvChiDuong;
        tvChiDuong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapIntent = new Intent(Intent.ACTION_VIEW);
                mapIntent.setData(Uri.parse("geo:21.0277644, 105.8341598"));
                Intent chooser = Intent.createChooser(mapIntent,"Lauch map");
                startActivity(chooser);
            }
        });


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT,"Subject Here");
                    i.putExtra(Intent.EXTRA_TEXT,"link app : https://play.google.com/store/apps/details?id="+getApplicationContext().getPackageName());
                    startActivity(Intent.createChooser(i,"share With"));
                }catch (Exception e){
                    Toast.makeText(ShowDetailActivity.this, "unable to share this app", Toast.LENGTH_SHORT).show();
                }

            }
        });


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseAuth.getCurrentUser() == null){

                }
                else {
                    if (isInMyFavorite){
                        removeFavorite(getApplicationContext(),roomModel.getId());
                    }else {
                        addToFavorite(getApplicationContext(),roomModel.getId());
                    }
                }
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null){
            checkIsFavorite();
        }

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }

        binding.btnContactReviews.setOnClickListener(v -> {
            Intent intent = new Intent(this, HostDetailsActivity.class);
            Bundle bundle1 = new Bundle();
            bundle1.putString("id", roomModel.getUid());
            intent.putExtras(bundle1);
            startActivity(intent);
        });

        binding.btnBack.setOnClickListener(v -> {
            onBackPressed();
        });

        Picasso.get().load(roomModel.getImg()).into(binding.imgview);
        Locale locale = new Locale("vi", "VN");
        NumberFormat currencyFormat = NumberFormat.getNumberInstance(locale);
        if (roomModel.getPrice() != null) {
            binding.tvPriceReview.setText(currencyFormat.format(Integer.parseInt (roomModel.getPrice())) + " VNĐ/Phòng");
        }
        binding.tvTitleRoomReview.setText(roomModel.getName());
//        binding.tvAddressContactReviews.setText(roomModel.getAddress());
        binding.tvAreaReview.setText(roomModel.getSizeRoom()+"m2");
        binding.tvDetailReviews.setText(roomModel.getDescription());
        String uid = roomModel.getUid();
        referenceHost = FirebaseDatabase.getInstance().getReference("Users");
        referenceHost.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = ""+snapshot.child("name").getValue();
                String address = ""+snapshot.child("address").getValue();
                String img = ""+snapshot.child("avatar").getValue();
                binding.tvNameContactReviews.setText(name);
                binding.tvAddressContactReviews.setText(address);
                Picasso.get().load(img).into(binding.imgContactReviews);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("",error.getMessage());

            }


        });
        initRcvReview();
        Comment();
    }




    private void initRcvReview(){
        mListDescription = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Room");
        reference.child(roomModel.getId()).child("Reviews")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        mListDescription.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            ReviewModel reviewModel = dataSnapshot.getValue(ReviewModel.class);
                            mListDescription.add(reviewModel);

                        }
                        descriptionAdapter = new DescriptionAdapter(getApplicationContext(),mListDescription);
                        recyclerView.setAdapter(descriptionAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ShowDetailActivity.this, "Looxi", Toast.LENGTH_SHORT).show();
                    }
                });

    }
    private void baocao(){
        binding.tvBaoCao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    final Dialog dialog = new Dialog(ShowDetailActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.bottomsheetlayoutbaocao);
                    Button buttonThanhToan = dialog.findViewById(R.id.btnThanhToan);
                    EditText edtBaoCao = dialog.findViewById(R.id.edtBaoCao);

                    buttonThanhToan.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            String bc =  edtBaoCao.getText().toString().trim();
                            if (bc.length() == 0) {
                                Toast.makeText(getApplicationContext(), "Vui lòng nhập báo cáo sai phạm", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                            String key = FirebaseDatabase.getInstance().getReference("Report").push().getKey();
                            Report report = new Report(user.getUid().toString(), bc,
                                    roomModel.getId());
                            report.setIdReport(key);
                            mDatabase.child("Report").push().setValue(report, (databaseError, databaseReference) -> {
                                if (databaseError != null) {
                                    Toast.makeText(ShowDetailActivity.this, "Lỗi: " + databaseError + "", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ShowDetailActivity.this, "Đã gửi Báo cáo sai phạm", Toast.LENGTH_SHORT).show();
//                                    edtReviews.setText("");
                                }
                            });
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                    dialog.getWindow().setGravity(Gravity.CENTER);

            }
        });
    }

    private void Comment(){
        EditText edtReviews = findViewById(R.id.text_send);
        ImageButton btnsend = findViewById(R.id.btn_send);

        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bl =  edtReviews.getText().toString().trim();
                if (bl.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Vui lòng nhập bình luận", Toast.LENGTH_SHORT).show();
                    return;
                } else{
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Room");
                String key = FirebaseDatabase.getInstance().getReference("Reviews").push().getKey();
                String pattern = "HH:mm MM/dd/yyyy";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                String date = simpleDateFormat.format(new Date());
                ReviewModel reviewModel = new ReviewModel(
                        user.getUid().toString(), bl,
                        roomModel.getId()
                );
                reviewModel.setIdComment(key);
                reviewModel.setTime(date);
                mDatabase.child(roomModel.getId()).child("Reviews").push().setValue(reviewModel, (databaseError, databaseReference) -> {
                    if (databaseError != null) {
                        Toast.makeText(ShowDetailActivity.this, "Lỗi: " + databaseError + "", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ShowDetailActivity.this, "Đã gửi bình luận", Toast.LENGTH_SHORT).show();
                        edtReviews.setText("");
                    }
                });
            }
            }
        });
    }



    public  void checkIsFavorite(){
      DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
      reference.child(firebaseAuth.getUid()).child("favorites").child(roomModel.getId())
              .addValueEventListener(new ValueEventListener() {
                  @Override
                  public void onDataChange(@NonNull DataSnapshot snapshot) {
                      isInMyFavorite = snapshot.exists();
                      if (isInMyFavorite){
//                          imageView.setCompoun
                          imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_heart_red_24));
                      }else {
                          imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_false));

                      }
                  }

                  @Override
                  public void onCancelled(@NonNull DatabaseError error) {

                  }
              });
    }



    public void addToFavorite(Context context, String roomId){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null){
            Toast.makeText(context, "vui long dang nhap", Toast.LENGTH_SHORT).show();
        }else {
            HashMap<String,Object> hashMap = new HashMap<>();
            hashMap.put("roomId",""+roomId);

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(firebaseAuth.getUid()).child("favorites").child(roomId).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(context, "Đã thêm vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "That bai", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

    public static void removeFavorite(Context context,String roomId){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null){
            Toast.makeText(context, "vui long dang nhap", Toast.LENGTH_SHORT).show();
        }else {

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(firebaseAuth.getUid()).child("favorites").child(roomId).removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(context, "Đã bỏ yêu thích", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "That bai", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private  void onClickGoToDetail(RoomModel roomModel){
        Intent intent = new Intent(ShowDetailActivity.this, ShowDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("Room", roomModel);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}