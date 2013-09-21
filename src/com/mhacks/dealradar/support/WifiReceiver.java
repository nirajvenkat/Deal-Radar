package com.mhacks.dealradar.support;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;

import com.mhacks.dealradar.Constants;
import com.mhacks.dealradar.DealRadar;
import com.mhacks.dealradar.FullScreenImageView;
import com.mhacks.dealradar.objects.Advertisement;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sdickson on 9/21/13.
 */
public class WifiReceiver extends BroadcastReceiver
{
    private List<ScanResult> wifiList;
    private WifiManager mainWifi;
    private Context context;
    public static DealAdapter adapter;
    public static ArrayList<Advertisement> matches;
    public static ArrayList<Notification> notifications;
    private static boolean isSearching = false;

    public WifiReceiver(WifiManager mainWifi)
    {
        this.mainWifi = mainWifi;
        notifications = new ArrayList<Notification>();
    }

    public List<ScanResult> getWifiList()
    {
        return wifiList;
    }

    public List<ScanResult> getFilteredWifiList(Constants.SignalStrength filters[])
    {
        List<ScanResult> filteredList = new ArrayList<ScanResult>();

        if(wifiList == null)
        {
            return null;
        }

        for(int i = 0; i < filters.length; i++)
        {
            for(ScanResult accessPoint : wifiList)
            {
                if(getSignalStrength(accessPoint) == filters[i])
                {
                    filteredList.add(accessPoint);
                }
            }
        }

        return filteredList;
    }

    public int getNumNetworks()
    {
        if(wifiList != null)
        {
            return wifiList.size();
        }

        return -1;
    }

    public Constants.SignalStrength getSignalStrength(ScanResult accessPoint)
    {
        if(accessPoint == null)
        {
            return Constants.SignalStrength.NONE;
        }

        int signal = accessPoint.level;

        if(signal >= Constants.EXCELLENT_SIGNAL_LOWER_BOUND && signal <= Constants.EXCELLENT_SIGNAL_UPPER_BOUND)
        {
            return Constants.SignalStrength.EXCELLENT;
        }
        else if(signal >= Constants.GOOD_SIGNAL_LOWER_BOUND && signal <= Constants.GOOD_SIGNAL_UPPER_BOUND)
        {
            return Constants.SignalStrength.GOOD;
        }
        else if(signal >= Constants.FAIR_SIGNAL_LOWER_BOUND && signal <= Constants.FAIR_SIGNAL_UPPER_BOUND)
        {
            return Constants.SignalStrength.FAIR;
        }
        else if(signal >= Constants.POOR_SIGNAL_LOWER_BOUND && signal <= Constants.POOR_SIGNAL_UPPER_BOUND)
        {
            return Constants.SignalStrength.POOR;
        }

        return Constants.SignalStrength.NONE;
    }

    private boolean notificationExists(Advertisement ad)
    {
        for(Notification n : notifications)
        {
            if(n.getObjectId().equals(ad.objectId))
            {
                return true;
            }
        }

        return false;
    }

    private boolean noChangesToArray(ArrayList<Advertisement> changeList)
    {
        if(changeList.size() != adapter.deals.size())
        {
            return false;
        }

        for(int i = 0; i < changeList.size(); i++)
        {
            if(!adapter.deals.get(i).equals(changeList.get(i)))
            {
                return false;
            }
        }

        return true;
    }

    public static void setInterrupts(boolean enabled)
    {
        isSearching = enabled;

        if(enabled)
        {
            Log.d("fatal", "Interrupts enabled.");
        }
        else
        {
            Log.d("fatal", "Interrupts disabled.");
        }
    }

    public void onReceive(Context c, Intent intent)
    {
        if(!isSearching)
        {
        context = c;
        Log.d("fatal", "Scan at " + System.currentTimeMillis());
        wifiList = mainWifi.getScanResults();
        matches = new ArrayList<Advertisement>();
        int i = 0;

        for(ScanResult accessPoint : wifiList)
        {
            for(Advertisement ad : DealRadar.advertisements)
            {
                if(accessPoint.BSSID.equalsIgnoreCase(ad.BSSID))
                {
                    //Log.d("fatal", ad.title);
                    ad.signalStrength = getSignalStrength(accessPoint);
                    matches.add(ad);

                    if(!notificationExists(ad))
                    {
                        Notification tmp = new Notification(context, i++, ad.company, ad.title, ad.objectId);
                        tmp.pushNotification();
                        notifications.add(tmp);

                    }
                }
            }
        }

        if(adapter != null)
        {
            if(!noChangesToArray(matches))
            {
                adapter.setContent(matches);
                DealRadar.dealList.invalidateViews();

                //Remove notifications here
            }
        }
        else
        {
            adapter = new DealAdapter(context, matches);
            DealRadar.dealList.setAdapter(adapter);
            DealRadar.dealList.invalidateViews();
        }
        }

    }




}
