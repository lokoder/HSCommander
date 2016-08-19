package hackstyle.org.adapter;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.os.AsyncTaskCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.io.File;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.List;

import hackstyle.org.hscommander.R;
import hackstyle.org.pojo.Carga;

public class SensorCargasAdapter extends ArrayAdapter<Carga> {

    Context context;
    List<Carga> listCarga;

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
                String sensorIP = carga.getSensor().getIp();

                String message = "cmdexec:" + carga.getPino()+":" + comando;
                SendCommand sendCommand= new SendCommand();
                sendCommand.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, sensorIP, message);
                //AsyncTaskCompat.executeParallel(sendCommand, sensorIP, message);
                //sendCommand.execute(sensorIP, message);

                Log.d(getClass().getName(), "enviado " + message + " para " + sensorIP);
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

                sock.connect(addr, 3000);

                PrintWriter pout = new PrintWriter(sock.getOutputStream());
                pout.print(message+"\n");
                pout.flush();

                byte[] b = new byte[256];

                sock.setSoTimeout(6000);
                int bytes = sock.getInputStream().read(b);
                sock.close();

                String result = new String(b, 0, bytes - 1);

                return true;

            } catch (Exception e) {
                //timeout do socket
                if (e.getMessage() != null)
                    Log.e("CONN", e.getMessage());
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            Log.e(getClass().getName(), "Entrou em PostExecute...");

            ImageView imageView = (ImageView)((Activity)context).findViewById(R.id.imageView2);
/*            TextView txtResponse = (TextView)((Activity)context).findViewById(R.id.txtResponse);

            if (result) {

                txtResponse.setTextColor(Color.WHITE);
                txtResponse.setText("Comando executado com sucesso!");
                imageView.setImageResource(R.drawable.success);

            } else {

                txtResponse.setTextColor(Color.RED);
                txtResponse.setText("Houve um erro na execução do comando!");
                imageView.setImageResource(R.drawable.error);
            }


            final Animation alphaIn = new AlphaAnimation(0.0f,1.0f);
            alphaIn.setDuration(1500);
            alphaIn.setStartTime(0);

            final LinearLayout ll = (LinearLayout)((Activity)context).findViewById(R.id.llResponse);
            ll.startAnimation(alphaIn);

            alphaIn.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {}
                @Override
                public void onAnimationRepeat(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {

                    final Animation alphaOut = new AlphaAnimation(1.0f,0.0f);

                    alphaOut.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {}

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            TextView txtResponse = (TextView)((Activity)context).findViewById(R.id.txtResponse);
                            txtResponse.setText("");
                            ll.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                    });

                    alphaOut.setDuration(1500);
                    ll.startAnimation(alphaOut);
                }

            });
*/
        }
    }

}
