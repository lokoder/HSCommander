package hackstyle.org.adapter;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.List;

import hackstyle.org.hscommander.R;
import hackstyle.org.main.SensorSingleton;
import hackstyle.org.pojo.Carga;
import hackstyle.org.pojo.Sensor;

/*
public class SensorCargasAdapter extends ArrayAdapter<Carga> {

    Context context;
    List<Carga> listCarga;
    Handler myHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case 0:
                    Toast.makeText(context, "Houve um erro ao executar o comando!", Toast.LENGTH_SHORT).show();
                    break;

                case 1:
                    Toast.makeText(context, "Comando executado com sucesso!", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
        }
    };

    public SensorCargasAdapter(Context context, List<Carga> listCarga) {

        super(context, 0, listCarga);
        this.context = context;
        this.listCarga = listCarga;
    }


    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView;

        if (convertView == null) {
            rowView = inflater.inflate(R.layout.sensor_cargas_list, parent, false);
        } else {
            rowView = convertView;
        }

        ImageView imageView = (ImageView) rowView.findViewById(R.id.imageViewCarga);
        TextView tvNome = (TextView) rowView.findViewById(R.id.tvNome);
        Switch btSwitch = (Switch) rowView.findViewById(R.id.switchEstado);

        if (position > listCarga.size())
            return null;

        final Carga carga = listCarga.get(position);
        if (!carga.getNome().isEmpty()) {
            tvNome.setText(carga.getNome());
        }

        if (!carga.getImagePath().isEmpty()) {
            File imgFile = new File(carga.getImagePath());
            imageView.setImageURI(Uri.fromFile(imgFile));
        } else {
            imageView.setImageResource(R.drawable.selecione_imagem);
        }

        btSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked)
                    buttonView.setText("Ligado");
                else
                    buttonView.setText("Desligado");

                String comando = isChecked? "1::::::" : "0::::::";
                SendCommand sendCommand= new SendCommand();
                String sensorIP = carga.getSensor().getIp();
                String message = "cmdexec:" + carga.getPino()+":" + comando;
                sendCommand.execute(sensorIP, message);
            }
        });

        return rowView;
    }


    private class SendCommand extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... args) {

            String ip = args[0];
            String message = args[1];

            Socket sock = new Socket();
            SocketAddress addr = new InetSocketAddress(ip, 8000);

            try {

                sock.connect(addr, 300);

                PrintWriter pout = new PrintWriter(sock.getOutputStream());
                pout.print(message+"\n");
                pout.flush();

                byte[] b = new byte[256];

                sock.setSoTimeout(2000);
                int bytes = sock.getInputStream().read(b);
                sock.close();

                String result = new String(b, 0, bytes - 1);

                return true;

            } catch (Exception e) {
                //timeout do socket
                Log.e("CONN", e.getMessage());
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if (result) {
                myHandler.sendEmptyMessage(1);
            } else {
                myHandler.sendEmptyMessage(0);
            }
        }


    }
}
*/