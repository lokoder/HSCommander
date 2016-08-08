package hackstyle.org.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import hackstyle.org.hscommander.R;
import hackstyle.org.main.HSSensor;
import hackstyle.org.service.SensorCollector;
import hackstyle.org.sqlite.DBAdapter;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        getSupportActionBar().setLogo(R.drawable.appiconbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("  " + "HSCommander");

        ImageView imageView = (ImageView)findViewById(R.id.imageViewIntro);
        imageView.setImageResource(R.drawable.logo2);

        if (savedInstanceState == null) {
            Intent intent = new Intent(Intent.ACTION_SYNC, null, this, SensorCollector.class);
            startService(intent);
        }

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
                //if (HSSensor.getInstance().isScanning())
                //    i = new Intent(this, VarreduraSensoresActivity.class);
                //else
                    i = new Intent(this, SensoresActivity.class);

                startActivity(i);
                break;

            case R.id.start_varredura:
                //  i = new Intent(this, VarreduraSensoresActivity.class);
                //  startActivity(i);
                break;

            case R.id.start_wificred:
                i = new Intent(this, WiFiCredentialsActivity.class);
                startActivity(i);
                break;

            case R.id.start_zerabanco:
                DBAdapter dbAdapter = new DBAdapter(this);
                dbAdapter.zeraDB();
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

}
