package hackstyle.org.wizard;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
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

import com.eftimoff.viewpagertransformers.CubeOutTransformer;
import com.eftimoff.viewpagertransformers.FlipHorizontalTransformer;
import com.eftimoff.viewpagertransformers.ForegroundToBackgroundTransformer;
import com.eftimoff.viewpagertransformers.StackTransformer;
import com.eftimoff.viewpagertransformers.TabletTransformer;
import com.eftimoff.viewpagertransformers.ZoomInTransformer;
import com.eftimoff.viewpagertransformers.ZoomOutTranformer;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
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

    private WizardAdapter wizardAdapter;
    private ViewPager pager;
    private Button btnPrev, btnNext;
    private boolean finalizar = false;
    private ProgressDialog progressDialog;
    private SendNewSensorConfig sendNewSensorTask = null;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wizard);

        btnPrev = (Button)findViewById(R.id.wizard_previous_button);
        btnNext = (Button)findViewById(R.id.wizard_next_button);

        wizardAdapter = new WizardAdapter(getSupportFragmentManager());
        pager = (ViewPager)findViewById(R.id.pager);
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


                if (position+1 == wizardAdapter.getCount()) {
                    btnNext.setText("Finalizar");
                    finalizar = true;
                } else {
                    btnNext.setText("Próximo");
                    finalizar = false;
                }


                Fragment fragment = (Fragment) wizardAdapter.instantiateItem(pager, position);
                if (fragment != null) {

                    if (position == 5) {
                        TextView txtSSID = (TextView)fragment.getActivity().findViewById(R.id.txtSSIDSensor);
                        String str = WizardSensor.getInstance().getSensor().getSSID();
                        txtSSID.setText(str);

                        TextView txtSenha = (TextView)fragment.getActivity().findViewById(R.id.txtSenhaSensor);
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

                if (curPosition+1 > 1) {
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

                    sendNewSensorTask = new SendNewSensorConfig();
                    sendNewSensorTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);


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


    private class SendNewSensorConfig extends AsyncTask<Void, Void, HSError> {

        @Override
        protected HSError doInBackground(Void... args) {

            String sensorIP = WiFiUtil.getApIpAddr(WizardActivity.this);
            String err = "";

            for (int i=0; i<2; i++) {

                Socket sock = new Socket();
                SocketAddress addr = new InetSocketAddress(sensorIP, 8000);

                try {

                    sock.connect(addr, 3000);

                    PrintWriter pout = new PrintWriter(sock.getOutputStream());
                    pout.print(args[0] + "\n");
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

                    return new HSError(true, result);

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

            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            unlockOrientation();

            if (!status) {

                lockOrientation();
                new AlertDialog.Builder(WizardActivity.this)
                        .setMessage("Não foi possível configurar o sensor: " + message)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                unlockOrientation();
                                WizardActivity.this.finish();
                            }
                        })
                        .show();

            } else {

                SensorDAO sensorDAO = new SensorDAO(WizardActivity.this);
                Sensor sensor = buildSensorFromGetInfo(message);
                int id = sensorDAO.insertSensor(sensor);
                if (id < 1) {

                    lockOrientation();
                    new AlertDialog.Builder(WizardActivity.this)
                            .setMessage("Houve um erro inesperado ao salvar o sensor no SQLite")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    unlockOrientation();
                                    WizardActivity.this.finish();
                                }
                            })
                            .show();
                }

                HSSensor.getInstance().getListSensorOn().add(sensor);

                lockOrientation();
                new AlertDialog.Builder(WizardActivity.this)
                        .setMessage("Sensor configurado com sucesso!")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                unlockOrientation();
                                WizardActivity.this.finish();
                            }
                        })
                        .show();

                Intent intent = new Intent(WizardActivity.this, SensoresActivity.class);
                startActivity(intent);
            }

        }

    }

    public Sensor buildSensorFromGetInfo(String getinfo) {

        int count=0;

        for (int i=0; i<getinfo.length(); i++) {
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
        }
        else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT); //locks port
        }
    }

    private void unlockOrientation() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

}