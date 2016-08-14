package hackstyle.org.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import hackstyle.org.dao.AmbienteDAO;
import hackstyle.org.dao.SensorDAO;
import hackstyle.org.hscommander.R;
import hackstyle.org.pojo.Ambiente;
import hackstyle.org.pojo.Carga;
import hackstyle.org.pojo.Sensor;
import hackstyle.org.wifi.WiFiUtil;

public class NovoSensorActivity extends AppCompatActivity {

    Spinner spin;
    EditText edtNome, edtCarga1, edtCarga2;
    EditText edtPinoCarga1, edtPinoCarga2;
    Button btnInsert, btnNovoAmbiente;
    TextView txtIP;
    String sensorIP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_sensor);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        getSupportActionBar().setLogo(R.drawable.appiconbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("  " + "HSCommander");

        sensorIP = WiFiUtil.getApIpAddr(this);

        spin = (Spinner)findViewById(R.id.spinAmbiente);
        edtNome = (EditText)findViewById(R.id.edtNome);
        edtCarga1 = (EditText)findViewById(R.id.edtCarga1);
        edtCarga2 = (EditText)findViewById(R.id.edtCarga2);
        //edtPinoCarga1 = (EditText)findViewById(R.id.edtPinoCarga1);
        edtPinoCarga2 = (EditText)findViewById(R.id.edtPinoCarga2);
        txtIP = (TextView)findViewById(R.id.txtIP);

        AmbienteDAO ambienteDAO = new AmbienteDAO(this);
        List<Ambiente> listAmbiente = ambienteDAO.getListAmbiente();

        if (listAmbiente != null) {

            List<String> ls = new ArrayList<String>();
            Ambiente[] arrAmbiente = new Ambiente[listAmbiente.size()];

            for (int i = 0; i < listAmbiente.size(); i++)
                arrAmbiente[i] = listAmbiente.get(i);

            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, arrAmbiente);
            //ArrayAdapter adapter = new ArrayAdapter(this, R.layout.spinner_item, ls);
           // ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, arrAmbiente);
            //adapter.setDropDownViewResource(R.layout.spinner_item);
            spin.setAdapter(adapter);
        }


        btnInsert = (Button)findViewById(R.id.btnInsertSensor);
        btnInsert.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String nome = edtNome.getText().toString();
                String carga1 = edtCarga1.getText().toString();
                String carga2 = edtCarga2.getText().toString();
                int pinoCarga1=-1, pinoCarga2=-1;

                Ambiente ambiente = (Ambiente)spin.getSelectedItem();

                if (edtPinoCarga1.getText().length() > 0) {
                    pinoCarga1 = Integer.parseInt(edtPinoCarga1.getText().toString());
                }

                if (edtPinoCarga2.getText().length() > 0) {
                    pinoCarga2 = Integer.parseInt(edtPinoCarga2.getText().toString());
                }


                if (nome.isEmpty()) {
                    Toast.makeText(NovoSensorActivity.this, "Selecione um nome para o sensor!", Toast.LENGTH_SHORT).show();
                    edtNome.requestFocus();
                    return;
                }

                if (carga1.isEmpty() && carga2.isEmpty()) {
                    Toast.makeText(NovoSensorActivity.this, "Selecione pelo menos uma carga para o sensor!", Toast.LENGTH_SHORT).show();
                    edtCarga1.requestFocus();
                    return;
                }

                if (!carga1.isEmpty() && (pinoCarga1 == -1)) {
                    Toast.makeText(NovoSensorActivity.this, "Selecione o pino em que a carga está conectada!", Toast.LENGTH_SHORT).show();
                    edtPinoCarga1.requestFocus();
                    return;
                }

                if (!carga2.isEmpty() && (pinoCarga2 == -1)) {
                    Toast.makeText(NovoSensorActivity.this, "Selecione o pino em que a carga está conectada!", Toast.LENGTH_SHORT).show();
                    edtPinoCarga2.requestFocus();
                    return;
                }

                //String sep = " - ";
                //String str = nome + sep + carga1 + sep + pinoCarga1 + sep + carga2 + sep + pinoCarga2 + sep + ambiente.getNome();



                Sensor sensor = new Sensor();
                sensor.setNome(nome);
                sensor.setAmbiente(ambiente);

                Carga c1=new Carga(), c2=new Carga();

                if (!carga1.isEmpty() && (pinoCarga1 > 0)) {

                    c1.setNome(carga1);
                    c1.setPino(pinoCarga1);
                    sensor.putCarga(c1);
                }

                if (!carga2.isEmpty() && (pinoCarga2 > 0)) {

                    c2.setNome(carga2);
                    c2.setPino(pinoCarga2);
                    sensor.putCarga(c2);
                }


                SensorDAO sensorDAO = new SensorDAO(NovoSensorActivity.this);
                int sid = sensorDAO.insertSensor(sensor);
                if (sid == -1) {

                    Toast.makeText(NovoSensorActivity.this, "Houve um erro ao adicionar o sensor ao banco de dados!", Toast.LENGTH_LONG).show();
                    return;

                } else {

                    sensor.setId(sid);

                    String setinfo;

                    if (!c1.getNome().isEmpty() && !c2.getNome().isEmpty())
                        setinfo = String.format("setinfo:%s:AP 36:ap36erick:%d,%s:%d:%s,%d:%s,%d:", nome, ambiente.getId(), ambiente.getNome(), sensor.getId(), c1.getNome(), c1.getPino(), c2.getNome(), c2.getPino());
                    else if (c1.getNome().isEmpty())
                        setinfo = String.format("setinfo:%s:AP 36:ap36erick:%d,%s:%d::%s,%d:", nome, ambiente.getId(), ambiente.getNome(), sensor.getId(), c2.getNome(), c2.getPino());
                    else
                        setinfo = String.format("setinfo:%s:AP 36:ap36erick:%d,%s:%d:%s,%d::", nome, ambiente.getId(), ambiente.getNome(), sensor.getId(), c1.getNome(), c1.getPino());


                    SendNewSensor sendNewSensor = new SendNewSensor();
                    sendNewSensor.execute(setinfo);

                    //Toast.makeText(NovoSensorActivity.this, "Sensor adicionado com sucesso com id: " + Integer.toString(sid), Toast.LENGTH_LONG).show();
                }


                clearAllFields();
                edtNome.requestFocus();
            }

        });//onclick


        btnNovoAmbiente = (Button)findViewById(R.id.btnAddAmbiente);
        btnNovoAmbiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final PopupWindow mpopup;
                final View popUpView = getLayoutInflater().inflate(R.layout.popup_novo_ambiente,
                        null); // inflating popup layout
                mpopup = new PopupWindow(popUpView, LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT, true); // Creation of popup
                mpopup.setAnimationStyle(android.R.style.Animation_Dialog);
                mpopup.showAtLocation(popUpView, Gravity.CENTER, 0, 0); // Displaying popup

                Button btnInsertAmbiente = (Button)popUpView.findViewById(R.id.btnInsertAmbiente);
                btnInsertAmbiente.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mpopup.dismiss();
                    }
                });

            }
        });

        sensorIP = getIntent().getStringExtra("sensorIP");
        txtIP.setText("Novo sensor encontrado em " + sensorIP);


        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.WHITE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);
        Intent i = null;

        switch (item.getItemId()) {

            case R.id.start_novo_sensor:
                i = new Intent(this, CheckNovoSensorActivity.class);
                startActivity(i);
                break;

            case R.id.start_ambiente:
                i = new Intent(this, GerenciaAmbienteActivity.class);
                startActivity(i);
                break;

            case R.id.start_sensores:
                i = new Intent(this, SensoresActivity.class);
                startActivity(i);
                break;

            case R.id.start_varredura:
               // i = new Intent(this, VarreduraSensoresActivity.class);
               // startActivity(i);
                break;

            case R.id.start_wificred:
                i = new Intent(this, WiFiCredentialsActivity.class);
                startActivity(i);
                break;
        }

        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }


    private void clearAllFields() {

        edtNome.getText().clear();
        edtCarga1.getText().clear();
        edtCarga2.getText().clear();
        edtPinoCarga1.getText().clear();
        edtPinoCarga2.getText().clear();
    }



    private class SendNewSensor extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... args) {

            sensorIP = WiFiUtil.getApIpAddr(NovoSensorActivity.this);

            Socket sock = new Socket();
            SocketAddress addr = new InetSocketAddress(sensorIP, 8000);

            try {

                sock.connect(addr, 3000);

                PrintWriter pout = new PrintWriter(sock.getOutputStream());
                pout.print(args[0]+"\n");
                pout.flush();

                byte[] b = new byte[256];

                sock.setSoTimeout(8000);
                int bytes = sock.getInputStream().read(b); //a resposta é um getinfo do novo sensor
                sock.close();

                String result = new String(b, 0, bytes - 1);

                Sensor sensor = buildSensorFromGetInfo(result);
                if (sensor == null) {
                    //getinfo nao é valido
                    Log.e("CONN", "A resposta getinfo para nossa requisição setinfo nao é valida.. ");
                    return null;
                }

                return result;

            } catch (Exception e) {
                //timeout do socket
                if (e.getMessage() != null)
                    Log.e("CONN", e.getMessage());
                //Toast.makeText(HSCommander.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String param) {

            super.onPostExecute(param);

            if (param != null) {

                //Toast.makeText(NovoSensorActivity.this, "Sensor configurado com sucesso!", Toast.LENGTH_LONG).show();

                new AlertDialog.Builder(NovoSensorActivity.this)
                        .setMessage("Sensor configurado! Conecte em sua rede novamente")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(NovoSensorActivity.this, IntroActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                NovoSensorActivity.this.finish();
                            }
                        }).show();

                //rodar VarreduraSensores...
            } else {

                Toast.makeText(NovoSensorActivity.this, "Houve um erro na resposta do sensor!", Toast.LENGTH_LONG).show();
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
                AmbienteDAO ambienteDAO = new AmbienteDAO(NovoSensorActivity.this);
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
