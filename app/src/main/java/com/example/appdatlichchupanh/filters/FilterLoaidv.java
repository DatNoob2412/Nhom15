package com.example.appdatlichchupanh.filters;

import android.widget.Filter;

import com.example.appdatlichchupanh.adapters.AdapterLoaidv;
import com.example.appdatlichchupanh.models.ModelLoaidv;

import java.util.ArrayList;


public class FilterLoaidv extends Filter {

    ArrayList<ModelLoaidv> filterList;

    AdapterLoaidv adapterLoaidv;

    public FilterLoaidv(ArrayList<ModelLoaidv> filterList, AdapterLoaidv adapterLoaidv) {
        this.filterList = filterList;
        this.adapterLoaidv = adapterLoaidv;
    }


    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();

        if (constraint != null && constraint.length() > 0) {

            constraint = constraint.toString().toUpperCase();

            ArrayList<ModelLoaidv> filteredModels = new ArrayList<>();

            for (int i = 0; i < filterList.size(); i++) {
                //validate
                if (filterList.get(i).getLoaidv().toUpperCase().contains(constraint)) {
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
        adapterLoaidv.loaidvArrayList = (ArrayList<ModelLoaidv>)results.values;

        adapterLoaidv.notifyDataSetChanged();
    }
}
