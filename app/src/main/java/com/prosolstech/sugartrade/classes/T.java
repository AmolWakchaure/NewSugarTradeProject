package com.prosolstech.sugartrade.classes;

import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class T
{
    /*
    buy_offer_book.required_qty AS ordered_qty,buy_bid.required_qty AS original_required_qty, buy_offer_book.allotted AS alloted_quantity,buy_offer_book.curr_req_qty AS current_required_qty,buy_offer_book.offer_id, buy_offer_book.required_qty_id,buy_offer_book.id, buy_offer_book.is_interested, buy_offer_book.date, buy_offer_book.time, buy_offer_book.userby, buy_offer_book.created_date as requestedAT, buy_bid.id as offerId, buy_bid.validity_time, buy_bid.bid_start_time, buy_bid.bid_end_time, buy_bid.category, buy_bid.production_year, buy_bid.price_per_qtl, buy_bid.payment_date, buy_bid.due_delivery_date, buy_bid.remark, buy_bid.emd, buy_bid.userby as bidAddedBy, buy_bid.send_to, buy_bid.added_on, buy_bid.created_date,buy_bid.curr_req_qty,buy_bid.acquired_qty,user.id as userId, user.name, user.company_name, product_category.id as catId, product_category.category as catName
     */


    public static String getSystemDateTime()
    {

        String systemTime = null;

        try
        {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.ENGLISH);
            Calendar cal = Calendar.getInstance();
            systemTime = df.format(cal.getTime());
        }
        catch (Exception e)
        {


        }
        return systemTime;

    }
    public static String returnSeconds(String endTimeInput)
    {
        String status = null;
        try
        {
            String [] dsfgd = getSystemDateTime().split(" ");
            String [] dsfgdzz = dsfgd[1].split(":");
            long seconds = (Integer.valueOf(dsfgdzz[0]) * 60 * 60)+(Integer.valueOf(dsfgdzz[1]) * 60) + Integer.valueOf(dsfgdzz[2]) * 60;

            long endTime = Long.valueOf(endTimeInput) * 60;

            T.e("seconds : "+seconds);
            T.e("endTime : "+endTime);

            if(seconds >= endTime)
            {
                status = "end";
            }
            else
            {
                status = "start";
            }


        }
        catch(Exception e)
        {
            T.e(""+e);
            e.printStackTrace();
        }
        return status;
    }
    public static void t(String message)
    {
        Toast.makeText(MyApplication.context,message,Toast.LENGTH_LONG).show();
    }
    public static void e(String message)
    {
        Log.e("SUGAR_TRADE_LOG",message);
    }

    public static String returnTimeFormat(int seconds)
    {
        //long Seconds = seconds / 1000;
        long Minutes = seconds / 60;
        Log.e("Minutes", " : " + Minutes);
        long hours = Minutes / 60; //since both are ints, you get an int
        long minutes = Minutes % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);

    }
    public static String returnEndTime(String endTime)
    {
        long hours = Long.parseLong(endTime) / 60; //since both are ints, you get an int
        long minutes = Long.parseLong(endTime) % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);    }

    public static String formatTimeStamp(String inputTimeStamp)
    {

        String [] data = inputTimeStamp.split(" ");
        String [] dateData = data[0].split("-");

        String [] time_data = data[1].split(":");
        String formatedTimeStamp = dateData[2]+"-"+dateData[1]+"-"+dateData[0]+" "+time_data[0]+":"+time_data[1];
        return formatedTimeStamp;
    }

}
