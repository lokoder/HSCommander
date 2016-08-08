package hackstyle.org.activity;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import hackstyle.org.dao.SensorDAO;
import hackstyle.org.hscommander.R;
import hackstyle.org.main.HSSensor;

public class VarreduraSensoresActivity extends AppCompatActivity {

    TextView txtNumSensoresCadastrados;
    TextView txtNumSensoresEncontrados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_varredura_sensores);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        getSupportActionBar().setLogo(R.drawable.appiconbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("  " + "HSCommander");

        txtNumSensoresCadastrados = (TextView)findViewById(R.id.txtNumSensoresCadastrados);
        txtNumSensoresEncontrados = (TextView)findViewById(R.id.txtNumSensoresEncontrados);

        Test test = new Test();
        test.execute("");
    }


    private class Test extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {

            SensorDAO sensorDAO = new SensorDAO(VarreduraSensoresActivity.this);
            int numSensorsBD = sensorDAO.getListSensor().size();

            while (HSSensor.getInstance().isScanning()) {

                try {
                    Thread.sleep((long)500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int numSensorsOn = HSSensor.getInstance().getListSensorOn().size();
                publishProgress(Integer.toString(numSensorsBD), Integer.toString(numSensorsOn));
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            txtNumSensoresCadastrados.setText(values[0]);
            txtNumSensoresEncontrados.setText(values[1]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            /*if (HSSensor.getInstance().getListSensorOn().size() > 0) {
                Intent intent = new Intent(VarreduraSensoresActivity.this, SensoresActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                VarreduraSensoresActivity.this.finish();

            } else {

                Test test = new Test();
                test.execute("");
            }*/
        }
    }

}
