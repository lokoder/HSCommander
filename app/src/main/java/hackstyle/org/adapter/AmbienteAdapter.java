package hackstyle.org.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;

import hackstyle.org.activity.DetalheAmbienteActivity;
import hackstyle.org.hscommander.R;
import hackstyle.org.pojo.Ambiente;


public class AmbienteAdapter extends ArrayAdapter<Ambiente> {

    private Context context;
    private Activity activity;
    private List<Ambiente> listAmbiente;

    public AmbienteAdapter(Context context, Activity activity ,List<Ambiente> listAmbiente) {

        super(context, 0, listAmbiente);
        this.context = context;
        this.activity = activity;
        this.listAmbiente = listAmbiente;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView;
        final int pos = position;

        if (convertView == null) {
            rowView = inflater.inflate(R.layout.ambiente_list, parent, false);
        } else {
            rowView = convertView;
        }

        TextView tv = (TextView)rowView.findViewById(R.id.tvTitleListAmbiente);

        if (listAmbiente == null)
            return null;

        if (position >= listAmbiente.size())
            return null;

        Ambiente a = listAmbiente.get(position);
        tv.setText(a.getNome());
        tv.setTag(a.getId());

        //Typeface customFont = Typeface.createFromAsset(context.getAssets(), "fonts/monaco.ttf");
        //tv.setTypeface(customFont);

        ImageView img = (ImageView)rowView.findViewById(R.id.imageView);

        if (!a.getImagePath().isEmpty()) {
            File imgFile = new File(a.getImagePath());
            Bitmap myBitmap = BitmapFactory.decodeFile(a.getImagePath());
            img.setImageURI(Uri.fromFile(imgFile));
        } else {
            img.setImageResource(R.drawable.selecione_imagem);
        }
        //img.setImageResource(R.drawable.chip);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Ambiente ambiente = (Ambiente)listAmbiente.get(pos);

                Intent i = new Intent(context, DetalheAmbienteActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                Bundle bundle = new Bundle();
                bundle.putInt("id", ambiente.getId());
                bundle.putString("nome", ambiente.getNome());
                bundle.putInt("icone", ambiente.getIcone());
                bundle.putString("imagePath", ambiente.getImagePath());

                i.putExtras(bundle);

                context.startActivity(i);
            }
        });

        return rowView;
    }

}