package com.prosolstech.sugartrade;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.prosolstech.sugartrade.adapter.SellBidAdapterTestTimer;
import com.prosolstech.sugartrade.database.DataBaseConstants;
import com.prosolstech.sugartrade.database.DataBaseHelper;
import com.prosolstech.sugartrade.model.SellBidModel;
import com.prosolstech.sugartrade.util.ACU;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CountCutsom extends CountDownTimer {


    private long mill;
    private SellBidAdapterTestTimer.OriginalViewHolder holder;
    private SellBidModel sellBidModel;
    private Context context;



    public CountCutsom(long millisInFuture, long countDownInterval,  SellBidAdapterTestTimer.OriginalViewHolder holder, SellBidModel sellBidModel, Context context) {
        super(millisInFuture, countDownInterval);
        this.mill = millisInFuture;
        this.holder = holder;
        this.sellBidModel = sellBidModel;
        this.context = context;

    }

    @Override
    public void onTick(long millisUntilFinished) {

        mill = millisUntilFinished;
        long Seconds = mill / 1000;
        long Minutes = Seconds / 60;




        long hours = Minutes / 60; //since both are ints, you get an int
        long minutes = Minutes % 60;
        long seconds = Seconds % 60;


        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);


//                holder.txtTime.setText(timeLeftFormatted);


        if ((hours == 0 && minutes == 0)) {
            holder.txtTime.setText("Bid Over");

            updateValue(sellBidModel, context);


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

    @Override
    public void onFinish() {

    }


    private void updateValue(final SellBidModel sellBidModel, final Context context) {
        RequestQueue queue = Volley.newRequestQueue(context);

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
                params.put("role", ACU.MySP.getFromSP(context, ACU.MySP.ROLE, ""));
                params.put("offer_id", sellBidModel.getId());
                Log.e("BUYER_PARMAS", " ..... " + params.toString());

                return params;
            }
        };
        queue.add(stringRequest);
    }

}
