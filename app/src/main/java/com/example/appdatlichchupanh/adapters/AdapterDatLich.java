package com.example.appdatlichchupanh.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdatlichchupanh.MyApp;
import com.example.appdatlichchupanh.databinding.RowLichdatAdminBinding;
import com.example.appdatlichchupanh.models.ModelDichVu;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdapterDatLich extends RecyclerView.Adapter<AdapterDatLich.HolderDatLich> {

    private Context context;
    private ArrayList<ModelDichVu> dichVuArrayList;
    private RowLichdatAdminBinding binding;

    public AdapterDatLich(Context context, ArrayList<ModelDichVu> dichVuArrayList) {
        this.context = context;
        this.dichVuArrayList = dichVuArrayList;
    }

    @NonNull
    @Override
    public HolderDatLich onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowLichdatAdminBinding.inflate(LayoutInflater.from(context),parent,false);

        return new HolderDatLich(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderDatLich holder, int position) {
        //get data
        ModelDichVu model = dichVuArrayList.get(position);

        loadDichVuDetail(model, holder);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, DLichDetailActivity.class);
//                intent.putExtra("dvId",model.getId());
//                context.startActivity(intent);
            }
        });

        holder.btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApp.huyDatLich(context,model.getId());
            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, DLichDetailActivity.class);
//                intent.putExtra("dvId", dvId);
//                intent.putExtra("TenDichVu", tendv);
//                context.startActivity(intent);
            }
        });
    }

    private void loadDichVuDetail(ModelDichVu model, HolderDatLich holder) {
        String dvId = model.getId();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("DichVu");
        ref.child(dvId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String tendv = ""+snapshot.child("TenDichVu").getValue();
                        String gia = ""+snapshot.child("Gia").getValue();
                        String mota = ""+snapshot.child("mota").getValue();
                        String Loaiid = ""+snapshot.child("Loaiid").getValue();
                        String url = ""+snapshot.child("Url").getValue();
                        String uid = ""+snapshot.child("uid").getValue();
                        String timestamp= ""+snapshot.child("timestamp").getValue();
                        String viewsCount  = ""+snapshot.child("viewsCount").getValue();

                        model.setDatlich(true);
                        model.setTenDichVu(tendv);
                        model.setGia(gia);
                        model.setMota(mota);
                        model.setLoaiid(Loaiid);
                        model.setTimestamp(Long.parseLong(timestamp));
                        model.setUid(uid);
                        model.setUrl(url);

                        String date= MyApp.formatTimestamp(Long.parseLong(timestamp));
                        String formatgia = MyApp.formatgiatien(Long.parseLong(gia));

                        MyApp.LoadFormImgUrl(
                                context,
                                ""+url,
                                ""+tendv,
                                ""+gia,
                                ""+mota,
                                holder.imageIvtk
                        );


                        holder.timeTv.setText(date);
                        holder.giaTv.setText(formatgia);
                        holder.motaTv.setText(mota);
                        holder.tenTv.setText(tendv);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    @Override
    public int getItemCount() {
        return dichVuArrayList.size();
    }

    class HolderDatLich extends RecyclerView.ViewHolder{
        ImageView imageIvtk;
        TextView tenTv, giaTv,motaTv,timeTv;
        ImageButton btndelete;
        public HolderDatLich(@NonNull View itemView) {
            super(itemView);

            imageIvtk = binding.imageIvtk;
            tenTv = binding.tenTv;
            giaTv = binding.giaTv;
            motaTv = binding.motaTv;
            timeTv= binding.timeTv;
            btndelete= binding.btndelete;

        }
    }
}
