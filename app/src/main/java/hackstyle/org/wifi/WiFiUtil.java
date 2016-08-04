package hackstyle.org.wifi;


import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class WiFiUtil {

    private Context context;


    public WiFiUtil(Context context) {

        this.context = context;
    }


    public String getIPAddress() {

        WifiManager wifiManager = (WifiManager)context.getSystemService(context.WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();

        String sIPAddress = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));

        if (sIPAddress.equals("0.0.0.0")) {
            return null;
        }

        return sIPAddress;
    }


    public String getNetworkAddress() {

        WifiManager wifiManager = (WifiManager)context.getSystemService(context.WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();

        String sIPAddress = String.format("%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff));

        if (sIPAddress.equals("0.0.0")) {
            return null;
        }

        return sIPAddress;
    }



    public static String getApIpAddr(Context context) {

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
        byte[] ipAddress = convert2Bytes(dhcpInfo.serverAddress);

        try {
            String apIpAddr = InetAddress.getByAddress(ipAddress).getHostAddress();
            return apIpAddr;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return null;
    }


    private static byte[] convert2Bytes(int hostAddress) {

        byte[] addressBytes = { (byte)(0xff & hostAddress),
                (byte)(0xff & (hostAddress >> 8)),
                (byte)(0xff & (hostAddress >> 16)),
                (byte)(0xff & (hostAddress >> 24)) };

        return addressBytes;
    }

}
