package com.letscoffee.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.letscoffee.Interfaces.onSelectShopListener;
import com.letscoffee.Models.ModelShop;
import com.letscoffee.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterNearShops extends RecyclerView.Adapter<AdapterNearShops.mViewHolder> {
    Context mContext;
    ArrayList<ModelShop>data;
    private onSelectShopListener listener;

    public AdapterNearShops(Context mContext, ArrayList<ModelShop> data) {
        this.mContext = mContext;
        this.data = data;
    }
    public AdapterNearShops Callback(onSelectShopListener listener){
        this.listener=listener;
        return this;
    }

    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.layout_near,parent,false);
        return new mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {
        holder.tv_title.setText(data.get(position).getName());
        holder.tv_address.setText(data.get(position).getAddress());
        holder.tv_status.setText(data.get(position).getRes_status());
        holder.rating.setRating(Float.valueOf(data.get(position).getRating()));
        holder.tv_title.setSelected(true);
        Picasso.get().load(data.get(position).getImage()).placeholder(R.drawable.logo).into(holder.image);
        holder.itemView.setOnClickListener(v->listener.onSelectShop(data.get(position)));
        holder.tv_rating.setText(data.get(position).getRating());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        RatingBar rating;
        TextView tv_title,tv_address,tv_status,tv_rating;
        public mViewHolder(@NonNull View itemView) {
            super(itemView);
            image=itemView.findViewById(R.id.image);
            tv_title=itemView.findViewById(R.id.tv_title);
            tv_address=itemView.findViewById(R.id.tv_address);
            tv_status=itemView.findViewById(R.id.tv_status);
            rating=itemView.findViewById(R.id.rating);
            tv_rating=itemView.findViewById(R.id.tv_rating);
        }
    }
}
