package hackstyle.org.wizard;


import hackstyle.org.pojo.Sensor;

public class WizardSensor  {

    private static WizardSensor instance = null;
    private Sensor sensor;

    public static WizardSensor getInstance() {

        if (instance == null)
            instance = new WizardSensor();

        return instance;
    }

    public WizardSensor() {
        this.sensor = new Sensor();
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }


}
