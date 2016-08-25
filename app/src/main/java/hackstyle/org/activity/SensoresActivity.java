package hackstyle.org.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.List;

import hackstyle.org.adapter.SensoresAdapter;
import hackstyle.org.dao.AmbienteDAO;
import hackstyle.org.hscommander.R;
import hackstyle.org.log.HSError;
import hackstyle.org.main.HSSensor;
import hackstyle.org.pojo.Ambiente;
import hackstyle.org.pojo.Carga;
import hackstyle.org.pojo.Sensor;
import hackstyle.org.wizard.WizardActivity;

public class SensoresActivity extends AppCompatActivity {

    private int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 1001;

    private TextView txtStatus;
    private ListView listView;
    private SensoresAdapter sensoresAdapter;
    private SensoresUpdater sensoresUpdater;
    private WifiManager mWifiManager;
    private final BroadcastReceiver mWifiConnectReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context c, Intent intent) {


            if (intent.getAction().equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {

                int stateAuthorized = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, -1);

                if (mWifiManager.getConnectionInfo().getSupplicantState() == SupplicantState.DISCONNECTED) {

                    if (stateAuthorized == WifiManager.ERROR_AUTHENTICATING) {

                        unregisterReceiver(mWifiConnectReceiver);
                        lockOrientation();

                        new android.support.v7.app.AlertDialog.Builder(SensoresActivity.this)
                                .setMessage("Houve um erro de autenticação ao conectar na rede do sensor. Verifique a senha da rede")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        unlockOrientation();
                                        SensoresActivity.this.finish();
                                    }
                                }).show();
                    }

                } else if (mWifiManager.getConnectionInfo().getSupplicantState() == SupplicantState.COMPLETED) {

                    unregisterReceiver(mWifiConnectReceiver);

                    WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                    WifiInfo info = wifiManager.getConnectionInfo();
                    String ssid = info.getSSID().replace("\"", "");

                    //precisamos de um delay pra conexao wifi se estabelecer
                    try {
                        Thread.sleep(3000);
                    } catch (Exception e) {}

                    //chamar asynctask que verifica se é um sensor virgem
                    SensorInquirer sensorInquirer = new SensorInquirer();
                    sensorInquirer.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
                }

                Log.d(getClass().getName(), "onReceive: state: " + mWifiManager.getConnectionInfo().getSupplicantState() +
                        ", not authorized: " + (stateAuthorized == WifiManager.ERROR_AUTHENTICATING));

            }
        }
    };

    private ProgressDialog dialogNovoSensor;
    private String sensorSSID;
    private final BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context c, Intent intent) {

            sensorSSID = "AP 36";

            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {

                List<ScanResult> mScanResults = mWifiManager.getScanResults();

                for (ScanResult sr : mScanResults) {

                    if (sr.SSID.equals("AP 36")) {

                        Log.d(getClass().getName(), "Rede hackstyle encontrada. Rede do Sensor?");
                        dialogNovoSensor.setMessage("Rede do sensor encontrada!");

                        WifiConfiguration wfc = new WifiConfiguration();

                        wfc.SSID = "\"".concat(sr.SSID).concat("\"");
                        wfc.status = WifiConfiguration.Status.DISABLED;
                        wfc.priority = 40;

                        /*
                        //open
                        wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                        wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                        wfc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                        wfc.allowedAuthAlgorithms.clear();
                        wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                        wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                        wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                        wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                        wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                        wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                        */

                        wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                        wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                        wfc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                        wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                        wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                        wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                        wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                        wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                        wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

                        wfc.preSharedKey = "\"".concat("ap36erick").concat("\"");


                        WifiManager wfMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                        int networkId = wfMgr.addNetwork(wfc);
                        if (networkId != -1) {

                            unregisterReceiver(mWifiScanReceiver);

                            // success, can call wfMgr.enableNetwork(networkId, true) to connect
                            wfMgr.disconnect();

                            registerReceiver(mWifiConnectReceiver, new IntentFilter(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION));
                            wfMgr.enableNetwork(networkId, true);

                            dialogNovoSensor.setMessage("Conectando na rede do sensor...");
                        }

                    }

                    Log.d("X", sr.toString());
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensores);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //ActionBar actionBar = getSupportActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Sensores");

        setToolbarConfig(toolbar);


        ImageView imageView = (ImageView) findViewById(R.id.imageViewAddSensor);
        imageView.setImageResource(R.drawable.add);

        txtStatus = (TextView) findViewById(R.id.txtStatus);
        listView = (ListView) findViewById(android.R.id.list);

        /*if (HSSensor.getInstance().getListSensorOn().size() < 1) {

            new AlertDialog.Builder(SensoresActivity.this)
                    .setMessage("A varredura não encontrou sensores ativos!")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            finish();
                        }
                    }).show();

            //txtStatus.setText("scanning...");
            //Intent intent = new Intent(SensoresActivity.this, VarreduraSensoresActivity.class);
            //startActivity(intent);
            //return;

        } else {
            connect_list();
        }
*/

        sensoresAdapter = new SensoresAdapter(this, HSSensor.getInstance().getListSensorOn());
        if (sensoresAdapter != null)
            listView.setAdapter(sensoresAdapter);

        updateStatusSensors();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long l) {

                Sensor sensor = (Sensor) adapter.getItemAtPosition(position);

                if (!sensor.isActive()) {

                    new android.support.v7.app.AlertDialog.Builder(SensoresActivity.this)
                            .setMessage("Este sensor não está ativo!")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            }).show();

                } else {

                    Intent i = new Intent(SensoresActivity.this, DetalheSensorActivity.class);
                    i.putExtra("id", sensor.getId());
                    startActivity(i);
                }

            }
        });

        if (savedInstanceState == null) {
            sensoresUpdater = new SensoresUpdater();
            //sensoresUpdater.execute(true);
            sensoresUpdater.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, true);
        }


        ImageView imageViewAddSensor = (ImageView) findViewById(R.id.imageViewAddSensor);
        imageViewAddSensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
                }


                mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                registerReceiver(mWifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                mWifiManager.startScan();

                lockOrientation();
                dialogNovoSensor = new ProgressDialog(SensoresActivity.this);
                dialogNovoSensor.setMessage("Procurando rede do sensor...");
                dialogNovoSensor.setCancelable(false);
                dialogNovoSensor.show();

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        } else {

            lockOrientation();
            new android.support.v7.app.AlertDialog.Builder(SensoresActivity.this)
                    .setMessage("Para configurar novos sensores, ative a permissão \"Localização\"")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            unlockOrientation();
                            finish();
                        }
                    }).show();
        }
    }

    private void lockOrientation() {

        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE); //locks landscape
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT); //locks port
        }
    }

    private void unlockOrientation() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

    private void setToolbarConfig(Toolbar toolbar) {

        try {

            Field f = toolbar.getClass().getDeclaredField("mTitleTextView");
            f.setAccessible(true);

            TextView mToolbarTitleTextView = (TextView) f.get(toolbar);
            mToolbarTitleTextView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/HighlandGothicFLF.ttf"));

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    public void onPause() {

        super.onPause();
        if (sensoresUpdater != null && sensoresUpdater.getStatus() == AsyncTask.Status.RUNNING)
            sensoresUpdater.cancel(true);

        try {
            if (dialogNovoSensor != null && dialogNovoSensor.isShowing())
                dialogNovoSensor.dismiss();
        } catch (Exception e) {
            if (e.getMessage() != null)
                Log.e(getClass().getName(), e.getMessage());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {

            case android.R.id.home:

                Intent homeIntent = new Intent(this, IntroActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
                break;
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_sensores, menu);
        return true;
    }

    private void updateStatusSensors() {

        String message = null;

        int countOnline = 0;
        for (Sensor s : HSSensor.getInstance().getListSensorOn())
            if (s.isActive())
                countOnline++;

        if (countOnline == 0)
            message = "Nenhum sensor ativo";
        else if (countOnline == 1)
            message = "1 sensor ativo";
        else if (countOnline > 1)
            message = countOnline + " sensores ativos";

        txtStatus.setText(message);

        /*
        PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, IntroActivity.class), 0);
        Notification notification = new NotificationCompat.Builder(this)
                .setTicker("HSCommander - Sensores Ativos")
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle("HSCommander")
                .setContentText(message)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
        */
    }

    public Sensor buildSensorFromGetInfo(String getinfo) {

        int count = 0;

        for (int i = 0; i < getinfo.length(); i++) {
            if (getinfo.charAt(i) == ':')
                count++;
        }

        if (count != 8)
            return null;


        String[] str = getinfo.split(":");

        Sensor sensor = new Sensor();

        sensor.setNome(str[1]);
        sensor.setId(Integer.parseInt(str[5]));

        String[] amb = str[4].split(",");
        int id_ambiente = Integer.parseInt(amb[0]);

        AmbienteDAO ambienteDAO = new AmbienteDAO(getApplicationContext());
        Ambiente ambiente = ambienteDAO.getAmbiente(id_ambiente);
        if (ambiente != null) {

            sensor.setAmbiente(ambiente);

        } else {

            if (ambienteDAO.getListAmbiente().size() > 0) {
                Ambiente am = ambienteDAO.getListAmbiente().get(0);
                sensor.setAmbiente(am);
            }
        }

        for (int i = 6; i < str.length; i++) {

            if (str[i].isEmpty())
                continue;

            String[] ss = str[i].split(",");

            Carga c = new Carga();
            c.setNome(ss[0].trim());
            c.setPino(Integer.parseInt(ss[1].trim()));

            sensor.putCarga(c);
        }

        return sensor;
    }

    private class SensoresUpdater extends AsyncTask<Boolean, Boolean, Boolean> {

        @Override
        protected Boolean doInBackground(Boolean... args) {

            while (!isCancelled()) {

                if (HSSensor.getInstance().isScanning()) {

                    publishProgress(true);

                } else {

                    publishProgress(false);
                }

                try {
                    Thread.sleep((long) 2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Boolean... values) {
            super.onProgressUpdate(values);

            /*if (values[0]) {

                progressBar.setVisibility(View.VISIBLE);

            } else {

                progressBar.setVisibility(View.GONE);
            }*/

            sensoresAdapter.refresh(HSSensor.getInstance().getListSensorOn());
            updateStatusSensors();
        }

    }

    private class SensorInquirer extends AsyncTask<Void, Void, HSError> {

        @Override
        protected HSError doInBackground(Void... voids) {

            String sensorIP = "192.168.1.46";
            String err = "";

            for (int i=0; i<2; i++) {

                Log.d(getClass().getName(), "Execução numero: " + i);

                if (i == 1) {

                    try {
                        Thread.sleep(6000);
                    } catch (Exception e) {}
                }

                Socket sock = new Socket();
                SocketAddress addr = new InetSocketAddress(sensorIP, 8000);

                try {

                    sock.connect(addr, 5000);

                    PrintWriter pout = new PrintWriter(sock.getOutputStream());
                    pout.print("getinfo:::::::checknew:\n");
                    pout.flush();

                    byte[] b = new byte[256];

                    sock.setSoTimeout(5000);
                    int bytes = sock.getInputStream().read(b);
                    sock.close();

                    String result = new String(b, 0, bytes - 1);

                    Sensor sensor = buildSensorFromGetInfo(result);
                    if (sensor == null) {
                        //getinfo nao é valido
                        err = "Resposta do dispositivo não é um sensor válido!";
                        Log.d("CONN", err);
                        continue;
                    }

                    if (sensor.getId() == -1) {

                        return new HSError(true, "Sensor virgem válido!");

                    } else {

                        return new HSError(false, "O dispositivo não é um sensor virgem!");
                    }

                } catch (Exception e) {

                    Log.d(getClass().getName(), "ERRO XXX");

                    if (e.getMessage() != null) {
                        err = e.getMessage();
                        Log.d(getClass().getName(), err);
                    }
                }

            }

            return new HSError(false, err);
        }

        @Override
        protected void onPostExecute(HSError result) {
            super.onPostExecute(result);


            if (result.getStatus()) {
                Intent i = new Intent(SensoresActivity.this, WizardActivity.class);
                startActivity(i);

            } else {

                lockOrientation();
                new android.support.v7.app.AlertDialog.Builder(SensoresActivity.this)
                        .setMessage("Erro: " + result.getMessage())
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialogNovoSensor.dismiss();
                                unlockOrientation();
                            }
                        }).show();
            }
        }
    }


}