package com.example.appdatlichchupanh.activities;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appdatlichchupanh.adapters.AdapterDatLich;
import com.example.appdatlichchupanh.databinding.ActivityLichDatBinding;
import com.example.appdatlichchupanh.models.ModelDichVu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LichDatActivity extends AppCompatActivity {

    private ActivityLichDatBinding binding;

    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelDichVu> dichVuArrayList;

    private AdapterDatLich adapterDatLich;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLichDatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        //get data from intent
//        Intent intent = getIntent();
//        dvId = intent.getStringExtra("dvId");
//        TenDichVu =  intent.getStringExtra("TenDichVu");

        //firebase
        firebaseAuth = FirebaseAuth.getInstance();
        loadinfo();
        loaddatlichlist();


        //quay lại
        binding.btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void loadinfo() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
//        ref.child(firebaseAuth.getUid())
        ref.child("8aXDdIwaJTgpIPUQmUeLnpTesPD3")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String email = ""+snapshot.child("email").getValue();
                        String name = ""+snapshot.child("Name").getValue();
                        //String imgProfile = ""+snapshot.child("ImageProfile").getValue();

//                        String uid= ""+snapshot.child("uid").getValue();
//                        String timestamp= ""+snapshot.child("timestamp").getValue();
//
//                        String formatedDate= MyApp.formatTimestamp(Long.parseLong(timestamp));

                        binding.email.setText(email);
                        binding.hoten.setText(name);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void loaddatlichlist() {
        dichVuArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
//        ref.child(firebaseAuth.getUid()).child("LichDat")
        ref.child("8aXDdIwaJTgpIPUQmUeLnpTesPD3").child("LichDat")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        dichVuArrayList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {

                            String dvid = ""+ds.child("dvId").getValue();

                            ModelDichVu modelDichVu = new ModelDichVu();
                            modelDichVu.setId(dvid);

                            dichVuArrayList.add(modelDichVu);

                        }
                        adapterDatLich = new AdapterDatLich(LichDatActivity.this,dichVuArrayList);
                        binding.datlichRv.setAdapter(adapterDatLich);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}