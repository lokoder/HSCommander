package hackstyle.org.pojo;


public class WiFiCredentials {

    private int id;
    private String ssid;
    private String senha;

    public  WiFiCredentials() {
        this.id = -1;
        this.ssid = "";
        this.senha = "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSSID() {
        return ssid;
    }

    public void setSSID(String ssid) {
        this.ssid = ssid;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
