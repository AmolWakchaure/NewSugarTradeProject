package com.prosolstech.sugartrade.database;

public class DataBaseConstants {

    public static final String DATABASE_NAME = "sugartrade.db";
    public static final int DATABASE_VERSION = 1;

    public static class TableNames {

        public static final String TBL_CATEGORY = "tbl_category";
        public static final String TBL_SEASON = "tbl_season";
        public static final String TBL_DISTRICT = "tbl_district";
        public static final String TBL_NOTIFICATION = "tbl_notification";
        public static final String TBL_BUY_BID_DATA = "tbl_buy_bid_data";
        public static final String TBL_SELL_BID_DATA = "tbl_sell_bid_data";
    }

    public static class CategoryName {
        public static final String ID = "id";
        public static final String CATEGORY_ID = "category_id";
        public static final String CATEGORY = "category";
        public static final String PRODUCT_NAME = "product_name";
    }

    public static class DistrictName {
        public static final String ID = "id";
        public static final String DISTRICT_ID = "district_id";
        public static final String DISTRICT_NAME = "district_name";
    }

    public static class NotificationData {

        public static final String ID = "id";
        public static final String TITLE = "title";
        public static final String BODY = "body";
    }

    public static class SeasonName {
        public static final String ID = "id";
        public static final String SEASON_ID = "season_id";
        public static final String SEASON_YEAR = "season_year";
    }

    public static class BuyBidDataConstants {
        public static final String ID = "id";
        public static final String BID_ID = "bid_id";
        public static final String VALIDITY_TIME = "validity_time";
        public static final String IS_TIMER_RUNNING = "is_timer_running";
        public static final String END_TIME = "end_time";
        public static final String BID_END_TIME = "bid_end_time";
        public static final String IS_DELETED = "is_deleted";
    }

    public static class SellBidDataConstants {
        public static final String ID = "id";
        public static final String BID_ID = "bid_id";
        public static final String VALIDITY_TIME = "validity_time";
        public static final String IS_TIMER_RUNNING = "is_timer_running";
        public static final String END_TIME = "end_time";
        public static final String BID_END_TIME = "bid_end_time";
        public static final String IS_DELETED = "is_deleted";
    }
}