package hackstyle.org.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

import java.lang.reflect.Field;
import java.util.List;

import hackstyle.org.adapter.AmbienteAdapter;
import hackstyle.org.dao.AmbienteDAO;
import hackstyle.org.hscommander.R;
import hackstyle.org.pojo.Ambiente;

public class GerenciaAmbienteActivity extends AppCompatActivity {

    ImageView imgInsert;
    ListView listView;
    AmbienteAdapter ambienteAdapter;
    List<Ambiente> listAmbiente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerencia_ambiente);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Ambientes");

        setToolbarConfig(toolbar);


        listView = (ListView) findViewById(R.id.listViewAmbiente);
        connect_list();

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long l) {

                final int pos = position;

                new AlertDialog.Builder(GerenciaAmbienteActivity.this)
                        .setMessage("Remover o ambiente '" + listAmbiente.get(pos).getNome() + "' ?")
                        .setCancelable(false)
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                if (listAmbiente == null)
                                    return;

                                if (pos >= listAmbiente.size())
                                    return;

                                AmbienteDAO ambienteDAO = new AmbienteDAO(GerenciaAmbienteActivity.this);
                                ambienteDAO.deleteAmbiente(listAmbiente.get(pos).getId());

                                Toast.makeText(GerenciaAmbienteActivity.this, "Removendo o id " + listAmbiente.get(pos).getId(), Toast.LENGTH_LONG).show();

                                connect_list();
                            }
                        })
                        .setNegativeButton("Não", null)
                        .show();

                return false;
            }
        });


        imgInsert = (ImageView) findViewById(R.id.imgInsertAmbiente);
        imgInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(GerenciaAmbienteActivity.this);
                final View popup = getLayoutInflater().inflate(R.layout.popup_novo_ambiente, null);
                builder.setView(popup);

                final AlertDialog ad = builder.show();

                Button btnPopupOK = (Button)popup.findViewById(R.id.btnPopupOK);
                btnPopupOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        EditText txtAmbiente = (EditText)popup.findViewById(R.id.edtNomeAmbiente);
                        String ambiente = txtAmbiente.getText().toString();
                        if (ambiente.isEmpty())
                            return;

                        AmbienteDAO ambienteDAO = new AmbienteDAO(GerenciaAmbienteActivity.this);
                        ambienteDAO.insertAmbiente(ambiente);
                        connect_list();

                        ad.dismiss();
                    }
                });

            }
        });

    }



    private void setToolbarConfig(Toolbar toolbar) {

        try {

            Field f = toolbar.getClass().getDeclaredField("mTitleTextView");
            f.setAccessible(true);

            TextView mToolbarTitleTextView = (TextView) f.get(toolbar);
            mToolbarTitleTextView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/HighlandGothicFLF.ttf"));

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

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
        inflater.inflate(R.menu.menu, menu);
        return true;
    }


    private void connect_list() {

        AmbienteDAO ambienteDAO = new AmbienteDAO(this);
        listAmbiente = ambienteDAO.getListAmbiente();
        ambienteAdapter = new AmbienteAdapter(this, GerenciaAmbienteActivity.this, listAmbiente);

        if (listAmbiente != null)
            listView.setAdapter(ambienteAdapter);

    }


    @Override
    protected void onResume() {
        super.onResume();
        connect_list();
    }

}
