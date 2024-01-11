package com.example.appdatlichchupanh.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdatlichchupanh.MyApp;
import com.example.appdatlichchupanh.activities.DichVuDetailActivity;
import com.example.appdatlichchupanh.databinding.RowDichvuUserBinding;
import com.example.appdatlichchupanh.filters.FilterDichVuUser;
import com.example.appdatlichchupanh.models.ModelDichVu;

import java.util.ArrayList;

public class AdapterDichVuUser extends RecyclerView.Adapter<AdapterDichVuUser.HolderDichVuUser> implements Filterable {

    private Context context;
    public ArrayList<ModelDichVu> dichVuArrayList, filterList;
    private RowDichvuUserBinding binding;
    private FilterDichVuUser filter;

    public AdapterDichVuUser(Context context, ArrayList<ModelDichVu> dichVuArrayList) {
        this.context = context;
        this.dichVuArrayList = dichVuArrayList;
        this.filterList = dichVuArrayList;
    }

    @NonNull
    @Override
    public HolderDichVuUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowDichvuUserBinding.inflate(LayoutInflater.from(context),parent,false);

        return new HolderDichVuUser(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderDichVuUser holder, int position) {
        //get data
        ModelDichVu model = dichVuArrayList.get(position);
        String dichvuId= model.getId();
        String ten= model.getTenDichVu();
        //String gia = model.getGia();
        String mota = model.getMota();
        String imgUrl =model.getUrl();
        String LoaiId = model.getLoaiid();
        //convert time
        long timestamp = model.getTimestamp();
        String formatedDate= MyApp.formatTimestamp(timestamp);

        //gia tien
        String gia= model.getGia();
        String formatgia = MyApp.formatgiatien(Long.parseLong(gia));

        //set data
        holder.tenTv.setText(ten);
        holder.giaTv.setText(formatgia);
        holder.motaTv.setText(mota);
        holder.timeTv.setText(formatedDate);

        MyApp.LoadFormImgUrl(context,""+imgUrl,""+ten ,""+gia,""+mota,holder.imageIv);
        MyApp.LoadLoaidv(""+LoaiId,holder.loaiTv);

        //show details activitiy
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DichVuDetailActivity.class);
                intent.putExtra("dvId",dichvuId);
                context.startActivity(intent);
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
            filter = new FilterDichVuUser(filterList, this);
        }
        return filter;
    }

    class HolderDichVuUser extends RecyclerView.ViewHolder{
        ImageView imageIv;
        TextView tenTv, giaTv,motaTv, loaiTv,timeTv;

        public HolderDichVuUser(@NonNull View itemView) {
            super(itemView);
            imageIv = binding.imageIv;
            tenTv = binding.tenTv;
            giaTv = binding.giaTv;
            motaTv = binding.motaTv;
            loaiTv = binding.loaiTv;
            timeTv= binding.timeTv;

        }
    }
}
