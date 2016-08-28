package hackstyle.org.wizard;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import hackstyle.org.dao.WiFiCredentialsDAO;
import hackstyle.org.hscommander.R;
import hackstyle.org.pojo.WiFiCredentials;

public class Tela5 extends Fragment {

    EditText edtSSID, edtSenha;

    public Tela5() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.wizard_step5, container, false);

        ImageView imageView = (ImageView)v.findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.transferxxl);

        TextView txtHeader = (TextView) v.findViewById(R.id.txtHeader);
        txtHeader.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/HighlandGothicFLF-Bold.ttf"));

        TextView txtIntro = (TextView) v.findViewById(R.id.txtIntroMessage);
        txtIntro.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Simply Rounded.ttf"));

        TextView txtSSID = (TextView) v.findViewById(R.id.txtSSID);
        txtSSID.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Simply Rounded.ttf"));

        TextView txtSenha = (TextView) v.findViewById(R.id.txtSenha);
        txtSenha.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Simply Rounded.ttf"));

        edtSSID = (EditText) v.findViewById(R.id.edtSSID);
        edtSenha = (EditText) v.findViewById(R.id.edtSenha);

        ImageView imageViewIntro = (ImageView)v.findViewById(R.id.imageViewIntro);
        imageViewIntro.setImageResource(R.drawable.signalxxl);

        edtSSID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String ssid = charSequence.toString();
                WizardSensor.getInstance().getSensor().setSSID(ssid);
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });

        edtSenha.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String senha = charSequence.toString();
                WizardSensor.getInstance().getSensor().setSenha(senha);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });


        WiFiCredentialsDAO wifiDao = new WiFiCredentialsDAO(getContext());
        WiFiCredentials wifi = wifiDao.getWiFiCredentials();

        String ssid = "";
        if (!wifi.getSSID().isEmpty())
            ssid = wifi.getSSID();

        String senha = "";
        if (!wifi.getSenha().isEmpty())
            senha = wifi.getSenha();

        edtSSID.setText(ssid);
        edtSenha.setText(senha);

        return v;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        super.setUserVisibleHint(isVisibleToUser);

        if (!isVisibleToUser) {
            Log.d("MyFragment", "Tela 5 Not visible anymore.  Stopping audio.");
        }
    }
}
