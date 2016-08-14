package hackstyle.org.wizard;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import hackstyle.org.hscommander.R;


public class Tela4 extends Fragment {

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

        EditText edtAparelho1 = (EditText)v.findViewById(R.id.edtAparelho1);
        //edtAparelho1.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Simply Rounded.ttf"));

        EditText edtAparelho2 = (EditText)v.findViewById(R.id.edtAparelho2);
        //edtAparelho2.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Simply Rounded.ttf"));

        ImageView imageViewIntro = (ImageView)v.findViewById(R.id.imageViewIntro);
        imageViewIntro.setImageResource(R.drawable.plug4xxl);


        return v;
    }

}
