package hackstyle.org.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import hackstyle.org.dao.AmbienteDAO;
import hackstyle.org.hscommander.R;
import hackstyle.org.pojo.Ambiente;

public class BrandNewSensorActivity extends AppCompatActivity {

    Spinner spinAmbiente, spinSaida1, spinSaida2;
    Button btnAddAmbiente, btnContinuar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand_new_sensor);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        //getSupportActionBar().setLogo(R.drawable.);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("  " + "Novo Sensor");

        if (savedInstanceState == null) {
            Dialog myDialog = new Dialog(BrandNewSensorActivity.this);
            myDialog.setContentView(R.layout.popup_novo_sensor);
            myDialog.show();
        }

        spinAmbiente = (Spinner)findViewById(R.id.spinAmbiente);
        spinSaida1 = (Spinner)findViewById(R.id.spinSaida1);
        spinSaida2 = (Spinner)findViewById(R.id.spinSaida2);
        btnAddAmbiente = (Button)findViewById(R.id.btnAddAmbiente);
        btnContinuar = (Button)findViewById(R.id.btnContinuar);

        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinSaida1.setAdapter(arrayAdapter);
        spinSaida2.setAdapter(arrayAdapter);

        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getChildAt(0) != null)
                    ((TextView) adapterView.getChildAt(0)).setTextColor(Color.WHITE);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        };


        btnAddAmbiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog dialog = new Dialog(BrandNewSensorActivity.this);
                dialog.setContentView(R.layout.popup_novo_ambiente);
                dialog.setCancelable(false);
                dialog.show();

                Button btnInsertAmbiente = (Button)dialog.findViewById(R.id.btnPopupOK);
                btnInsertAmbiente.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        TextView txtNomeAmbiente = (TextView)dialog.findViewById(R.id.edtNomeAmbiente);
                        String strAmbiente = txtNomeAmbiente.getText().toString();

                        if (strAmbiente.trim().isEmpty()) {
                            dialog.dismiss();
                            return;
                        }

                        AmbienteDAO ambienteDAO = new AmbienteDAO(BrandNewSensorActivity.this);
                        int id = ambienteDAO.insertAmbiente(strAmbiente);

                        loadSpinAmbiente(id);
                        dialog.dismiss();
                    }
                });
            }
        });

        spinAmbiente.setOnItemSelectedListener(onItemSelectedListener);
        spinSaida1.setOnItemSelectedListener(onItemSelectedListener);
        spinSaida2.setOnItemSelectedListener(onItemSelectedListener);

        ImageView imageView = (ImageView)findViewById(R.id.imageViewNovoSensor);
        imageView.setImageResource(R.drawable.chip);
        imageView.requestFocus();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                verifyStoragePermissions(BrandNewSensorActivity.this);

                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        loadSpinAmbiente();

    }



    protected void loadSpinAmbiente() {
        loadSpinAmbiente(-1);
    }

    protected  void loadSpinAmbiente(int selected_id) {

        AmbienteDAO ambienteDAO = new AmbienteDAO(this);
        List<Ambiente> listAmbiente = ambienteDAO.getListAmbiente();

        if (listAmbiente != null) {
            Ambiente[] arrAmbiente = new Ambiente[listAmbiente.size()];

            for (int i = 0; i < listAmbiente.size(); i++)
                arrAmbiente[i] = listAmbiente.get(i);

            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, arrAmbiente);
            spinAmbiente.setAdapter(adapter);
        }

        if (selected_id < 0)
            return;

        for (int i=0; i<spinAmbiente.getCount(); i++) {

            Ambiente amb = (Ambiente) spinAmbiente.getItemAtPosition(i);
            if (amb.getId() == selected_id) {
                spinAmbiente.setSelection(i);
                break;
            }
        }

    }

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


            ImageView img = (ImageView)findViewById(R.id.imageViewNovoSensor);

            File imgFile= new File(picturePath);
            img.setImageURI(Uri.fromFile(imgFile));

            //aqui devemos setar a imagePath do sensor
        }
    }

    public static void verifyStoragePermissions(Activity activity) {

        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }



    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static int RESULT_LOAD_IMAGE = 1;

}
