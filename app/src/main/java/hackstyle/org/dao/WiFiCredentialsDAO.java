package hackstyle.org.dao;


import android.content.ContentValues;
import android.content.Context;

import hackstyle.org.pojo.WiFiCredentials;
import hackstyle.org.sqlite.DBAdapter;

public class WiFiCredentialsDAO {

    private DBAdapter dbAdapter;
    private Context context;

    public WiFiCredentialsDAO(Context context) {
        dbAdapter = new DBAdapter(context);
        this.context = context;
    }


    public int insertWiFiCredentials(WiFiCredentials wiFiCredentials) {
        ContentValues c = new ContentValues();
        c.put("ssid", wiFiCredentials.getSSID());
        c.put("senha", wiFiCredentials.getSenha());

        return dbAdapter.insert("Wifi", c);
    }

    public WiFiCredentials getWiFiCredentials() {

        return dbAdapter.getWiFiCredentials();
    }

    public int updateWiFiCredentials(WiFiCredentials wiFiCredentials) {

        return dbAdapter.updateWiFiCredentials(wiFiCredentials);
    }


}
