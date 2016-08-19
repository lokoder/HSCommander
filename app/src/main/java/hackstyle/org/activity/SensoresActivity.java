package hackstyle.org.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Field;

import hackstyle.org.adapter.SensoresAdapter;
import hackstyle.org.hscommander.R;
import hackstyle.org.main.HSSensor;
import hackstyle.org.pojo.Sensor;

public class SensoresActivity extends AppCompatActivity {

    TextView txtStatus;
    ListView listView;
    SensoresAdapter sensoresAdapter;
    SensoresUpdater sensoresUpdater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensores);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //ActionBar actionBar = getSupportActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Sensores");

        setToolbarConfig(toolbar);


        ImageView imageView = (ImageView)findViewById(R.id.imageViewAddSensor);
        imageView.setImageResource(R.drawable.add);

        txtStatus = (TextView)findViewById(R.id.txtStatus);
        listView = (ListView)findViewById(android.R.id.list);

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



    private void setToolbarConfig(Toolbar toolbar) {

        try {

            Field f = toolbar.getClass().getDeclaredField("mTitleTextView");
            f.setAccessible(true);

            TextView mToolbarTitleTextView = (TextView)f.get(toolbar);
            mToolbarTitleTextView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/HighlandGothicFLF.ttf"));

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    public void OnDestroy() {
        super.onDestroy();
        sensoresUpdater.cancel(true);
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

            /*if (values[0]) {

                progressBar.setVisibility(View.VISIBLE);

            } else {

                progressBar.setVisibility(View.GONE);
            }*/

            sensoresAdapter.refresh(HSSensor.getInstance().getListSensorOn());
            updateStatusSensors();
        }

    }





}
