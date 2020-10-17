package com.letscoffee.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.letscoffee.Interfaces.onSelectCategoryListener;
import com.letscoffee.Interfaces.onSelectMembershipListener;
import com.letscoffee.Models.ModelCategory;
import com.letscoffee.Models.ModelMembership;
import com.letscoffee.R;

import java.util.ArrayList;

public class AdapterMembership extends RecyclerView.Adapter<AdapterMembership.mViewHolder> {
    Context mContext;
    ArrayList<ModelMembership>data;
    private onSelectMembershipListener listener;

    public AdapterMembership(Context mContext, ArrayList<ModelMembership> data) {
        this.mContext = mContext;
        this.data = data;
    }
    public AdapterMembership Callback(onSelectMembershipListener listener){
        this.listener=listener;
        return this;
    }

    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.layout_membership,parent,false);
        return new mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {
        holder.tv_name.setText(data.get(position).getName());
        holder.tv_amount.setText(mContext.getResources().getString(R.string.currency)+" " +data.get(position).getAmount());
        holder.tv_try_now.setOnClickListener(v->{
           listener.onSelectMembership(data.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder{
        TextView tv_name,tv_amount,tv_try_now;
        public mViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name=itemView.findViewById(R.id.tv_name);
            tv_amount=itemView.findViewById(R.id.tv_amount);
            tv_try_now=itemView.findViewById(R.id.tv_try_now);
        }
    }
}
