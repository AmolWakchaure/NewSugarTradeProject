package com.prosolstech.sugartrade.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.prosolstech.sugartrade.R;
import com.prosolstech.sugartrade.activity.MyBidRequestedListActivity;
import com.prosolstech.sugartrade.classes.MyApplication;
import com.prosolstech.sugartrade.model.MyBid;
import com.prosolstech.sugartrade.model.MyBidRequestedList;
import com.prosolstech.sugartrade.util.ACU;
import com.prosolstech.sugartrade.util.DTU;
import com.prosolstech.sugartrade.util.ItemAnimation;

import java.util.List;

public class MyBidListRequestedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Activity ctx;
    private OnItemClickListener mOnItemClickListener;
    private int animation_type = 0;
    private List<MyBidRequestedList> array;


    public interface OnItemClickListener {
        void onItemClick(View view, Integer obj, int position);
    }


    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public MyBidListRequestedAdapter(Activity context, List<MyBidRequestedList> array, int animation_type) {
        ctx = context;
        this.array = array;
        this.animation_type = animation_type;


    }


    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView requested__tv, required_quantity_tv, season_tv, grade_tv, offered_quantity_tv, price_tv;
        public TextView MyRequestAdapterBtnAccept,required_quantity_tvlbl,offered_quantity_tvlbl;


        public OriginalViewHolder(View v) {
            super(v);
            requested__tv = (TextView) v.findViewById(R.id.requested__tv);
            required_quantity_tv = (TextView) v.findViewById(R.id.required_quantity_tv);
            season_tv = (TextView) v.findViewById(R.id.season_tv);
            grade_tv = (TextView) v.findViewById(R.id.grade_tv);
            grade_tv = (TextView) v.findViewById(R.id.grade_tv);
            offered_quantity_tv = (TextView) v.findViewById(R.id.offered_quantity_tv);
            price_tv = (TextView) v.findViewById(R.id.price_tv);
            MyRequestAdapterBtnAccept = v.findViewById(R.id.MyRequestAdapterBtnAccept);

            required_quantity_tvlbl = (TextView) v.findViewById(R.id.required_quantity_tvlbl);
            offered_quantity_tvlbl = (TextView) v.findViewById(R.id.offered_quantity_tvlbl);


        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_bid_list_requested_adapter, parent, false);
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

            MyBidRequestedList myBid = array.get(position);
            view.requested__tv.setText(DTU.changeDateTimeFormat(myBid.getRequestedTime()));

            view.grade_tv.setText(myBid.getGrade());



            if (myBid.getType().equalsIgnoreCase("Tender"))
            {
                view.price_tv.setText(myBid.getTenderPrice());

            } else {

                view.price_tv.setText(myBid.getPriceQty());
            }
         //   view.price_tv.setText(myBid.getPriceQty());


            view.season_tv.setText(myBid.getSeason());


            if (myBid.getIsinteredstre().equalsIgnoreCase("Accept")) {
                view.MyRequestAdapterBtnAccept.setText("Accepted");
            } else if (myBid.getIsinteredstre().equalsIgnoreCase("Reject")) {
                view.MyRequestAdapterBtnAccept.setText("Rejected");
            } else {
                view.MyRequestAdapterBtnAccept.setText("Pending");
            }

            String loginStatus = ACU.MySP.getFromSP(MyApplication.context, ACU.MySP.ROLE,"");

            if(loginStatus.equals("Seller"))
            {
                view.required_quantity_tvlbl.setText("Required Quantity : ");
                view.offered_quantity_tvlbl.setText("Offered Quantity : ");
                view.required_quantity_tv.setText(myBid.getAvailQty());
                view.offered_quantity_tv.setText(myBid.getReqty());
            }
            else  if(loginStatus.equals("Buyer"))
            {
                view.required_quantity_tvlbl.setText("Available Quantity : ");
                view.offered_quantity_tvlbl.setText("Required Quantity : ");
                view.required_quantity_tv.setText(myBid.getAvailQty());
                view.offered_quantity_tv.setText(myBid.getReqty());
            }

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
