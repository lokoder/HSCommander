package hackstyle.org.activity;

import android.app.Activity;
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

import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import hackstyle.org.dao.AmbienteDAO;
import hackstyle.org.hscommander.R;
import hackstyle.org.pojo.Ambiente;
import hackstyle.org.pojo.Carga;
import hackstyle.org.pojo.Sensor;
import hackstyle.org.wifi.WiFiUtil;

public class CheckNovoSensorActivity extends AppCompatActivity {

    String sensorIP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_novo_sensor);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        getSupportActionBar().setLogo(R.drawable.appiconbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("  " + "HSCommander");

        sensorIP = WiFiUtil.getApIpAddr(this);
        CheckNewSensor checkNewSensor = new CheckNewSensor(this);
        checkNewSensor.execute();
    }



    private class CheckNewSensor extends AsyncTask<Void, Void, String> {

        Context context;
        Activity activity;

        public CheckNewSensor(Activity activity) {

            this.context = activity.getApplicationContext();
            this.activity = activity;
        }

        @Override
        protected String doInBackground(Void... voids) {

            Socket sock = new Socket();
            SocketAddress addr = new InetSocketAddress(sensorIP, 8000);

            try {

                sock.connect(addr, 2000);

                PrintWriter pout = new PrintWriter(sock.getOutputStream());
                pout.print("getinfo:::::::check:\n");
                pout.flush();

                byte[] b = new byte[256];

                sock.setSoTimeout(2000);
                int bytes = sock.getInputStream().read(b);

                String result = new String(b, 0, bytes - 1);

                Sensor sensor = buildSensorFromGetInfo(result);
                if (sensor == null) {
                    //getinfo nao é valido
                    Log.e("CONN", "Dispositivo em " + sensorIP +" não é um sensor válido!");
                    throw new Exception("Dispositivo em " + sensorIP +" não é um sensor válido!");
                }

                pout.print("id=-1 ok!\n");
                pout.flush();

                sock.close();

                if (sensor.getId() == -1) { //sensor virgem, podemos configurar o brinquedo
                    return result;
                }

            } catch (Exception e) {

                Log.e("CONN", e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String message) {
            super.onPostExecute(message);

            if (message == null) {

                new AlertDialog.Builder(CheckNovoSensorActivity.this)
                        .setMessage("Não foi possível encontrar um sensor para configurar! Conecte na rede WiFi criada pelo sensor e tente novamente!")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                activity.finish();
                            }
                        }).show();

            } else {

                Intent i = new Intent(context, NovoSensorActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("sensorIP", sensorIP);
                context.startActivity(i);
                activity.finish();
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

            String[] str = getinfo.split(":"); //nao funciona depois do ultimo valor... getinfo:::::-1::: deixa o vetor com 5...

            Sensor sensor = new Sensor();

            if (str[1].isEmpty())
                sensor.setNome("");
            else
                sensor.setNome(str[1]);

            if (str[5].isEmpty())
                sensor.setId(-1);
            else
                sensor.setId(Integer.parseInt(str[5]));

            if (str[4].isEmpty()) {
                sensor.setAmbiente(null);
            } else {

                String[] idamb = str[4].split(",");

                int id_ambiente = Integer.parseInt(idamb[0]);
                AmbienteDAO ambienteDAO = new AmbienteDAO(context);
                Ambiente ambiente = ambienteDAO.getAmbiente(id_ambiente);
                sensor.setAmbiente(ambiente);
            }

            for (int i = 6; i < str.length; i++) {

                if (str[i].isEmpty())
                    continue;

                String[] ss = str[i].split(",");

                Carga c = new Carga();
                if (ss[0].isEmpty())
                    c.setNome("");
                else
                    c.setNome(ss[0].trim());

                if (ss[1].isEmpty())
                    c.setPino(-1);
                else
                    c.setPino(Integer.parseInt(ss[1].trim()));

                sensor.putCarga(c);
            }

            return sensor;
        }

    }
}
