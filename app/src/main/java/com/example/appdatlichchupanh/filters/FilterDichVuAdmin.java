package com.example.appdatlichchupanh.filters;

import android.widget.Filter;

import com.example.appdatlichchupanh.adapters.AdapterDichVu;
import com.example.appdatlichchupanh.models.ModelDichVu;

import java.util.ArrayList;


public class FilterDichVuAdmin extends Filter {

    ArrayList<ModelDichVu> filterList;

    AdapterDichVu adapterDichVu;

    public FilterDichVuAdmin(ArrayList<ModelDichVu> filterList, AdapterDichVu adapterDichVu) {
        this.filterList = filterList;
        this.adapterDichVu = adapterDichVu;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();

        if (constraint != null && constraint.length() > 0) {

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
    protected void publishResults(CharSequence charSequence, FilterResults results) {
        adapterDichVu.dichVuArrayList = (ArrayList<ModelDichVu>)results.values;

        adapterDichVu.notifyDataSetChanged();
    }
}
