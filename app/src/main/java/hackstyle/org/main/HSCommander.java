package hackstyle.org.main;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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


public class HSCommander extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hscommander);

        //Intent i = new Intent(this, VarreduraSensoresActivity.class);
        //startActivity(i);

        AskSensor askSensor = new AskSensor();
        askSensor.execute();
    }


    private class AskSensor extends AsyncTask<Void, Void, Boolean> {

        Sensor sensor;

        @Override
        protected Boolean doInBackground(Void... args) {

            String apIP = WiFiUtil.getApIpAddr(HSCommander.this);
            Socket sock = new Socket();
            SocketAddress addr = new InetSocketAddress(apIP, 8000);

            try {

                sock.connect(addr, 20);

                PrintWriter pout = new PrintWriter(sock.getOutputStream());
                pout.print("getinfo::::::::\n");
                pout.flush();

                byte[] b = new byte[256];

                sock.setSoTimeout(30000);
                int bytes = sock.getInputStream().read(b);
                //sock.close();

                String result = new String(b, 0, bytes - 1);

                sensor = buildSensorFromGetInfo(result);
                if (sensor == null) {
                    //getinfo nao é valido
                    Log.e("CONN", "Dispositivo em " + apIP +" não é um sensor válido!");
                    throw new Exception("Dispositivo em " + apIP +" não é um sensor válido!");
                }

                pout.print("id=-1 ok!\n");
                pout.flush();

                sock.close();

                if (sensor.getId() == -1) { //sensor virgem, podemos configurar o brinquedo

                    sensor.setIp(apIP);
                    sensor.setPorta(8000);
                    return true;
                }

            } catch (Exception e) {

                Log.e("CONN", e.getLocalizedMessage());
                //Toast.makeText(HSCommander.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }

            return false;
        }


        protected void onPostExecute(Boolean arg) {

            if (arg) {

                //Intent i = new Intent(HSCommander.this, NovoSensorActivity.class);
                //i.putExtra("sensorIP", sensor.getIp());
                //startActivity(i);

            } else {

                Toast.makeText(HSCommander.this, "O Access Point não é um sensor virgem!", Toast.LENGTH_LONG).show();
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
                int id_ambiente = Integer.parseInt(str[4]);
                AmbienteDAO ambienteDAO = new AmbienteDAO(HSCommander.this);
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