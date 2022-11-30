package com.example.kn.util;

public class MessageConstants {

    private static final String newLine = System.getProperty("line.separator");

    public static final String AT_STARTUP = "This application loads, stores and provides the BTC rate and statistics " +
            "for the set list of currencies. In-project application.properties file allows for some tuning, " +
            "including changing the list of available currencies." + newLine + newLine +
            "stats <currency> - get the pre-stored data for <currency>" + newLine +
            "refresh - refresh data for all currencies" + newLine + newLine +
            "Disclaimer: the highest and lowest rates are calculated using the data available via the CoinDesk API, " +
            "which only provides data between 2010-07-17 and 2022-07-10." + newLine;

    public static final String NO_SUCH_CURRENCY = "No data exists for {0}. " +
            "Check if this currency is available via the CoinDesk API and listed in the application.properties file.";

    public static final String CURRENCY_INFO_FULL = "The rate of BTC at {1} is {2} {0}. According to " +
            "the available data, during the last {3} days highest rate was {4} {0}, lowest rate was {5} {0}, " +
            "average rate was {6} {0}.";

    public static final String CURRENCY_INFO_SHORT = "The rate of BTC at {1} is {2} {0}. " +
            "Rate statistics of the last {3} days is unavailable.";
    public static final String REFRESHED = "Refreshed data for the configured currencies.";

}
