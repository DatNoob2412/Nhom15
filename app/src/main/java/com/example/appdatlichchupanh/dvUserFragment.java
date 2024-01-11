package com.example.appdatlichchupanh;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.appdatlichchupanh.adapters.AdapterDichVuUser;
import com.example.appdatlichchupanh.databinding.FragmentDvUserBinding;
import com.example.appdatlichchupanh.models.ModelDichVu;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link dvUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class dvUserFragment extends Fragment {

    private String loaiid;
    private String loaidv;
    private String uid;

    private ArrayList<ModelDichVu> dichVuArrayList;
    private AdapterDichVuUser adapterDichVuUser;

    private FragmentDvUserBinding binding;


    public dvUserFragment() {
        // Required empty public constructor
    }


    public static dvUserFragment newInstance(String loaiid, String Loaidv, String uid) {
        dvUserFragment fragment = new dvUserFragment();
        Bundle args = new Bundle();
        args.putString("loaiid", loaiid);
        args.putString("Loaidv", Loaidv);
        args.putString("uid", uid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            loaiid = getArguments().getString("loaiid");
            loaidv = getArguments().getString("Loaidv");
            uid = getArguments().getString("uid");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDvUserBinding.inflate(LayoutInflater.from(getContext()), container, false);
        if (loaidv.equals("Tất cả")) {
            loadAllDichVu();
        } else if (loaidv.equals("Hot")) {
            loadMostViewDV("viewsCount");
        } else {
            loadloaiDV();
        }

        //tim kiem
        binding.txttimkiem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                try {
                    adapterDichVuUser.getFilter().filter(s);
                }catch (Exception e){

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return binding.getRoot();
    }

    private void loadloaiDV() {
        dichVuArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("DichVu");
        ref.orderByChild("Loaiid").equalTo(loaiid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        dichVuArrayList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            //get data
                            ModelDichVu model = ds.getValue(ModelDichVu.class);
                            //add to list
                            dichVuArrayList.add(model);
                        }
                        //setup adapter
                        adapterDichVuUser = new AdapterDichVuUser(getContext(), dichVuArrayList);
                        //set adapter to recyclerview
                        binding.RvDichVu.setAdapter(adapterDichVuUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadMostViewDV(String orderBy) {
        dichVuArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("DichVu");
        ref.orderByChild(orderBy).limitToLast(10)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        dichVuArrayList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            //get data
                            ModelDichVu model = ds.getValue(ModelDichVu.class);
                            //add to list
                            dichVuArrayList.add(model);
                        }
                        //setup adapter
                        adapterDichVuUser = new AdapterDichVuUser(getContext(), dichVuArrayList);
                        //set adapter to recyclerview
                        binding.RvDichVu.setAdapter(adapterDichVuUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }


    private void loadAllDichVu() {
        dichVuArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("DichVu");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dichVuArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    //get data
                    ModelDichVu model = ds.getValue(ModelDichVu.class);
                    //add to list
                    dichVuArrayList.add(model);
                }
                //setup adapter
                adapterDichVuUser = new AdapterDichVuUser(getContext(), dichVuArrayList);
                //set adapter to recyclerview
                binding.RvDichVu.setAdapter(adapterDichVuUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}