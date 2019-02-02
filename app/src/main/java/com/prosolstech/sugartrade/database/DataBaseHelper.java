package com.prosolstech.sugartrade.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.EditText;

import com.prosolstech.sugartrade.model.BuyBidModel;
import com.prosolstech.sugartrade.model.CategoryModel;
import com.prosolstech.sugartrade.model.DistrictModelClass;
import com.prosolstech.sugartrade.model.ModelNotification;
import com.prosolstech.sugartrade.model.SeasonModel;
import com.prosolstech.sugartrade.model.SellBidModel;

import org.json.JSONArray;
import org.json.JSONObject;


public class DataBaseHelper {
    public static SQLiteDatabase db;
    public Context context;
    private SQLiteHelper sqliteopenhelper;
    private JSONArray jArray;
    private boolean isExist = false;
    private String str_column_name;
    private String TAG = "DataBaseHelper";
    private JSONObject json_data;
    private EditText et_search;

    public DataBaseHelper(Context context) {
        this.context = context;
        sqliteopenhelper = new SQLiteHelper(context);
        db = sqliteopenhelper.getWritableDatabase();
        db = sqliteopenhelper.getReadableDatabase();
    }


    public static class DistrictDB {

        public static boolean districtNameInsert(DistrictModelClass object) {

            ContentValues cv = new ContentValues();
            cv.put(DataBaseConstants.DistrictName.DISTRICT_ID, object.getdistrict_id());
            cv.put(DataBaseConstants.DistrictName.DISTRICT_NAME, object.getDistrict_name());


            db.insert(DataBaseConstants.TableNames.TBL_DISTRICT, null, cv);
            return true;
        }

        public static Cursor getDistrictName() {
            Cursor cursor = null;
            cursor = db.rawQuery("SELECT * FROM " + DataBaseConstants.TableNames.TBL_DISTRICT, null);
            return cursor;
        }

        public static boolean deleteDistrictData(String tablename) {

            db.delete(tablename, null, null);
            return true;
        }
    }

    public static class CategoryDB {

        public static boolean CategoryNameInsert(CategoryModel object) {

            ContentValues cv = new ContentValues();
            cv.put(DataBaseConstants.CategoryName.CATEGORY_ID, object.getCategory_id());
            cv.put(DataBaseConstants.CategoryName.CATEGORY, object.getCategory());
            cv.put(DataBaseConstants.CategoryName.PRODUCT_NAME, object.getProduct_name());


            db.insert(DataBaseConstants.TableNames.TBL_CATEGORY, null, cv);
            return true;
        }

        public static Cursor getCategoryName() {
            Cursor cursor = null;
            cursor = db.rawQuery("SELECT * FROM " + DataBaseConstants.TableNames.TBL_CATEGORY, null);
            return cursor;
        }

        public static boolean deleteCategoryName(String tablename) {

            db.delete(tablename, null, null);
            return true;
        }
    }

    public static class SeasonDB {

        public static boolean SeasonNameInsert(SeasonModel object) {

            ContentValues cv = new ContentValues();
            cv.put(DataBaseConstants.SeasonName.SEASON_ID, object.getSeason_id());
            cv.put(DataBaseConstants.SeasonName.SEASON_YEAR, object.getSeason_year());


            db.insert(DataBaseConstants.TableNames.TBL_SEASON, null, cv);
            return true;
        }

        public static Cursor getSeasonName() {
            Cursor cursor = null;
            cursor = db.rawQuery("SELECT * FROM " + DataBaseConstants.TableNames.TBL_SEASON, null);
            return cursor;
        }

        public static boolean deleteSeasonName(String tablename) {

            db.delete(tablename, null, null);
            return true;
        }
    }

    public static class DBNotification {
        static String TAG = "DBNotification ";

        //TODO Primary methods
        public static boolean insert(ModelNotification object) {
            ContentValues cv = new ContentValues();
            cv.put(DataBaseConstants.NotificationData.TITLE, object.getTitle());
            cv.put(DataBaseConstants.NotificationData.BODY, object.getBody());

            try {
                long id = db.insert(DataBaseConstants.TableNames.TBL_NOTIFICATION, null, cv);
            } catch (Exception e) {
            }
            return true;
        }

