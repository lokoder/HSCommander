package hackstyle.org.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import hackstyle.org.dao.WiFiCredentialsDAO;
import hackstyle.org.hscommander.R;
import hackstyle.org.pojo.WiFiCredentials;


public class WiFiCredentialsActivity extends AppCompatActivity {

    EditText edtSSID, edtSenha;
    Button btnGravar;
    WiFiCredentials wiFiCredentials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wi_fi_credentials);

        /*getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        getSupportActionBar().setLogo(R.drawable.appiconbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("  " + "HSCommander");*/

        edtSSID = (EditText)findViewById(R.id.edtSSID);
        edtSenha = (EditText)findViewById(R.id.edtSenha);
        btnGravar = (Button)findViewById(R.id.btnGravar);

        WiFiCredentialsDAO wiFiCredentialsDAO = new WiFiCredentialsDAO(WiFiCredentialsActivity.this);
        wiFiCredentials = wiFiCredentialsDAO.getWiFiCredentials();
        if (wiFiCredentials != null) {

            edtSSID.setText(wiFiCredentials.getSSID());
            edtSenha.setText(wiFiCredentials.getSenha());
            btnGravar.setText("Atualizar");

        } else {

            btnGravar.setText("Gravar");
        }

        btnGravar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String ssid = edtSSID.getText().toString();
                String senha = edtSenha.getText().toString();

                if (ssid.isEmpty() || senha.isEmpty()) {
                    Toast.makeText(WiFiCredentialsActivity.this, "Preencha os campos SSID e Senha!", Toast.LENGTH_SHORT).show();
                    return;
                }

                int ret=0;
                WiFiCredentialsDAO wiFiCredentialsDAO = new WiFiCredentialsDAO(WiFiCredentialsActivity.this);

                if (wiFiCredentials == null) {

                    wiFiCredentials = new WiFiCredentials();

                    wiFiCredentials.setSSID(ssid);
                    wiFiCredentials.setSenha(senha);

                    ret = wiFiCredentialsDAO.insertWiFiCredentials(wiFiCredentials);

                } else {

                    wiFiCredentials.setSSID(ssid);
                    wiFiCredentials.setSenha(senha);

                    ret = wiFiCredentialsDAO.updateWiFiCredentials(wiFiCredentials);
                }

                if (ret > 0) {
                    Toast.makeText(WiFiCredentialsActivity.this, "Credenciais gravadas com sucesso!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(WiFiCredentialsActivity.this, "Houve um erro ao gravar as credenciais!", Toast.LENGTH_SHORT).show();
                }

            }
        });


        CheckBox cbExibirSenha = (CheckBox)findViewById(R.id.cbExibirSenha);
        cbExibirSenha.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (isChecked) {

                    edtSenha.setInputType(InputType.TYPE_CLASS_TEXT);

                } else {

                    edtSenha.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

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
                //  i = new Intent(this, VarreduraSensoresActivity.class);
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
