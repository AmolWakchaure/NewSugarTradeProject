package com.prosolstech.sugartrade.adapter;

import android.app.Activity;
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
import com.prosolstech.sugartrade.CountCutsom;
import com.prosolstech.sugartrade.R;
import com.prosolstech.sugartrade.activity.SellDetailsActivity;
import com.prosolstech.sugartrade.database.DataBaseConstants;
import com.prosolstech.sugartrade.database.DataBaseHelper;
import com.prosolstech.sugartrade.model.SellBidModel;
import com.prosolstech.sugartrade.util.ACU;
import com.prosolstech.sugartrade.util.DTU;
import com.prosolstech.sugartrade.util.ItemAnimation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class SellBidAdapterTestTimer extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Activity ctx;
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

    public SellBidAdapterTestTimer(Activity context, JSONArray array, int animation_type, List<SellBidModel> listSellBidModel, RefreshListner listner) {
        ctx = context;
        this.array = array;
        this.animation_type = animation_type;
        this.listSellBidModel = listSellBidModel;
        this.listner = listner;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView txtType, txtMillName, txtGrade, txtRate, txtTime, txtClaimed, txtAvalQty, idTv, id_tv, posted_at, start_time, end_time;
        ;
        LinearLayout llclaimed, llRate, typeIdLv, idLv;
        private ImageView SellerListAdapterImgUnFav;
        public CountDownTimer timer;
        private Timer timerOne;
        private TimerTask timerTwo;

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
            start_time = v.findViewById(R.id.start_time);
            end_time = v.findViewById(R.id.end_time);

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.test_buyer_dashboard
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
                view.posted_at.setText(DTU.changeDateTimeFormat(sellBidModel.getDate()));

                if (sellBidModel.getIs_favorite().equalsIgnoreCase("y")) {
                    view.SellerListAdapterImgUnFav.setVisibility(View.VISIBLE);
                } else if (sellBidModel.getIs_favorite().equalsIgnoreCase("N")) {
                    view.SellerListAdapterImgUnFav.setVisibility(View.GONE);

                }


                view.start_time.setText(DTU.changeTime(sellBidModel.getBid_start_time()));


                long hours5 = Long.parseLong(sellBidModel.getEnd_bid_time()) / 60; //since both are ints, you get an int
                long minutes5 = Long.parseLong(sellBidModel.getEnd_bid_time()) % 60;


                String timeLeftFormatted1 = String.format(Locale.getDefault(), "%02d:%02d", hours5, minutes5);
                view.end_time.setText(timeLeftFormatted1);


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


                if (view.timerOne != null) {
                    view.timerOne.cancel();
                    view.timerTwo.cancel();
                }


                view.timerOne = new Timer();
                view.timerOne.schedule(view.timerTwo = new TimerTask() {
                    @Override
                    public void run() {
                        Log.e("kjgjhgjh ", "abcc");
//                        fetchCategoryName(sellBidModel, view);
                    }
                }, 30000, 30000);


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


                    if (sellBidModel1.getIsTimerRunning().equalsIgnoreCase("0")) {
                        Calendar calendar = Calendar.getInstance();
                        int hours1 = calendar.get(Calendar.HOUR_OF_DAY);
                        int minutes1 = calendar.get(Calendar.MINUTE);


                        long hours = Long.parseLong(sellBidModel1.getEnd_bid_time()) / 60; //since both are ints, you get an int
                        long minutes = Long.parseLong(sellBidModel.getEnd_bid_time()) % 60;

                        try {
                            Date date5 = timeFormat.parse(hours + ":" + minutes);
                            Date date6 = timeFormat.parse(hours1 + ":" + minutes1);
                            long sum2 = date5.getTime() - date6.getTime();


                            if (sum2 > 0) {
                                String date10 = timeFormat.format(new Date(sum2));
                                mill = TimeUnit.MINUTES.toMillis(toMins(date10));
                            } else {


                                SellBidModel test2 = new SellBidModel();
                                test2.setId(sellBidModel1.getId());
                                test2.setIsTimerRunning("3");//0 means true 1 means false

                                try {
                                    DataBaseHelper.DBSellBidData.SellBidDataUpdateTest(DataBaseConstants.TableNames.TBL_SELL_BID_DATA, test2);
                                } catch (Exception e) {
                                }

//                                view.txtTime.setText("Bid Over");

                                updateValue(sellBidModel1);
                            }

                        } catch (Exception e) {

                        }
                    } else if (sellBidModel1.getIsTimerRunning().equalsIgnoreCase("1")) {

                        Calendar calendar = Calendar.getInstance();
                        int hours1 = calendar.get(Calendar.HOUR_OF_DAY);
                        int minutes1 = calendar.get(Calendar.MINUTE);


                        long hours = Long.parseLong(sellBidModel1.getEnd_bid_time()) / 60; //since both are ints, you get an int
                        long minutes = Long.parseLong(sellBidModel.getEnd_bid_time()) % 60;

                        try {
                            Date date5 = timeFormat.parse(hours + ":" + minutes);
                            Date date6 = timeFormat.parse(hours1 + ":" + minutes1);
                            long sum2 = date5.getTime() - date6.getTime();


                            if (sum2 > 0) {
                                String date10 = timeFormat.format(new Date(sum2));
                                mill = TimeUnit.MINUTES.toMillis(toMins(date10));
                            } else {


                                SellBidModel test2 = new SellBidModel();
                                test2.setId(sellBidModel1.getId());
                                test2.setIsTimerRunning("3");//0 means true 1 means false

                                try {
                                    DataBaseHelper.DBSellBidData.SellBidDataUpdateTest(DataBaseConstants.TableNames.TBL_SELL_BID_DATA, test2);
                                } catch (Exception e) {
                                }

                                view.txtTime.setText("Bid Over");

                                updateValue(sellBidModel1);

                            }

                        } catch (Exception e) {

                        }


                    }


                    if (view.timer != null) {
                        view.timer.cancel();
                    }


                    final SellBidModel sellBidModel2 = DataBaseHelper.DBSellBidData.getSellModel(sellBidModel.getId());

                    if (sellBidModel2.getIsTimerRunning().equalsIgnoreCase("0") || sellBidModel2.getIsTimerRunning().equalsIgnoreCase("1")) {


                        Log.e("checkTimeOne: ", "isItComing");
                        if (checkTimeOne(array.getJSONObject(position).getString("bid_start_time"))) {
                            if (view.timer != null) {
                                view.timer.cancel();
                                view.timer = null;
                            }
                            startTimer(view, mill, sellBidModel2);


//                            final CountCutsom countCutsom = new CountCutsom(mill, 10000, view, sellBidModel1, ctx);
//                            countCutsom.start();
                        } else {


                            long hours = Long.parseLong(sellBidModel1.getValidity_time()) / 60; //since both are ints, you get an int
                            long minutes = Long.parseLong(sellBidModel.getValidity_time()) % 60;
                            String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);

                            view.txtTime.setText(timeLeftFormatted);


                        }
                    } else {
                        view.txtTime.setText("Bid Over");
                        updateValue(sellBidModel1);

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

//                            if (convertedDateCurrent.after(convertBidDate)) {


//                            if (!view.txtAvalQty.getText().toString().equalsIgnoreCase("0")) {
                                if (checkTime(array.getJSONObject(position).getString("bid_start_time"))) {

                                    if (!view.txtTime.getText().toString().equalsIgnoreCase("Bid Over")) {


                                        Intent in = new Intent(ctx, SellDetailsActivity.class);
                                        in.putExtra("flag", ACU.MySP.getFromSP(ctx, ACU.MySP.ROLE, ""));
                                        in.putExtra("data", String.valueOf(array.getJSONObject(position)));
                                        ctx.startActivity(in);
                                    } else {
                                        Toast.makeText(ctx, "Bid is over!", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(ctx, "This record not booked before offer start time", Toast.LENGTH_SHORT).show();
                                }

//                            else {
//                                Toast.makeText(ctx, "This request is fulfill", Toast.LENGTH_SHORT).show();
//
//                            }

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


    private boolean checkTimeOne(String time) {
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


            if (currentDate.before(startTime) || currentDate.equals(startTime)) {
                isValid = false;
            }

        } catch (ParseException e) {
            // Invalid date was entered
            Log.e(": ", e.toString());
        }


        return isValid;
    }


    public void fetchCategoryName(final SellBidModel sellBidModel, final SellBidAdapterTestTimer.OriginalViewHolder view) {
        RequestQueue queue = Volley.newRequestQueue(ctx);
        String url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/getQuantityByOffer";


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            Log.e("Category_NAME1", " " + response);
                            JSONArray jsonArray = null;
                            jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                final JSONObject jsonObject = jsonArray.getJSONObject(i);

                                ctx.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            String original_qty = jsonObject.getString("original_qty");
                                            String claimed = jsonObject.getString("claimed");
                                            view.txtAvalQty.setText(original_qty.equalsIgnoreCase("null") ? view.txtAvalQty.getText().toString() : original_qty);
                                            view.txtClaimed.setText(claimed.equalsIgnoreCase("null") ? "0" : claimed);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });


                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("No Response", " GET ");
            }
        })

        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("offer_id", sellBidModel.getId() + "");
                Log.e("getParams: ", params + "");
                return params;

            }
        };
        queue.add(stringRequest);
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

        if (holder.timer != null) {
            holder.timer = null;
        }

        holder.timer = new CountDownTimer(mill, 10000) {

            public void onTick(long millisUntilFinished) {


                long Seconds = millisUntilFinished / 1000;
                long Minutes = Seconds / 60;

                Log.e("Minutes", " : " + Minutes);
                Log.e("Hashcode", " : " + holder.timer.hashCode());


                long hours = Minutes / 60; //since both are ints, you get an int
                long minutes = Minutes % 60;
                long seconds = Seconds % 60;


                String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);


//                holder.txtTime.setText(timeLeftFormatted);


                if ((hours == 0 && minutes == 0)) {
                    holder.txtTime.setText("Bid Over");

                    updateValue(sellBidModel);


                    SellBidModel test2 = new SellBidModel();
                    test2.setId(sellBidModel.getId());
                    test2.setIsTimerRunning("3");//0 means true 1 means false

                    try {
                        DataBaseHelper.DBSellBidData.SellBidDataUpdateTest(DataBaseConstants.TableNames.TBL_SELL_BID_DATA, test2);
                    } catch (Exception e) {
                    }


                } else {
                    Log.e("seconds", " : " + seconds);
                    holder.txtTime.setText(timeLeftFormatted);
                }
            }


            public void onFinish() {

            }

        }.start();

        if (sellBidModel.getIsTimerRunning().equalsIgnoreCase("1") || sellBidModel.getIsTimerRunning().equalsIgnoreCase("0")) {

            SellBidModel test2 = new SellBidModel();
            test2.setId(sellBidModel.getId());
            test2.setEndtime(end + "");
            test2.setIsTimerRunning("0");//0 means true 1 means false

            try {
                DataBaseHelper.DBSellBidData.SellBidDataUpdate(DataBaseConstants.TableNames.TBL_SELL_BID_DATA, test2);
            } catch (Exception e) {
            }
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



