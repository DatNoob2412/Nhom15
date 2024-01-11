package com.example.appdatlichchupanh.adapters;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdatlichchupanh.MyApp;
import com.example.appdatlichchupanh.activities.DichVuDetailActivity;
import com.example.appdatlichchupanh.activities.DichVuEditActivity;
import com.example.appdatlichchupanh.databinding.RowDichvuAdminBinding;
import com.example.appdatlichchupanh.filters.FilterDichVuAdmin;
import com.example.appdatlichchupanh.models.ModelDichVu;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AdapterDichVu extends RecyclerView.Adapter<AdapterDichVu.HolderDichVuAdmin> implements Filterable {

    private Context context;
    public ArrayList<ModelDichVu> dichVuArrayList, filterList;

    private RowDichvuAdminBinding binding;

    private FilterDichVuAdmin filter;

    private ProgressDialog progressDialog;

    public AdapterDichVu(Context context, ArrayList<ModelDichVu> dichVuArrayList) {
        this.context = context;
        this.dichVuArrayList = dichVuArrayList;
        this.filterList = dichVuArrayList;

        // setup progress dialog
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Vui lòng chờ...");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @NonNull
    @Override
    public HolderDichVuAdmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowDichvuAdminBinding.inflate(LayoutInflater.from(context),parent,false);

        return new HolderDichVuAdmin(binding.getRoot());

    }

    @Override
    public void onBindViewHolder(@NonNull HolderDichVuAdmin holder, int position) {
        //get data
        ModelDichVu model = dichVuArrayList.get(position);
        String dvId = model.getId();
        String ten= model.getTenDichVu();
        String gia = model.getGia();
        String mota = model.getMota();
        String imgUrl = model.getUrl();
        String loaiid = model.getLoaiid();
        long timestamp = model.getTimestamp();
        String formatedDate= MyApp.formatTimestamp(timestamp);

        String formatgia = MyApp.formatgiatien(Long.parseLong(gia));

        //set data
        holder.tenTv.setText(ten);
        holder.giaTv.setText(formatgia);
        holder.motaTv.setText(mota);
        holder.timeTv.setText(formatedDate);

        //load details

        MyApp.LoadLoaidv(
                ""+loaiid,
                holder.loaiTv
        );

        MyApp.LoadFormImgUrl(
                context,
                ""+imgUrl,
                ""+ten,
                ""+gia,
                ""+mota,
                holder.imageIv
        );

        //click more: edit, delete
        holder.btnmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moreOptiosnDialog(model, holder);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DichVuDetailActivity.class);
                intent.putExtra("dvId",dvId);
                context.startActivity(intent);
            }
        });

    }

    private void moreOptiosnDialog(ModelDichVu model, HolderDichVuAdmin holder) {
        String dvId = model.getId();
        String dvUrl=model.getUrl();
        String dvTen=model.getTenDichVu();

        //show dialog
        String[] options = {"Sửa","Xóa"};

        //Thong bao dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Dịch Vụ")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Intent intent = new Intent(context, DichVuEditActivity.class);
                            intent.putExtra("dvId", dvId);
                            context.startActivity(intent);

                        }else if (which == 1) {
                           // MyApp.deleteDichVu(context,"","","");
                            deleteDichVu(model,holder);
                        }
                    }
                })
                .show();

    }
private void deleteDichVu(ModelDichVu model, HolderDichVuAdmin holder) {
    String dvId = model.getId();
    String dvUrl=model.getUrl();
    String dvTen=model.getTenDichVu();

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





    @Override
    public int getItemCount() {
        return dichVuArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new FilterDichVuAdmin(filterList, this);
        }
        return filter;
    }

    class HolderDichVuAdmin extends RecyclerView.ViewHolder {

        ImageView imageIv;
//        ProgressBar pBar;
        TextView tenTv, giaTv,motaTv, loaiTv,timeTv;
        ImageButton btnmore;

        public HolderDichVuAdmin(@NonNull View itemView) {
            super(itemView);

            imageIv = binding.imageIv;
//            pBar = binding.pBar;
            tenTv = binding.tenTv;
            giaTv = binding.giaTv;
            motaTv = binding.motaTv;
            loaiTv = binding.loaiTv;
            timeTv= binding.timeTv;
            btnmore= binding.btnmore;

        }
    }

}
