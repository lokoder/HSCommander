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

import hackstyle.org.hscommander.R;
import hackstyle.org.pojo.Carga;
import hackstyle.org.pojo.Sensor;


public class Tela4 extends Fragment {

    EditText edtAparelho1, edtAparelho2;

    public Tela4() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.wizard_step4, container, false);

        ImageView imageView = (ImageView)v.findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.transferxxl);

        TextView txtHeader = (TextView) v.findViewById(R.id.txtHeader);
        txtHeader.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/HighlandGothicFLF-Bold.ttf"));

        TextView txtIntro = (TextView) v.findViewById(R.id.txtIntroMessage);
        txtIntro.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Simply Rounded.ttf"));

        TextView txtAparelho1 = (TextView) v.findViewById(R.id.txtAparelho1);
        txtAparelho1.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Simply Rounded.ttf"));

        TextView txtAparelho2 = (TextView) v.findViewById(R.id.txtAparelho2);
        txtAparelho2.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Simply Rounded.ttf"));

        edtAparelho1 = (EditText)v.findViewById(R.id.edtAparelho1);
        //edtAparelho1.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Simply Rounded.ttf"));

        edtAparelho2 = (EditText)v.findViewById(R.id.edtAparelho2);
        //edtAparelho2.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Simply Rounded.ttf"));

        edtAparelho1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String aparelho1 = charSequence.toString();
                Sensor sensor = WizardSensor.getInstance().getSensor();
                Carga carga = null;

                if (sensor.getCarga(0) == null) {

                    carga = new Carga();
                    carga.setNome(aparelho1);
                    carga.setPino(12);
                    sensor.putCarga(carga);

                } else {

                    sensor.getCarga(0).setNome(aparelho1);
                }
                WizardSensor.getInstance().setSensor(sensor);
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });


        edtAparelho2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String aparelho1 = charSequence.toString();
                Sensor sensor = WizardSensor.getInstance().getSensor();
                Carga carga = null;

                if (sensor.getCarga(1) == null) {

                    carga = new Carga();
                    carga.setNome(aparelho1);
                    carga.setPino(12);
                    sensor.putCarga(carga);

                } else {

                    sensor.getCarga(1).setNome(aparelho1);
                }
                WizardSensor.getInstance().setSensor(sensor);
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });


        ImageView imageViewIntro = (ImageView)v.findViewById(R.id.imageViewIntro);
        imageViewIntro.setImageResource(R.drawable.plug4xxl);

        return v;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        super.setUserVisibleHint(isVisibleToUser);
    }


}
