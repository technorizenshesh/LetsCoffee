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

public class AdapterCartItem extends RecyclerView.Adapter<AdapterCartItem.mViewHolder> {
    Context mContext;
    ArrayList<ModelItem> data;
    private onSelectItemListener listener;

    public AdapterCartItem(Context mContext, ArrayList<ModelItem> data) {
        this.mContext = mContext;
        this.data = data;
    }

    public AdapterCartItem Callback(onSelectItemListener listener) {
        this.listener = listener;
        return this;
    }

    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_cart_item, parent, false);
        return new mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {
        ModelItem item = data.get(position);
        holder.tv_title.setText(data.get(position).getName());
        holder.tv_description.setText(data.get(position).getDescription());
        holder.tv_size.setText(data.get(position).getSize());
        holder.tv_qty.setText("" + data.get(position).getSelectedQty());
        holder.tv_price.setText(mContext.getResources().getString(R.string.currency)+" " + data.get(position).getPrice());
        Picasso.get().load(data.get(position).getImage()).placeholder(R.drawable.card_bg).into(holder.image);
        holder.itemView.setOnClickListener(v -> {
            listener.onItemDetails(data.get(position));
        });
        holder.img_add.setOnClickListener(v -> {
            int maxQty = Integer.parseInt(item.getMax_quantity());
            if (maxQty > item.getSelectedQty()) {
                item.setSelectedQty(item.getSelectedQty() + 1);
                listener.onAddToCart(data.get(position));
            } else {
                Toast.makeText(mContext, mContext.getString(R.string.mx_qty_msg) + item.getMax_quantity(), Toast.LENGTH_SHORT).show();
            }
            notifyDataSetChanged();
        });
        holder.img_remove.setOnClickListener(v -> {
            if (item.getSelectedQty() > 0) {
                item.setSelectedQty(item.getSelectedQty() - 1);
                listener.onRemoverToCart(data.get(position));
            }
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title, tv_description, tv_price, tv_qty,tv_size;
        ImageView image, img_add, img_remove;

        public mViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_description = itemView.findViewById(R.id.tv_description);
            tv_price = itemView.findViewById(R.id.tv_price);
            image = itemView.findViewById(R.id.image);
            img_add = itemView.findViewById(R.id.img_add);
            tv_qty = itemView.findViewById(R.id.tv_qty);
            img_remove = itemView.findViewById(R.id.img_remove);
            tv_size = itemView.findViewById(R.id.tv_size);
        }
    }
}
