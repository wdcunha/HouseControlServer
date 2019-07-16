package pt.ipp.estg.housecontrol.Server;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import pt.ipp.estg.housecontrol.FirebaseClasses.SensorsFRDManaging;
import pt.ipp.estg.housecontrol.Rules.RulesClass;
import pt.ipp.estg.housecontrol.Sensors.Sensor;
import pt.ipp.estg.housecontrol.Sensors.ServerHome;

public class ThreadSensorsData implements Runnable {

    private InputStream cliInpt = null;
    private ServerSocketClass serverSocketClass;
    private SensorsFRDManaging sensorsFRDManaging;
    private ServerHome serverHome =  new ServerHome();
    private RulesClass rulesClass = new RulesClass();

    public ThreadSensorsData(InputStream clientInput, ServerSocketClass serverSocketClass, SensorsFRDManaging sensorsFRDManaging) throws IOException {
        this.cliInpt = clientInput;
        this.serverSocketClass = serverSocketClass;
        this.sensorsFRDManaging = sensorsFRDManaging;
    }

    public void run() {

        // send message when received
        Scanner s = new Scanner(this.cliInpt);

        while (s.hasNextLine()) {
            String msg = String.valueOf(s.nextLine());
            Sensor recData = serverHome.parseData(msg);

            try {
                rulesClass.checkSensorsToSendFCM(recData);
                rulesClass.classifyDataToWriteFRD(recData);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // TODO - retirar essa msg antes de enviar o trabalho
            System.out.println("[Client Msg received] "+msg);
        }
        serverSocketClass.setClientIsOff(true);
        s.close();
    }

    public void takeMsgReturnSensor() {
        
    }

}

