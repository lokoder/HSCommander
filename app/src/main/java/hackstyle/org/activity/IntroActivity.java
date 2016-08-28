package hackstyle.org.activity;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TypefaceSpan;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.webkit.HttpAuthHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import hackstyle.org.adapter.OnSwipeTouchListener;
import hackstyle.org.dao.SensorDAO;
import hackstyle.org.hscommander.R;
import hackstyle.org.service.SensorCollector;
import hackstyle.org.sqlite.DBAdapter;
import hackstyle.org.wizard.WizardActivity;

public class IntroActivity extends AppCompatActivity {

    ImageView imageViewCamera;
    DownloadImageTask downloadImageTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.house);
        toolbar.setTitle("  Hackstyle");

        setSupportActionBar(toolbar);
        setToolbarConfig(toolbar);

        ImageView imageView = (ImageView)findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.chip4xxl);

        imageView = (ImageView)findViewById(R.id.imageView1);
        imageView.setImageResource(R.drawable.camera);

        imageView = (ImageView)findViewById(R.id.imageView2);
        imageView.setImageResource(R.drawable.livingroomxxl);

        imageView = (ImageView)findViewById(R.id.imageView3);
        imageView.setImageResource(R.drawable.settingsxxl);

        imageView = (ImageView)findViewById(R.id.imageView4);
        imageView.setImageResource(R.drawable.calendarxxl);

        imageView = (ImageView)findViewById(R.id.imageView5);
        imageView.setImageResource(R.drawable.infoxxl);

        /* serviço de scan inicial */
        if (savedInstanceState == null) {
            Intent intent = new Intent(Intent.ACTION_SYNC, null, this, SensorCollector.class);
            startService(intent);
        }

        imageViewCamera = (ImageView)findViewById(R.id.imageViewCamera);
        downloadImageTask = new DownloadImageTask(imageViewCamera);
        //downloadImageTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "http://192.168.1.43:7777/snapshot.cgi?user=admin&pwd=admin");


        imageViewCamera.setOnTouchListener(new OnSwipeTouchListener(this) {

            @Override
            public void onSwipeDown() {
                SendCmd sendCmd = new SendCmd();
                sendCmd.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                        "http://192.168.1.43:7777/media/?action=cmd&code=2&value=1",
                        "http://192.168.1.43:7777/media/?action=cmd&code=3&value=1"
                );
            }

            @Override
            public void onSwipeLeft() {
                SendCmd sendCmd = new SendCmd();

                sendCmd.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                        "http://192.168.1.43:7777/media/?action=cmd&code=2&value=3",
                        "http://192.168.1.43:7777/media/?action=cmd&code=3&value=3"
                );
            }

            @Override
            public void onSwipeUp() {
                SendCmd sendCmd = new SendCmd();

                sendCmd.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                        "http://192.168.1.43:7777/media/?action=cmd&code=2&value=2",
                        "http://192.168.1.43:7777/media/?action=cmd&code=3&value=2"
                );
            }

            @Override
            public void onSwipeRight() {
                SendCmd sendCmd = new SendCmd();
                sendCmd.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                        "http://192.168.1.43:7777/media/?action=cmd&code=2&value=4",
                        "http://192.168.1.43:7777/media/?action=cmd&code=3&value=4"
                );
            }


        });

        //if (savedInstanceState == null)
        //    HSDialog.newInstance(this, "tttt", "zzzz", false).show(getFragmentManager(), "");

       // new SensorDAO(this).deleteSensor(25);
    }


    private void setToolbarConfig(Toolbar toolbar) {

        try {

            Field f = toolbar.getClass().getDeclaredField("mTitleTextView");
            f.setAccessible(true);

            TextView mToolbarTitleTextView = (TextView)f.get(toolbar);
            mToolbarTitleTextView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/HighlandGothicFLF.ttf"));

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);
        Intent i = null;

        switch (item.getItemId()) {

            case R.id.start_novo_sensor:
                i = new Intent(this, WizardActivity.class);
                startActivity(i);
                break;

            case R.id.start_ambiente:
                i = new Intent(this, GerenciaAmbienteActivity.class);
                startActivity(i);
                break;

            case R.id.start_sensores:
                //if (HSSensor.getInstance().isScanning())
                //    i = new Intent(this, VarreduraSensoresActivity.class);
                //else
                    i = new Intent(this, SensoresActivity.class);

                startActivity(i);
                break;

            case R.id.start_varredura:
                //  i = new Intent(this, VarreduraSensoresActivity.class);
                //  startActivity(i);
                break;

            case R.id.start_wificred:
                i = new Intent(this, WiFiCredentialsActivity.class);
                startActivity(i);
                break;

            case R.id.start_zerabanco:
                //DBAdapter dbAdapter = new DBAdapter(this);
                //dbAdapter.zeraDB();
                Intent in = new Intent(this, ComandosGeraisActivity.class);
                startActivity(in);

                break;
        }

        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }


    private class DownloadImageTask extends AsyncTask<String, Bitmap, Bitmap> {

        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {

            String urldisplay = urls[0];
            Bitmap mIcon11 = null;

            while (!isCancelled()) {

                try {

                    Thread.sleep(200);

                    InputStream in = new java.net.URL(urldisplay).openStream();
                    mIcon11 = BitmapFactory.decodeStream(in);
                    publishProgress(mIcon11);
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Bitmap... values) {
            super.onProgressUpdate(values);

            Bitmap bm = (Bitmap)values[0];
            imageViewCamera.setImageBitmap(bm);
        }
    }



    public void onDestroy() {
        super.onDestroy();
        downloadImageTask.cancel(true);
    }

    private class SendCmd extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... urls) {


            for (int i=0; i<2; i++) {

                try {

                    if (i == 1) {
                        try {
                            Thread.sleep(400);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    //URL url = new URL("http://192.168.1.43:7777/media/?action=cmd&code=2&value=3");
                    URL url = new URL(urls[i]);

                    URLConnection urlConnection = url.openConnection();
                    HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
                    httpURLConnection.setRequestMethod("GET");

                    byte[] encoded = Base64.encode("admin:admin".getBytes(), Base64.NO_WRAP);
                    String enc = new String(encoded);

                    httpURLConnection.setRequestProperty("Authorization", "Basic " + enc);
                    httpURLConnection.setRequestProperty("Upgrade-Insecure-Requests", "1");

                    httpURLConnection.connect();
                    int code = httpURLConnection.getResponseCode();

                    String content = httpURLConnection.getResponseMessage();
                    Log.d("XXX", content);

                } catch (MalformedURLException e) {
                    Log.d("XXX", e.getMessage());
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.d("XXX", e.getMessage());
                    e.printStackTrace();
                }

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }



    public static class HSDialog extends DialogFragment {

        private static Context context;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            super.onCreateDialog(savedInstanceState);

            Bundle args = getArguments();

            String title = args.getString("title");
            String message = args.getString("message");

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(message)
                    .setTitle(title)
                    .setPositiveButton("sim", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            Toast.makeText(context, "AAAAAAAAAAAAAAAAhhhhh!!!!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("nao", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            dialog.dismiss();
                        }
                    });

            return builder.create();
        }


        public static HSDialog newInstance(Context context, String title, String message, boolean cancelable) {

            HSDialog frag = new HSDialog();
            Bundle args = new Bundle();
            
            HSDialog.context = context;

            args.putString("title", title);
            args.putString("message", message);

            frag.setCancelable(cancelable);
            frag.setArguments(args);
            return frag;
        }
    }

}
