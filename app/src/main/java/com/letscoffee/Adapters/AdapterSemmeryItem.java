package com.letscoffee.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.letscoffee.Interfaces.onSelectItemListener;
import com.letscoffee.Models.ModelItem;
import com.letscoffee.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterSemmeryItem extends RecyclerView.Adapter<AdapterSemmeryItem.mViewHolder> {
    Context mContext;
    ArrayList<ModelItem> data;
    private onSelectItemListener listener;

    public AdapterSemmeryItem(Context mContext, ArrayList<ModelItem> data) {
        this.mContext = mContext;
        this.data = data;
    }

    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_my_order, parent, false);
        return new mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {
        holder.tv_title.setText(data.get(position).getName());
        holder.tv_qty.setText("(" + data.get(position).getSelectedQty()+")");
        holder.tv_size.setText("(" + data.get(position).getSize()+")");
        holder.tv_qty.setVisibility(data.get(position).getSelectedQty()==0?View.GONE:View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title,  tv_qty,tv_size;
        public mViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_qty = itemView.findViewById(R.id.tv_unit);
            tv_size = itemView.findViewById(R.id.tv_size);
        }
    }
}
