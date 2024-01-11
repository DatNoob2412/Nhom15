package com.example.appdatlichchupanh.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.appdatlichchupanh.databinding.ActivityDsUserBinding;
import com.example.appdatlichchupanh.dvUserFragment;
import com.example.appdatlichchupanh.models.ModelLoaidv;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class dsUserActivity extends AppCompatActivity {

    //show tab
    public ArrayList<ModelLoaidv> loaidvArrayList;
    public ViewPagerAdapter viewPagerAdapter;

    private ActivityDsUserBinding binding;
    private FirebaseAuth firebaseAuth;

    GoogleSignInClient gsc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDsUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        setupViewPagerAdapter(binding.viewPager);
        binding.tablayout.setupWithViewPager(binding.viewPager);

        binding.btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                checkUser();
            }
        });

         binding.btnprofile.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 startActivity(new Intent(dsUserActivity.this, ProfileActivity.class) );
             }
         });
    }

    private void setupViewPagerAdapter(ViewPager viewPager) {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, this);
        loaidvArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("LoaiDichVu");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                loaidvArrayList.clear();

                //add data to models
                ModelLoaidv modelAll = new ModelLoaidv("01", "Tất cả", "", 1);
                ModelLoaidv modelMostViewed = new ModelLoaidv("02", "Hot", "", 1);

                //add model list
                loaidvArrayList.add(modelAll);
                loaidvArrayList.add(modelMostViewed);

                //add data to view pager adapter
                viewPagerAdapter.addFragment(dvUserFragment.newInstance(
                        "" + modelAll.getId(),
                        "" + modelAll.getLoaidv(),
                        "" + modelAll.getUid()
                ), modelAll.getLoaidv());
                viewPagerAdapter.addFragment(dvUserFragment.newInstance(
                        "" + modelMostViewed.getId(),
                        "" + modelMostViewed.getLoaidv(),
                        "" + modelMostViewed.getUid()
                ), modelMostViewed.getLoaidv());
                //refresh list
                viewPagerAdapter.notifyDataSetChanged();
                //load from firebase
                for (DataSnapshot ds : snapshot.getChildren()) {
                    //get data
                    ModelLoaidv model = ds.getValue(ModelLoaidv.class);
                    //add data to list
                    loaidvArrayList.add(model);
                    //add data to viewPagerAdapter
                    viewPagerAdapter.addFragment(dvUserFragment.newInstance(
                            "" + model.getId(),
                            "" + model.getLoaidv(),
                            "" + model.getUid()), model.getLoaidv());
                    //refresh list
                    viewPagerAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        //set adapter to view pager
        viewPager.setAdapter(viewPagerAdapter);

    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<dvUserFragment> fragmentList = new ArrayList<>();
        private ArrayList<String> fragmentTitleList = new ArrayList<>();
        private Context context;

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior, dsUserActivity dsUserActivity) {
            super(fm, behavior);
            this.context = context;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        private void addFragment(dvUserFragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }

    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            //not login
            binding.titleTv.setText("Not Logged In");
        } else {
            String email = firebaseUser.getEmail();
            binding.sutitleTv.setText(email);
        }
    }

}