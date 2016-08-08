package hackstyle.org.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import hackstyle.org.dao.AmbienteDAO;
import hackstyle.org.dao.SensorDAO;
import hackstyle.org.main.HSSensor;
import hackstyle.org.pojo.Ambiente;
import hackstyle.org.pojo.Carga;
import hackstyle.org.pojo.Sensor;
import hackstyle.org.wifi.WiFiUtil;


public class SensorCollector extends IntentService {

    public SensorCollector() {
        super("HS Intent Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Runnable runnable = new Runnable() {

            SensorDAO sensorDAO;
            List<Sensor> listSensorDB;

            @Override
            public void run() {

                HSSensor.getInstance().setScanning(true);
                Log.d(getClass().getName(), "inicio do serviço");

                sensorDAO = new SensorDAO(getApplicationContext());
                listSensorDB = sensorDAO.getListSensor();

                int foundSensors = checkRegisteredSensors();
                if (foundSensors < listSensorDB.size())
                    checkLostSensors();

                //SSensor.getInstance().setListSensorOn(listSensorDB);
                HSSensor.getInstance().setScanning(false);

                Log.d(getClass().getName(), "fim do serviço");

                sensorDAO.closeConnection();
            }


            /* retorna o numero de sensores online */
            private int checkRegisteredSensors() {

                int counter = 0;

                if (listSensorDB.size() < 1) {
                    Log.d(getClass().getName(), "Lista de sensores cadastrados é ZERO! Retornando de checkRegisteredSensors()");
                    return 0;
                }

                if (HSSensor.getInstance().getListSensorOn().size() < 1)
                    HSSensor.getInstance().setListSensorOn(listSensorDB);


                for (Sensor sensor : HSSensor.getInstance().getListSensorOn()) {

                    Socket sock = new Socket();
                    if (sensor.getIp().isEmpty()) {
                        Log.d(getClass().getName(), "O IP do sensor [" + sensor.getNome() + "] está vazio. Acabou de ser configurado? " +
                                "Nada a fazer em checkRegisteredSensors()");
                        continue;
                    }

                    SocketAddress addr = new InetSocketAddress(sensor.getIp(), 8000);

                    try {

                        sock.connect(addr, 3000);
                        Log.d(getClass().getName(), "Conectamos em " + sensor.getIp());


                        PrintWriter pout = new PrintWriter(sock.getOutputStream());

                        pout.print("getinfo::::::::\n");
                        pout.flush();

                        Log.d(getClass().getName(), "Enviado getinfo:::::::: para " + sensor.getIp() + " - Aguardando resposta...");

                        byte[] b = new byte[256];

                        sock.setSoTimeout(5000);
                        int bytes = sock.getInputStream().read(b);
                        sock.close();

                        String result = new String(b, 0, bytes - 1);
                        Log.d(getClass().getName(), "Recebida resposta de " + sensor.getIp() + "para nosso getinfo::::::::");

                        Sensor tmpSensor = buildSensorFromGetInfo(result);
                        if (tmpSensor == null) {
                            Log.d(getClass().getName(), "Resposta de " + sensor.getIp() + " nao é um sensor valido!");
                            Log.d(getClass().getName(), "Resposta: " + result);
                            continue;
                        }

                        if (sensor.equals(tmpSensor)) {

                            sensor.setActive(true);
                            counter++;

                            Log.d(getClass().getName(), "Resposta de " + sensor.getIp() + " é um sensor valido!");
                            Log.d(getClass().getName(), "Sensor " + sensor.getNome() + " : " + sensor.getIp() + " PAREADO!");
                        }


                    } catch (Exception e) {

                        if (e.getMessage() != null) {
                            Log.d(getClass().getName(), e.getMessage());
                        }
                        sensor.setActive(false);
                    }

                }

                return counter;
            }


            private void checkLostSensors() {

                WiFiUtil wiFiUtil = new WiFiUtil(getApplicationContext());
                String subnet = wiFiUtil.getNetworkAddress();
                boolean configured = false;

                for (int i=2;i<255;i++) {

                    String host = subnet + "." + i;

                    if (isIpBusy(host, HSSensor.getInstance().getListSensorOn()))
                        continue;


                    try {

                        Socket sock = new Socket();
                        SocketAddress addr = new InetSocketAddress(host, 8000);

                        sock.connect(addr, 100);

                        PrintWriter pout = new PrintWriter(sock.getOutputStream());

                        pout.print("getinfo::::::::\n");
                        pout.flush();

                        byte[] b = new byte[256];

                        sock.setSoTimeout(5000);
                        int bytes = sock.getInputStream().read(b);
                        sock.close();

                        String result = new String(b, 0, bytes - 1);

                        Sensor tmpSensor = buildSensorFromGetInfo(result);
                        if (tmpSensor == null)
                            continue;

                        for (Sensor sensor : HSSensor.getInstance().getListSensorOn()) {

                            if (!sensor.isActive()) {
                                if (sensor.equals(tmpSensor)) {

                                    Log.d(getClass().getName(), "Encontrado sensor " + sensor.getNome() + " que mudou de ip" +
                                            " anterior: " + sensor.getIp() + " - novo: " + host);

                                    sensor.setActive(true);
                                    sensor.setIp(host);
                                    sensorDAO.updateIP(sensor);
                                    configured = true;
                                }
                            }
                        }

                        if (configured)
                            continue;


                        //se chegou aqui, é um sensor valido que nao está no nosso BD
                        tmpSensor.setIp(host);
                        SensorDAO sensorDAO = new SensorDAO(getApplicationContext());
                        sensorDAO.insertSensor(tmpSensor);
                        tmpSensor.setActive(true);
                        HSSensor.getInstance().getListSensorOn().add(tmpSensor);

                        Log.d(getClass().getName(), "Encontrado novo sensor: " + tmpSensor.getNome() + " - " + tmpSensor.getIp());

                    } catch (Exception e) {
                        Log.e(getClass().getName(), "ip " + host + " offline!" + e.getClass());
                    }
                }

            }


            public boolean isIpBusy(String ip, List<Sensor> list) {

                for (Sensor sensor : list) {

                    if (sensor.isActive())
                        if (sensor.getIp().equals(ip))
                            return true;
                }
                return false;
            }


            public Sensor buildSensorFromGetInfo(String getinfo) {

                int count=0;

                for (int i=0; i<getinfo.length(); i++) {
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

                AmbienteDAO ambienteDAO = new AmbienteDAO(getApplicationContext());
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



        };

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(runnable, 0, 30, TimeUnit.SECONDS);
    }

}
