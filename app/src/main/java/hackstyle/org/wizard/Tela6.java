package hackstyle.org.wizard;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import hackstyle.org.hscommander.R;

public class Tela6 extends Fragment {

    TextView txtNomeSensor, txtSaida1Sensor, txtSaida2Sensor;

    public Tela6() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.wizard_step6, container, false);

        ImageView imageView = (ImageView)v.findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.transferxxl);

        TextView txtHeader = (TextView) v.findViewById(R.id.txtHeader);
        txtHeader.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/HighlandGothicFLF-Bold.ttf"));

        TextView txt = (TextView)v.findViewById(R.id.txt1);
        txt.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Simply Rounded.ttf"));

        txt =  (TextView)v.findViewById(R.id.txt2);
        txt.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Simply Rounded.ttf"));

        txt =  (TextView)v.findViewById(R.id.txt3);
        txt.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Simply Rounded.ttf"));

        txt =  (TextView)v.findViewById(R.id.txt4);
        txt.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Simply Rounded.ttf"));

        txt =  (TextView)v.findViewById(R.id.txt5);
        txt.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Simply Rounded.ttf"));

        txt =  (TextView)v.findViewById(R.id.txt6);
        txt.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Simply Rounded.ttf"));

        return v;
    }

}
