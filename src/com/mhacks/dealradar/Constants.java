package com.mhacks.dealradar;

/**
 * Created by sdickson on 9/21/13.
 */
public class Constants
{
    //Main Activity Constants
    public static final String PARSE_APPLICATION_ID = "XIyae4PpFDVrRGo0P7inT7j9WGxVUeNqHEMtwQGl";
    public static final String PARSE_CLIENT_KEY = "sEIXfoVLOEh4ViH8FOUAzbCU3I682quauKFRQerR";

    //Wifi Signal Constants
    public enum SignalStrength{
        EXCELLENT, GOOD, FAIR, POOR, NONE;
    }

    public static final int EXCELLENT_SIGNAL_UPPER_BOUND = 0;
    public static final int EXCELLENT_SIGNAL_LOWER_BOUND = -57;

    public static final int GOOD_SIGNAL_UPPER_BOUND = -58;
    public static final int GOOD_SIGNAL_LOWER_BOUND = -75;

    public static final int FAIR_SIGNAL_UPPER_BOUND = -76;
    public static final int FAIR_SIGNAL_LOWER_BOUND = -85;

    public static final int POOR_SIGNAL_UPPER_BOUND = -86;
    public static final int POOR_SIGNAL_LOWER_BOUND = -95;

    //Wifi Scanning Constants
    public static final int ASYNC_SCAN_TICK = 3; //In seconds!
}
