package com.example.appdatlichchupanh.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.appdatlichchupanh.MyApp;
import com.example.appdatlichchupanh.R;
import com.example.appdatlichchupanh.databinding.ActivityDichVuDetailBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DichVuDetailActivity extends Activity {
    private ActivityDichVuDetailBinding binding;
    String dvId;
    boolean isInMyDL = false;
    private Context context;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDichVuDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //       get data from intent dvId
        Intent intent = getIntent();
        dvId = intent.getStringExtra("dvId");

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            checkIsDatLich();
        }

        loadDvDetail();

        MyApp.dvviewcount(dvId);

        //       quay lại
        binding.btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //dat, huy lich
        binding.btnDatLich.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Toast.makeText(context, "Bạn cần đăng nhập", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (isInMyDL) {
                        MyApp.huyDatLich(DichVuDetailActivity.this, dvId);
                    }else {
                        MyApp.themDatLich(DichVuDetailActivity.this, dvId);
                    }
                }
            }
        });
    }

    private void loadDvDetail() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("DichVu");
        ref.child(dvId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get data
                        String dvten = "" + snapshot.child("TenDichVu").getValue();
                        String dvgia = "" + snapshot.child("Gia").getValue();
                        String loaiid = "" + snapshot.child("Loaiid").getValue();
                        String viewsCount = "" + snapshot.child("viewsCount").getValue();
                        String url = "" + snapshot.child("Url").getValue();
                        String timestamp = "" + snapshot.child("timestamp").getValue();
                        String dvmota = "" + snapshot.child("mota").getValue();

                        //format date
                        String date = MyApp.formatTimestamp(Long.parseLong(timestamp));

                        String giatien = MyApp.formatgiatien(Long.parseLong(dvgia));

                        MyApp.LoadLoaidv(
                                "" + loaiid,
                                binding.loaiTv
                        );
                        MyApp.LoadFormImgUrl(
                                context,
                                "" + url,
                                "" + dvten,
                                "" + dvgia,
                                "" + dvmota,
                                binding.imageIv
                        );

                        //set data
                        binding.tenTv.setText(dvten);
                        binding.giaTv.setText(giatien);
                        binding.motaTv.setText(dvmota);
                        binding.viewTv.setText(viewsCount.replace("null", "N/A"));
                        binding.timeTv.setText(date);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void checkIsDatLich() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("LichDat").child(dvId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        isInMyDL = snapshot.exists();
                        if (isInMyDL) {
                            binding.btnDatLich.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_bookmark_add,0,0);
                            binding.btnDatLich.setText("Huy Lich");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        binding.btnDatLich.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_bookmark_border,0,0);
                        binding.btnDatLich.setText("Dat Lich");
                    }
                });
    }
}
