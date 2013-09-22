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
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

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
    private static List<ScanResult> wifiList;
    private WifiManager mainWifi;
    private Context context;
    public static DealAdapter adapter;
    public static ArrayList<Advertisement> matches;
    public static ArrayList<Notification> notifications;
    private AdapterHandler handler;
    private String current_filter = "All";
    private static boolean isSearching = false;
    private static boolean mayNotify = true;

    public WifiReceiver(WifiManager mainWifi)
    {
        this.mainWifi = mainWifi;
        notifications = new ArrayList<Notification>();
        handler = new AdapterHandler();
    }

    public static Object[] getWifiList()
    {
        return wifiList.toArray();
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

    public void setCurrentFilter(String filter)
    {
        setInterrupts(true);
        current_filter = filter;
        handler.sendEmptyMessage(0);
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

    public static boolean getInterruptsEnabled()
    {
        return isSearching;
    }

    public static boolean getMaySetNotify()
    {
        return mayNotify;
    }

    public static void setMayNotify(boolean enabled)
    {
        mayNotify = enabled;

        if(enabled)
        {
            Log.d("fatal", "Notifications enabled.");
        }
        else
        {
            Log.d("fatal", "Notifications disabled.");
            for(Notification n : notifications)
            {
                n.remove();
            }
            notifications = new ArrayList<Notification>();
        }
    }

    public void onReceive(Context c, Intent intent)
    {
        if(!isSearching)
        {
            context = c;
            wifiList = mainWifi.getScanResults();
            matches = new ArrayList<Advertisement>();
            setInterrupts(true);
            new parseReceive().execute(handler);
        }

    }

    private class parseReceive extends AsyncTask<Object, Integer, Void>
    {
        Handler handler;

        protected Void doInBackground(Object... arg0)
        {
            Log.d("fatal", "Scan at " + System.currentTimeMillis());
            int i = 0;
            handler = (Handler) arg0[0];

            for(ScanResult accessPoint : wifiList)
            {
                for(Advertisement ad : DealRadar.advertisements)
                {
                    if(accessPoint.BSSID.equalsIgnoreCase(ad.BSSID))
                    {
                                ad.signalStrength = getSignalStrength(accessPoint);
                                matches.add(ad);

                                if(!notificationExists(ad) && mayNotify)
                                {
                                    Notification tmp = new Notification(context, i++, ad.company, ad.title, ad.objectId);
                                    tmp.pushNotification();
                                    notifications.add(tmp);

                                }
                    }
                }
            }

            return null;
        }

        protected void onPostExecute(Void v)
        {
            handler.sendEmptyMessage(0);
        }
    }

    public class AdapterHandler extends Handler
    {
        public void handleMessage(Message msg)
        {
            ArrayList<Advertisement> content = new ArrayList<Advertisement>();

            if(!current_filter.equalsIgnoreCase("All"))
            {
                for(Advertisement match : matches)
                {
                    if(match.category.equalsIgnoreCase(current_filter))
                    {
                        content.add(match);
                    }
                }
            }
            else
            {
                content = matches;
            }

            if(adapter != null)
            {
                if(!noChangesToArray(content))
                {
                    adapter.setContent(content);
                    DealRadar.dealList.invalidateViews();

                    //Remove notifications here
                }
            }
            else
            {
                adapter = new DealAdapter(context, content);
                DealRadar.dealList.setAdapter(adapter);
                DealRadar.dealList.invalidateViews();
            }

            if(DealRadar.firstLoad)
            {
                DealRadar.firstLoad = false;
                final Animation in = new AlphaAnimation(0.0f, 1.0f);
                in.setDuration(1000);
                DealRadar.dealList.startAnimation(in);
            }

            if(content.size() == 0)
            {
                DealRadar.noEvents.setVisibility(View.VISIBLE);
                DealRadar.dealList.setVisibility(View.GONE);
                DealRadar.searchBar.setEnabled(false);
            }
            else
            {
                if(DealRadar.noEvents.getVisibility() == View.VISIBLE)
                {
                    DealRadar.noEvents.setVisibility(View.GONE);
                    DealRadar.dealList.setVisibility(View.VISIBLE);
                    DealRadar.searchBar.setEnabled(true);
                }
            }

            setInterrupts(false);
        }
    }




}