package com.example.appdatlichchupanh.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appdatlichchupanh.databinding.ActivityDichVuAddBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

public class DichVuAddActivity extends AppCompatActivity {
    private ActivityDichVuAddBinding binding;

    private FirebaseAuth firebaseAuth;

    private Uri imgUri = null;

    private ProgressDialog progressDialog;


    private ArrayList<String> loaidvTitleArrayList;
    private ArrayList<String> loaidvIdArrayList;

    private static final String TAG = "ADD_IMG_TAG";
    private final int IMG_PICK_CODE = 71;
//    private static final int IMG_PICK_CODE=1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDichVuAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //khởi tạo firebase
        firebaseAuth = FirebaseAuth.getInstance();

        // setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Vui lòng chờ...");
        progressDialog.setCanceledOnTouchOutside(false);

        loadloaidv();

        //quay lại
        binding.btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.btnimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgPickIntent();
            }
        });

        binding.loaidvTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loaidvpickDialog();
            }
        });

        binding.btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }

    private void imgPickIntent() {
        Log.d(TAG, "Starting img intent");
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMG_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMG_PICK_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == IMG_PICK_CODE) {
                Log.d(TAG, "onActivityResult: img picked");
                imgUri = data.getData();

                Log.d(TAG, "onActivityResult: img picked" + imgUri);
            }

        } else {
            Log.d(TAG, "onActivityResult: cancel picked");
            Toast.makeText(this, "Không thể tải lên", Toast.LENGTH_SHORT).show();

        }
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
//                    ModelLoaidv model = ds.getValue(ModelLoaidv.class);
//
//                    loaidvTitleArrayList.add(model);
//
//                    Log.d(TAG, "OnDataChange"+model.getLoaidv());

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

    private String selectedloaidvId, selectedloaidvTitle;

    private void loaidvpickDialog() {


        Log.d(TAG, "loaidvpickDialog: show");
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
        } else if (TextUtils.isEmpty(selectedloaidvTitle)) {
            Toast.makeText(this, "Nhập thể loại dịch vụ!", Toast.LENGTH_SHORT).show();
        } else if (imgUri == null) {
            Toast.makeText(this, "Thêm ảnh", Toast.LENGTH_SHORT).show();
        } else {
            uploadimgStorage();

        }
    }

    private void uploadimgStorage() {
        progressDialog.setMessage("Thêm ảnh...");
        progressDialog.show();

        long timestamp = System.currentTimeMillis();

        //path of img firebase storage
        String filePathName = "DichVu/" + timestamp;
        //storage reference
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathName);
        storageReference.putFile(imgUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        String uploadedimgurl = "" + uriTask.getResult();

//                        createloaidv();
                        uploadedInfoToDb(uploadedimgurl, timestamp);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Upload failed");
                        Toast.makeText(DichVuAddActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void uploadedInfoToDb(String uploadedimgurl, long timestamp) {
        progressDialog.setMessage("uploading img...");
        progressDialog.show();

        String uid = firebaseAuth.getUid();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", "" + uid);
        hashMap.put("id", "" + timestamp);
        hashMap.put("TenDichVu", "" + ten);
        hashMap.put("Gia","" +gia);
        hashMap.put("mota", "" + mota);
        hashMap.put("Loaiid", "" + selectedloaidvId);
        hashMap.put("Url", "" + uploadedimgurl);
        hashMap.put("timestamp", timestamp);
        hashMap.put("viewsCount", 0);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("DichVu");
        ref.child("" + timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(DichVuAddActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(DichVuAddActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

//    private void createloaidv() {
//
//        progressDialog.setMessage("Thêm thể loại...");
//        progressDialog.show();
//
//        long timestamp = System.currentTimeMillis();
//
//
//        String uid = firebaseAuth.getUid();
//
//        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("uid", "" + uid);
//        hashMap.put("id", "" + timestamp);
//        hashMap.put("TenDichVu", "" + ten);
//        hashMap.put("Gia", "" + gia);
//        hashMap.put("mota", "" + mota);
//        hashMap.put("Loaiid", "" + selectedloaidvId);
//        hashMap.put("timestamp", timestamp);
//
//
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("DichVu");
//        ref.child("" + timestamp)
//                .setValue(hashMap)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        progressDialog.dismiss();
//                        Toast.makeText(DichVuAddActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        progressDialog.dismiss();
//                        Toast.makeText(DichVuAddActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }

}