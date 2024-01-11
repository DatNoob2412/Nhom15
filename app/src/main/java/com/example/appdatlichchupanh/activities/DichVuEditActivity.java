package com.example.appdatlichchupanh.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appdatlichchupanh.databinding.ActivityDichVuEditBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class DichVuEditActivity extends AppCompatActivity {

    private ActivityDichVuEditBinding binding;

    private String dvId;

    private ProgressDialog progressDialog;

    private ArrayList<String> loaidvTitleArrayList, loaidvIdArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDichVuEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dvId = getIntent().getStringExtra("dvId");

        loadloaidv();
        loaddvInfo();

        // setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Vui lòng chờ...");
        progressDialog.setCanceledOnTouchOutside(false);


        binding.loaidvTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loaidvpickDialog();
            }
        });

        //quay lại
        binding.btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }

    private void loaddvInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("DichVu");
        ref.child(dvId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get Dich vu info
                        selectedloaidvId = ""+snapshot.child("Loaiid").getValue();
                        String dvten = ""+snapshot.child("TenDichVu").getValue();
                        String dvgia = ""+snapshot.child("Gia").getValue();
                        String dvmota = ""+snapshot.child("mota").getValue();

                        //set to view
                        binding.txttendv.setText(dvten);
                        binding.txtgiadv.setText(dvgia);
                        binding.txtmotadv.setText(dvmota);

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("LoaiDichVu");
                        ref.child(selectedloaidvId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        String dvloai = ""+snapshot.child("Loaidv").getValue();

                                        binding.loaidvTv.setText(dvloai);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private String ten = "", gia = "", mota = "";
    private void validateData() {
        //get data
        ten = binding.txttendv.getText().toString().trim();
        gia = binding.txtgiadv.getText().toString().trim();
        mota = binding.txtmotadv.getText().toString().trim();


        //validate data

        if (TextUtils.isEmpty(ten)) {
            Toast.makeText(this, "Nhập tên!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(gia)) {
            Toast.makeText(this, "Nhập giá!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mota)) {
            Toast.makeText(this, "Nhập mô tả!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(selectedloaidvId)) {
            Toast.makeText(this, "Nhập thể loại dịch vụ!", Toast.LENGTH_SHORT).show();
        } else {
            updatedv();

        }

    }

    private void updatedv() {
        progressDialog.setMessage("uploading img...");
        progressDialog.show();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("TenDichVu", "" + ten);
        hashMap.put("Gia", "" + gia);
        hashMap.put("mota", "" + mota);
        hashMap.put("Loaiid", "" + selectedloaidvId);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("DichVu");
        ref.child(dvId)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(DichVuEditActivity.this, "Sửa thành công!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(DichVuEditActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private String selectedloaidvId = "", selectedloaidvTitle = "";

    private void loaidvpickDialog() {
        String[] loaidvArray = new String[loaidvTitleArrayList.size()];
        for (int i = 0; i < loaidvTitleArrayList.size(); i++) {
            loaidvArray[i] = loaidvTitleArrayList.get(i);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Danh sách loại dich vụ")
                .setItems(loaidvArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        selectedloaidvTitle = loaidvTitleArrayList.get(which);
                        selectedloaidvId = loaidvIdArrayList.get(which);

                        binding.loaidvTv.setText(selectedloaidvTitle);
                    }
                })
                .show();
    }

    private void loadloaidv() {
        loaidvTitleArrayList = new ArrayList<String>();
        loaidvIdArrayList = new ArrayList<String>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("LoaiDichVu");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loaidvTitleArrayList.clear();
                loaidvIdArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {

                    String loaidvId = "" + ds.child("id").getValue();
                    String loaidvtitle = "" + ds.child("Loaidv").getValue();

                    //add to arraylists
                    loaidvTitleArrayList.add(loaidvtitle);
                    loaidvIdArrayList.add(loaidvId);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}