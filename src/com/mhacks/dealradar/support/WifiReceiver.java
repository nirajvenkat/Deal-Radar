package com.mhacks.dealradar.support;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.mhacks.dealradar.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sdickson on 9/21/13.
 */
public class WifiReceiver extends BroadcastReceiver
{
    private List<ScanResult> wifiList;
    private WifiManager mainWifi;

    public WifiReceiver(WifiManager mainWifi)
    {
        this.mainWifi = mainWifi;
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

    public void onReceive(Context c, Intent intent)
    {
        wifiList = mainWifi.getScanResults();
    }

}
