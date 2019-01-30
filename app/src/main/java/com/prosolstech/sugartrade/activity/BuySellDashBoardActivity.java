package com.prosolstech.sugartrade.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.prosolstech.sugartrade.ConfirmationDialog;
import com.prosolstech.sugartrade.DialogListner;
import com.prosolstech.sugartrade.LoginActiveDialog;
import com.prosolstech.sugartrade.MyService;
import com.prosolstech.sugartrade.R;
import com.prosolstech.sugartrade.SplashActivity;
import com.prosolstech.sugartrade.adapter.MyBuyOfferAdapter;
import com.prosolstech.sugartrade.classes.MyApplication;
import com.prosolstech.sugartrade.database.DataBaseConstants;
import com.prosolstech.sugartrade.database.DataBaseHelper;
import com.prosolstech.sugartrade.fragment.MyBuyBookingFragment;
import com.prosolstech.sugartrade.fragment.MyBuyOfferFragment;
import com.prosolstech.sugartrade.fragment.MySellBookingFragment;
import com.prosolstech.sugartrade.fragment.MySellOfferFragment;
import com.prosolstech.sugartrade.fragment.SellBidFragment;
import com.prosolstech.sugartrade.fragment.BuyBidFragment;
import com.prosolstech.sugartrade.model.CategoryModel;
import com.prosolstech.sugartrade.model.SeasonModel;
import com.prosolstech.sugartrade.util.ACU;
import com.prosolstech.sugartrade.util.Constant;
import com.prosolstech.sugartrade.util.VU;
import com.prosolstech.sugartrade.util.ViewAnimation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BuySellDashBoardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Context context;
    private ViewPager mViewPager;
    private ReceivablesAdapter receivablesAdapter;
    DataBaseHelper db;
    TextView txtName, txtEmail;
    Toolbar toolbar;
    TabLayout tabLayout;
    public static String checkStatus = "";
    private FragmentManager fragmentManager;
    ProgressDialog pDialog;

    DrawerLayout drawer_layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_sell_dash_board);
        context = BuySellDashBoardActivity.this;
        db = new DataBaseHelper(context);
        fragmentManager = getSupportFragmentManager();

