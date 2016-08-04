package hackstyle.org.sqlite;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import hackstyle.org.dao.AmbienteDAO;
import hackstyle.org.dao.CargaDAO;
import hackstyle.org.dao.SensorDAO;
import hackstyle.org.main.SensorSingleton;
import hackstyle.org.pojo.Ambiente;
import hackstyle.org.pojo.Carga;
import hackstyle.org.pojo.Sensor;
import hackstyle.org.pojo.WiFiCredentials;

public class DBAdapter {

    private DBHelper dbHelper;
    private SQLiteDatabase database;
    private Context context;

    public DBAdapter(Context context) {

        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
        this.context = context;
    }


    public int insert(String table, ContentValues contentValues) {

        long ret = database.insert(table, null, contentValues);
        return (int)ret; // -1 erro, novo id sucesso
    }

    public int delete(String table, int id) {

        long ret = database.delete(table, "id="+id, null);
        return (int)ret; //numero de rows afetadas
    }

    public Ambiente getAmbiente(int id) {

        String query = "SELECT * FROM Ambiente WHERE id=?";
        String[] args = new String[] { Integer.toString(id) };

        Cursor c = database.rawQuery(query, args);
        if (c.moveToFirst()) {

            Ambiente ambiente = new Ambiente();
            ambiente.setId(c.getInt(c.getColumnIndex("id")));
            ambiente.setNome(c.getString(c.getColumnIndex("nome")));
            ambiente.setIcone(c.getInt(c.getColumnIndex("icone")));
            ambiente.setImagePath(c.getString(c.getColumnIndex("image_path")));

            return ambiente;
        }

        return null;
    }


    public List<Ambiente> getListAmbiente() {

        String query = "SELECT * FROM Ambiente ORDER BY id";
        Cursor c = database.rawQuery(query, null);
        List<Ambiente> listAmbiente = new ArrayList<Ambiente>();

        while (c.moveToNext()) {

            Ambiente ambiente = new Ambiente();
            ambiente.setId(c.getInt(c.getColumnIndex("id")));
            ambiente.setNome(c.getString(c.getColumnIndex("nome")));
            ambiente.setIcone(c.getInt(c.getColumnIndex("icone")));
            String path = c.getString(c.getColumnIndex("image_path"));
            if (path != null)
                ambiente.setImagePath(path);

            listAmbiente.add(ambiente);
        }

        return listAmbiente;
    }


    public Carga getCarga(int id) {

        String query = "SELECT * FROM Carga WHERE id=?";
        String[] args = new String[] { Integer.toString(id) };

        Cursor c = database.rawQuery(query, args);
        if (c.moveToFirst()) {

            Carga carga = new Carga();
            carga.setId(c.getInt(c.getColumnIndex("id")));
            carga.setNome(c.getString(c.getColumnIndex("nome")));
            carga.setIcone(c.getInt(c.getColumnIndex("icone")));
            carga.setPino(c.getInt(c.getColumnIndex("pino")));
            carga.setImagePath(c.getString(c.getColumnIndex("image_path")));

            int id_sensor = c.getInt(c.getColumnIndex("id_sensor"));
            SensorDAO sensorDAO = new SensorDAO(context);
            Sensor sensor = sensorDAO.getSensor(id_sensor);
            carga.setSensor(sensor);

            return carga;
        }

        return null;
    }


    public List<Carga> getListCarga(int sensorId) {

        String query = "SELECT * FROM Carga WHERE id_sensor=?";
        String[] args = new String[] { Integer.toString(sensorId) };

        Cursor c = database.rawQuery(query, args);
        List<Carga> listCarga = new ArrayList<Carga>();

        while (c.moveToNext()) {

            Carga carga = new Carga();
            carga.setId(c.getInt(c.getColumnIndex("id")));
            carga.setNome(c.getString(c.getColumnIndex("nome")));
            carga.setPino(c.getInt(c.getColumnIndex("pino")));
            carga.setIcone(c.getInt(c.getColumnIndex("icone")));
            carga.setImagePath(c.getString(c.getColumnIndex("image_path")));

            listCarga.add(carga);
        }

        return listCarga;
    }


