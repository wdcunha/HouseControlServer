package pt.ipp.estg.housecontrol.Server;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import pt.ipp.estg.housecontrol.FirebaseClasses.SensorsFRDManaging;
import pt.ipp.estg.housecontrol.Rules.RulesClass;
import pt.ipp.estg.housecontrol.Sensors.Door;
import pt.ipp.estg.housecontrol.Sensors.HVAC;
import pt.ipp.estg.housecontrol.Sensors.Light;
import pt.ipp.estg.housecontrol.Sensors.Sensor;
import pt.ipp.estg.housecontrol.Sensors.ServerHome;

public class TreatSensorsMsg implements Runnable {

    private InputStream cliInpt = null;
    private ServerClass serverClass;
    private SensorsFRDManaging sensorsFRDManaging;
    private ServerHome serverHome =  new ServerHome();
    private RulesClass rulesClass = new RulesClass();

    public TreatSensorsMsg(InputStream clientInput, ServerClass serverClass,SensorsFRDManaging sensorsFRDManaging) throws IOException {
        this.cliInpt = clientInput;
        this.serverClass = serverClass;
        this.sensorsFRDManaging = sensorsFRDManaging;
    }

    public void run() {

        // send message when received
        Scanner s = new Scanner(this.cliInpt);

        while (s.hasNextLine()) {
            String msg = String.valueOf(s.nextLine());
//            serverClass.sendToHomeBus(msg);
            Sensor recData = serverHome.parseData(msg);

            try {
                rulesClass.checkSensorsToSendFCM(recData);
                prepareSensorDataToWriteFRD(recData);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // TODO - retirar essa msg antes de enviar o trabalho
            System.out.println("[Client Msg received] "+msg);
        }
        serverClass.setClientIsOff(true);
        s.close();
    }

    /**
     * TODO *
     *
     * isso terá depois que evoluir para a classe Rules para progr. funcional
     *
     */

    public void prepareSensorDataToWriteFRD(Sensor mSensor) throws IOException {

        //TODO adicionar msg no Aj aqui
        System.out.println("Preparing sensor data: " + mSensor.toString());


                switch(mSensor.getSensorClass()) {
                    case 0:
                        System.out.println("... This is Temperature value: "+mSensor.getValue());
                        System.out.println("... This is getSensorClass value: "+mSensor.getSensorClass());
                        System.out.println("... This is getIdentifier value: "+mSensor.getIdentifier());
                        sensorsFRDManaging.writeSensorFRD("temperature", String.valueOf(mSensor));
                        break;

                    case 1:
                        System.out.println("... This is Blinder value: "+mSensor.getValue());
                        System.out.println("... This is getSensorClass value: "+mSensor.getSensorClass());
                        System.out.println("... This is getIdentifier value: "+mSensor.getIdentifier());
                        sensorsFRDManaging.writeSensorFRD("blinder", String.valueOf(mSensor));
                        break;

                    // door
                    case 2:
                        System.out.println("... This is Door value: "+((Door)mSensor).isOpen());
                        System.out.println("... This is getSensorClass value: "+mSensor.getSensorClass());
                        System.out.println("... This is getIdentifier value: "+mSensor.getIdentifier());
                        sensorsFRDManaging.writeSensorFRD("door", String.valueOf(mSensor));
                        break;

                    // Light
                    case 3:
                        System.out.println("... This is Light value: "+((Light)mSensor).isOn());
                        System.out.println("... This is getSensorClass value: "+mSensor.getSensorClass());
                        System.out.println("... This is getIdentifier value: "+mSensor.getIdentifier());
                        sensorsFRDManaging.writeSensorFRD("light", String.valueOf(mSensor));
                        break;

                    case 4:
                        System.out.println("... This is HVAC value: "+((HVAC)mSensor).isOn());
                        System.out.println("... This is getSensorClass value: "+mSensor.getSensorClass());
                        System.out.println("... This is getIdentifier value: "+mSensor.getIdentifier());
                        sensorsFRDManaging.writeSensorFRD("hvac", String.valueOf(mSensor));
                        break;
                }
    }

}

