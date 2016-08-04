package hackstyle.org.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import hackstyle.org.adapter.SensoresAdapter;
import hackstyle.org.hscommander.R;
import hackstyle.org.main.SensorSingleton;
import hackstyle.org.pojo.Sensor;
import hackstyle.org.sqlite.DBAdapter;

public class SensoresActivity extends AppCompatActivity {

    SensoresAdapter sensoresAdapter;
    TextView txtStatus;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensores);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        getSupportActionBar().setLogo(R.drawable.appiconbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("  " + "HSCommander");

        if (SensorSingleton.getInstance().getListSensorProd().size() < 1) {

            new AlertDialog.Builder(SensoresActivity.this)
                    .setMessage("Não há sensores pareados. Utilize o menu 'Varredura' para descobrir sensores na rede!")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    }).show();
        }

        txtStatus = (TextView)findViewById(R.id.txtStatus);
        listView = (ListView)findViewById(android.R.id.list);

        connect_list();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long l) {

                Sensor sensor = (Sensor)adapter.getItemAtPosition(position);

                Intent i = new Intent(SensoresActivity.this, DetalheSensorActivity.class);
                i.putExtra("id", sensor.getId());
                startActivity(i);
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
                i = new Intent(this, NovoAmbienteActivity.class);
                startActivity(i);
                break;

            case R.id.start_sensores:
                i = new Intent(this, SensoresActivity.class);
                startActivity(i);
                break;

            case R.id.start_varredura:
                i = new Intent(this, VarreduraSensoresActivity.class);
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

        DBAdapter dbAdapter = new DBAdapter(this);
        dbAdapter.updateListSensorProd();

        sensoresAdapter = new SensoresAdapter(this, SensorSingleton.getInstance().getListSensorProd());
        listView.setAdapter(sensoresAdapter);

        if (sensoresAdapter.getCount() == 0)
            txtStatus.setText("Nenhum sensor ativo");
        else if (sensoresAdapter.getCount() == 1)
            txtStatus.setText("1 sensor ativo");
        else if (sensoresAdapter.getCount() > 1)
                txtStatus.setText(sensoresAdapter.getCount() + " sensores ativos");

    }


    @Override
    protected void onResume() {
        super.onResume();
        connect_list();
    }

}
