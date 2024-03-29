package com.mhacks.dealradar.objects;

import com.mhacks.dealradar.Constants;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by sdickson on 9/21/13.
 */
public class Advertisement implements Serializable
{
    public String objectId;
    public String company;
    public String title;
    public String image_url;
    public String category;
    public Date expDate;
    public String BSSID;
    public Integer rating;
    public Constants.SignalStrength signalStrength;

    public boolean equals(Object other)
    {
        Advertisement otherAdvertisement = (Advertisement) other;
        return BSSID.equals(otherAdvertisement.BSSID);
    }
}
