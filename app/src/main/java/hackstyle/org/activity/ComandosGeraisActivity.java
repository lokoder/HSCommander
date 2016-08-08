package hackstyle.org.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import hackstyle.org.dao.AmbienteDAO;
import hackstyle.org.dao.SensorDAO;
import hackstyle.org.hscommander.R;
import hackstyle.org.pojo.Sensor;

public class ComandosGeraisActivity extends AppCompatActivity {

    int sensorId;
    Sensor sensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comandos_gerais);

        sensorId = getIntent().getIntExtra("id", -1);
        if (sensorId == -1) {

            new AlertDialog.Builder(ComandosGeraisActivity.this)
                    .setMessage("Não há um sensor atrelado a esta tela!")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    })
                    .show();
        }

        SensorDAO sensorDAO = new SensorDAO(this);
        sensor = sensorDAO.getSensor(sensorId);


        Button btnZerarFS = (Button)findViewById(R.id.btnZerarFS);
        btnZerarFS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SensorPipe sensorPipe = new SensorPipe(ComandosGeraisActivity.this);
                sensorPipe.execute(sensor.getIp(), "zetinfo::::::::");
            }
        });
    }


    private class SensorPipe extends AsyncTask<String, String, String> {

        Context context;

        SensorPipe(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... args) {

            String ip = args[0];
            String message = args[1];

            Socket sock = new Socket();
            SocketAddress addr = new InetSocketAddress(ip, 8000);

            try {

                sock.connect(addr, 3000);

                PrintWriter pout = new PrintWriter(sock.getOutputStream());
                pout.print(message+"\n");
                pout.flush();

                byte[] b = new byte[256];

                sock.setSoTimeout(3000);
                int bytes = sock.getInputStream().read(b);
                sock.close();

                String result = new String(b, 0, bytes - 1);

                return result;

            } catch (Exception e) {

                if (e.getMessage() != null)
                    Log.e("CONN", e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s == null)
                Toast.makeText(context, "Houve um erro na resposta do sensor! Verifique!", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context, "Resposta: " + s, Toast.LENGTH_LONG).show();

        }
    }
}
