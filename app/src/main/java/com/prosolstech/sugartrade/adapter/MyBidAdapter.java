package com.prosolstech.sugartrade.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.prosolstech.sugartrade.R;
import com.prosolstech.sugartrade.activity.BookingDetailsActivity;
import com.prosolstech.sugartrade.activity.MyBidDetailActivity;
import com.prosolstech.sugartrade.activity.MyBidRequestedListActivity;
import com.prosolstech.sugartrade.model.MyBid;
import com.prosolstech.sugartrade.util.ACU;
import com.prosolstech.sugartrade.util.DTU;
import com.prosolstech.sugartrade.util.ItemAnimation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MyBidAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Activity ctx;
    private OnItemClickListener mOnItemClickListener;
    private int animation_type = 0;
    private List<MyBid> array;


    public interface OnItemClickListener {
        void onItemClick(View view, Integer obj, int position);
    }


    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public MyBidAdapter(Activity context, List<MyBid> array, int animation_type) {
        ctx = context;
        this.array = array;
        this.animation_type = animation_type;


    }


    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView id_my_bid, txtDate, type_id, type_my_bid;
        public LinearLayout type_lv, tap_lv;
        public Button view_details_btn, view_bids_btn;

        public OriginalViewHolder(View v) {
            super(v);
            id_my_bid = (TextView) v.findViewById(R.id.id_my_bid);
            type_id = (TextView) v.findViewById(R.id.type_id);
            txtDate = (TextView) v.findViewById(R.id.company_named);
            view_details_btn = v.findViewById(R.id.view_details_btn);
            tap_lv = v.findViewById(R.id.tap_lv);
            type_lv = v.findViewById(R.id.type_lv);
            type_my_bid = v.findViewById(R.id.type_my_bid);
            view_bids_btn = v.findViewById(R.id.view_bids_btn);

        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_bid_list_item, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Log.e("onBindViewHolder", "onBindViewHolder : " + position);
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;
            Log.e("array_TOSTRING", ": " + array.toString());

            final MyBid myBid = array.get(position);

            if (ACU.MySP.getFromSP(ctx, ACU.MySP.ROLE, "").equalsIgnoreCase("Buyer")) {
                view.type_id.setText("Sell Post Id: ");
                view.id_my_bid.setText(myBid.getId());
                view.type_lv.setVisibility(View.VISIBLE);
                view.type_my_bid.setText(myBid.getType());

            } else {
                view.type_lv.setVisibility(View.GONE);
                view.type_id.setText("Buy Post Id: ");
                view.id_my_bid.setText(myBid.getId());

            }
            view.txtDate.setText(myBid.getCompanyName());


            view.view_bids_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ctx, MyBidRequestedListActivity.class);
                    intent.putExtra("MyBid", myBid);
                    ctx.startActivity(intent);

                }
            });


            view.view_details_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Intent intent = new Intent(ctx, MyBidDetailActivity.class);
                    intent.putExtra("MyBid", myBid);
                    ctx.startActivity(intent);


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
        return array.size();
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