        //TODO: method to get list of notification
        public static JSONArray getList() {
            JSONArray array = new JSONArray();
            try {
                Cursor cursor = null;
                cursor = db.rawQuery("SELECT * FROM " + DataBaseConstants.TableNames.TBL_NOTIFICATION, null);
                while (cursor.moveToNext()) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(DataBaseConstants.NotificationData.ID, cursor.getString(cursor.getColumnIndex(DataBaseConstants.NotificationData.ID)));
                    jsonObject.put(DataBaseConstants.NotificationData.TITLE, cursor.getString(cursor.getColumnIndex(DataBaseConstants.NotificationData.TITLE)));
                    jsonObject.put(DataBaseConstants.NotificationData.BODY, cursor.getString(cursor.getColumnIndex(DataBaseConstants.NotificationData.BODY)));
                    array.put(jsonObject);
                }
                return array;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }
        public static void deleteAllNotes()
        {
            db.execSQL("DELETE FROM "+DataBaseConstants.TableNames.TBL_NOTIFICATION);
        }
        public static void deleteParticularNoti(String id)
        {
            db.execSQL("DELETE FROM "+DataBaseConstants.TableNames.TBL_NOTIFICATION+" WHERE "+DataBaseConstants.NotificationData.ID+" = '"+id+"'");
        }
    }

    public static class DBBuyBidData
    {
        static String TAG = "DBBuyBidData";

        //TODO Primary methods
        public static boolean BuyBidDatainsert(BuyBidModel buyBidModel)
        {
            ContentValues cv = new ContentValues();
            cv.put(DataBaseConstants.BuyBidDataConstants.BID_ID, buyBidModel.getId());
            cv.put(DataBaseConstants.BuyBidDataConstants.VALIDITY_TIME, buyBidModel.getValidity_time());
            cv.put(DataBaseConstants.BuyBidDataConstants.END_TIME, buyBidModel.getEndTime());
            cv.put(DataBaseConstants.BuyBidDataConstants.IS_TIMER_RUNNING, buyBidModel.getIsTimerTuuning());
            cv.put(DataBaseConstants.BuyBidDataConstants.BID_END_TIME, buyBidModel.getBidEndTime());
            cv.put(DataBaseConstants.BuyBidDataConstants.IS_DELETED, buyBidModel.getIs_delelted());

            try
            {
                if (!checkBidId(buyBidModel.getId()))
                {
                    long id = db.insert(DataBaseConstants.TableNames.TBL_BUY_BID_DATA, null, cv);

                    if (id == -1)
                    {
                        return false;
                    }
                    else
                    {
                        return false;
                    }
                }

            }
            catch (Exception e)
            {

            }
            return true;
        }


        public static int SellBidDataUpdateValueIsDeleted(String table, BuyBidModel BuyBidModel) {
            // TODO Auto-generated method stub
            ContentValues cv = new ContentValues();
            int count;
//            cv.put(DataBaseConstants.SellBidDataConstants.END_TIME, BuyBidModel.getEndTime());
            cv.put(DataBaseConstants.SellBidDataConstants.IS_DELETED, BuyBidModel.getIs_delelted());
//            cv.put(DataBaseConstants.SellBidDataConstants.IS_TIMER_RUNNING, sellBidModel.getIsTimerRunning());

            Log.e("SellBidDataUpdate", " : " + cv.toString());
            count = db.update(DataBaseConstants.TableNames.TBL_BUY_BID_DATA, cv, DataBaseConstants.SellBidDataConstants.BID_ID + " = " + BuyBidModel.getId(), null);
            return count;
        }


        public static boolean checkBidId(String strBidId) {
            Cursor cursor = db.rawQuery("select * from " + DataBaseConstants.TableNames.TBL_BUY_BID_DATA + " where " + DataBaseConstants.BuyBidDataConstants.BID_ID + " = " + strBidId, null);
            Log.e(TAG, " check_COUNT_BUY " + cursor.getCount());
            if (cursor.getCount() > 0) {
                return true;
            } else {
                return false;
            }
        }


        public static int BuyBidDataUpdate(String table, BuyBidModel buyBidModel) {
            // TODO Auto-generated method stub
            ContentValues cv = new ContentValues();
            int count;
            cv.put(DataBaseConstants.BuyBidDataConstants.VALIDITY_TIME, buyBidModel.getValidity_time());
            cv.put(DataBaseConstants.BuyBidDataConstants.END_TIME, buyBidModel.getEndTime());
            cv.put(DataBaseConstants.BuyBidDataConstants.IS_TIMER_RUNNING, buyBidModel.getIsTimerTuuning());
            Log.e("BuyBidDataUpdate", " : " + cv.toString());
            count = db.update(table, cv, DataBaseConstants.BuyBidDataConstants.BID_ID + " = " + buyBidModel.getId(), null);
            return count;
        }

