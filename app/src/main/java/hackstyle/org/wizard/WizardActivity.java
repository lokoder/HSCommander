package hackstyle.org.wizard;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.Executor;

import hackstyle.org.activity.SensoresActivity;
import hackstyle.org.adapter.WizardAdapter;
import hackstyle.org.dao.AmbienteDAO;
import hackstyle.org.dao.SensorDAO;
import hackstyle.org.hscommander.R;
import hackstyle.org.log.HSError;
import hackstyle.org.main.HSSensor;
import hackstyle.org.pojo.Ambiente;
import hackstyle.org.pojo.Carga;
import hackstyle.org.pojo.Sensor;
import hackstyle.org.wifi.WiFiUtil;

public class WizardActivity extends FragmentActivity {

    Sensor sensor;
    private WizardAdapter wizardAdapter;
    private ViewPager pager;
    private Button btnPrev, btnNext;
    private boolean finalizar = false;
    private ProgressDialog progressDialog;
    private SendNewSensorConfig sendNewSensorTask = null;
    private WifiManager mWifiManager;


    private final BroadcastReceiver mWifiConnectReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context c, Intent intent) {


            if (intent.getAction().equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {

                Log.d(getClass().getName(), "SUPPLICANT_STATE_CHANGED_ACTION");

                int stateAuthorized = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, -1);

                if (mWifiManager.getConnectionInfo().getSupplicantState() == SupplicantState.DISCONNECTED) {

                    if (stateAuthorized == WifiManager.ERROR_AUTHENTICATING) {

                        unregisterReceiver(mWifiConnectReceiver);
                        lockOrientation();

                        new android.support.v7.app.AlertDialog.Builder(WizardActivity.this)
                                .setMessage("Houve um erro de autenticação ao conectar na rede do sensor. Verifique a senha da rede")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        unlockOrientation();
                                    }
                                }).show();
                    }

                } else if (mWifiManager.getConnectionInfo().getSupplicantState() == SupplicantState.COMPLETED) {

                    Log.d(getClass().getName(), "SupplicantState.COMPLETED");

                    unregisterReceiver(mWifiConnectReceiver);

                    progressDialog.dismiss();

                    WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                    WifiInfo info = wifiManager.getConnectionInfo();
                    String ssid = info.getSSID().replace("\"", "");

                    //precisamos de um delay pra conexao wifi se estabelecer
                    //try {
                    //    Thread.sleep(3000);
                    //} catch (Exception e) {
                    //}

                    new AlertDialog.Builder(WizardActivity.this)
                            .setMessage("Sensor configurado com sucesso!")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    Intent intent = new Intent(WizardActivity.this, SensoresActivity.class);
                                    startActivity(intent);
                                    WizardActivity.this.finish();
                                }
                            })
                            .show();


                    //chamar asynctask que verifica se é um sensor virgem
                    //SensorInquirer sensorInquirer = new SensorInquirer();
                    //sensorInquirer.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
                }

                Log.d(getClass().getName(), "onReceive: state: " + mWifiManager.getConnectionInfo().getSupplicantState() +
                        ", not authorized: " + (stateAuthorized == WifiManager.ERROR_AUTHENTICATING));

            }
        }
    };


    private final BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {

        private String sensorSSID;

        @Override
        public void onReceive(Context c, Intent intent) {

            sensorSSID = "AP 36";

            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {

                List<ScanResult> mScanResults = mWifiManager.getScanResults();

                for (ScanResult sr : mScanResults) {

                    if (sr.SSID.equals(sensorSSID)) {

                        Log.d(getClass().getName(), "Rede AP 36 encontrada!");

                        WifiConfiguration wfc = new WifiConfiguration();

                        wfc.SSID = "\"".concat(sr.SSID).concat("\"");
                        wfc.allowedAuthAlgorithms.clear();
                        wfc.status = WifiConfiguration.Status.DISABLED;
                        wfc.priority = 40;


                        //open
                        //wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);


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

                            //unregisterReceiver(mWifiScanReceiver);

                            // success, can call wfMgr.enableNetwork(networkId, true) to connect
                            wfMgr.disconnect();

                            unregisterReceiver(mWifiScanReceiver);

                            registerReceiver(mWifiConnectReceiver, new IntentFilter(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION));
                            wfMgr.enableNetwork(networkId, true);


                            //progressDialog.dismiss();

                           /* new AlertDialog.Builder(WizardActivity.this)
                                    .setMessage("Sensor configurado com sucesso!")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            Intent intent = new Intent(WizardActivity.this, SensoresActivity.class);
                                            startActivity(intent);
                                            WizardActivity.this.finish();
                                        }
                                    })
                                    .show();*/

                            //return;
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
        setContentView(R.layout.activity_wizard);

        btnPrev = (Button) findViewById(R.id.wizard_previous_button);
        btnNext = (Button) findViewById(R.id.wizard_next_button);

        wizardAdapter = new WizardAdapter(getSupportFragmentManager());
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(wizardAdapter);

        //pager.setPageTransformer(true, new CubeOutTransformer());
        //pager.setPageTransformer(true, new FlipHorizontalTransformer());
        //pager.setPageTransformer(true, new ForegroundToBackgroundTransformer());
        //pager.setPageTransformer(true, new StackTransformer());
        //pager.setPageTransformer(true, new TabletTransformer());
        //pager.setPageTransformer(true, new ZoomInTransformer());
        //pager.setPageTransformer(true, new ZoomOutTranformer());

        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {

                Log.d("XXXX", "onPageSelected :" + position);

                if (position < 5)
                    finalizar = false;

                if (position < 1) {

                    btnPrev.setEnabled(false);
                    btnPrev.setAlpha(0.2F);

                } else {

                    btnPrev.setEnabled(true);
                    btnPrev.setAlpha(1F);
                }


                if (position + 1 == wizardAdapter.getCount()) {
                    btnNext.setText("Finalizar");
                    finalizar = true;
                } else {
                    btnNext.setText("Próximo");
                    finalizar = false;
                }


                Fragment fragment = (Fragment) wizardAdapter.instantiateItem(pager, position);
                if (fragment != null) {

                    if (position == 5) {
                        TextView txtSSID = (TextView) fragment.getActivity().findViewById(R.id.txtSSIDSensor);
                        String str = WizardSensor.getInstance().getSensor().getSSID();
                        txtSSID.setText(str);

                        TextView txtSenha = (TextView) fragment.getActivity().findViewById(R.id.txtSenhaSensor);
                        str = WizardSensor.getInstance().getSensor().getSenha();
                        txtSenha.setText(str);
                    }

                }
            }
        });


        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int curPosition = pager.getCurrentItem();

                if (curPosition + 1 > 1) {
                    pager.setCurrentItem(pager.getCurrentItem() - 1, true);

                }
            }
        });


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (finalizar) {

                    lockOrientation();

                    /*dialog = new Dialog(WizardActivity.this);
                    dialog.setContentView(R.layout.popup_novo_sensor);
                    dialog.setCancelable(false);
                    dialog.show();*/
                    progressDialog = new ProgressDialog(WizardActivity.this);
                    progressDialog.setMessage("Enviando informações...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    sensor = WizardSensor.getInstance().getSensor();
                    if (sensor != null) {

                        //precisamos de um id...

                        SensorDAO sensorDAO = new SensorDAO(WizardActivity.this);
                        int id = sensorDAO.insertSensor(sensor);
                        if (id < 1) {

                            try {
                                throw new Exception("Erro ao inserir sensor no banco SensorDAO.insertSensor(s) < 1");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        sensor.setId(id);

                        String message = null;

                        if ((sensor.getCarga(0).getPino() != -1) && (sensor.getCarga(1).getPino() != -1)) {

                            message = "setinfo:" + sensor.getNome() + ":" + sensor.getSSID() + ":" + sensor.getSenha() + ":" +
                                    sensor.getAmbiente().getId() + "," + sensor.getAmbiente().getNome() + ":" + sensor.getId() + ":" +
                                    sensor.getCarga(0).getNome() + "," + sensor.getCarga(0).getPino() + ":" +
                                    sensor.getCarga(1).getNome() + "," + sensor.getCarga(1).getPino() + ":";

                        } else if ((sensor.getCarga(0).getPino() != -1) && (sensor.getCarga(1).getPino() == -1)) {

                            message = "setinfo:" + sensor.getNome() + ":" + sensor.getSSID() + ":" + sensor.getSenha() + ":" +
                                    sensor.getAmbiente().getId() + "," + sensor.getAmbiente().getNome() + ":" + sensor.getId() + ":" +
                                    sensor.getCarga(0).getNome() + "," + sensor.getCarga(0).getPino() + ":" +
                                    "-1" + "," + "-1" + ":";

                        } else if ((sensor.getCarga(0).getPino() == -1) && (sensor.getCarga(1).getPino() != -1)) {

                            message = "setinfo:" + sensor.getNome() + ":" + sensor.getSSID() + ":" + sensor.getSenha() + ":" +
                                    sensor.getAmbiente().getId() + "," + sensor.getAmbiente().getNome() + ":" + sensor.getId() + ":" +
                                    "-1" + "," + "-1" + ":" +
                                    sensor.getCarga(1).getNome() + "," + sensor.getCarga(1).getPino() + ":";
                        }

                        sendNewSensorTask = new SendNewSensorConfig();
                        sendNewSensorTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, message);
                    }


                } else {

                    int numPages = wizardAdapter.getCount();
                    int curPosition = pager.getCurrentItem();

                    if (curPosition + 1 < numPages) {
                        pager.setCurrentItem(pager.getCurrentItem() + 1, true);

                    } else if (curPosition + 1 == numPages) {
                        //MainActivity.this.finish();
                    }
                }
            }
        });

        btnPrev.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/HighlandGothicFLF.ttf"));
        btnNext.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/HighlandGothicFLF.ttf"));

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

    private class SendNewSensorConfig extends AsyncTask<String, Void, HSError> {

        @Override
        protected HSError doInBackground(String... arg) {

            String sensorIP = WiFiUtil.getApIpAddr(WizardActivity.this);
            //String sensorIP = "192.168.1.46";
            String err = "";

            for (int i = 0; i < 2; i++) {

                Socket sock = new Socket();
                SocketAddress addr = new InetSocketAddress(sensorIP, 8000);

                try {

                    sock.connect(addr, 5000);

                    PrintWriter pout = new PrintWriter(sock.getOutputStream());
                    //pout.print(args[0] + "\n");
                    pout.print(arg[0] + "\n");
                    pout.flush();

                    byte[] b = new byte[256];

                    sock.setSoTimeout(5000);
                    int bytes = sock.getInputStream().read(b); //a resposta é um getinfo do novo sensor
                    sock.close();

                    String result = new String(b, 0, bytes - 1);

                    Sensor sensor = buildSensorFromGetInfo(result);
                    if (sensor == null) { //getinfo nao é valido
                        err = "A resposta getinfo para nossa requisição setinfo nao é valida.. ";
                        Log.e("CONN", err);
                        continue;
                    }

                    if (WizardSensor.getInstance().getSensor().getId() == sensor.getId()) {

                        return new HSError(true, result);

                    } else {

                        return new HSError(false, result);
                    }

                } catch (Exception e) {

                    if (e.getMessage() != null) {
                        err = e.getMessage();
                        Log.e(getClass().getName(), err);
                    }
                }
            }

            return new HSError(false, err);
        }


        @Override
        protected void onPostExecute(HSError result) {
            super.onPostExecute(result);

            boolean status = result.getStatus();
            String message = result.getMessage();

            //if (progressDialog != null && progressDialog.isShowing()) {
            //    progressDialog.dismiss();
            //}

            if (!status) {

                lockOrientation();
                new AlertDialog.Builder(WizardActivity.this)
                        .setMessage("Não foi possível configurar o sensor: " + message)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                progressDialog.dismiss();
                                unlockOrientation();
                                SensorDAO sensorDAO = new SensorDAO(WizardActivity.this);
                                sensorDAO.deleteSensor(sensor);
                                WizardActivity.this.finish();
                            }
                        })
                        .show();

            } else { //sensor configurado com sucesso!

                //SensorDAO sensorDAO = new SensorDAO(WizardActivity.this);
                //Sensor sensor = buildSensorFromGetInfo(message);
                //int id = sensorDAO.insertSensor(sensor);
                //if (id < 1) {

                //lockOrientation();
                //new AlertDialog.Builder(WizardActivity.this)
                //        .setMessage("Sensor configurado com sucesso!")
                //        .setCancelable(false)
                //        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                //            public void onClick(DialogInterface dialog, int id) {

                                                /* o sensor foi configurado... conectamos de volta à rede do ap */
                                config();
                                progressDialog.setMessage("Voltando à rede do ap...");
                  //              unlockOrientation();

                    //            progressDialog.setMessage("conectando em ap36...");
                                //WizardActivity.this.finish();

                          //  }
                      //  })
                        //.show();

            }

        }
    }

    private void config() {

        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        registerReceiver(mWifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        mWifiManager.startScan();

        HSSensor.getInstance().getListSensorOn().add(sensor);
    }
}