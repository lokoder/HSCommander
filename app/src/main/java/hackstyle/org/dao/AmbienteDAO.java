package hackstyle.org.dao;


import android.content.ContentValues;
import android.content.Context;
import java.util.List;
import hackstyle.org.pojo.Ambiente;
import hackstyle.org.sqlite.DBAdapter;

public class AmbienteDAO {

    private DBAdapter dbAdapter;

    public AmbienteDAO(Context context) {

        dbAdapter = new DBAdapter(context);
    }


    public int insertAmbiente(Ambiente ambiente) {

        ContentValues c = new ContentValues();
        c.put("nome", ambiente.getNome());
        c.put("icone", ambiente.getIcone());
        c.put("image_path", ambiente.getImagePath());

        return dbAdapter.insert("Ambiente", c);
    }

    public int insertAmbiente(String nome) {

        ContentValues c = new ContentValues();
        c.put("nome", nome);
        c.put("icone", -1);

        return dbAdapter.insert("Ambiente", c);
    }


    public int deleteAmbiente(Ambiente ambiente) {

        return dbAdapter.delete("Ambiente", ambiente.getId());
    }

    public int deleteAmbiente(int id) {

        return dbAdapter.delete("Ambiente", id);
    }

    public Ambiente getAmbiente(int id) {

        return dbAdapter.getAmbiente(id);
    }

    public List<Ambiente> getListAmbiente() {

        return dbAdapter.getListAmbiente();
    }

}
