package com.letscoffee.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.letscoffee.Interfaces.onSelectCategoryListener;
import com.letscoffee.Models.ModelCategory;
import com.letscoffee.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.mViewHolder> {
    Context mContext;
    ArrayList<ModelCategory>data;
    private onSelectCategoryListener listener;

    public AdapterCategory(Context mContext, ArrayList<ModelCategory> data) {
        this.mContext = mContext;
        this.data = data;
    }
    public AdapterCategory Callback(onSelectCategoryListener listener){
        this.listener=listener;
        return this;
    }

    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.layout_category,parent,false);
        return new mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {
        holder.cat_name.setText(data.get(position).getName());
        holder.cat_name.setSelected(true);
        Picasso.get().load(data.get(position).getImage()).into(holder.cat_image);
        holder.itemView.setOnClickListener(v->listener.onSelectCategory(data.get(position)));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder{
        CircleImageView cat_image;
        TextView cat_name;
        public mViewHolder(@NonNull View itemView) {
            super(itemView);
            cat_image=itemView.findViewById(R.id.cat_image);
            cat_name=itemView.findViewById(R.id.cat_name);
        }
    }
}
