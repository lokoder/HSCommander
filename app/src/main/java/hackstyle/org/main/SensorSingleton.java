package hackstyle.org.main;


import java.util.ArrayList;
import java.util.List;

import hackstyle.org.dao.SensorDAO;
import hackstyle.org.pojo.Sensor;

public class SensorSingleton {

    private static SensorSingleton instance = null;

    private List<Sensor> listSensor;
    private List<Sensor> listSensorProd;
    private List<Sensor> listSensorErr;

    public static SensorSingleton getInstance() {

        if (instance == null)
            instance = new SensorSingleton();

        return instance;
    }

    public SensorSingleton() {

        listSensor = new ArrayList<Sensor>();
        listSensorProd = new ArrayList<Sensor>();
        listSensorErr = new ArrayList<Sensor>();
    }

    public void addSensor(Sensor sensor) {
        if (sensor != null)
            listSensor.add(sensor);
    }


    public void removeSensor(Sensor sensor) {
        listSensor.remove(sensor);
    }

    public List<Sensor> getListSensor() {
        return listSensor;
    }

    public int getListSensorSize() {
        return listSensor.size();
    }


    public void addSensorProd(Sensor sensor) {
        if (sensor != null)
            listSensorProd.add(sensor);
    }

    public void removeSensorProd(Sensor sensor) {
        listSensorProd.remove(sensor);
    }

    public void eraseListSensorProd() {

        listSensorProd.clear();
    }

    public List<Sensor> getListSensorProd() { return listSensorProd; }

    public int getListSensorProdSize() {
        return listSensorProd.size();
    }


    public void addSensorErr(Sensor sensor) {
        if (sensor != null)
            listSensorErr.add(sensor);
    }

    public void removeSensorErr(Sensor sensor) {
        listSensorErr.remove(sensor);
    }

    public List<Sensor> getListSensorErr() { return listSensorErr; }

    public int getListSensorErrSize() {
        return listSensorErr.size();
    }

}
