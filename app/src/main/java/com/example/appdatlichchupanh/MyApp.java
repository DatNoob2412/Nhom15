package com.example.appdatlichchupanh;

import static com.example.appdatlichchupanh.Constants.MAX_BYTES_IMG;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.text.format.DateFormat;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class MyApp extends Application {
    private Context context;


    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static final String formatTimestamp(long timestamp){
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timestamp);

        String date = DateFormat.format("dd.MM/yyyy",cal).toString();

        return date;
    }

    public static final String formatgiatien(long giatien){
        //Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getInstance();
        String gia = currencyVN.format(giatien)+" ₫";

        return gia;
    }



    public static void deleteDichVu(Context context, String dvId, String dvTen, String dvUrl) {

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Xóa "+dvTen+"...");
        progressDialog.show();

        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(dvUrl);
        storageReference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("DichVu");
                        reference.child(dvId)
                                .removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        progressDialog.dismiss();
                                        Toast.makeText(context, "Xóa thành công!", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static void LoadFormImgUrl(Context context, String imgUrl, String tendv,String gia,String mota, ImageView imageIv ) {
        //String imgUrl = model.getUrl();
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(imgUrl);
        ref.getBytes(MAX_BYTES_IMG)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {

                        //set to img
                        Picasso.with(context)
                                .load(imgUrl)
//                                .resize(50,50)
//                                .centerCrop()
                                .into(imageIv);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public static void LoadLoaidv(String Loaiid, TextView loaiTv) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("LoaiDichVu");
        ref.child(Loaiid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String Loaidv = ""+snapshot.child("Loaidv").getValue();

                        loaiTv.setText(Loaidv);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    public static void dvviewcount(String dvId){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("DichVu");
        ref.child(dvId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String viewcount = ""+snapshot.child("viewsCount").getValue();

                        if (viewcount.equals("")||viewcount.equals("null")) {
                            viewcount ="0";
                        }

                        long newViewCount = Long.parseLong(viewcount)+1;

                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("viewsCount", newViewCount);

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("DichVu");
                        reference.child(dvId)
                                .updateChildren(hashMap);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    public static void themDatLich(Context context, String dvId){

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            Toast.makeText(context, "Bạn cần đăng nhập", Toast.LENGTH_SHORT).show();
        }
        else {
            long timestamp=System.currentTimeMillis();

            HashMap<String, Object> hashMap= new HashMap<>();
            hashMap.put("dvId",""+dvId);
            hashMap.put("timestamp",""+timestamp);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).child("LichDat").child(dvId)
                    .setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(context, "Đã đặt lịch thành công", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Đặt không thành công", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public  static void huyDatLich(Context context, String dvId){

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            Toast.makeText(context, "Bạn cần đăng nhập", Toast.LENGTH_SHORT).show();
        }
        else {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).child("LichDat").child(dvId)
                    .removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(context, "Hủy lịch thành công", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Hủy không thành công", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
