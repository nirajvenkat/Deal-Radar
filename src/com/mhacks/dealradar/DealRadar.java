package com.mhacks.dealradar;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.mhacks.dealradar.objects.Advertisement;
import com.mhacks.dealradar.support.CustomAdapter;
import com.mhacks.dealradar.support.DealAdapter;
import com.mhacks.dealradar.support.WifiReceiver;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class DealRadar extends Activity
{
    public static ArrayList<Advertisement> advertisements;
    public static Typeface myriadProRegular, myriadProSemiBold;
    public static ListView dealList;
    public static ListView drawerList;
    public static ArrayList<String> listOfCategories =
            new ArrayList<String>(Arrays.asList("All", "Clothing", "Games", "Movies", "Pets", "Tech", "Toys"));

    WifiManager mainWifi;
    WifiReceiver receiverWifi;
    ProgressDialog progressDialog;
    DrawerLayout drawerLayout;
    EditText searchBar;
    ImageButton drawerButton, settingsButton;
    ActionBarDrawerToggle drawerToggle;
    InputMethodManager imm;
    wifiscan scanThread;
    Context context;
    boolean firstLoad = true;

	@Override
	protected void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        this.context = this;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Parse.initialize(this,  Constants.PARSE_APPLICATION_ID, Constants.PARSE_CLIENT_KEY);
        ParseAnalytics.trackAppOpened(getIntent());
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("");
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        ActionBar actionBar = getActionBar();
        actionBar.setCustomView(R.layout.action_bar);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        myriadProRegular = Typeface.createFromAsset(getAssets(), "fonts/MyriadPro-Regular.otf");
        myriadProSemiBold = Typeface.createFromAsset(getAssets(), "fonts/MyriadPro-Semibold.otf");
        TextView txtTitle = (TextView) findViewById(R.id.action_bar_title);
        txtTitle.setTypeface(myriadProSemiBold);

        dealList = (ListView) findViewById(R.id.deal_list_view);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerList.setAdapter(new CustomAdapter(this, listOfCategories));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

//
//        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawerButton = (ImageButton) findViewById(R.id.toggle_button);
//        drawerToggle = new ActionBarDrawerToggle(
//                this,                  // host Activity */
//                drawerLayout,         // DrawerLayout object */
//                R.drawable.ic_launcher,  // nav drawer icon to replace 'Up' caret */
//                R.string.hello_world,  // "open drawer" description */
//                R.string.hello_world  // "close drawer" description */
//        );
//
//        // Set the drawer toggle as the DrawerListener
//        drawerLayout.setDrawerListener(drawerToggle);
//
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        //drawerButton.setOnClickListener(drawerListener);


        searchBar = (EditText) findViewById(R.id.search_bar);
        searchBar.setTypeface(myriadProRegular);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence cs, int i, int i2, int i3) {}

            @Override
            public void onTextChanged(CharSequence cs, int i, int i2, int i3)
            {
                if(!searchBar.getText().toString().isEmpty() && receiverWifi != null)
                {
                    WifiReceiver.setInterrupts(true);
                    String search = searchBar.getText().toString().toUpperCase();
                    ArrayList<Advertisement> searchResults = new ArrayList<Advertisement>();

                    for(Advertisement ad : receiverWifi.matches)
                    {
                        if(ad.company.toUpperCase().contains(search) || ad.title.toUpperCase().contains(search))
                        {
                            searchResults.add(ad);
                        }
                    }
                    receiverWifi.adapter.setContent(searchResults);
                    receiverWifi.adapter.notifyDataSetChanged();
                }
                else
                {
                    if(receiverWifi != null)
                    {
                        WifiReceiver.setInterrupts(false);
                        receiverWifi.adapter.setContent(receiverWifi.matches);
                        receiverWifi.adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable)
            {
            }
        });

        settingsButton = (ImageButton) findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(context, Settings.class);
                if(intent != null)
                {
                    startActivity(intent);
                }
            }
        });
    }

    public void onResume()
    {
        super.onResume();
        mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        receiverWifi = new WifiReceiver(mainWifi);
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        if(firstLoad)
        {
            firstLoad = false;
            findMatches();
        }
        else
        {
            scanThread = new wifiscan();
            scanThread.start();
        }
    }

    private Date fixDate(Date date)
    {
        TimeZone tz = TimeZone.getDefault();
        Date fixed = new Date(date.getTime() - tz.getRawOffset());

        if(tz.inDaylightTime(fixed))
        {
            Date dst = new Date(fixed.getTime() - tz.getDSTSavings());

            if(tz.inDaylightTime(dst))
            {
                fixed = dst;
            }
        }

        return fixed;
    }

    public void findMatches()
    {
        Log.d("fatal", "Refreshing Parse...");
        progressDialog.show();
        advertisements = new ArrayList<Advertisement>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Routers");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> result, ParseException e) {
                if (e == null) {
                    for (ParseObject parse : result) {
                        Advertisement tmp = new Advertisement();
                        tmp.objectId = parse.getObjectId();
                        tmp.title = parse.getString("Deal_Title");
                        tmp.category = parse.getString("Category");

                        if (parse.getDate("Exp_Date") != null) {
                            tmp.expDate = fixDate(parse.getDate("Exp_Date"));
                        }
                        tmp.company = parse.getString("Company");
                        tmp.BSSID = parse.getString("BSSID");

                        ParseFile coupon = parse.getParseFile("Deal_Image");
                        if (coupon != null) {
                            tmp.image_url = coupon.getUrl();
                        }

                        advertisements.add(tmp);
                    }
                    progressDialog.dismiss();
                    scanThread = new wifiscan();
                    scanThread.start();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private class wifiscan extends Thread
    {
        boolean running = true;

        public void setRunning(boolean running)
        {
            this.running = running;
        }

        public void run()
        {
            while(running)
            {
                mainWifi.startScan();
                try
                {
                    Thread.sleep(Constants.ASYNC_SCAN_TICK * 1000);
                }
                catch(Exception e){}
            }
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            //selectItem(position);
        }
    }

}






