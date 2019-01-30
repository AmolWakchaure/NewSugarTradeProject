package com.prosolstech.sugartrade.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.prosolstech.sugartrade.R;

import com.prosolstech.sugartrade.activity.SellDetailsActivity;
import com.prosolstech.sugartrade.database.DataBaseConstants;
import com.prosolstech.sugartrade.database.DataBaseHelper;
import com.prosolstech.sugartrade.model.BuyBidModel;
import com.prosolstech.sugartrade.util.ACU;
import com.prosolstech.sugartrade.util.DTU;
import com.prosolstech.sugartrade.util.ItemAnimation;

import org.json.JSONArray;
import org.json.JSONException;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class BuyBidAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context ctx;
    private OnItemClickListener mOnItemClickListener;
    private int animation_type = 0;
    private JSONArray array;
    private List<BuyBidModel> listbuyBidModel;
    private RefreshListner refreshListner;

    public interface OnItemClickListener {
        void onItemClick(View view, Integer obj, int position);
    }


    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    //    public BuyBidAdapter(Context context, List<People> items, int animation_type) {
    public BuyBidAdapter(Context context, JSONArray array, int animation_type, List<BuyBidModel> listbuyBidModel, RefreshListner refreshListner) {
        ctx = context;
        this.array = array;
        this.animation_type = animation_type;
        this.listbuyBidModel = listbuyBidModel;
        this.refreshListner = refreshListner;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView txtMillName, txtGrade, txtRate, txtTime, txtClaimed, txtAvalQty, txtQty, txtId, id_tv, posted_at, SellBidFragmentTxtQty;
        LinearLayout llclaimed, typeIdLv;
        ImageView SellerListAdapterImgUnFav;
        CountDownTimer timer;


        public OriginalViewHolder(View v) {
            super(v);
            txtMillName = (TextView) v.findViewById(R.id.SellBidFragmentTxtMillName);
            txtTime = (TextView) v.findViewById(R.id.SellBidFragmentTxtTime);
            txtGrade = (TextView) v.findViewById(R.id.SellBidFragmentTxtGrade);
            txtRate = (TextView) v.findViewById(R.id.SellBidFragmentTxtRate);
            txtClaimed = (TextView) v.findViewById(R.id.SellBidFragmentTxtClaimed);
            txtAvalQty = (TextView) v.findViewById(R.id.SellBidFragmentTxtAvailableQty);
            txtQty = (TextView) v.findViewById(R.id.SellBidFragmentTxtAvailableQty);
            llclaimed = (LinearLayout) v.findViewById(R.id.SellBidFragmentLinearLayoutClaimed);
            typeIdLv = (LinearLayout) v.findViewById(R.id.idLv);
            txtId = v.findViewById(R.id.id);
            id_tv = v.findViewById(R.id.id_tv);
            SellerListAdapterImgUnFav = v.findViewById(R.id.SellerListAdapterImgUnFav);
            posted_at = v.findViewById(R.id.posted_at);
            SellBidFragmentTxtQty = v.findViewById(R.id.SellBidFragmentTxtQty);


        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_buy_bid, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Log.e("onBindViewHolder", "onBindViewHolder : " + position);
        if (holder instanceof OriginalViewHolder) {
            final OriginalViewHolder view = (OriginalViewHolder) holder;
            try {
                Log.e("BUY_BID_array", ": " + " Buy_position  " + position + "   " + array.getJSONObject(position).toString());

                final BuyBidModel buyBidModel = listbuyBidModel.get(position);


                if (buyBidModel.getIs_favorite().equalsIgnoreCase("y")) {
                    view.SellerListAdapterImgUnFav.setVisibility(View.VISIBLE);
                } else if (buyBidModel.getIs_favorite().equalsIgnoreCase("N")) {
                    view.SellerListAdapterImgUnFav.setVisibility(View.GONE);
                }


                view.typeIdLv.setVisibility(View.VISIBLE);
                if (ACU.MySP.getFromSP(ctx, ACU.MySP.ROLE, "").equals("Seller")) {
                    view.SellBidFragmentTxtQty.setText("Required Qty:");
                    view.id_tv.setText("Buy Post Id:");
                } else {
                    view.id_tv.setText("Sell Post Id:");
                }
                view.txtId.setText(buyBidModel.getId());


//                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                //Date/time pattern of desired output date
//                DateFormat outputformat = new SimpleDateFormat("dd-MM-yyyy hh:mm");
//                Date date = null;
//                String output = null;
//                try {
//                    //Conversion of input String to date
//                    date = df.parse(buyBidModel.getDate());
//                    //old date format to new date format
//                    output = outputformat.format(date);
//                    view.posted_at.setText(output);
//                } catch (ParseException pe) {
//                    pe.printStackTrace();
//                }


                view.posted_at.setText(DTU.changeDateTimeFormat(buyBidModel.getDate()));


//                view.posted_at.setText(buyBidModel.getDate());

                view.txtMillName.setText(buyBidModel.getCompany_name());
                view.txtGrade.setText(buyBidModel.getCategory());
                view.txtRate.setText(buyBidModel.getPrice_per_qtl());
                view.llclaimed.setVisibility(View.GONE);                                        // For Buyer Claimed not display
                view.txtQty.setText("Required Qty : ");
                view.txtAvalQty.setText(buyBidModel.getAvailable_qty());           // remove "bag_qty"  in place of "required_qty"  Add Required qty field for buyer


                final BuyBidModel buyBidModel1 = DataBaseHelper.DBBuyBidData.getBuyModel(buyBidModel.getId());


                if (buyBidModel1 != null) {

                    long mill = TimeUnit.MINUTES.toMillis(Long.parseLong(buyBidModel.getValidity_time()));
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                    timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

                    BuyBidModel test3 = DataBaseHelper.DBBuyBidData.getBuyModel(buyBidModel.getId());
                    if (test3 != null) {
                        if (test3.getIsTimerTuuning().equalsIgnoreCase("0")) {
                            mill = Long.parseLong(test3.getEndTime()) - System.currentTimeMillis();


//                        Calendar calendar = Calendar.getInstance();
//
//                        int hours1 = calendar.get(Calendar.HOUR_OF_DAY);
//                        int minutes1 = calendar.get(Calendar.MINUTE);
//
//
//                        long hours = Long.parseLong(test3.getBidEndTime()) / 60; //since both are ints, you get an int
//                        long minutes = Long.parseLong(test3.getBidEndTime()) % 60;
//
//                        try {
//                            Date date5 = timeFormat.parse(hours + ":" + minutes);
//                            Date date6 = timeFormat.parse(hours1 + ":" + minutes1);
//                            long sum2 = date5.getTime() - date6.getTime();
//
//
//                            String date10 = timeFormat.format(new Date(sum2));
//
//                            mill = TimeUnit.MINUTES.toMillis(toMins(date10));
//
//                        } catch (ParseException e) {
//                            e.printStackTrace();
//                        }


                            if (test3.getIs_delelted().equalsIgnoreCase("3")) {
                                updateValue(test3);
                            }

                        }

                    }


                    if (view.timer != null) {
                        view.timer.cancel();
                    }

//                    startTimer(view, mill, buyBidModel1);


                    if (mill > 0) {

                        if (checkTime(array.getJSONObject(position).getString("bid_start_time"))) {
                            startTimer(view, mill, buyBidModel1);
                        } else {

                            long Seconds = mill / 1000;
                            long Minutes = Seconds / 60;

                            Log.e("Minutes", " : " + Minutes);


                            long hours = Minutes / 60; //since both are ints, you get an int
                            long minutes = Minutes % 60;
                            String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
                            view.txtTime.setText(timeLeftFormatted);
                        }
                    } else {

                        updateValue(test3);

//                        view.txtTime.setText("Bid Over");
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view1) {
                    try {


                        String dateTime[] = array.getJSONObject(position).getString("created_date").split(" ");
                        String date = dateTime[0];

                        try {
                            SimpleDateFormat dateFormatOne = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                            Date convertedDateCurrent = new Date();
                            convertedDateCurrent = dateFormatOne.parse(DTU.getCurrentDateTimeStamp(DTU.YMD_HMS));

                            SimpleDateFormat dateformatTwo = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                            Date convertBidDate = new Date();
                            convertBidDate = dateformatTwo.parse(date + " " + array.getJSONObject(position).getString("bid_start_time"));


                            Log.e("bid_start_time", ": " + array.getJSONObject(position).getString("bid_start_time"));
                            Log.e("convertedDateCurrent", ": " + convertedDateCurrent.toString());
                            Log.e("convertBidDate", ": " + convertBidDate.toString());
                            Log.e("CAMPARE_DATE_COND_BUY", ": " + convertedDateCurrent.after(convertBidDate));

//                            if (convertBidDate.after(convertedDateCurrent)) {

                            if (checkTime(array.getJSONObject(position).getString("bid_start_time"))) {
                                Intent in = new Intent(ctx, SellDetailsActivity.class);
                                in.putExtra("flag", ACU.MySP.getFromSP(ctx, ACU.MySP.ROLE, ""));
                                in.putExtra("data", String.valueOf(array.getJSONObject(position)));
                                ctx.startActivity(in);
                            } else {
                                Toast.makeText(ctx, "This record not booked before offer start time", Toast.LENGTH_SHORT).show();
                            }


                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            setAnimation(view.itemView, position);
        }
    }


    private boolean checkTime(String time) {
        boolean isValid = true;
        Calendar calendar = Calendar.getInstance();
        int hours1 = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes1 = calendar.get(Calendar.MINUTE);
        int sec = calendar.get(Calendar.SECOND);

        String ho = "";
        String mi = "";
        String si = "";
        try {
            String[] p = time.split(":");
            ho = p[0];
            mi = p[1];
            si = p[2];
        } catch (Exception e) {

        }
        SimpleDateFormat parser = new SimpleDateFormat("HH:mm:ss");
        try {
            Date currentDate = parser.parse(hours1 + ":" + minutes1 + ":" + sec);
            Date startTime = parser.parse(ho + ":" + mi + ":" + si);


            if (currentDate.before(startTime)) {
                isValid = false;
            }

        } catch (ParseException e) {
            // Invalid date was entered
            Log.e(": ", e.toString());
        }


        return isValid;
    }


    private static int toMins(String s) {
        String[] hourMin = s.split(":");
        int hour = Integer.parseInt(hourMin[0]);
        int mins = Integer.parseInt(hourMin[1]);
        int hoursInMins = hour * 60;
        return hoursInMins + mins;
    }


    private void startTimer(final BuyBidAdapter.OriginalViewHolder holder, final long mill, final BuyBidModel bidModel) {

        long end = System.currentTimeMillis() + mill;


        holder.timer = new CountDownTimer(mill, 5000) {

            public void onTick(long millisUntilFinished) {

                long Seconds = millisUntilFinished / 1000;
                long Minutes = Seconds / 60;

                Log.e("Minutes", " : " + Minutes);


                long hours = Minutes / 60; //since both are ints, you get an int
                long minutes = Minutes % 60;


                String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);

                holder.txtTime.setText(timeLeftFormatted);
//                if ((hours == 0 && minutes == 0) || (holder.txtTime.getText().toString().equalsIgnoreCase(""))) {
////                    updateValue(bidModel);
//                }

                if ((hours == 00 && minutes == 00) || (holder.txtTime.getText().toString().equalsIgnoreCase(""))) {
                    BuyBidModel sellBidModel1 = new BuyBidModel();
                    sellBidModel1.setId(sellBidModel1.getId());
                    sellBidModel1.setIs_delelted("1");
//                        sellBidModel1.setIsTimerRunning("1");//2 means deleted


//                    holder.txtTime.setText("Bid Over");

                    try {
                        DataBaseHelper.DBBuyBidData.SellBidDataUpdateValueIsDeleted(DataBaseConstants.TableNames.TBL_BUY_BID_DATA, sellBidModel1);
                    } catch (Exception e) {
                    }

                    updateValue(bidModel);

                }


            }

            public void onFinish() {

            }

        }.start();


        BuyBidModel test2 = new BuyBidModel();
        test2.setId(bidModel.getId());
        test2.setEndTime(end + "");
        test2.setIsTimerTuuning("0");//0 means true 1 means false

        try {
            DataBaseHelper.DBBuyBidData.BuyBidDataUpdate(DataBaseConstants.TableNames.TBL_BUY_BID_DATA, test2);
        } catch (Exception e) {
        }
    }


    private void updateValue(final BuyBidModel sellBidModel) {
        RequestQueue queue = Volley.newRequestQueue(ctx);

        String url = "http://www.sugarcatalog.com/anakantuser/sugar_trade/index.php/API/removeOffer";      //for server
        Log.e("BUYER_BothURL", " ....." + url);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        Log.e("updateValue", " RESPONSE " + response);


                        BuyBidModel sellBidModel1 = new BuyBidModel();
                        sellBidModel1.setId(sellBidModel.getId());
                        sellBidModel1.setIs_delelted("2");
//                        sellBidModel1.setIsTimerRunning("1");//2 means deleted

                        try {
                            DataBaseHelper.DBBuyBidData.SellBidDataUpdateValueIsDeleted(DataBaseConstants.TableNames.TBL_BUY_BID_DATA, sellBidModel1);
                        } catch (Exception e) {
                        }
//                        refreshListner.refresh(ctx);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("No Response", error.toString());


            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("role", ACU.MySP.getFromSP(ctx, ACU.MySP.ROLE, ""));
                params.put("offer_id", sellBidModel.getId());
                Log.e("BUYER_PARMAS", " ..... " + params.toString());

                return params;
            }
        };
        queue.add(stringRequest);
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

    public interface RefreshListner {

        void refresh(Context ctx);
    }
}