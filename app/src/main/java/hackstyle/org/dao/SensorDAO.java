package hackstyle.org.dao;


import android.content.ContentValues;
import android.content.Context;
import java.util.List;
import hackstyle.org.pojo.Carga;
import hackstyle.org.pojo.Sensor;
import hackstyle.org.sqlite.DBAdapter;

public class SensorDAO {

    private DBAdapter dbAdapter;
    private Context context;

    public SensorDAO(Context context) {

        dbAdapter = new DBAdapter(context);
        this.context = context;
    }


    public int insertSensor(Sensor sensor) {

        ContentValues c = new ContentValues();
        c.put("nome", sensor.getNome());
        c.put("icone", sensor.getIcone());
        c.put("id_ambiente", sensor.getAmbiente().getId());
        c.put("ip", sensor.getIp());
        c.put("porta", sensor.getPorta());

        int sensorId = dbAdapter.insert("Sensor", c);
        if (sensorId == -1)
            return sensorId;

        for (int i=0; i<2; i++) {

            if (sensor.existsCarga(i)) {
                Carga carga = sensor.getCarga(i);
                CargaDAO cargaDAO = new CargaDAO(context);
                cargaDAO.insertCarga(carga, sensorId);
            }
        }

        return sensorId;
    }


    public Sensor getSensor(int id) {

        return dbAdapter.getSensor(id);
    }

    public List<Sensor> getListSensor() {

        return dbAdapter.getListSensor();
    }

    public int updateIP(Sensor sensor) {

        return dbAdapter.updateSensorIP(sensor);
    }


}
