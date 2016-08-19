package hackstyle.org.wizard;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import hackstyle.org.dao.AmbienteDAO;
import hackstyle.org.hscommander.R;
import hackstyle.org.pojo.Ambiente;

public class Tela3 extends Fragment {

    Spinner spinAmbiente;

    public Tela3() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.wizard_step3, container, false);

        ImageView imageView = (ImageView)v.findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.transferxxl);

        TextView txtHeader = (TextView) v.findViewById(R.id.txtHeader);
        txtHeader.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/HighlandGothicFLF-Bold.ttf"));

        TextView txtIntro = (TextView) v.findViewById(R.id.txtIntroMessage);
        txtIntro.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Simply Rounded.ttf"));;

        ImageView imageViewIntro = (ImageView)v.findViewById(R.id.imageViewIntro);
        imageViewIntro.setImageResource(R.drawable.livingroomxxl);


        spinAmbiente = (Spinner)v.findViewById(R.id.spinAmbienteSensor);
        loadSpinAmbiente(-1);
        spinAmbiente.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                AmbienteDAO ambienteDAO = new AmbienteDAO(getContext());
                Ambiente amb = (Ambiente)adapterView.getItemAtPosition(position);
                WizardSensor.getInstance().getSensor().setAmbiente(amb);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        return v;
    }



    protected  void loadSpinAmbiente(int selected_id) {

        AmbienteDAO ambienteDAO = new AmbienteDAO(getContext());
        List<Ambiente> listAmbiente = ambienteDAO.getListAmbiente();

        if (listAmbiente != null) {
            Ambiente[] arrAmbiente = new Ambiente[listAmbiente.size()];

            for (int i = 0; i < listAmbiente.size(); i++)
                arrAmbiente[i] = listAmbiente.get(i);

            ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, arrAmbiente);
            spinAmbiente.setAdapter(adapter);
        }

        if (selected_id < 0)
            return;

        for (int i=0; i<spinAmbiente.getCount(); i++) {

            Ambiente amb = (Ambiente) spinAmbiente.getItemAtPosition(i);
            if (amb.getId() == selected_id) {
                spinAmbiente.setSelection(i);
                break;
            }
        }

    }

}
