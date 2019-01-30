package com.prosolstech.sugartrade.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class SQLiteHelper extends SQLiteOpenHelper {

    public SQLiteHelper(Context context) {
        super(context, DataBaseConstants.DATABASE_NAME, null, DataBaseConstants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(SQLiteQueries.CREATE_CATEGORY);
        db.execSQL(SQLiteQueries.CREATE_DISTRICT_NAME);
        db.execSQL(SQLiteQueries.CREATE_NOTIFICATION);
        db.execSQL(SQLiteQueries.CREATE_SEASON);
        db.execSQL(SQLiteQueries.CREATE_BUY_BID);
        db.execSQL(SQLiteQueries.CREATE_SELL_BID);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA automatic_index = off;");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL("DROP TABLE IF EXISTS " + DataBaseConstants.TableNames.TBL_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + DataBaseConstants.TableNames.TBL_DISTRICT);
        db.execSQL("DROP TABLE IF EXISTS " + DataBaseConstants.TableNames.TBL_NOTIFICATION);
        db.execSQL("DROP TABLE IF EXISTS " + DataBaseConstants.TableNames.TBL_SEASON);
        db.execSQL("DROP TABLE IF EXISTS " + DataBaseConstants.TableNames.TBL_BUY_BID_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + DataBaseConstants.TableNames.TBL_SELL_BID_DATA);
        onCreate(db);
    }
}