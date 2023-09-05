package com.example.friendnavi;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TrafficOptionAdapter extends RecyclerView.Adapter<TrafficOptionAdapter.TrafficOptionViewHolder>{

    private ArrayList<TrafficOption> trafficOptionList;
    int selectedPosition;
    public TrafficOptionAdapter () {
        trafficOptionList = new ArrayList<>();
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int selectedPosition);
    }

    private OnItemClickListener listener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class TrafficOptionViewHolder extends RecyclerView.ViewHolder {

        LinearLayout optionItem;
        TextView option;
        TextView distance;
        TextView duration;
        TextView timeArrive;
        TextView tollFare;

        Context context;

        public TrafficOptionViewHolder (Context context, View view) {
            super(view);

            this.context = context;

            optionItem = view.findViewById(R.id.optionItem);
            option = view.findViewById(R.id.option);
            distance = view.findViewById(R.id.distance);
            duration = view.findViewById(R.id.duration);
            timeArrive = view.findViewById(R.id.timeArrive);
            tollFare = view.findViewById(R.id.tollFare);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int selectPosition = getAdapterPosition();
                    int oldPosition = 0;
                    if (selectPosition != RecyclerView.NO_POSITION) {
                        if (listener != null) {
                            selectedPosition = selectPosition;
                            listener.onItemClick(view, selectPosition);
                        }
                    }
                }
            });
        }

    }

    @NonNull
    @Override
    public TrafficOptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_option, parent, false);

        TrafficOptionViewHolder viewholder = new TrafficOptionViewHolder(context, view);

        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull TrafficOptionViewHolder holder, int position) {
        String option = trafficOptionList.get(position).getOption();
        String timeArrive = trafficOptionList.get(position).getTimeArrive();
        String duration = trafficOptionList.get(position).getDuration();
        String distance = trafficOptionList.get(position).getDistance();
        String tollFare = trafficOptionList.get(position).getTollFare();

        holder.option.setText(option);
        holder.timeArrive.setText(timeArrive);
        holder.duration.setText(duration);
        holder.distance.setText(distance);
        holder.tollFare.setText(tollFare);

        if (position == selectedPosition) {
            holder.optionItem.setBackgroundColor(ContextCompat.getColor(holder.context, R.color.theme));
        }
        else {
            holder.optionItem.setBackgroundColor(ContextCompat.getColor(holder.context, R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return trafficOptionList.size();
    }

    public void addData(TrafficOption trafficOption) {
        trafficOptionList.add(trafficOption);
    }

}
