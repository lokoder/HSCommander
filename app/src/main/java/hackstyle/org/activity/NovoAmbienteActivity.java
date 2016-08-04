package hackstyle.org.activity;

import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.drawable.DrawableWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.util.List;

import hackstyle.org.adapter.AmbienteAdapter;
import hackstyle.org.dao.AmbienteDAO;
import hackstyle.org.hscommander.R;
import hackstyle.org.pojo.Ambiente;
import hackstyle.org.sqlite.DBAdapter;

public class NovoAmbienteActivity extends AppCompatActivity {

    Button btnInsert;
    ListView listView;
    AmbienteAdapter ambienteAdapter;
    List<Ambiente> listAmbiente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_ambiente);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        getSupportActionBar().setLogo(R.drawable.appiconbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("  " + "HSCommander");

        listView = (ListView) findViewById(R.id.listViewAmbiente);
        connect_list();

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long l) {

                final int pos = position;

                new AlertDialog.Builder(NovoAmbienteActivity.this)
                        .setMessage("Remover o ambiente '" + listAmbiente.get(pos).getNome() + "' ?")
                        .setCancelable(false)
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                if (listAmbiente == null)
                                    return;

                                if (pos >= listAmbiente.size())
                                    return;

                                AmbienteDAO ambienteDAO = new AmbienteDAO(NovoAmbienteActivity.this);
                                ambienteDAO.deleteAmbiente(listAmbiente.get(pos).getId());

                                Toast.makeText(NovoAmbienteActivity.this, "Removendo o id " + listAmbiente.get(pos).getId(), Toast.LENGTH_LONG).show();

                                connect_list();
                            }
                        })
                        .setNegativeButton("Não", null)
                        .show();

                return false;
            }
        });


        btnInsert = (Button) findViewById(R.id.btnInsertAmbiente);
        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LinearLayout ll = (LinearLayout) view.getParent();
                EditText edtAmbienteTitle = (EditText) findViewById(R.id.edtAmbienteTitle);
                String title = edtAmbienteTitle.getText().toString();
                if (title.isEmpty())
                    return;

                AmbienteDAO ambienteDAO = new AmbienteDAO(view.getContext());
                ambienteDAO.insertAmbiente(title);

                edtAmbienteTitle.setText("");

                Toast.makeText(view.getContext(), "Ambiente " + title + " inserido com sucesso!", Toast.LENGTH_SHORT).show();
                connect_list();
                btnInsert.requestFocus();
            }
        });

        TextView txtAmbienteCount = (TextView) findViewById(R.id.txtAmbienteCount);
        if (listAmbiente.size() > 1) {
            txtAmbienteCount.setText(listAmbiente.size() + " ambientes cadastrados:");
        } else if (listAmbiente.size() < 1) {
            txtAmbienteCount.setText("Nenhum ambiente cadastrado");
        } else {
            txtAmbienteCount.setText("1 ambiente cadastrado:");
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

        AmbienteDAO ambienteDAO = new AmbienteDAO(this);
        listAmbiente = ambienteDAO.getListAmbiente();
        ambienteAdapter = new AmbienteAdapter(this, NovoAmbienteActivity.this, listAmbiente);

        if (listAmbiente != null)
            //this.setListAdapter(ambienteAdapter);
            listView.setAdapter(ambienteAdapter);
        else
            listView.setAdapter(null);
    }


    @Override
    protected void onResume() {
        super.onResume();
        connect_list();
    }

}
