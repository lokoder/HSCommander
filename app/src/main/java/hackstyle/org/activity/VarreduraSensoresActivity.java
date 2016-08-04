package hackstyle.org.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import hackstyle.org.dao.AmbienteDAO;
import hackstyle.org.dao.SensorDAO;
import hackstyle.org.hscommander.R;
import hackstyle.org.main.SensorSingleton;
import hackstyle.org.pojo.Ambiente;
import hackstyle.org.pojo.Carga;
import hackstyle.org.pojo.Sensor;
import hackstyle.org.sqlite.DBHelper;
import hackstyle.org.wifi.WiFiUtil;

public class VarreduraSensoresActivity extends AppCompatActivity {

    ProgressBar progressBar;
    TextView txtSensoresCadastrados, txtSensoresEncontrados;
    TextView txtStatus;
    CheckSensors checkSensors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_varredura_sensores);

        txtStatus = (TextView)findViewById(R.id.txtStatus);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        getSupportActionBar().setLogo(R.drawable.appiconbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("  " + "HSCommander");

        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        DBHelper dbHelper = new DBHelper(this);

        txtStatus.setText("Escaneando rede à procura de sensores...");

        WindowManager windowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        int screenWidth = windowManager.getDefaultDisplay().getWidth();

        progressBar.getLayoutParams().width = screenWidth/2;
        progressBar.invalidate();

        txtSensoresEncontrados = (TextView) findViewById(R.id.txtNumSensoresEncontrados);
        txtSensoresCadastrados = (TextView)findViewById(R.id.txtNumSensoresCadastrados);
        SensorDAO sensorDAO = new SensorDAO(this);
        List<Sensor> list = sensorDAO.getListSensor();

        int num = list.size();
        txtSensoresCadastrados.setText(Integer.toString(num));

        SensorSingleton.getInstance().eraseListSensorProd();

        checkSensors = new CheckSensors(this);
        checkSensors.execute();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        checkSensors.cancel(true);
    }

    private class CheckSensors extends AsyncTask<Void, Integer, Void> {

        private Context context;

        public CheckSensors(Context context) {
            this.context = context.getApplicationContext();
        }

        @Override
        protected Void doInBackground(Void... args) {

            loadSingletonTempList(); //carrega os sensores ja cadastrados

            checkRegisteredSensors(); //checa se o sensor ainda se encontra no ip cadastrado

            List<String> onlineIPs = findSensorsIP(); //escaneia a rede local por portas 8000 abertas
            checkLostSensors(onlineIPs); //conecta nas portas 8000 e envia getinfo..
            //se bater com o sensor cadastrado, atualiza IP e passa pra listProd
            //se id == -1, ignora
            //se id nao existe em nossa base, cadastramos esse sensor e passa pra listProd


            //a partir daqui, listProd é pra conter os sensores que trabalharemos....
            //listErr os sensores cadastrados que nao forem encontrados (geralmente 0, né..)

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {

            super.onPostExecute(aVoid);

            if (SensorSingleton.getInstance().getListSensorProd().size() > 0) {
                Intent i = new Intent(context, SensoresActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
                finish();

            } else {

                new AlertDialog.Builder(VarreduraSensoresActivity.this)
                        .setMessage("Nenhum sensor foi detectado! Realizar a varredura novamente?")
                        .setCancelable(false)
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                Intent intent = new Intent(VarreduraSensoresActivity.this, VarreduraSensoresActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();

                            }}).setNegativeButton("Não", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(VarreduraSensoresActivity.this, IntroActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                                finish();
                            }}).show();

            }
        }

        @Override
        protected void onProgressUpdate(Integer... args) {

            super.onProgressUpdate(args);

            if (args[0] != -1) {
                progressBar.setProgress(args[0]);
                return;
            }

            int nrEncontrados = Integer.parseInt(txtSensoresEncontrados.getText().toString());
            int nrCadastrados = Integer.parseInt(txtSensoresCadastrados.getText().toString());

            txtSensoresEncontrados.setText(Integer.toString(nrEncontrados+1));
            if (nrEncontrados+1 == nrCadastrados) {

                /*new AlertDialog.Builder(VarreduraSensoresActivity.this)
                        .setMessage("Os sensores cadastrados já foram encontrados. Deseja continuar escaneando a rede à procura de outros sensores?")
                        .setCancelable(false)
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {}
                        }).setNegativeButton("Não", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                    */
                                checkSensors.cancel(true);
                                if (SensorSingleton.getInstance().getListSensorProd().size() > 0) {
                                    Intent intent = new Intent(context, SensoresActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);

                                    checkSensors.cancel(true);
                                    finish();
                                }
                       /*     }
                        }).show(); */
            }

        }

        private void loadSingletonTempList() {

            SensorDAO sensorDAO = new SensorDAO(VarreduraSensoresActivity.this);
            List<Sensor> listSensor = sensorDAO.getListSensor();

            for (Sensor s : listSensor) {
                SensorSingleton.getInstance().addSensor(s);
            }
        }


        private void checkRegisteredSensors() {

            List<Sensor> listSensor = SensorSingleton.getInstance().getListSensor();

            //for (Sensor sensor : listSensor) {
            for (int i=0; i<listSensor.size(); i++) {

                if (Thread.interrupted())
                    return;

                Sensor sensor = (Sensor)listSensor.get(i);

                String ip = sensor.getIp();
                //int port = sensor.getPorta();
                int port = 8000;

                if (ip.isEmpty()) //provavelmente um sensor recem configurado...
                    continue;

                Socket sock = new Socket();
                SocketAddress addr = new InetSocketAddress(ip, port);

                try {

                    sock.connect(addr, 3000);

                    PrintWriter pout = new PrintWriter(sock.getOutputStream());
                    pout.print("getinfo::::::::\n");
                    pout.flush();

                    byte[] b = new byte[256];

                    sock.setSoTimeout(3000);
                    int bytes = sock.getInputStream().read(b);
                    sock.close();

                    String result = new String(b, 0, bytes - 1);

                    Sensor s = buildSensorFromGetInfo(result);
                    if (s == null) {
                        continue;
                    }

                    if (sensor.equals(s)) { //é o sensor cadastrado, add em sensorSingletonProd e remove daqui

                        SensorSingleton.getInstance().addSensorProd(sensor);
                        SensorSingleton.getInstance().removeSensor(sensor);
                        Log.e("BATEUU", "SENSOR PAREADO!!!");
                        publishProgress(-1);
                    }

                } catch (Exception e) {

                    //Log.e("CONN", e.getLocalizedMessage());
                }

            }

        }


        //retorna uma lista de ips na subrede com a porta 8000 aberta
        private List<String> findSensorsIP() {

            List<String> listOnlineIPs = new ArrayList<String>();
            WiFiUtil wiFiUtil = new WiFiUtil(VarreduraSensoresActivity.this);
            String subnet = wiFiUtil.getNetworkAddress();

            for (int i=1;i<255;i++) {

                if (Thread.interrupted())
                    return null;

                String host = subnet + "." + i;
                boolean configured = false;

                publishProgress(i);

                /* pula os ips dos sensores ja configurados */
                if (SensorSingleton.getInstance().getListSensorProd().size() > 0) {

                    List<Sensor> list = SensorSingleton.getInstance().getListSensorProd();
                    for (Sensor s : list) {

                        if (s.getIp().equals(host)) {
                            configured = true;
                            break;
                        }
                    }

                    if (configured)
                        continue;
                }


                try {

                    Socket sock = new Socket();
                    SocketAddress addr = new InetSocketAddress(host, 8000);

                    sock.connect(addr, 50);

                    PrintWriter pout = new PrintWriter(sock.getOutputStream());

                    pout.print("dummy\n");
                    pout.flush();

                    listOnlineIPs.add(host);

                    Log.e("SCAN", "ip " + host + " online!");
                    sock.close();

                } catch (Exception e) {

                    Log.e("SCAN", "ip " + host + " offline!");
                }
            }

            return listOnlineIPs;

        }


        //da lista de ips com a porta 8000 aberta, consulta com getinfo...
        //se o sensor bater com o cadastrado (id), atualizamos o ip no banco, removemos de list
        //e adicionamos a listProd
        public void checkLostSensors(List<String> listOnlineIPs) {

            List<Sensor> listSensor = SensorSingleton.getInstance().getListSensor();
            SensorDAO sensorDAO = new SensorDAO(VarreduraSensoresActivity.this);

            for (String ip : listOnlineIPs) {

                if (Thread.interrupted())
                    return;

                Socket sock = new Socket();
                SocketAddress addr = new InetSocketAddress(ip, 8000);

                try {

                    sock.connect(addr, 3000);

                    PrintWriter pout = new PrintWriter(sock.getOutputStream());

                    pout.print("getinfo::::::::\n");
                    pout.flush();

                    byte[] b = new byte[256];

                    sock.setSoTimeout(5000);
                    int bytes = sock.getInputStream().read(b);
                    sock.close();

                    String result = new String(b, 0, bytes - 1);

                    Sensor sensor = buildSensorFromGetInfo(result);
                    if (sensor == null)
                        continue;


                    boolean found = false;

                    for (Sensor s : listSensor) {

                        if (sensor.equals(s)) { //é o sensor cadastrado, com ip diferente... atualizamos o ip no banco

                            s.setIp(ip);
                            s.setPorta(8000);

                            sensorDAO.updateIP(s);

                            SensorSingleton.getInstance().addSensorProd(s);
                            SensorSingleton.getInstance().removeSensor(s);

                            found = true;
                            publishProgress(-1);
                        }
                    }

                    if (found)
                        break;

                    //o sensor vindo da rede nao é nenhum dos cadastrados...
                    //o id é -1? nao deveria... ignoramos
                    //o id é != -1, provavelmente um sensor adicionado à rede por outra pessoa
                    //cadastramos ele com o id existente vindo da rede

                    if (sensor.getId() != -1) {

                        sensor.setIp(ip);
                        sensor.setPorta(8000);
                        sensorDAO.insertSensor(sensor);
                        SensorSingleton.getInstance().addSensorProd(sensor);
                    }


                    //o que restou em listSensor deu erro... adicionamos numa lista de erro e ja era

                    listSensor = SensorSingleton.getInstance().getListSensor();

                    for (Sensor s : listSensor) {
                        SensorSingleton.getInstance().addSensorErr(s);
                        SensorSingleton.getInstance().removeSensor(s);
                    }


                } catch (Exception e) {

                    //Log.e("EXCEP", e.getLocalizedMessage());
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
            AmbienteDAO ambienteDAO = new AmbienteDAO(VarreduraSensoresActivity.this);
            Ambiente ambiente = ambienteDAO.getAmbiente(id_ambiente);
            if (ambiente != null)
                sensor.setAmbiente(ambiente);
            else
                sensor.setAmbiente(new Ambiente());

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

    } //AsyncTask



}
