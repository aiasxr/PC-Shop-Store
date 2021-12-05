package com.softwareengineering.pcstore;

import android.widget.Filter;

import java.util.ArrayList;

public class FilterProduct extends Filter {

    private itemRVAdapter adapter ;
    private ArrayList<Item> filterList ;

    public FilterProduct(itemRVAdapter adapter, ArrayList<Item> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();

        //validate search

        if(constraint!=null && constraint.length()>.0){
            constraint = constraint.toString().toUpperCase();

            ArrayList<Item> filtered = new ArrayList<>() ;

            for (int i=0 ; i< filterList.size() ; i++)
            {
                //check by product title
                if (filterList.get(i).getName().toUpperCase().contains(constraint))
                {
                    filtered.add(filterList.get(i));
                }
            }

            results.count = filtered.size();
            results.values = filtered ;
        }

        else {
            results.count = filterList.size();
            results.values = filterList ;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.list = (ArrayList<Item>) results.values ;
        adapter.notifyDataSetChanged();
    }
}