        public static long getCountOfBIdData() {
            long count = DatabaseUtils.queryNumEntries(db, DataBaseConstants.TableNames.TBL_BUY_BID_DATA);
            return count;
        }


        public static BuyBidModel getBuyModel(String id) {
            Cursor c = db.rawQuery("select * from " + DataBaseConstants.TableNames.TBL_BUY_BID_DATA + " where " + DataBaseConstants.BuyBidDataConstants.BID_ID + " = " + id, null);
            BuyBidModel buymodel = null;
            Log.e("BuyBidM_CURSOR_COUNT", " : " + c.getCount());
            while (c.moveToNext()) {
                buymodel = new BuyBidModel();
                buymodel.setId(c.getString(c.getColumnIndex(DataBaseConstants.BuyBidDataConstants.BID_ID)));
                buymodel.setValidity_time(c.getString(c.getColumnIndex(DataBaseConstants.BuyBidDataConstants.VALIDITY_TIME)));
                buymodel.setEndTime(c.getString(c.getColumnIndex(DataBaseConstants.BuyBidDataConstants.END_TIME)));
                buymodel.setIsTimerTuuning(c.getString(c.getColumnIndex(DataBaseConstants.BuyBidDataConstants.IS_TIMER_RUNNING)));
                buymodel.setBidEndTime(c.getString(c.getColumnIndex(DataBaseConstants.BuyBidDataConstants.BID_END_TIME)));
                buymodel.setIs_delelted(c.getString(c.getColumnIndex(DataBaseConstants.BuyBidDataConstants.IS_DELETED)));
            }
            c.close();
            return buymodel;
        }


        public static boolean deleteRowById(int id) {
            int o = db.delete(DataBaseConstants.TableNames.TBL_BUY_BID_DATA, DataBaseConstants.SellBidDataConstants.BID_ID + "=?", new String[]{Integer.toString(id)});
            if (o == 0) {
                return false;
            } else {
                return true;
            }
        }






        public static int BuyBidDataUpdateTest(String table, BuyBidModel buyBidModel) {
            // TODO Auto-generated method stub
            ContentValues cv = new ContentValues();
            int count;
            cv.put(DataBaseConstants.BuyBidDataConstants.IS_TIMER_RUNNING, buyBidModel.getIsTimerTuuning());
            Log.e("BuyBidDataUpdate", " : " + cv.toString());
            count = db.update(table, cv, DataBaseConstants.BuyBidDataConstants.BID_ID + " = " + buyBidModel.getId(), null);
            return count;
        }


    }

    public static class DBSellBidData {
        static String TAG = "DBSellBidData";

        //TODO Primary methods
        public static boolean SellBidDatainsert(SellBidModel sellBidModel) {
            ContentValues cv = new ContentValues();
            cv.put(DataBaseConstants.SellBidDataConstants.BID_ID, sellBidModel.getId());
            cv.put(DataBaseConstants.SellBidDataConstants.VALIDITY_TIME, sellBidModel.getValidity_time());
            cv.put(DataBaseConstants.SellBidDataConstants.IS_TIMER_RUNNING, sellBidModel.getIsTimerRunning());
            cv.put(DataBaseConstants.SellBidDataConstants.END_TIME, sellBidModel.getEndtime());
            cv.put(DataBaseConstants.SellBidDataConstants.BID_END_TIME, sellBidModel.getEnd_bid_time());
            cv.put(DataBaseConstants.SellBidDataConstants.IS_DELETED, sellBidModel.getIs_deleted());

            Log.e(TAG, "SellBidDatainsert:" + cv.toString());
            try {
                if (!checkSellId(sellBidModel.getId())) {
                    long id = db.insert(DataBaseConstants.TableNames.TBL_SELL_BID_DATA, null, cv);
                    if (id == -1) {
                        return false;
                    } else {
                        return false;
                    }
                }

            } catch (Exception e) {
            }
            return true;
        }


