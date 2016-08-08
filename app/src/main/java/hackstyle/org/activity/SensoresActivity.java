package hackstyle.org.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import hackstyle.org.adapter.SensoresAdapter;
import hackstyle.org.hscommander.R;
import hackstyle.org.main.HSSensor;
import hackstyle.org.pojo.Sensor;

public class SensoresActivity extends AppCompatActivity {

    TextView txtStatus;
    ListView listView;
    ProgressBar progressBar;
    SensoresAdapter sensoresAdapter;
    SensoresUpdater sensoresUpdater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensores);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        getSupportActionBar().setLogo(R.drawable.appiconbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("  " + "Sensores");


        txtStatus = (TextView)findViewById(R.id.txtStatus);
        listView = (ListView)findViewById(android.R.id.list);
        progressBar = (ProgressBar)findViewById(R.id.progressBarSensores);

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
        connect_list();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long l) {

                Sensor sensor = (Sensor)adapter.getItemAtPosition(position);

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
            sensoresUpdater.execute(true);
        }
    }


    public void OnDestroy() {
        super.onDestroy();
        sensoresUpdater.cancel(true);
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
                i = new Intent(this, NovoAmbienteActivity.class);
                startActivity(i);
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


    private void connect_list() {

        sensoresAdapter = new SensoresAdapter(this, HSSensor.getInstance().getListSensorOn());
        if (sensoresAdapter != null)
            listView.setAdapter(sensoresAdapter);

        String message= null;

        int countOnline = 0;
        for(Sensor s : HSSensor.getInstance().getListSensorOn())
            if (s.isActive())
                countOnline++;

        if (countOnline == 0)
            message = "Nenhum sensor ativo";
        else if (countOnline == 1)
            message = "1 sensor ativo";
        else if (countOnline > 1)
                message = sensoresAdapter.getCount() + " sensores ativos";

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


    @Override
    protected void onResume() {
        super.onResume();
        //connect_list();
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
                    Thread.sleep((long)600);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Boolean... values) {
            super.onProgressUpdate(values);

            if (values[0]) {

                progressBar.setVisibility(View.VISIBLE);

            } else {

                progressBar.setVisibility(View.GONE);
            }

            sensoresAdapter.refresh(HSSensor.getInstance().getListSensorOn());
        }

    }





}
