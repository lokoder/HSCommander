package hackstyle.org.pojo;


public class Ambiente {

    private int id;
    private String nome;
    private int icone;
    private String imagePath;

    public Ambiente() {

        this.id = -1;
        this.nome = "";
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

    public int getIcone() {
        return icone;
    }

    public void setIcone(int icone) {
        this.icone = icone;
    }

    public String toString() { return this.nome; }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

}