//        if (!ACU.MySP.getSPBoolean(context, ACU.MySP.FLAG_ACCEPT, false)) {
////            showCustomDialog();
//            ConfirmationDialog confirmationDialog = new ConfirmationDialog(BuySellDashBoardActivity.this, dialogListner);
//            confirmationDialog.show();
//
//
//        }
        initializeUI();

        Intent intent = getIntent();
        if (intent != null) {

            String val = intent.getStringExtra("viewPager");
            String val1 = intent.getStringExtra("BookingDetails");
            String val2 = intent.getStringExtra("MyRequest");


            if (val != null) {
                mViewPager.setCurrentItem(3);
            }
            if (val1 != null) {
                mViewPager.setCurrentItem(3);
            }
            if (val2 != null) {
                mViewPager.setCurrentItem(1);
            }
        }


        if (ACU.MySP.getFromSP(context, ACU.MySP.NOTIFY_MY_BOOKINGS, "").equalsIgnoreCase("2")) {
            mViewPager.setCurrentItem(3);
            ACU.MySP.saveSP(context, ACU.MySP.NOTIFY_MY_BOOKINGS, "");
        } else if (ACU.MySP.getFromSP(context, ACU.MySP.NOTIFY_MY_BOOKINGS, "").equalsIgnoreCase("3")) {
            mViewPager.setCurrentItem(0);
            ACU.MySP.saveSP(context, ACU.MySP.NOTIFY_MY_BOOKINGS, "");
        }


        setToolBar();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(


                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                // Do whatever you want here

                txtName.setText(ACU.MySP.getFromSP(context, ACU.MySP.COMPANY_NAME, ""));
                txtEmail.setText(ACU.MySP.getFromSP(context, ACU.MySP.EMAIL_ID, ""));
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // Do whatever you want here

                txtName.setText(ACU.MySP.getFromSP(context, ACU.MySP.COMPANY_NAME, ""));
                txtEmail.setText(ACU.MySP.getFromSP(context, ACU.MySP.EMAIL_ID, ""));

            }


        };

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        NavigationMenuView navMenuView = (NavigationMenuView) navigationView.getChildAt(0);

        navigationView.setNavigationItemSelectedListener(this);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        txtName = (TextView) header.findViewById(R.id.BuySellDashActvitityTxtName);
        txtEmail = (TextView) header.findViewById(R.id.BuySellDashActvitityTxtEmail);


        Menu nav_Menu = navigationView.getMenu();
        if (ACU.MySP.getFromSP(context, ACU.MySP.ROLE, "").equals("Seller")) {
            nav_Menu.findItem(R.id.nav_all_seller_post).setVisible(true);
            nav_Menu.findItem(R.id.nav_all_buyer_list).setVisible(true);
            MySellOffer();
        } else {
            nav_Menu.findItem(R.id.nav_all_seller_list).setVisible(true);
            MyBuyOffer();
        }


        if (VU.isConnectingToInternet(context)) {
            Cursor cursorCategory = DataBaseHelper.CategoryDB.getCategoryName();
            Cursor cursorSeason = DataBaseHelper.SeasonDB.getSeasonName();

            if (cursorCategory.getCount() > 0 && cursorSeason.getCount() > 0) {          // if record is greater than 0 than data not inserted
            } else {                     // if record is blank  than data inserted
                fetchCategoryName();
                fetchSeasonYear();
            }
        }


        if (VU.isConnectingToInternet(this))
            checkStudentActiveOrNot();


    }


    private void checkStudentActiveOrNot() {


        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(context);


        String url = "http://www.sugarcatalog.com/anakantuser/sugar_trade/index.php/API/chekValidUser";

        Log.e("url: ", url);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        try {
                            pDialog.dismiss();


                            JSONArray jsonArray = null;


                            jsonArray = new JSONArray(response);

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject = jsonArray.getJSONObject(i);


                                String isActivie = jsonObject.getString("is_active");
                                String isDeleted = jsonObject.getString("is_deleted");
                                Log.e("onResponse: ", isActivie);


                                if (isActivie.equalsIgnoreCase("N") || isDeleted.equalsIgnoreCase("Y")) {
                                    LoginActiveDialog dialog = new LoginActiveDialog(context, listner);
                                    dialog.show();

                                }

                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", ACU.MySP.getFromSP(context, ACU.MySP.ID, ""));
                ;
                return params;
            }
        };
        queue.add(stringRequest);
    }


    LoginActiveDialog.Listner listner = new LoginActiveDialog.Listner() {
        @Override
        public void onClkic() {


            finish();
        }
    };


    private void setToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (ACU.MySP.getFromSP(context, ACU.MySP.ROLE, "").equals("Seller")) {
            toolbar.setTitle("Seller");
        } else if (ACU.MySP.getFromSP(context, ACU.MySP.ROLE, "").equals("Buyer")) {
            toolbar.setTitle("Buyer");
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    private void initializeUI() {
        Log.e("ROLE", " : " + ACU.MySP.getFromSP(context, ACU.MySP.ROLE, ""));
        receivablesAdapter = new ReceivablesAdapter(fragmentManager);

        mViewPager = (ViewPager) findViewById(R.id.BusSellDashBoardActivityViewPager);
        mViewPager.setAdapter(receivablesAdapter);


        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


//        final FloatingActionButton fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
//
//        fab_add.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (ACU.MySP.getFromSP(context, ACU.MySP.ROLE, "").equals("Seller")) {
//                    startActivity(new Intent(context, PlaceSellBidActivity.class));
//                } else if (ACU.MySP.getFromSP(context, ACU.MySP.ROLE, "").equals("Buyer")) {
//                    startActivity(new Intent(context, PlaceBuyBidActivity.class));
//                }
//            }
//        });
    }

    public class ReceivablesAdapter extends FragmentPagerAdapter {

        public ReceivablesAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            if (ACU.MySP.getFromSP(context, ACU.MySP.ROLE, "").equals("Seller")) {
                switch (position) {
                    case 0:
                        SellBidFragment sellBidFragment = new SellBidFragment();
                        Log.e("getItem: ", "SellBidFragment");
                        fragment = sellBidFragment;
//                        return sellBidFragment;
                        break;
                    case 1:
                        MySellOfferFragment mySellOfferFragment = new MySellOfferFragment(tabLayout);
                        Log.e("getItem: ", "MySellOfferFragment");
                        fragment = mySellOfferFragment;
                        break;
//                        return mySellOfferFragment;
                    case 2:
                        MySellBookingFragment mySellBookingFragment = new MySellBookingFragment(tabLayout);
                        Log.e("getItem: ", "MySellBookingFragment");
                        fragment = mySellBookingFragment;

//                        return mySellBookingFragment;
                        break;
                }
            } else {
                switch (position) {
                    case 0:
                        BuyBidFragment buyBidFragment = new BuyBidFragment(tabLayout);
                        Log.e("getItem: ", "BuyBidFragment");
                        fragment = buyBidFragment;
//                        return buyBidFragment;
                        break;
                    case 1:
                        MyBuyOfferFragment myBuyOfferFragment = new MyBuyOfferFragment(tabLayout);
                        Log.e("getItem: ", "MyBuyOfferFragment");
                        fragment = myBuyOfferFragment;
//                        return myBuyOfferFragment;
                        break;
                    case 2:
                        MyBuyBookingFragment myBuyBookingFragment = new MyBuyBookingFragment(tabLayout);
                        Log.e("getItem: ", "MyBuyBookingFragment");
                        fragment = myBuyBookingFragment;
//                        return myBuyBookingFragment;
                        break;
                }
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            if (ACU.MySP.getFromSP(context, ACU.MySP.ROLE, "").equals("Seller")) {
                switch (position) {
                    case 0:
                        return "Buy Offers";
                    case 1:
                        return "MyPost";
                    case 2:
                        return "My Booking";
                }
            } else {
                switch (position) {
                    case 0:
                        return "Sell Offers";
                    case 1:
                        return "MyPost";
                    case 2:
                        return "My Booking";
                }
            }
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_home) {
        } else if (id == R.id.nav_all_seller_post) {
            startActivity(new Intent(context, AllSellerOffersActivity.class));
        } else if (id == R.id.nav_all_seller_list) {
            startActivity(new Intent(context, SellerListActivity.class));                    //  if Buyer login than all seller list show
        } else if (id == R.id.nav_my_bid) {
            startActivity(new Intent(context, MyBidActivity.class));                    //  if Buyer login than all seller list show
        } else if (id == R.id.nav_all_buyer_list) {
            startActivity(new Intent(context, BuyerListActivity.class));                         //  if Seller login than all Buyer list show
        } else if (id == R.id.nav_my_profile) {
            startActivity(new Intent(context, MyProfileActivity.class));
        } else if (id == R.id.nav_my_notification) {
            startActivity(new Intent(context, NotificationActivity.class));
        } else if (id == R.id.nav_ask_query) {
            startActivity(new Intent(context, ViewQueryActivity.class));
        } else if (id == R.id.nav_my_contact_us) {
            links("contact_us", ACU.MySP.MAIN_URL + "sugar_trade/contact_us/");
        } else if (id == R.id.nav_help) {
            links("help", ACU.MySP.MAIN_URL + "sugar_trade/FAQ/");               // comment on 26-09-18
        } else if (id == R.id.nav_terms_con) {
            links("terms_and_condition", ACU.MySP.MAIN_URL + "sugar_trade/tandc/");
        } else if (id == R.id.nav_place) {
            if (ACU.MySP.getFromSP(context, ACU.MySP.ROLE, "").equals("Seller")) {
                if (!ACU.MySP.getSPBoolean(context, ACU.MySP.IS_FIRST_TIME_LAUNCH, false)) {
                    Toast.makeText(context, "Create Sell offer", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(context, PlaceSellBidActivity.class));
                    ACU.MySP.setSPBoolean(context, ACU.MySP.IS_FIRST_TIME_LAUNCH, true);
                } else {
                    startActivity(new Intent(context, PlaceSellBidActivity.class));
                }

            } else if (ACU.MySP.getFromSP(context, ACU.MySP.ROLE, "").equals("Buyer")) {

                if (!ACU.MySP.getSPBoolean(context, ACU.MySP.IS_FIRST_TIME_LAUNCH, false)) {
                    Toast.makeText(context, "Create Buyer offer", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(context, PlaceBuyBidActivity.class));
                    ACU.MySP.setSPBoolean(context, ACU.MySP.IS_FIRST_TIME_LAUNCH, true);
                } else {
                    startActivity(new Intent(context, PlaceBuyBidActivity.class));
                }

            }


        } else if (id == R.id.nav_privacy_policy) {
            links("privacy_policy", ACU.MySP.MAIN_URL + "sugar_trade/privacy_policy.html");
        }
        else if (id == R.id.nav_logout)
        {
            //logout app
            Snackbar snackbar = Snackbar
                    .make(drawer_layout, "Do you want to logout app?", Snackbar.LENGTH_LONG)
                    .setAction("Logout", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {


                            ACU.MySP.setSPBoolean(context, ACU.MySP.LOGIN_STATUS, false);

                            Intent intent = new Intent(BuySellDashBoardActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();

                        }
                    });

            snackbar.show();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    public void links(String flag, String url) {
        if (VU.isConnectingToInternet(context)) {
            Intent in_contact = new Intent(context, ContactUsActivity.class);
            in_contact.putExtra("flag", flag);
            in_contact.putExtra("url", url);
            startActivity(in_contact);
        }
    }

    public void fetchCategoryName() {
        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please Wait Category Name Fetch...");
        pDialog.setCancelable(false);
        pDialog.show();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/categories";      //for server


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            pDialog.dismiss();
                            Log.e("Category_NAME", " " + response);
                            setCategoryName(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                Log.e("No Response", " GET ");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
//                params.put("farmer_id", ACU.MySP.getFromSP(context, ACU.MySP.ID, ""));
                return params;

            }
        };
        queue.add(stringRequest);
    }

    private void setCategoryName(String result) {

        JSONArray jsonArray;
        CategoryModel categoryModel = new CategoryModel();
        try {
            jsonArray = new JSONArray(result);
            boolean tab = DataBaseHelper.CategoryDB.deleteCategoryName(DataBaseConstants.TableNames.TBL_CATEGORY);
            Log.e("COUNT", " " + tab);
            if (tab == true) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    categoryModel.setCategory_id(jsonObject.getString("id"));
                    categoryModel.setCategory(jsonObject.getString("category"));
                    categoryModel.setProduct_name(jsonObject.getString("product_name"));
                    DataBaseHelper.CategoryDB.CategoryNameInsert(categoryModel);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void fetchSeasonYear() {
        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please Wait Category Name Fetch...");
        pDialog.setCancelable(false);
        pDialog.show();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/getAllSeasons";      //for server


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            pDialog.dismiss();
                            Log.e("fetchSeasonYear", " " + response);
                            setSeasonYear(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                Log.e("No Response", " GET ");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
//                params.put("farmer_id", ACU.MySP.getFromSP(context, ACU.MySP.ID, ""));
                return params;

            }
        };
        queue.add(stringRequest);
    }

    private void setSeasonYear(String result) {

        JSONArray jsonArray;
        SeasonModel seasonModel = new SeasonModel();
        try {
            jsonArray = new JSONArray(result);
            boolean tab = DataBaseHelper.SeasonDB.deleteSeasonName(DataBaseConstants.TableNames.TBL_SEASON);
            Log.e("COUNT", " " + tab);
            if (tab == true) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    seasonModel.setSeason_id(jsonObject.getString("id"));
                    seasonModel.setSeason_year(jsonObject.getString("season"));
                    DataBaseHelper.SeasonDB.SeasonNameInsert(seasonModel);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




    private void showCustomDialog() {
        WebView webView;
        Button btnAccept;
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_light);
        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        btnAccept = (Button) dialog.findViewById(R.id.DialogoBtnAccept);
        webView = (WebView) dialog.findViewById(R.id.DailogwebView);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        //webView.loadUrl("https://stackoverflow.com/questions/9172805/android-webview-inside-dialog-or-popup");
        webView.loadUrl(ACU.MySP.MAIN_URL + "sugar_trade/tandc/");
        dialog.show();
        dialog.getWindow().setAttributes(lp);

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ACU.MySP.setSPBoolean(context, ACU.MySP.FLAG_ACCEPT, true);
                dialog.dismiss();
            }
        });
    }

    private void MyBuyOffer() {

        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
            pDialog = null;
        }
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait while data fetch from server...");
        pDialog.setCancelable(false);
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(context);

        String url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/buyBidsById";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        Log.e("MyBuyOffer", " RESPONSE " + response);

                        pDialog.dismiss();
                        setListAdapterBuyer(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("No Response", "FOUND");
                pDialog.dismiss();
//                Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", ACU.MySP.getFromSP(context, ACU.MySP.ID, ""));
                return params;
            }
        };
        queue.add(stringRequest);
    }

    private void setListAdapterBuyer(String result) {
        try {
            JSONArray array;
            Log.e("DASH_BUYER_setListAdapter", " RESULT " + result.trim());
            array = new JSONArray(result);
            if (array != null && array.length() > 0) {
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonObject = array.getJSONObject(i);
                    if (array.getJSONObject(1).getString("offer_required_qty").equalsIgnoreCase("") || array.getJSONObject(i).getString("offer_required_qty").equalsIgnoreCase("null")) {
                        tabLayout.getTabAt(1).setText("My Post");
                    } else {
                        tabLayout.getTabAt(1).setText("My Post●");
                    }
                }
            } else {
                Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void MySellOffer() {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
            pDialog = null;
        }
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait while data fetch from server...");
        pDialog.setCancelable(false);
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/sellBidsById";      //for server

        Log.e("URL_MySellOffer", " : " + url);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        Log.e("DATA_MySellOffer", " RESPONSE " + response);
                        pDialog.dismiss();
                        setListAdapterSeller(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("No Response", "FOUND");
                pDialog.dismiss();
//                Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", ACU.MySP.getFromSP(context, ACU.MySP.ID, ""));
                return params;
            }
        };
        queue.add(stringRequest);
    }

    private void setListAdapterSeller(String result) {
        try {
            JSONArray array;
            Log.e("DASH_SELL_setListAdapter", " RESULT " + result.trim());
            array = new JSONArray(result);
            if (array != null && array.length() > 0) {
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonObject = array.getJSONObject(i);
                    if (array.getJSONObject(i).getString("offer_required_qty").equalsIgnoreCase("") || array.getJSONObject(i).getString("offer_required_qty").equalsIgnoreCase("null")) {
                        tabLayout.getTabAt(1).setText("My Post");
                    } else {
                        tabLayout.getTabAt(1).setText("My Post●");
                    }
                }
            } else {
//                Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        forceUpdate();
        if (ACU.MySP.getFromSP(context, ACU.MySP.ROLE, "").equals("Seller")) {
            MySellOffer();
        } else {
            MyBuyOffer();
        }
    }

    @Override
    protected void onDestroy() {
        pDialog.dismiss();
        super.onDestroy();
    }

    public void forceUpdate() {
        PackageManager packageManager = this.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String currentVersion = packageInfo.versionName;
        new ForceUpdateAsync(currentVersion, BuySellDashBoardActivity.this).execute();
    }

    public class ForceUpdateAsync extends AsyncTask<String, String, String> {
        private String latestVersion;
        private String currentVersion;
        private Context context;

        public ForceUpdateAsync(String currentVersion, Context context) {
            this.currentVersion = currentVersion;
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Document document = Jsoup.connect("https://play.google.com/store/apps/details?id=" + context.getPackageName() + "&hl=en")
                        .timeout(10000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get();
                if (document != null) {
                    Elements element = document.getElementsContainingOwnText("Current Version");
                    for (Element ele : element) {
                        if (ele.siblingElements() != null) {
                            Elements sibElemets = ele.siblingElements();
                            for (Element sibElemet : sibElemets) {
                                latestVersion = sibElemet.text();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return latestVersion;
        }

        @Override
        protected void onPostExecute(String jsonObject) {
            if (latestVersion != null) {
                if (!currentVersion.equalsIgnoreCase(latestVersion)) {
                    if (!(context instanceof SplashActivity)) {
                        if (!((Activity) context).isFinishing()) {
                            showForceUpdateDialog();
                        }
                    }
                }
            }
            super.onPostExecute(jsonObject);
        }

        public void showForceUpdateDialog() {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AppTheme));

            alertDialogBuilder.setTitle(context.getString(R.string.youAreNotUpdatedTitle));
            alertDialogBuilder.setMessage("A new version of anekant App is available. Please update App.");
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName())));
                    dialog.cancel();
                }
            });
            alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            alertDialogBuilder.show();
        }
    }

}