    public Sensor getSensor(int id) {

        String query = "SELECT * FROM Sensor WHERE id=?";
        String[] args = new String[] { Integer.toString(id) };

        Cursor c = database.rawQuery(query, args);
        if (c.moveToFirst()) {

            Sensor sensor = new Sensor();
            sensor.setId(c.getInt(c.getColumnIndex("id")));
            sensor.setNome(c.getString(c.getColumnIndex("nome")));
            sensor.setIcone(c.getInt(c.getColumnIndex("icone")));
            sensor.setIp(c.getString(c.getColumnIndex("ip")));
            sensor.setPorta(c.getInt(c.getColumnIndex("porta")));
            sensor.setImagePath(c.getString(c.getColumnIndex("image_path")));

            AmbienteDAO ambienteDAO = new AmbienteDAO(context);
            int ambienteId = c.getInt(c.getColumnIndex("id_ambiente"));

            Ambiente ambiente = ambienteDAO.getAmbiente(ambienteId);
            sensor.setAmbiente(ambiente);

            CargaDAO cargaDAO = new CargaDAO(context);
            List<Carga> listCarga = cargaDAO.getListCarga(sensor.getId());

            for (Carga carga : listCarga) {
                carga.setSensor(sensor);
                sensor.putCarga(carga);
            }

            return sensor;
        }

        return null;
    }


    public List<Sensor> getListSensor() {

        String query = "SELECT * FROM Sensor ORDER by id";
        Cursor c = database.rawQuery(query, null);

        List<Sensor> listSensor = new ArrayList<Sensor>();

        while (c.moveToNext()) {

            Sensor sensor = new Sensor();
            sensor.setId(c.getInt(c.getColumnIndex("id")));
            sensor.setNome(c.getString(c.getColumnIndex("nome")));
            sensor.setIcone(c.getInt(c.getColumnIndex("icone")));
            sensor.setIp(c.getString(c.getColumnIndex("ip")));
            sensor.setPorta(c.getInt(c.getColumnIndex("porta")));
            sensor.setImagePath(c.getString(c.getColumnIndex("image_path")));

            AmbienteDAO ambienteDAO = new AmbienteDAO(context);
            int ambienteId = c.getInt(c.getColumnIndex("id_ambiente"));

            Ambiente ambiente = ambienteDAO.getAmbiente(ambienteId);
            sensor.setAmbiente(ambiente);

            CargaDAO cargaDAO = new CargaDAO(context);
            List<Carga> listCarga = cargaDAO.getListCarga(sensor.getId());

            for (Carga carga : listCarga) {
                carga.setSensor(sensor);
                sensor.putCarga(carga);
            }

            listSensor.add(sensor);
        }

        return listSensor;
    }


    public WiFiCredentials getWiFiCredentials() {

        String query = "SELECT * FROM Wifi";

        Cursor c = database.rawQuery(query, null);
        if (c.moveToFirst()) {

            WiFiCredentials wiFiCredentials = new WiFiCredentials();
            wiFiCredentials.setId(c.getInt(c.getColumnIndex("id")));
            wiFiCredentials.setSSID(c.getString(c.getColumnIndex("ssid")));
            wiFiCredentials.setSenha(c.getString(c.getColumnIndex("senha")));

            return wiFiCredentials;
        }

        return null;
    }


    public int updateSensorIP(Sensor sensor) {

        ContentValues cv = new ContentValues();
        cv.put("ip", sensor.getIp());
        cv.put("porta", sensor.getPorta());

        long ret = database.update("Sensor", cv, "id="+sensor.getId(), null);
        return (int)ret;
    }

    public int updateSensorImagePath(Sensor sensor) {

        ContentValues cv = new ContentValues();
        cv.put("image_path", sensor.getImagePath());

        long ret = database.update("Sensor", cv, "id="+sensor.getId(), null);
        return (int)ret;
    }

    public int updateAmbienteImagePath(Ambiente ambiente) {

        ContentValues cv = new ContentValues();
        cv.put("image_path", ambiente.getImagePath());

        long ret = database.update("Ambiente", cv, "id="+ambiente.getId(), null);
        return (int)ret;
    }

    public int updateCargaImagePath(Carga carga) {

        ContentValues cv = new ContentValues();
        cv.put("image_path", carga.getImagePath());

        long ret = database.update("Carga", cv, "id="+carga.getId(), null);
        return (int)ret;
    }

    public int updateWiFiCredentials(WiFiCredentials wiFiCredentials) {

        ContentValues cv = new ContentValues();
        cv.put("ssid", wiFiCredentials.getSSID());
        cv.put("senha", wiFiCredentials.getSenha());

        long ret = database.update("Wifi", cv, "id="+wiFiCredentials.getId(), null);
        return (int)ret;
    }


    public void updateListSensorProd() {

        List<Sensor> listSensor = new ArrayList<Sensor>(SensorSingleton.getInstance().getListSensorProd());

        SensorSingleton.getInstance().eraseListSensorProd();

        for (Sensor s : listSensor) {

            Sensor sensor = getSensor(s.getId());
            if (sensor != null) {
                SensorSingleton.getInstance().addSensorProd(sensor);
            }
        }
    }


    public void zeraDB() {

        database.delete("Ambiente", null, null);
        database.delete("Carga", null, null);
        database.delete("Wifi", null, null);
        database.delete("Sensor", null, null);
    }

}
