package com.example.appdatlichchupanh.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appdatlichchupanh.adapters.AdapterLoaidv;
import com.example.appdatlichchupanh.databinding.ActivityDsAdminBinding;
import com.example.appdatlichchupanh.models.ModelLoaidv;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class dsAdminActivity extends AppCompatActivity {

    private ActivityDsAdminBinding binding;

    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelLoaidv> loaidvArrayList;

    private AdapterLoaidv adapterLoaidv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDsAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();
        loadloaidv();

        //tim kiem
        binding.txttimkiem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                try {
                    adapterLoaidv.getFilter().filter(s);
                }catch (Exception e){

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.btnlogoutad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                checkUser();
            }
        });

        //click loại dv
        binding.btnloaidv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(dsAdminActivity.this, LoaiDichVuActivity.class));
            }
        });

        //click them dich vu
        binding.btndv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(dsAdminActivity.this, DichVuAddActivity.class));
            }
        });

        //click lịch dặt
        binding.btnlichdat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(dsAdminActivity.this, LichDatActivity.class));
            }
        });

        binding.btnprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(dsAdminActivity.this, ProfileActivity.class) );
            }
        });

    }

    private void loadloaidv() {
        //init araylist
        loaidvArrayList = new ArrayList<>();


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("LoaiDichVu");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loaidvArrayList.clear();
                for (DataSnapshot ds:snapshot.getChildren()) {
                    ModelLoaidv model = ds.getValue(ModelLoaidv.class);

                    loaidvArrayList.add(model);

                }
                adapterLoaidv = new AdapterLoaidv(dsAdminActivity.this, loaidvArrayList);
                binding.Rvloaidv.setAdapter(adapterLoaidv);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            String email = firebaseUser.getEmail();
            binding.sutitleTv.setText(email);
        }
    }
}