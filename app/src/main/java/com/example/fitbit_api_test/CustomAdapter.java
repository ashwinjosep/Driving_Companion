package com.example.fitbit_api_test;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private ArrayList<places> dataSet;

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textViewPlaceName;
        TextView textViewPlaceRating;
        TextView textViewPlaceVicinity;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.textViewPlaceName = (TextView) itemView.findViewById(R.id.placeNameTextView);
            this.textViewPlaceRating = (TextView) itemView.findViewById(R.id.placeRatingTextView);
            this.textViewPlaceVicinity = (TextView) itemView.findViewById(R.id.placeVicinityTextView);
        }
    }

    public CustomAdapter(ArrayList<places> data){
        this.dataSet=data;
    }

    @NonNull
    @Override
    public CustomAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cards_layout, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.MyViewHolder holder, int position) {
        TextView textViewPlaceName = holder.textViewPlaceName;
        TextView textViewPlaceRating = holder.textViewPlaceRating;
        TextView textViewPlaceVicinity = holder.textViewPlaceVicinity;

        textViewPlaceName.setText(dataSet.get(position).getName());
        textViewPlaceRating.setText(dataSet.get(position).getRating());
        textViewPlaceVicinity.setText(dataSet.get(position).getVicinity());
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}
