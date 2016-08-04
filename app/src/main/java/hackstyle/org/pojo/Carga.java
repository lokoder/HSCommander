package hackstyle.org.pojo;


public class Carga {

    private int id;
    private String nome;
    private int pino;
    private int icone;
    private String imagePath;
    private Sensor sensor;


    public Carga() {

        this.id = -1;
        this.nome = "";
        this.pino = -1;
        this.icone = -1;
        this.imagePath = "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getPino() {
        return pino;
    }

    public void setPino(int pino) {
        this.pino = pino;
    }

    public int getIcone() {
        return icone;
    }

    public void setIcone(int icone) {
        this.icone = icone;
    }


    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }
}


