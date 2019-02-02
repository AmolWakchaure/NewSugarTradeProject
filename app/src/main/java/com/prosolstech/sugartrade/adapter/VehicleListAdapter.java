package com.prosolstech.sugartrade.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.prosolstech.sugartrade.R;
import com.prosolstech.sugartrade.activity.DetailsShowActivity;
import com.prosolstech.sugartrade.activity.PlaceSellBidActivity;
import com.prosolstech.sugartrade.util.ACU;
import com.prosolstech.sugartrade.util.ItemAnimation;
import com.prosolstech.sugartrade.util.VU;

import org.json.JSONArray;
import org.json.JSONException;

public class VehicleListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context ctx;
    private OnItemClickListener mOnItemClickListener;
    private int animation_type = 0;
    JSONArray array;

    public interface OnItemClickListener {
        void onItemClick(View view, Integer obj, int position);
    }


    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public VehicleListAdapter(Context context, JSONArray array, int animation_type) {
        ctx = context;
        this.array = array;
        this.animation_type = animation_type;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView txtVehicleNo,txtDate,txtQty,txtDriverNo,quantity_tv;


        public OriginalViewHolder(View v) {
            super(v);
            txtVehicleNo = (TextView) v.findViewById(R.id.VehicleListAdapterTxtVehicleNo);
            quantity_tv = (TextView) v.findViewById(R.id.quantity_tv);
            txtQty = (TextView) v.findViewById(R.id.VehicleListAdapterTxtQty);
            txtDate = (TextView) v.findViewById(R.id.VehicleListAdapterTxtDate);
            txtDriverNo = (TextView) v.findViewById(R.id.VehicleListAdapterTxtDriverNo);

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.vehicle_list, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Log.e("BuyerListAdapter", "onBindViewHolder : " + position);
        if (holder instanceof OriginalViewHolder) {
            final OriginalViewHolder view = (OriginalViewHolder) holder;
            Log.e("BuyerListAdapter", ": " + array.toString());
            try {

                if(ACU.MySP.getFromSP(ctx, ACU.MySP.ROLE, "").equals("Seller"))
                {
                    view.quantity_tv.setText("Sugar in Qtl : ");
                }

                view.txtVehicleNo.setText(array.getJSONObject(position).getString("vehicle_number"));
                //view.txtDate.setText(VU.formatDate(array.getJSONObject(position).getString("date_of_arrival")));
                view.txtDate.setText(VU.getddmmyyDate(VU.formatDate(array.getJSONObject(position).getString("date_of_arrival"))));
               // view.txtDate.setText(VU.formatDate(array.getJSONObject(position).getString("date_of_arrival")));
                view.txtQty.setText(array.getJSONObject(position).getString("quantity"));
                view.txtDriverNo.setText(array.getJSONObject(position).getString("driver_contact"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent in = new Intent(ctx, DetailsShowActivity.class);
                        in.putExtra("data", String.valueOf(array.getJSONObject(position)));
                        ctx.startActivity(in);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            setAnimation(view.itemView, position);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                on_attach = false;
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return array.length();
    }

    private int lastPosition = -1;
    private boolean on_attach = true;

    private void setAnimation(View view, int position) {
        if (position > lastPosition) {
            ItemAnimation.animate(view, on_attach ? position : -1, animation_type);
            lastPosition = position;
        }
    }
}