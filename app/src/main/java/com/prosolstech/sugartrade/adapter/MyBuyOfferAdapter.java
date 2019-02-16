package com.prosolstech.sugartrade.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.prosolstech.sugartrade.R;
import com.prosolstech.sugartrade.activity.BuySellDashBoardActivity;
import com.prosolstech.sugartrade.activity.MyRequestedActivity;
import com.prosolstech.sugartrade.activity.PlaceBuyBidActivity;
import com.prosolstech.sugartrade.util.ACU;
import com.prosolstech.sugartrade.util.DTU;
import com.prosolstech.sugartrade.util.ItemAnimation;
import com.prosolstech.sugartrade.util.VU;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MyBuyOfferAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Activity ctx;
    private OnItemClickListener mOnItemClickListener;
    private int animation_type = 0;
    private JSONArray array;
    private String flag;
    private TabLayout tabLayout;

    public interface OnItemClickListener {
        void onItemClick(View view, Integer obj, int position);
    }


    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public  MyBuyOfferAdapter(Activity context, JSONArray array, int animation_type, TabLayout tabLayout) {
        ctx = context;
        this.array = array;
        this.animation_type = animation_type;
        this.tabLayout = tabLayout;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView txtRate, txtGrade, txtDate, txtQuantity, idTv, id_tv, qty_val, start_time_tv, end_time_tv, org_sell_tv, claimed_tv, org, cliamed;

        LinearLayout
                llRevert,
                startTime_li,
                endTime_li;

        TextView orginal_required_tv,original_required_et,aqquired_required_tv,aqquired_required_et;


        public OriginalViewHolder(View v) {
            super(v);
            txtRate = (TextView) v.findViewById(R.id.MyOfferActivityTxtRate);
            txtDate = (TextView) v.findViewById(R.id.MyOfferActivityTxtDate);
            txtGrade = (TextView) v.findViewById(R.id.MyOfferActivityTxtGrade);
            txtQuantity = (TextView) v.findViewById(R.id.MyOfferActivityTxtQuantity);
            idTv = (TextView) v.findViewById(R.id.id);
            llRevert = (LinearLayout) v.findViewById(R.id.MyOfferActivityLinearLayoutRevert);
            startTime_li = (LinearLayout) v.findViewById(R.id.startTime_li);
            endTime_li = (LinearLayout) v.findViewById(R.id.endTime_li);
            id_tv = v.findViewById(R.id.id_tv);
            qty_val = v.findViewById(R.id.qty_val);
            start_time_tv = v.findViewById(R.id.start_time_tv);
            end_time_tv = v.findViewById(R.id.end_time_tv);
            org_sell_tv = v.findViewById(R.id.org_sell_tv);
            claimed_tv = v.findViewById(R.id.claimed_tv);
            org = v.findViewById(R.id.org);
            cliamed = v.findViewById(R.id.cliamed);
            orginal_required_tv = v.findViewById(R.id.orginal_required_tv);
            original_required_et = v.findViewById(R.id.original_required_et);
            aqquired_required_tv = v.findViewById(R.id.aqquired_required_tv);
            aqquired_required_et= v.findViewById(R.id.aqquired_required_et);

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sell_bid_test, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Log.e("onBindViewHolder", "onBindViewHolder : " + position);
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;
            Log.e("BUY_TOSTRING", ": " + array.toString());
            try {


                view.start_time_tv.setText(DTU.changeTime(array.getJSONObject(position).getString("bid_start_time")));

                long hours5 = Long.parseLong(array.getJSONObject(position).getString("bid_end_time")) / 60; //since both are ints, you get an int
                long minutes5 = Long.parseLong(array.getJSONObject(position).getString("bid_end_time")) % 60;


                String timeLeftFormatted1 = String.format(Locale.getDefault(), "%02d:%02d", hours5, minutes5);
                view.end_time_tv.setText(timeLeftFormatted1);


                view.startTime_li.setVisibility(View.GONE);
                view.endTime_li.setVisibility(View.GONE);

                if (ACU.MySP.getFromSP(ctx, ACU.MySP.ROLE, "").equals("Seller")) {
                    view.qty_val.setText("Current Available Qty: ");
                    view.id_tv.setText("Sell Post Id: ");

                    view.original_required_et.setVisibility(View.GONE);
                    view.orginal_required_tv.setVisibility(View.GONE);
                    view.aqquired_required_tv.setVisibility(View.GONE);
                } else {
                    view.org.setVisibility(View.GONE);
                    view.cliamed.setVisibility(View.GONE);
                    view.claimed_tv.setVisibility(View.GONE);
                    view.org_sell_tv.setVisibility(View.GONE);
                    view.qty_val.setText("Current Required Qty (In Qtl): ");
                    view.id_tv.setText("Buy Post Id: ");
                    view.original_required_et.setVisibility(View.VISIBLE);
                    view.orginal_required_tv.setVisibility(View.VISIBLE);
                    view.aqquired_required_tv.setVisibility(View.VISIBLE);
                }
                view.idTv.setText(array.getJSONObject(position).getString("id"));
                view.txtRate.setText(array.getJSONObject(position).getString("price_per_qtl"));


                //Date/time pattern of input date
//                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                //Date/time pattern of desired output date
//                DateFormat outputformat = new SimpleDateFormat("dd-MM-yyyy hh:mm");
//                Date date = null;
//                String output = null;
//                try {
//                    //Conversion of input String to date
//                    date = df.parse(array.getJSONObject(position).getString("created_date"));
//                    //old date format to new date format
//                    output = outputformat.format(date);
//                    view.txtDate.setText(output);
//                } catch (ParseException pe) {
//                    pe.printStackTrace();
//                }


                view.txtDate.setText(DTU.changeDateTimeFormat(array.getJSONObject(position).getString("created_date")));

//                view.txtDate.setText(array.getJSONObject(position).getString("created_date"));


                view.txtGrade.setText(array.getJSONObject(position).getString("category"));
                view.txtQuantity.setText(array.getJSONObject(position).getString("curr_req_qty"));//curr req qty
                view.original_required_et.setText(array.getJSONObject(position).getString("required_qty"));//original req qty
                view.aqquired_required_et.setText(array.getJSONObject(position).getString("acquired_qty"));//acquired req qty



                if (array.getJSONObject(position).getString("offer_required_qty").equalsIgnoreCase("") || array.getJSONObject(position).getString("offer_required_qty").equalsIgnoreCase("null")) {
                    view.llRevert.setVisibility(View.GONE);
                    tabLayout.getTabAt(1).setText("My Post");
                } else {
                    view.llRevert.setVisibility(View.VISIBLE);
                    String tt = "MyPost●";
                    tabLayout.getTabAt(1).setText(tt);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent in = new Intent(ctx, PlaceBuyBidActivity.class);
                        in.putExtra("flag", "update");
                        in.putExtra("post_status", "single_postzz");

                        in.putExtra("data", String.valueOf(array.getJSONObject(position)));
                        ctx.startActivity(in);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            view.llRevert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent in = new Intent(ctx, MyRequestedActivity.class);
                        in.putExtra("check_flag", ACU.MySP.getFromSP(ctx, ACU.MySP.ROLE, ""));
                        in.putExtra("value", String.valueOf(array.getJSONObject(position)));
                        ctx.startActivity(in);
                        ((Activity)ctx).finish();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            setAnimation(view.itemView, position);
        }
    }


    public static String getddmmyyDate(String dt) {
        String dd = "", mm = "", yy = "";
        int i = 0;
        try {
            for (String retval : dt.split("-")) {
                if (i == 0)
                    yy = retval;
                else if (i == 1)
                    mm = retval;
                else
                    dd = retval;
                i++;
            }
            return (dd + "-" + mm + "-" + yy).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
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