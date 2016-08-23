package hackstyle.org.adapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import hackstyle.org.activity.CheckNovoSensorActivity;
import hackstyle.org.dao.SensorDAO;
import hackstyle.org.hscommander.R;
import hackstyle.org.main.HSSensor;
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

    public void refresh(List<Sensor> listSensor) {

        this.listSensor = listSensor;
        notifyDataSetChanged();
    }


    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView;

        if (convertView == null) {
            rowView = inflater.inflate(R.layout.sensores_list_ng, parent, false);
        } else {
            rowView = convertView;
        }

        TextView tvSensor = (TextView)rowView.findViewById(R.id.tvSensor);
        //TextView tvIP = (TextView)rowView.findViewById(R.id.tvIP);
        TextView tvAmbiente = (TextView)rowView.findViewById(R.id.tvAmbiente);
        TextView tvCarga1 = (TextView)rowView.findViewById(R.id.tvCarga1);
        TextView tvCarga2 = (TextView)rowView.findViewById(R.id.tvCarga2);
        ImageView imgC1 = (ImageView)rowView.findViewById(R.id.imageViewC1);
        ImageView imgC2 = (ImageView)rowView.findViewById(R.id.imageViewC2);
        ProgressBar progressBar = (ProgressBar)rowView.findViewById(R.id.pgbar);


        /*tvSensor.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Simply Rounded.ttf"));
        tvIP.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Simply Rounded.ttf"));
        tvAmbiente.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Simply Rounded.ttf"));
        tvCarga1.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Simply Rounded.ttf"));
        tvCarga2.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Simply Rounded.ttf"));*/

        if (listSensor == null)
            return null;

        if (position > listSensor.size())
            return null;

        Sensor s = listSensor.get(position);

        tvSensor.setText(s.getNome());
        tvSensor.setTag(s.getId());

        //tvIP.setText(s.getIp());

        tvAmbiente.setText(s.getAmbiente().getNome());

        Carga carga = s.getCarga(0);
        if (carga != null) {
            tvCarga1.setText(carga.getNome());
            imgC1.setImageResource(R.drawable.on);
        } else {
            tvCarga1.setText("");
            imgC1.setImageResource(0);
        }

        carga = s.getCarga(1);
        if (carga != null) {
            tvCarga2.setText(carga.getNome());
            imgC2.setImageResource(R.drawable.on);
        } else {
            tvCarga2.setText("");
            imgC2.setImageResource(0);
        }


        ImageView img = (ImageView)rowView.findViewById(R.id.imageView);

        if (!s.getImagePath().isEmpty()) {
            File imgFile= new File(s.getImagePath());
            img.setImageURI(Uri.fromFile(imgFile));
        } else {
            img.setImageResource(R.drawable.selecione_imagem);
        }


        //TextView txtNetState = (TextView)rowView.findViewById(R.id.txtNetState);
        if (!s.isActive()) {

            img.setImageResource(R.drawable.disconnected);
            tvSensor.setTextColor(Color.RED);
            tvAmbiente.setTextColor(Color.RED);
            //tvIP.setTextColor(Color.RED);
            tvCarga1.setTextColor(Color.RED);
            tvCarga2.setTextColor(Color.RED);
            //txtNetState.setTextColor(Color.RED);

            progressBar.setVisibility(View.VISIBLE);
            if (HSSensor.getInstance().isScanning())
                progressBar.setVisibility(View.VISIBLE);
            else
                progressBar.setVisibility(View.INVISIBLE);


        } else {

            //txtNetState.setTextColor(Color.BLACK);
            //txtNetState.setText("Online");
            if (!s.getImagePath().isEmpty()) {
                File imgFile= new File(s.getImagePath());
                img.setImageURI(Uri.fromFile(imgFile));
            } else {
                img.setImageResource(R.drawable.selecione_imagem);
            }

            tvSensor.setTextColor(Color.BLACK);
            tvAmbiente.setTextColor(Color.BLACK);
            //tvIP.setTextColor(Color.parseColor("#FFA0A0A0"));
            tvCarga1.setTextColor(Color.BLACK);
            tvCarga2.setTextColor(Color.BLACK);

            progressBar.setVisibility(View.INVISIBLE);
            //txtNetState.setText("");
        }


        /*LinearLayout layout = (LinearLayout)rowView.findViewById(R.id.layoutCargas);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.popup_sensor_cargas);
                //dialog.setCancelable(false);

                ListView listView = (ListView)dialog.findViewById(R.id.listView);
                TextView txtHeader = (TextView)dialog.findViewById(R.id.txtHeader);


                SensorDAO sensorDAO = new SensorDAO(context);
                Sensor sensor = sensorDAO.getSensor(listSensor.get(position).getId());

                txtHeader.setText(sensor.getNome());
                //TextView txtSensorName = (TextView)dialog.findViewById(R.id.txtSensorName);
                //txtSensorName.setText(sensor.getNome());

                List<Carga> listCarga = new ArrayList<Carga>();

                for (int i = 0; i < 2; i++) {

                    Carga c = sensor.getCarga(i);
                    if (c != null)
                        listCarga.add(c);
                }

                SensorCargasAdapter sensorCargasAdapter = new SensorCargasAdapter(context, listCarga);
                listView.setAdapter(sensorCargasAdapter);




                final RelativeLayout rl = (RelativeLayout)dialog.findViewById(R.id.layoutFastOptions);
                rl.setVisibility(View.INVISIBLE);
                rl.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));

                final ImageView imageViewMenu = (ImageView)dialog.findViewById(R.id.imageViewMenu);
                imageViewMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (rl.getVisibility() == View.INVISIBLE) {
                            rl.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            rl.setVisibility(View.VISIBLE);
                        } else {
                            rl.setVisibility(View.INVISIBLE);
                            rl.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));
                        }
                    }
                });

                ImageView imageViewClose = (ImageView)dialog.findViewById(R.id.imageViewClose);
                imageViewClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                CheckBox checkBox = (CheckBox)dialog.findViewById(R.id.checkBoxFecharAutomaticamente);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {

                    }
                });

                dialog.show();

            }
        });
        */

        return rowView;
    }

}
