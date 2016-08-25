package hackstyle.org.service;

import android.content.Context;

import hackstyle.org.dao.AmbienteDAO;
import hackstyle.org.pojo.Ambiente;
import hackstyle.org.pojo.Carga;
import hackstyle.org.pojo.Sensor;

public class SensorBuilder {

    public static Sensor buildSensorFromGetInfo(Context context, String getinfo) {

        int count = 0;

        for (int i = 0; i < getinfo.length(); i++) {
            if (getinfo.charAt(i) == ':')
                count++;
        }

        if (count != 8)
            return null;


        String[] str = getinfo.split(":");

        Sensor sensor = new Sensor();

        sensor.setNome(str[1]);
        sensor.setId(Integer.parseInt(str[5]));

        String[] amb = str[4].split(",");
        int id_ambiente = Integer.parseInt(amb[0]);

        AmbienteDAO ambienteDAO = new AmbienteDAO(context);
        Ambiente ambiente = ambienteDAO.getAmbiente(id_ambiente);
        if (ambiente != null) {

            sensor.setAmbiente(ambiente);

        } else {

            if (ambienteDAO.getListAmbiente().size() > 0) {
                Ambiente am = ambienteDAO.getListAmbiente().get(0);
                sensor.setAmbiente(am);
            }
        }

        for (int i = 6; i < str.length; i++) {

            if (str[i].isEmpty())
                continue;

            String[] ss = str[i].split(",");

            Carga c = new Carga();
            c.setNome(ss[0].trim());
            c.setPino(Integer.parseInt(ss[1].trim()));

            sensor.putCarga(c);
        }

        return sensor;
    }

}
