package com.letscoffee.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.letscoffee.Models.ModelCategory;
import com.letscoffee.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterBanner extends RecyclerView.Adapter<AdapterBanner.mViewHolder> {
    Context mContext;
    ArrayList<String>data;

    public AdapterBanner(Context mContext, ArrayList<String> data) {
        this.mContext = mContext;
        this.data = data;
    }

    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.layout_banner,parent,false);
        return new mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {
        Picasso.get().load(data.get(position)).into(holder.banner_image);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder{
        ImageView banner_image;
        public mViewHolder(@NonNull View itemView) {
            super(itemView);
            banner_image=itemView.findViewById(R.id.banner_image);
        }
    }
}
