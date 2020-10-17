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
import com.letscoffee.Interfaces.onSelectOrderListener;
import com.letscoffee.Models.ModelItem;
import com.letscoffee.Models.ModelMyOrder;
import com.letscoffee.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterMyOrder extends RecyclerView.Adapter<AdapterMyOrder.mViewHolder> {
    Context mContext;
    ArrayList<ModelMyOrder> data;
    private onSelectOrderListener listener;

    public AdapterMyOrder(Context mContext, ArrayList<ModelMyOrder> data) {
        this.mContext = mContext;
        this.data = data;
    }

    public AdapterMyOrder Callback(onSelectOrderListener listener) {
        this.listener = listener;
        return this;
    }

    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_order, parent, false);
        return new mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {
        ModelMyOrder item = data.get(position);
        holder.tv_shop_name.setText(item.getShop_name());
        holder.tv_time.setText(item.getBook_time());
        holder.tv_amount.setText(mContext.getResources().getString(R.string.currency)+" " + item.getAmount());
        Picasso.get().load(item.getShop_image()).placeholder(R.drawable.card_bg).into(holder.image);
        holder.tv_view.setOnClickListener(v -> {
            listener.onView(data.get(position));
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder {
        TextView tv_shop_name, tv_amount, tv_view,tv_time;
        ImageView image;

        public mViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_shop_name = itemView.findViewById(R.id.tv_shop_name);
            tv_amount = itemView.findViewById(R.id.tv_amount);
            image = itemView.findViewById(R.id.image);
            tv_view = itemView.findViewById(R.id.tv_view);
            tv_time = itemView.findViewById(R.id.tv_time);
        }
    }
}
