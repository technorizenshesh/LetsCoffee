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

public class AdapterCategoryName extends RecyclerView.Adapter<AdapterCategoryName.mViewHolder> {
    Context mContext;
    ArrayList<ModelCategory>data;
    private onSelectCategoryListener listener;

    public AdapterCategoryName(Context mContext, ArrayList<ModelCategory> data) {
        this.mContext = mContext;
        this.data = data;
    }
    public AdapterCategoryName Callback(onSelectCategoryListener listener){
        this.listener=listener;
        return this;
    }

    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.layout_category_name,parent,false);
        return new mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {
        holder.cat_name.setText(data.get(position).getName());
        holder.cat_name.setSelected(true);
        holder.cat_name.setBackgroundResource(data.get(position).isSelected()?R.drawable.bg_underline:R.color.white);
        holder.cat_name.setOnClickListener(v->{
            for (int i=0;i<getItemCount();i++){
                data.get(i).setSelected(false);
            }
            data.get(position).setSelected(true);
            notifyDataSetChanged();
            listener.onSelectCategory(data.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder{
        TextView cat_name;
        public mViewHolder(@NonNull View itemView) {
            super(itemView);
            cat_name=itemView.findViewById(R.id.cat_name);
        }
    }
}
