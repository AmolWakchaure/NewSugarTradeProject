package com.prosolstech.sugartrade.database;


public class SQLiteQueries {
    public static final String CREATE_CATEGORY = "create table IF NOT EXISTS "
            + DataBaseConstants.TableNames.TBL_CATEGORY + "( "
            + DataBaseConstants.CategoryName.ID + " INTEGER primary key AUTOINCREMENT,"
            + DataBaseConstants.CategoryName.CATEGORY_ID + " VARCHAR,"
            + DataBaseConstants.CategoryName.CATEGORY + " VARCHAR,"
            + DataBaseConstants.CategoryName.PRODUCT_NAME + " VARCHAR );";

    public static final String CREATE_DISTRICT_NAME = "create table IF NOT EXISTS "
            + DataBaseConstants.TableNames.TBL_DISTRICT + "( "
            + DataBaseConstants.DistrictName.ID + " INTEGER primary key AUTOINCREMENT,"
            + DataBaseConstants.DistrictName.DISTRICT_ID + " VARCHAR,"
            + DataBaseConstants.DistrictName.DISTRICT_NAME + " VARCHAR );";

    public static final String CREATE_NOTIFICATION = "create table IF NOT EXISTS "
            + DataBaseConstants.TableNames.TBL_NOTIFICATION + "( "
            + DataBaseConstants.NotificationData.ID + " INTEGER primary key AUTOINCREMENT,"
            + DataBaseConstants.NotificationData.TITLE + " VARCHAR,"
            + DataBaseConstants.NotificationData.BODY + " VARCHAR );";

    public static final String CREATE_SEASON = "create table IF NOT EXISTS "
            + DataBaseConstants.TableNames.TBL_SEASON + "( "
            + DataBaseConstants.SeasonName.ID + " INTEGER primary key AUTOINCREMENT,"
            + DataBaseConstants.SeasonName.SEASON_ID + " VARCHAR,"
            + DataBaseConstants.SeasonName.SEASON_YEAR + " VARCHAR );";

    public static final String CREATE_BUY_BID = "create table IF NOT EXISTS "
            + DataBaseConstants.TableNames.TBL_BUY_BID_DATA + "( "
            + DataBaseConstants.BuyBidDataConstants.ID + " INTEGER primary key AUTOINCREMENT,"
            + DataBaseConstants.BuyBidDataConstants.BID_ID + " VARCHAR,"
            + DataBaseConstants.BuyBidDataConstants.IS_TIMER_RUNNING + " VARCHAR,"
            + DataBaseConstants.BuyBidDataConstants.END_TIME + " VARCHAR,"
            + DataBaseConstants.BuyBidDataConstants.BID_END_TIME + " VARCHAR,"
            + DataBaseConstants.BuyBidDataConstants.IS_DELETED + " VARCHAR,"
            + DataBaseConstants.BuyBidDataConstants.VALIDITY_TIME + " VARCHAR );";


    public static final String CREATE_SELL_BID = "create table IF NOT EXISTS "
            + DataBaseConstants.TableNames.TBL_SELL_BID_DATA + "( "
            + DataBaseConstants.SellBidDataConstants.ID + " INTEGER primary key AUTOINCREMENT,"
            + DataBaseConstants.SellBidDataConstants.BID_ID + " VARCHAR,"
            + DataBaseConstants.SellBidDataConstants.IS_TIMER_RUNNING + " VARCHAR,"
            + DataBaseConstants.SellBidDataConstants.END_TIME + " VARCHAR,"
            + DataBaseConstants.SellBidDataConstants.BID_END_TIME + " VARCHAR,"
            + DataBaseConstants.SellBidDataConstants.IS_DELETED + " VARCHAR,"
            + DataBaseConstants.SellBidDataConstants.VALIDITY_TIME + " VARCHAR );";
}
