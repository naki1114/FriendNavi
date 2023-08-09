package com.example.friendnavi;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    private ArrayList<ItemSearch> searchList;

    public class SearchViewHolder extends RecyclerView.ViewHolder {
        protected TextView name;
        protected TextView address;

        public SearchViewHolder(View view) {
            super(view);
            this.name = view.findViewById(R.id.name);
            this.address = view.findViewById(R.id.address);
        }
    }

    public SearchAdapter(ArrayList<ItemSearch> searchList) {
        this.searchList = searchList;
    }

    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_search, viewGroup, false);
        SearchViewHolder viewHolder = new SearchViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder viewHolder, int position) {

        viewHolder.name.setText(searchList.get(position).getName());
        viewHolder.address.setText(searchList.get(position).getAddress());

    }

    @Override
    public int getItemCount() {
        return (null != searchList ? searchList.size() : 0);
    }

}
