package hackstyle.org.wizard;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import hackstyle.org.hscommander.R;
import hackstyle.org.pojo.Sensor;

public class Tela6 extends Fragment {

    TextView txtNomeSensor, txtSaida1Sensor, txtSaida2Sensor;
    TextView txtSSID, txtSenha, txtAmbiente;
    Sensor sensor;

    public Tela6() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.wizard_step6, container, false);

        ImageView imageView = (ImageView) v.findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.transferxxl);


        txtNomeSensor = (TextView) v.findViewById(R.id.txtNomeSensor);
        txtSaida1Sensor = (TextView) v.findViewById(R.id.txtSaida1Sensor);
        txtSaida2Sensor = (TextView) v.findViewById(R.id.txtSaida2Sensor);
        txtSSID = (TextView) v.findViewById(R.id.txtSSIDSensor);
        txtSenha = (TextView) v.findViewById(R.id.txtSenhaSensor);
        txtAmbiente =(TextView) v.findViewById(R.id.txtAmbienteSensor);

        TextView txtIntro = (TextView) v.findViewById(R.id.txtIntroMessage);
        txtIntro.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Simply Rounded.ttf"));


        configTextViewFonts(v);

        sensor = WizardSensor.getInstance().getSensor();
        if (sensor == null) {
            Log.d("Tela 6", "SENSOR NULL");

        } else {

            updateInfo(sensor);
        }


        return v;
    }


    private void updateInfo(Sensor sensor) {

        txtNomeSensor.setText(sensor.getNome());
        if (sensor.getCarga(0) != null)
            txtSaida1Sensor.setText(sensor.getCarga(0).getNome());
        else
            txtSaida1Sensor.setText("n/a");

        if (sensor.getCarga(1) != null)
            txtSaida2Sensor.setText(sensor.getCarga(1).getNome());
        else
            txtSaida2Sensor.setText("n/a");

        txtSSID.setText(sensor.getSSID());
        txtSenha.setText(sensor.getSenha());
        if (sensor.getAmbiente() != null)
            txtAmbiente.setText(sensor.getAmbiente().getNome());
    }


    private void configTextViewFonts(View v) {
        TextView txtHeader = (TextView) v.findViewById(R.id.txtHeader);
        txtHeader.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/HighlandGothicFLF-Bold.ttf"));

        TextView txt = (TextView) v.findViewById(R.id.txt1);
        txt.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Simply Rounded.ttf"));

        txt = (TextView) v.findViewById(R.id.txt2);
        txt.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Simply Rounded.ttf"));

        txt = (TextView) v.findViewById(R.id.txt3);
        txt.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Simply Rounded.ttf"));

        txt = (TextView) v.findViewById(R.id.txt4);
        txt.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Simply Rounded.ttf"));

        txt = (TextView) v.findViewById(R.id.txt5);
        txt.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Simply Rounded.ttf"));

        txt = (TextView) v.findViewById(R.id.txt6);
        txt.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Simply Rounded.ttf"));
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        super.setUserVisibleHint(isVisibleToUser);

        Log.d("XXX", "Tela 6 - dentro de setuservisiblehint...");
        if (sensor != null)
            updateInfo(sensor);
    }

}
