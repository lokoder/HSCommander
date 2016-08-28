package hackstyle.org.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import hackstyle.org.adapter.SensorCargasAdapter;
import hackstyle.org.dao.SensorDAO;
import hackstyle.org.hscommander.R;
import hackstyle.org.pojo.Carga;
import hackstyle.org.pojo.Sensor;
import hackstyle.org.sqlite.DBAdapter;

public class DetalheSensorActivity extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static int RESULT_LOAD_IMAGE = 1;

    ImageView imageView;
    SensorCargasAdapter sensorCargasAdapter;
    ListView listViewCarga;
    TextView txtResponse;
    int sensorId;
    Sensor sensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_sensor);

        sensorId = getIntent().getIntExtra("id", -1);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Detalhe do sensor");

        setToolbarConfig(toolbar);

        final LinearLayout llResponse = (LinearLayout)findViewById(R.id.llResponse);
        llResponse.setVisibility(View.INVISIBLE);

        LinearLayout ll = (LinearLayout)findViewById(R.id.llSensorInfo);
        ll.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                Intent i = new Intent(DetalheSensorActivity.this, ComandosGeraisActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("id", sensorId);
                startActivity(i);

                return true;
            }
        });

        imageView = (ImageView) findViewById(R.id.imageViewCarga);
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                verifyStoragePermissions(DetalheSensorActivity.this);

                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
                return true;
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DetalheSensorActivity.this, "Clique longo para escolher uma imagem", Toast.LENGTH_SHORT).show();
            }
        });

        SensorDAO sensorDAO = new SensorDAO(this);
        sensor = sensorDAO.getSensor(sensorId);
        if (!sensor.getImagePath().isEmpty()) {
            File imgFile = new File(sensor.getImagePath());
            imageView.setImageURI(Uri.fromFile(imgFile));
        } else {
            imageView.setImageResource(R.drawable.selecione_imagem);
        }


        listViewCarga = (ListView) findViewById(R.id.listViewSensorCargas);

        listViewCarga.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapter, View view, int position, long l) {

                Carga carga = (Carga) adapter.getItemAtPosition(position);
                if (carga == null)
                    return false;

                Intent i = new Intent(DetalheSensorActivity.this, DetalheCargaActivity.class);
                i.putExtra("id", carga.getId());

                startActivity(i);
                return true;
            }
        });

        TextView txtNome = (TextView)findViewById(R.id.txtNome);
        TextView txtAmbiente = (TextView)findViewById(R.id.txtAmbiente);
        TextView txtIP = (TextView)findViewById(R.id.txtIP);

        txtNome.setText(sensor.getNome());
        txtAmbiente.setText(sensor.getAmbiente().getNome());
        txtIP.setText(sensor.getIp());

        txtResponse= (TextView)findViewById(R.id.txtResponse);
        txtResponse.setText("");

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            ImageView img = (ImageView) findViewById(R.id.imageViewCarga);

            File imgFile = new File(picturePath);
            img.setImageURI(Uri.fromFile(imgFile));

            DBAdapter dbAdapter = new DBAdapter(this);
            Sensor sensor = new Sensor();
            sensor.setId(sensorId);
            sensor.setImagePath(picturePath);

            if (dbAdapter.updateSensorImagePath(sensor) != -1)
                Toast.makeText(this, picturePath, Toast.LENGTH_LONG).show();

        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);
        Intent i = null;

        switch (item.getItemId()) {

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

            case android.R.id.home:

                Intent homeIntent = new Intent(this, SensoresActivity.class);
                startActivity(homeIntent);
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


    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


    public void connect_list() {

        SensorDAO sensorDAO = new SensorDAO(this);
        sensor = sensorDAO.getSensor(sensorId);

        List<Carga> listCarga = new ArrayList<Carga>();

        for (int i = 0; i < 2; i++) {

            Carga c = sensor.getCarga(i);
            if (c != null)
                listCarga.add(c);
        }

        sensorCargasAdapter = new SensorCargasAdapter(this, listCarga);
        listViewCarga.setAdapter(sensorCargasAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        connect_list();
    }









}
