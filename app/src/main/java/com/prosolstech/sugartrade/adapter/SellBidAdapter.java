package com.prosolstech.sugartrade.adapter;

import android.app.ProgressDialog;
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
import com.prosolstech.sugartrade.model.SellBidModel;
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

public class SellBidAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context ctx;
    private OnItemClickListener mOnItemClickListener;
    private int animation_type = 0;
    JSONArray array;
    List<SellBidModel> listSellBidModel;
    long mEndTime;

    private RefreshListner listner;

    public interface OnItemClickListener {
        void onItemClick(View view, Integer obj, int position);
    }


    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public SellBidAdapter(Context context, JSONArray array, int animation_type, List<SellBidModel> listSellBidModel, RefreshListner listner) {
        ctx = context;
        this.array = array;
        this.animation_type = animation_type;
        this.listSellBidModel = listSellBidModel;
        this.listner = listner;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView txtType, txtMillName, txtGrade, txtRate, txtTime, txtClaimed, txtAvalQty, idTv, id_tv, posted_at;
        LinearLayout llclaimed, llRate, typeIdLv, idLv;
        private ImageView SellerListAdapterImgUnFav;
        private CountDownTimer timer;

        public OriginalViewHolder(View v) {
            super(v);
            txtType = (TextView) v.findViewById(R.id.SellBidFragmentTxtType);
            idTv = v.findViewById(R.id.id);
            txtMillName = (TextView) v.findViewById(R.id.SellBidFragmentTxtMillName);
            txtTime = (TextView) v.findViewById(R.id.SellBidFragmentTxtTime);
            txtGrade = (TextView) v.findViewById(R.id.SellBidFragmentTxtGrade);
            txtRate = (TextView) v.findViewById(R.id.SellBidFragmentTxtRate);
            txtClaimed = (TextView) v.findViewById(R.id.SellBidFragmentTxtClaimed);
            txtAvalQty = (TextView) v.findViewById(R.id.SellBidFragmentTxtAvailableQty);
            llclaimed = (LinearLayout) v.findViewById(R.id.SellBidFragmentLinearLayoutClaimed);
            llRate = (LinearLayout) v.findViewById(R.id.SellBidFragmentLinearLayoutRate);
            typeIdLv = (LinearLayout) v.findViewById(R.id.typeIdLv);
            idLv = (LinearLayout) v.findViewById(R.id.idLv);
            id_tv = v.findViewById(R.id.id_tv);
            posted_at = v.findViewById(R.id.posted_at);
            SellerListAdapterImgUnFav = v.findViewById(R.id.SellerListAdapterImgUnFav);

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.test_one
                , parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    long millis;

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Log.e("onBindViewHolder", "onBindViewHolder : " + position);
        if (holder instanceof OriginalViewHolder) {
            final OriginalViewHolder view = (OriginalViewHolder) holder;
            try {
                Log.e("SELL_BID_array", ": " + " Sell_position  " + position + "   " + array.getJSONObject(position).toString());

                final SellBidModel sellBidModel = listSellBidModel.get(position);
                view.typeIdLv.setVisibility(View.VISIBLE);
                view.idLv.setVisibility(View.VISIBLE);
                view.txtType.setVisibility(View.VISIBLE);
                view.idTv.setVisibility(View.VISIBLE);


//                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                //Date/time pattern of desired output date
//                DateFormat outputformat = new SimpleDateFormat("dd-MM-yyyy hh:mm");
//                Date date = null;
//                String output = null;
//                try {
//                    //Conversion of input String to date
//                    date = df.parse(sellBidModel.getDate());
//                    //old date format to new date format
//                    output = outputformat.format(date);
//                    view.posted_at.setText(output);
//                } catch (ParseException pe) {
//                    pe.printStackTrace();
//                }


                view.posted_at.setText(DTU.changeDateTimeFormat(sellBidModel.getDate()));


//                view.posted_at.setText(sellBidModel.getDate());

                if (sellBidModel.getIs_favorite().equalsIgnoreCase("y")) {
                    view.SellerListAdapterImgUnFav.setVisibility(View.VISIBLE);
                } else if (sellBidModel.getIs_favorite().equalsIgnoreCase("N")) {
                    view.SellerListAdapterImgUnFav.setVisibility(View.GONE);

                }

                if (ACU.MySP.getFromSP(ctx, ACU.MySP.ROLE, "").equals("Seller")) {
                    view.id_tv.setText("Buy Post Id:");
                } else {
                    view.id_tv.setText("Sell Post Id:");
                }

                view.idTv.setText(sellBidModel.getId());
                view.txtType.setText(sellBidModel.getType());


                view.txtMillName.setText(sellBidModel.getCompany_name());

                view.txtGrade.setText(sellBidModel.getCategory());
                if (sellBidModel.getOriginal_qty().contains("-")) {
                    view.txtAvalQty.setText("0");
                } else {
                    view.txtAvalQty.setText(sellBidModel.getOriginal_qty());
                }


                if (!sellBidModel.getClaimed().equalsIgnoreCase("null")) {


//                    if (sellBidModel.getIsIntrested().equalsIgnoreCase("Accept")) {
                    view.txtClaimed.setText("(" + sellBidModel.getClaimed() + ")");
//                        view.txtAvalQty.setText(sellBidModel.getAvailable_qty()-);
//                    } else if (sellBidModel.getIsIntrested().equalsIgnoreCase("pending")) {
//                        view.txtClaimed.setText("(0)");
//                    }


                    // emd field to claimed 08-10-18
                } else {
                    view.txtClaimed.setText("(0)");
                }
                if (!sellBidModel.getPrice_per_qtl().equalsIgnoreCase("0")) {
                    view.llRate.setVisibility(View.VISIBLE);
                    view.txtRate.setText(sellBidModel.getPrice_per_qtl());
                } else {
                    view.llRate.setVisibility(View.GONE);
                }

                final SellBidModel sellBidModel1 = DataBaseHelper.DBSellBidData.getSellModel(sellBidModel.getId());


                if (sellBidModel1 != null) {
                    long mill = TimeUnit.MINUTES.toMillis(Long.parseLong(sellBidModel1.getValidity_time()));

                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                    timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    SellBidModel test3 = DataBaseHelper.DBSellBidData.getSellModel(sellBidModel.getId());


                    if (test3 != null) {
                        if (test3.getIsTimerRunning().equalsIgnoreCase("0")) {
                            try {

                                mill = Long.parseLong(test3.getEndtime()) - System.currentTimeMillis();


//                          Calendar calendar = Calendar.getInstance();
//                        int hours1 = calendar.get(Calendar.HOUR_OF_DAY);
//                        int minutes1 = calendar.get(Calendar.MINUTE);
//                        long hours = Long.parseLong(test3.getEnd_bid_time()) / 60; //since both are ints, you get an int
//                        long minutes = Long.parseLong(test3.getEnd_bid_time()) % 60;
//
//                        try {
//                            Date date5 = timeFormat.parse(hours + ":" + minutes);
//                            Date date6 = timeFormat.parse(hours1 + ":" + minutes1);
//                            long sum2 = date5.getTime() - date6.getTime();
//                            String date10 = timeFormat.format(new Date(sum2));
//                            mill = TimeUnit.MINUTES.toMillis(toMins(date10));
//
//                        } catch (ParseException e) {
//                            e.printStackTrace();
//                        }


                            } catch (Exception e) {


                            }
                        }


                        if (test3.getIs_deleted().equalsIgnoreCase("3")) {
                            updateValue(sellBidModel);
                        }


                    }

                    if (view.timer != null) {
                        view.timer.cancel();
                    }


//                    String dateTime[] = array.getJSONObject(position).getString("created_date").split(" ");
//                    String date = dateTime[0];
//                    try {
//                        SimpleDateFormat dateFormatOne = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//                        Date convertedDateCurrent = new Date();
//                        convertedDateCurrent = dateFormatOne.parse(DTU.getCurrentDateTimeStamp(DTU.YMD_HMS));
//                        SimpleDateFormat dateformatTwo = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//                        Date convertBidDate = new Date();
//                        convertBidDate = dateformatTwo.parse(date + " " + array.getJSONObject(position).getString("bid_start_time"));
//
//
//                        Log.e("convertedDateCurrent", ": " + convertedDateCurrent.toString());
//                        Log.e("convertBidDate", ": " + convertBidDate.toString());
//                        Log.e("CAMPARE_DATE_COND_SELL", ": " + convertedDateCurrent.after(convertBidDate));
//
//                        if (convertedDateCurrent.after(convertBidDate)) {


                    if (mill > 0) {
//                        startTimer(view, mill, sellBidModel1);



                        if (checkTime(array.getJSONObject(position).getString("bid_start_time"))) {
                            startTimer(view, mill, sellBidModel1);
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


                            Log.e("convertedDateCurrent", ": " + convertedDateCurrent.toString());
                            Log.e("convertBidDate", ": " + convertBidDate.toString());
                            Log.e("CAMPARE_DATE_COND_SELL", ": " + convertedDateCurrent.after(convertBidDate));


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


    private void startTimer(final OriginalViewHolder holder, final long mill, final SellBidModel sellBidModel) {

        long end = System.currentTimeMillis() + mill;


        holder.timer = new CountDownTimer(mill, 10000) {

            public void onTick(long millisUntilFinished) {


                long Seconds = millisUntilFinished / 1000;
                long Minutes = Seconds / 60;

                Log.e("Minutes", " : " + Minutes);


                long hours = Minutes / 60; //since both are ints, you get an int
                long minutes = Minutes % 60;


                String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);


                holder.txtTime.setText(timeLeftFormatted);


                if ((hours == 00 && minutes == 00) || (holder.txtTime.getText().toString().equalsIgnoreCase(""))) {
                    try {

//                        SellBidModel test2 = new SellBidModel();
//                        test2.setId(sellBidModel.getId());
//                        test2.setIsTimerRunning("1");//0 means true 1 means false
//
//                        try {
//                            DataBaseHelper.DBSellBidData.SellBidDataUpdate(DataBaseConstants.TableNames.TBL_SELL_BID_DATA, test2);
//                        } catch (Exception e) {
//                        }

//                        holder.txtTime.setText("Bid Over");


                        if (sellBidModel.getIs_deleted().equalsIgnoreCase("0")) {
                            SellBidModel sellBidModel1 = new SellBidModel();
                            sellBidModel1.setId(sellBidModel.getId());
                            sellBidModel1.setIs_deleted("1");
//                        sellBidModel1.setIsTimerRunning("1");//2 means deleted

                            try {
                                DataBaseHelper.DBSellBidData.SellBidDataUpdateValueIsDeleted(DataBaseConstants.TableNames.TBL_SELL_BID_DATA, sellBidModel1);
                            } catch (Exception e) {
                            }


                            updateValue(sellBidModel);
                        }


                    } catch (Exception e) {

                    }
                }


            }

            public void onFinish() {

            }

        }.start();


        SellBidModel test2 = new SellBidModel();
        test2.setId(sellBidModel.getId());
        test2.setEndtime(end + "");
        test2.setIsTimerRunning("0");//0 means true 1 means false

        try {
            DataBaseHelper.DBSellBidData.SellBidDataUpdate(DataBaseConstants.TableNames.TBL_SELL_BID_DATA, test2);
        } catch (Exception e) {
        }

    }


    private void updateValue(final SellBidModel sellBidModel) {
        RequestQueue queue = Volley.newRequestQueue(ctx);

        String url = "http://www.sugarcatalog.com/anakantuser/sugar_trade/index.php/API/removeOffer";      //for server
        Log.e("BUYER_BothURL", " ....." + url);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        Log.e("updateValue", response);


                        SellBidModel sellBidModel1 = new SellBidModel();
                        sellBidModel1.setId(sellBidModel.getId());
                        sellBidModel1.setIs_deleted("2");
//                        sellBidModel1.setIsTimerRunning("1");//2 means deleted

                        try {
                            DataBaseHelper.DBSellBidData.SellBidDataUpdateValueIsDeleted(DataBaseConstants.TableNames.TBL_SELL_BID_DATA, sellBidModel1);
                        } catch (Exception e) {
                        }


//                        listner.refresh(ctx);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("NoResponse", error.toString());


                SellBidModel sellBidModel1 = new SellBidModel();
                sellBidModel1.setId(sellBidModel.getId());
                sellBidModel1.setIs_deleted("3");

                try {
                    DataBaseHelper.DBSellBidData.SellBidDataUpdateValueIsDeleted(DataBaseConstants.TableNames.TBL_SELL_BID_DATA, sellBidModel1);
                } catch (Exception e) {
                }


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

        void refresh(Context context);
    }

}



