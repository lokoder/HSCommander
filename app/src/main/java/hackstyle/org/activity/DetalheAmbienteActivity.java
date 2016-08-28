package hackstyle.org.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import hackstyle.org.hscommander.R;
import hackstyle.org.pojo.Ambiente;
import hackstyle.org.sqlite.DBAdapter;

public class DetalheAmbienteActivity extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static int RESULT_LOAD_IMAGE = 1;

    int idAmbiente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_ambiente);

        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        //getSupportActionBar().setLogo(R.drawable.appiconbar);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setDisplayUseLogoEnabled(true);
        //getSupportActionBar().setTitle("  " + "HSCommander");

        TextView txtAmbiente = (TextView)findViewById(R.id.txtAmbiente);
        Bundle bundle = getIntent().getExtras();

        ImageView img = (ImageView) findViewById(R.id.imageViewAmbiente);
        TextView tvNome = (TextView) findViewById(R.id.txtAmbiente);

        tvNome.setText(bundle.getString("nome"));

        if (bundle.getInt("icone") != -1)
            img.setImageResource(bundle.getInt("icone"));

        idAmbiente = bundle.getInt("id");

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                verifyStoragePermissions(DetalheAmbienteActivity.this);

                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        String imagePath = bundle.getString("imagePath");

        if (!imagePath.isEmpty()) {
            File imgFile = new File(imagePath);
            Bitmap myBitmap = BitmapFactory.decodeFile(imagePath);
            img.setImageURI(Uri.fromFile(imgFile));
        } else {
            img.setImageResource(R.drawable.selecione_imagem);

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


            ImageView img = (ImageView)findViewById(R.id.imageViewAmbiente);

            File imgFile= new File(picturePath);
            Bitmap myBitmap = BitmapFactory.decodeFile(picturePath);

            //img.setImageBitmap(myBitmap);
            img.setImageURI(Uri.fromFile(imgFile));

            DBAdapter dbAdapter = new DBAdapter(this);

            Ambiente a = new Ambiente();
            a.setId(idAmbiente);
            a.setImagePath(picturePath);

            if (dbAdapter.updateAmbienteImagePath(a) != -1)
                Toast.makeText(this, picturePath, Toast.LENGTH_LONG).show();

        }
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
