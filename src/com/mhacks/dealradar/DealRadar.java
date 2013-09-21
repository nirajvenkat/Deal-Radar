package com.mhacks.dealradar;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.widget.ListView;

import com.mhacks.dealradar.objects.Advertisement;
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
import java.util.List;

public class DealRadar extends Activity
{
    public static ArrayList<Advertisement> advertisements;
    public static ListView dealList;

    WifiManager mainWifi;
    WifiReceiver receiverWifi;

	@Override
	protected void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        dealList = (ListView) findViewById(R.id.deal_list_view);
        Parse.initialize(this,  Constants.PARSE_APPLICATION_ID, Constants.PARSE_CLIENT_KEY);
        ParseAnalytics.trackAppOpened(getIntent());
        ActionBar actionBar = getActionBar();
        actionBar.setCustomView(R.layout.action_bar);;
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
    }

    public void onResume()
    {
        super.onResume();
        mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        receiverWifi = new WifiReceiver(mainWifi);
        findMatches();
    }

    public void findMatches()
    {
        advertisements = new ArrayList<Advertisement>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Routers");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> result, ParseException e)
            {
                if (e == null)
                {
                    for(ParseObject parse : result)
                    {
                                Advertisement tmp = new Advertisement();
                                tmp.objectId = parse.getObjectId();
                                tmp.title = parse.getString("Deal_Title");
                                tmp.company = parse.getString("Company");
                                tmp.BSSID = parse.getString("BSSID");

                                ParseFile coupon = parse.getParseFile("Deal_Image");
                                if(coupon != null)
                                {
                                    tmp.image_url = coupon.getUrl();
                                }

                                advertisements.add(tmp);
                    }
                    new wifiscan().execute();
                }
                else
                {
                    e.printStackTrace();
                }
            }
        });
    }

    private class wifiscan extends AsyncTask<Void, String, Void> {

        protected void onPostExecute(Void result)
        {
            new wifiscan().execute();
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            mainWifi.startScan();
            try
            {
                Thread.sleep(Constants.ASYNC_SCAN_TICK * 1000);
            }
            catch(Exception e){}
            return null;
        }
    }

}
