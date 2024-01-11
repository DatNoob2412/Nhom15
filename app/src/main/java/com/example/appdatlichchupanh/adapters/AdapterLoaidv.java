package com.example.appdatlichchupanh.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appdatlichchupanh.activities.DichVuListAdminActivity;
import com.example.appdatlichchupanh.filters.FilterLoaidv;
import com.example.appdatlichchupanh.models.ModelLoaidv;
import com.example.appdatlichchupanh.databinding.RowLoaidvBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AdapterLoaidv extends RecyclerView.Adapter<AdapterLoaidv.HolderLoaidv> implements Filterable {

    private Context context;
    public ArrayList<ModelLoaidv> loaidvArrayList, filterList;

    private RowLoaidvBinding binding;

    private FilterLoaidv filter;

    public AdapterLoaidv(Context context, ArrayList<ModelLoaidv> loaidvArrayList) {
        this.context = context;
        this.loaidvArrayList = loaidvArrayList;
        this.filterList = loaidvArrayList;
    }

    @NonNull
    @Override
    public HolderLoaidv onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //bind row_loaidv.xml
        binding = RowLoaidvBinding.inflate(LayoutInflater.from(context),parent,false);

        return new HolderLoaidv(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderLoaidv holder, int position) {
    //get data
        ModelLoaidv model = loaidvArrayList.get(position);
        String id= model.getId();
        String Loaidv = model.getLoaidv();
        String uid= model.getUid();
        long timestamp = model.getTimestamp();

        //set data
        holder.loaidv.setText(Loaidv);

        //delete loaidv
        holder.btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Xóa")
                        .setMessage("Bạn có chắc chắn muốn xóa loại dịch vụ này?")
                        .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(context, "Xóa...", Toast.LENGTH_SHORT).show();
                                deleteloaidv(model, holder);
                            }
                        })
                        .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
            }
        });

        //goto DichVuListAdminActivity
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DichVuListAdminActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("Loaidv", Loaidv);
                context.startActivity(intent);
            }
        });
    }

    private void deleteloaidv(ModelLoaidv model, HolderLoaidv holder) {
        String id = model.getId();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("LoaiDichVu");
        ref.child(id)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return loaidvArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new FilterLoaidv(filterList, this);
        }
        return filter;
    }

    class HolderLoaidv extends RecyclerView.ViewHolder{

        //ui views of row_loaidv.xml
        TextView loaidv;
        ImageView btndelete;

        public HolderLoaidv(@NonNull View itemView){
            super(itemView);

            //init ui view
            loaidv = binding.Loaidv;
            btndelete =  binding.btndelete;
        }
    }

}