        public static boolean checkSellId(String strSellId) {
            Cursor cursor = db.rawQuery("select * from " + DataBaseConstants.TableNames.TBL_SELL_BID_DATA + " where " + DataBaseConstants.SellBidDataConstants.BID_ID + " = " + strSellId, null);
            Log.e(TAG, " check_COUNT_SELL " + cursor.getCount());
            if (cursor.getCount() > 0) {

                return true;
            } else {
                return false;
            }
        }

        public static int SellBidDataUpdateTest(String table, SellBidModel sellBidModel) {
            // TODO Auto-generated method stub
            ContentValues cv = new ContentValues();
            int count;

            cv.put(DataBaseConstants.SellBidDataConstants.IS_TIMER_RUNNING, sellBidModel.getIsTimerRunning());

            Log.e("SellBidDataUpdate", " : " + cv.toString());
            count = db.update(table, cv, DataBaseConstants.SellBidDataConstants.BID_ID + " = " + sellBidModel.getId(), null);
            return count;
        }



        public static int SellBidDataUpdate(String table, SellBidModel sellBidModel) {
            // TODO Auto-generated method stub
            ContentValues cv = new ContentValues();
            int count;
            cv.put(DataBaseConstants.SellBidDataConstants.END_TIME, sellBidModel.getEndtime());
            cv.put(DataBaseConstants.SellBidDataConstants.IS_TIMER_RUNNING, sellBidModel.getIsTimerRunning());

            Log.e("SellBidDataUpdate", " : " + cv.toString());
            count = db.update(table, cv, DataBaseConstants.SellBidDataConstants.BID_ID + " = " + sellBidModel.getId(), null);
            return count;
        }


        public static int SellBidDataUpdateValueIsDeleted(String table, SellBidModel sellBidModel) {
            // TODO Auto-generated method stub
            ContentValues cv = new ContentValues();
            int count;
//            cv.put(DataBaseConstants.SellBidDataConstants.END_TIME, sellBidModel.getEndtime());
            cv.put(DataBaseConstants.SellBidDataConstants.IS_DELETED, sellBidModel.getIs_deleted());
//            cv.put(DataBaseConstants.SellBidDataConstants.IS_TIMER_RUNNING, sellBidModel.getIsTimerRunning());

            Log.e("SellBidDataUpdate", " : " + cv.toString());
            count = db.update(table, cv, DataBaseConstants.SellBidDataConstants.BID_ID + " = " + sellBidModel.getId(), null);
            return count;
        }


        public static long getCountOfSellData() {
            long count = DatabaseUtils.queryNumEntries(db, DataBaseConstants.TableNames.TBL_SELL_BID_DATA);
            return count;
        }


        public static SellBidModel getSellModel(String id) {
            Cursor c = db.rawQuery("select * from " + DataBaseConstants.TableNames.TBL_SELL_BID_DATA + " where " + DataBaseConstants.SellBidDataConstants.BID_ID + " = " + id, null);
            SellBidModel sellModel = null;
            Log.e("SellBidM_CURSOR_COUNT", " : " + c.getCount());
            while (c.moveToNext()) {
                sellModel = new SellBidModel();
                sellModel.setId(c.getString(c.getColumnIndex(DataBaseConstants.SellBidDataConstants.BID_ID)));
                sellModel.setValidity_time(c.getString(c.getColumnIndex(DataBaseConstants.SellBidDataConstants.VALIDITY_TIME)));
                sellModel.setEndtime(c.getString(c.getColumnIndex(DataBaseConstants.SellBidDataConstants.END_TIME)));
                sellModel.setIsTimerRunning(c.getString(c.getColumnIndex(DataBaseConstants.SellBidDataConstants.IS_TIMER_RUNNING)));
                sellModel.setEnd_bid_time(c.getString(c.getColumnIndex(DataBaseConstants.SellBidDataConstants.BID_END_TIME)));
                sellModel.setIs_deleted(c.getString(c.getColumnIndex(DataBaseConstants.SellBidDataConstants.IS_DELETED)));
            }
            c.close();
            return sellModel;
        }


        public static boolean deleteRowById(int id) {
            int o = db.delete(DataBaseConstants.TableNames.TBL_SELL_BID_DATA, DataBaseConstants.SellBidDataConstants.BID_ID + "=?", new String[]{Integer.toString(id)});
            if (o == 0) {
                return false;
            } else {
                return true;
            }
        }


    }
}