package com.mhacks.dealradar;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.mhacks.dealradar.support.WifiReceiver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Niraj Venkat on 9/21/13.
 */
public class Settings extends Activity
{
    Switch wifiMonitoring, doNotDisturb;
    Button outputWifi, debugFlags;
    Context context;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_menu);
        this.context = this;
        ActionBar actionBar = getActionBar();
        actionBar.setCustomView(R.layout.action_bar);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        wifiMonitoring = (Switch) findViewById(R.id.wifi_monitor_selector);
        doNotDisturb = (Switch) findViewById(R.id.do_not_disturb_selector);
        outputWifi = (Button) findViewById(R.id.debug_wireless_output);
        debugFlags = (Button) findViewById(R.id.debug_flags);

        wifiMonitoring.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                if(b)
                {
                    WifiReceiver.setInterrupts(false);
                }
                else
                {
                    WifiReceiver.setInterrupts(true);
                }
            }
        });

        doNotDisturb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    WifiReceiver.setMayNotify(false);
                } else {
                    WifiReceiver.setMayNotify(true);
                }
            }
        });

        outputWifi.setOnClickListener(new View.OnClickListener()
        {

            public void onClick(View view)
            {
                Object networks[] = WifiReceiver.getWifiList();
                String networkList = "";
                for(int i = 0; i < networks.length; i++)
                {
                    ScanResult tmp = (ScanResult) networks[i];
                    networkList += tmp.SSID + ", " + tmp.BSSID + "\n";
                }

                new AlertDialog.Builder(context)
                        .setTitle("Wireless Networks:")
                        .setMessage(networkList)
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });

        debugFlags.setOnClickListener(new View.OnClickListener()
        {

            public void onClick(View view)
            {
                String flagList = "";
                flagList += "Interrupts are ";
                if(WifiReceiver.getInterruptsEnabled())
                {
                    flagList += "ON\n";
                }
                else
                {
                    flagList += "OFF\n";
                }
                flagList += "Notifications are ";
                if(WifiReceiver.getInterruptsEnabled())
                {
                    flagList += "ON\n";
                }
                else
                {
                    flagList += "OFF\n";
                }
                    new AlertDialog.Builder(context)
                        .setTitle("Flags:")
                        .setMessage(flagList)
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });
    }
}
