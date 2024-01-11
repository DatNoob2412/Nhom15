package com.example.appdatlichchupanh.filters;


import android.widget.Filter;

import com.example.appdatlichchupanh.adapters.AdapterDichVuUser;
import com.example.appdatlichchupanh.models.ModelDichVu;

import java.util.ArrayList;

public class FilterDichVuUser extends Filter {

    ArrayList<ModelDichVu> filterList;

    AdapterDichVuUser adapterDichVuUser;

    public FilterDichVuUser(ArrayList<ModelDichVu> filterList, AdapterDichVuUser adapterDichVuUser) {
        this.filterList = filterList;
        this.adapterDichVuUser = adapterDichVuUser;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
       FilterResults results = new FilterResults();

        if (constraint != null || constraint.length() > 0) {

            constraint = constraint.toString().toUpperCase();

            ArrayList<ModelDichVu> filteredModels = new ArrayList<>();

            for (int i = 0; i < filterList.size(); i++) {
                //validate
                if (filterList.get(i).getTenDichVu().toUpperCase().contains(constraint)) {
                    //add to filtered list
                    filteredModels.add(filterList.get(i));
                }

            }
            results.count = filteredModels.size();
            results.values = filteredModels;
        } else {
            results.count = filterList.size();
            results.values = filterList;
        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapterDichVuUser.dichVuArrayList = (ArrayList<ModelDichVu>)results.values;

        adapterDichVuUser.notifyDataSetChanged();
    }
}
