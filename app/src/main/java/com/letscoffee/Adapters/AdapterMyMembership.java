package com.letscoffee.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.letscoffee.Interfaces.onSelectMembershipOrderListener;
import com.letscoffee.Interfaces.onSelectOrderListener;
import com.letscoffee.Models.ModelMyMembership;
import com.letscoffee.Models.ModelMyOrder;
import com.letscoffee.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterMyMembership extends RecyclerView.Adapter<AdapterMyMembership.mViewHolder> {
    Context mContext;
    ArrayList<ModelMyMembership> data;
    private onSelectMembershipOrderListener listener;

    public AdapterMyMembership(Context mContext, ArrayList<ModelMyMembership> data) {
        this.mContext = mContext;
        this.data = data;
    }

    public AdapterMyMembership Callback(onSelectMembershipOrderListener listener) {
        this.listener = listener;
        return this;
    }

    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_my_membership, parent, false);
        return new mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {
        ModelMyMembership item = data.get(position);
        holder.tv_start_date.setText("Start Date "+item.getStart_date());
        holder.tv_times.setText("Times: "+item.getTimes());
        holder.tv_days.setText("Days: "+item.getDays());
        holder.tv_view.setOnClickListener(v -> {
            listener.onView(data.get(position));
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder {
        TextView tv_start_date, tv_days, tv_times,tv_view;

        public mViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_start_date = itemView.findViewById(R.id.tv_start_date);
            tv_days = itemView.findViewById(R.id.tv_days);
            tv_times = itemView.findViewById(R.id.tv_times);
            tv_view = itemView.findViewById(R.id.tv_view);
        }
    }
}
