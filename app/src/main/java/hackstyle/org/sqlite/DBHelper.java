package hackstyle.org.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 9;
    public static final String DATABASE_NAME = "HSCommander.db";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String tb_sensor = "CREATE TABLE Sensor ( " +
                "id integer not null primary key autoincrement, " +
                "nome varchar(32) not null, " +
                "icone integer, " +
                "id_ambiente integer not null, "+
                "ip char(15), " +
                "porta integer, " +
                "image_path varchar(128) DEFAULT '', " +
                "FOREIGN KEY(id_ambiente) "+
                "REFERENCES Ambiente(id))";

        String tb_carga = "CREATE TABLE Carga ( " +
                "id integer not null primary key autoincrement, " +
                "nome varchar(32) not null, " +
                "pino integer not null, " +
                "icone integer, " +
                "id_sensor integer not null, " +
                "image_path varchar(128) DEFAULT '', " +
                "FOREIGN KEY(id_sensor) " +
                "REFERENCES Sensor(id))";

        String tb_ambiente = "CREATE TABLE Ambiente (" +
                "id integer not null primary key autoincrement, " +
                "nome varchar(32) not null, " +
                "icone integer," +
                "image_path varchar(128) DEFAULT '')";

        String tb_wifi = "CREATE TABLE wifi (" +
                "id integer not null primary key autoincrement, " +
                "ssid varchar(32) not null, " +
                "senha varchar(32) default '')";

        db.execSQL(tb_sensor);
        db.execSQL(tb_carga);
        db.execSQL(tb_ambiente);
        db.execSQL(tb_wifi);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS Sensor");
        db.execSQL("DROP TABLE IF EXISTS Carga");
        db.execSQL("DROP TABLE IF EXISTS Ambiente");
        db.execSQL("DROP TABLE IF EXISTS Wifi");

        onCreate(db);
    }

}
