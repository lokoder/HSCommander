package hackstyle.org.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import hackstyle.org.hscommander.R;
import hackstyle.org.pojo.Carga;
import hackstyle.org.pojo.Sensor;


public class SensoresAdapter extends ArrayAdapter<Sensor> {

    private Context context;
    private List<Sensor> listSensor;

    public SensoresAdapter(Context context, List<Sensor> listSensor) {

        super(context, 0, listSensor);

        this.context = context;
        this.listSensor = listSensor;
    }


    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView;

        if (convertView == null) {
            rowView = inflater.inflate(R.layout.sensores_list, parent, false);
        } else {
            rowView = convertView;
        }

        TextView tvSensor = (TextView)rowView.findViewById(R.id.tvSensor);
        TextView tvIP = (TextView)rowView.findViewById(R.id.tvIP);
        TextView tvAmbiente = (TextView)rowView.findViewById(R.id.tvAmbiente);
        TextView tvCarga1 = (TextView)rowView.findViewById(R.id.tvCarga1);
        TextView tvCarga2 = (TextView)rowView.findViewById(R.id.tvCarga2);

        if (listSensor == null)
            return null;

        if (position > listSensor.size())
            return null;

        Sensor s = listSensor.get(position);

        tvSensor.setText(s.getNome());
        tvSensor.setTag(s.getId());

        tvIP.setText(s.getIp());

        tvAmbiente.setText(s.getAmbiente().getNome());

        Carga carga = s.getCarga(0);
        if (carga != null)
            tvCarga1.setText("Carga 1:  " + carga.getNome());
        else
            tvCarga1.setText("");

        carga = s.getCarga(1);
        if (carga != null)
            tvCarga2.setText("Carga 2:  " + carga.getNome());
        else
            tvCarga2.setText("");


        ImageView img = (ImageView)rowView.findViewById(R.id.imageView);

        if (!s.getImagePath().isEmpty()) {
            File imgFile= new File(s.getImagePath());
            img.setImageURI(Uri.fromFile(imgFile));
        } else {
            img.setImageResource(R.drawable.selecione_imagem);
        }

        return rowView;
    }

}
