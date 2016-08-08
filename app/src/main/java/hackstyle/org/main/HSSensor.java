package hackstyle.org.main;

import java.util.ArrayList;
import java.util.List;

import hackstyle.org.adapter.SensoresAdapter;
import hackstyle.org.pojo.Sensor;

public class HSSensor  {

    private static HSSensor instance = null;

    private boolean isScanning;

    private List<Sensor> listSensorOn;
    private List<Sensor> listSensorNotFound;

    public static HSSensor getInstance() {

        if (instance == null)
            instance = new HSSensor();

        return instance;
    }

    public HSSensor() {

        listSensorOn = new ArrayList<>();
        listSensorNotFound = new ArrayList<>();
    }

    public List<Sensor> getListSensorOn() {
        return listSensorOn;
    }

    public void setListSensorOn(List<Sensor> listSensorOn) {
        this.listSensorOn = listSensorOn;
    }

    public List<Sensor> getListSensorNotFound() {
        return listSensorNotFound;
    }

    public void setListSensorNotFound(List<Sensor> listSensorNotFound) {
        this.listSensorNotFound = listSensorNotFound;
    }


    public boolean isScanning() {
        return isScanning;
    }

    public void setScanning(boolean scanning) {
        isScanning = scanning;
    }

}
