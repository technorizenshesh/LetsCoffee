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

import com.letscoffee.Interfaces.onSelectCouponListener;
import com.letscoffee.Interfaces.onSelectItemListener;
import com.letscoffee.Models.ModelCoupon;
import com.letscoffee.Models.ModelItem;
import com.letscoffee.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterCoupon extends RecyclerView.Adapter<AdapterCoupon.mViewHolder> {
    Context mContext;
    ArrayList<ModelCoupon> data;
    private onSelectCouponListener listener;

    public AdapterCoupon(Context mContext, ArrayList<ModelCoupon> data) {
        this.mContext = mContext;
        this.data = data;
    }

    public AdapterCoupon Callback(onSelectCouponListener listener) {
        this.listener = listener;
        return this;
    }

    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_coupon, parent, false);
        return new mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {
        ModelCoupon item = data.get(position);
        holder.tv_code.setText("use code\n"+data.get(position).getCoupon_code());
        holder.tv_valid.setText("Valid From "+item.getValid_from()+" To "+item.getValid_to());
        if (item.getType().equals("PERCENT")){
            holder.tv_description.setText("Use and Save "+data.get(position).getDiscount_percent()+"%");
        }else {
            holder.tv_description.setText("Use and Save "+data.get(position).getAmount()+" Flat");
        }
        holder.itemView.setOnClickListener(v -> {
            listener.onSelect(data.get(position));
        });


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder {
        TextView tv_code,tv_description,tv_valid;

        public mViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_code = itemView.findViewById(R.id.tv_code);
            tv_description = itemView.findViewById(R.id.tv_description);
            tv_valid = itemView.findViewById(R.id.tv_valid);
        }
    }
}
