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


public class Tela2 extends Fragment {

    EditText edtNomeSensor;

    public Tela2() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.wizard_step2, container, false);

        ImageView imageView = (ImageView)v.findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.transferxxl);

        TextView txtHeader = (TextView) v.findViewById(R.id.txtHeader);
        txtHeader.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/HighlandGothicFLF-Bold.ttf"));

        TextView txtIntro = (TextView) v.findViewById(R.id.txtIntroMessage);
        txtIntro.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Simply Rounded.ttf"));;

        TextView txtNomeSensor = (TextView) v.findViewById(R.id.txtIntroMessage);
        txtNomeSensor.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Simply Rounded.ttf"));;

        edtNomeSensor = (EditText)v.findViewById(R.id.edtNomeSensor);
        edtNomeSensor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String nomeSensor = charSequence.toString();
                WizardSensor.getInstance().getSensor().setNome(nomeSensor);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        //edtNomeSensor.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Simply Rounded.ttf"));

        ImageView imageViewIntro = (ImageView)v.findViewById(R.id.imageViewIntro);
        imageViewIntro.setImageResource(R.drawable.chip4xxl);

        return v;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        super.setUserVisibleHint(isVisibleToUser);
    }

}
