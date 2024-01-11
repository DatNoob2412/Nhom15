package com.example.appdatlichchupanh.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appdatlichchupanh.adapters.AdapterDichVu;
import com.example.appdatlichchupanh.databinding.ActivityDichVuListAdminBinding;
import com.example.appdatlichchupanh.models.ModelDichVu;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class DichVuListAdminActivity extends AppCompatActivity {

    private ActivityDichVuListAdminBinding binding;

    private ArrayList<ModelDichVu> dichVuArrayList;

    private AdapterDichVu adapterDichVu;

    private String loaiid, Loaidv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDichVuListAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //get data from intent
        Intent intent = getIntent();
        loaiid = intent.getStringExtra("id");
        Loaidv =  intent.getStringExtra("Loaidv");

        //set img loaidv
        binding.titleloaidvTv.setText(Loaidv);

        loadDVlist();

        //tim kiem
        binding.txttimkiem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                try {
                    adapterDichVu.getFilter().filter(s);
                }catch (Exception e){

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //quay lại
        binding.btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void loadDVlist() {
        dichVuArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("DichVu");
        ref.orderByChild("Loaiid").equalTo(loaiid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        dichVuArrayList.clear();

                        for (DataSnapshot ds : snapshot.getChildren()) {

                        ModelDichVu model = ds.getValue(ModelDichVu.class);

                        dichVuArrayList.add(model);

                        }
                        adapterDichVu = new AdapterDichVu(DichVuListAdminActivity.this,dichVuArrayList);
                        binding.dichvuRv.setAdapter(adapterDichVu);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}