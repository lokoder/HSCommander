package hackstyle.org.dao;

import android.content.ContentValues;
import android.content.Context;
import java.util.List;
import hackstyle.org.pojo.Carga;
import hackstyle.org.sqlite.DBAdapter;


public class CargaDAO {

    private DBAdapter dbAdapter;


    public CargaDAO(Context context) {

        dbAdapter = new DBAdapter(context);
    }

    public int insertCarga(Carga carga, int sensorId) {

        ContentValues c = new ContentValues();
        c.put("nome", carga.getNome());
        c.put("pino", carga.getPino());
        c.put("icone", carga.getIcone());
        c.put("id_sensor", sensorId);
        c.put("image_path", carga.getImagePath());

        return dbAdapter.insert("Carga", c);
    }


    public int deleteCarga(Carga carga) {

        return dbAdapter.delete("Carga", carga.getId());
    }

    public Carga getCarga(int id) {

        return dbAdapter.getCarga(id);
    }

    public List<Carga> getListCarga(int sensorId) {

        return dbAdapter.getListCarga(sensorId);
    }


}
