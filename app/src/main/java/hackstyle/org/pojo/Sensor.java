package hackstyle.org.pojo;


import java.util.ArrayList;
import java.util.List;

public class Sensor {

    private int id;
    private String nome;
    private Ambiente ambiente;
    private int icone;
    private String receivedMessage;
    private String ip;
    private int porta;
    private String imagePath;
    private List<Carga> listCarga;

    public Sensor() {

        listCarga = new ArrayList<Carga>();
        this.id = -1;
        this.nome = "";
        this.ip = "";
        this.ambiente = null;
        this.icone = -1;
        this.porta = 8000;
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

    public Ambiente getAmbiente() {
        return ambiente;
    }

    public void setAmbiente(Ambiente ambiente) {
        this.ambiente = ambiente;
    }

    public int getIcone() {
        return icone;
    }

    public void setIcone(int icone) {
        this.icone = icone;
    }

    public String getReceivedMessage() {
        return receivedMessage;
    }

    public void setReceivedMessage(String receivedMessage) {
        this.receivedMessage = receivedMessage;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPorta() {
        return porta;
    }

    public void setPorta(int porta) {
        this.porta = porta;
    }


    public void putCarga(Carga carga) {

        if (listCarga.size() < 2)
            listCarga.add(carga);
    }

    public Carga getCarga(int index) {

        if (index < listCarga.size()) {
            return listCarga.get(index);
        }

        return null;
    }

    public boolean existsCarga(int index) {

        if (index < listCarga.size())
            return true;

        return false;
    }


    public boolean equals(Sensor sensor2) {

        if (this.id == sensor2.getId())
            return true;

        return false;
    }


    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }


}
