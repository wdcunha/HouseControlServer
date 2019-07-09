package pt.ipp.estg.housecontrol.Server;

import java.io.InputStream;
import java.util.Scanner;

import pt.ipp.estg.housecontrol.Sensors.Door;
import pt.ipp.estg.housecontrol.Sensors.HVAC;
import pt.ipp.estg.housecontrol.Sensors.Light;
import pt.ipp.estg.housecontrol.Sensors.Sensor;

import static pt.ipp.estg.housecontrol.Sensors.TreatMsgReceived.parseData;

public class TreatSensorsMsg implements Runnable {

    private InputStream cliInpt = null;
    private ServerClass serverClass;

    public TreatSensorsMsg(InputStream clientInput, ServerClass serverClass) {
        this.cliInpt = clientInput;
        this.serverClass = serverClass;
    }

    public void run() {

        // send message when received
        Scanner s = new Scanner(this.cliInpt);

        while (s.hasNextLine()) {
            String msg = String.valueOf(s.nextLine());
            Sensor recData = parseData(msg);
            prepareSensorData(recData);
            System.out.println("[Client Msg received] "+msg);
            serverClass.sendMessage(msg);
        }
        serverClass.setClientIsOff(true);
        s.close();
    }

    /**
     * TODO
     * agora é gravar no firebase segundo cada sensor e tb enviar fcm ao
     * cliente mobile para avisá-lo da alteração
     *
     * em cada CASE fazer chamada da classe correspondente a FRD e FCM com
     * dispositivo certo pra atualizar, considerar que haverá salvamento
     * individual por sensor
     *
     * isso terá depois que evoluir para a classe Rules para progr. funcional
     *
     *
     */
    public void prepareSensorData(Sensor mSensor) {

        //TODO adicionar msg no Aj para cá
        System.out.println("Preparing sensor data: " + mSensor.toString());

            Sensor receivedDataSensor = mSensor;

                switch(mSensor.getSensorClass()) {
                    case 0:
                        System.out.println("... This is Temperature value: "+mSensor.getValue());
                        System.out.println("... This is getSensorClass value: "+mSensor.getSensorClass());
                        System.out.println("... This is getIdentifier value: "+mSensor.getIdentifier());
                        break;

                    case 1:
//                        receivedDataSensor.setValue(mSensor.getValue());
                        System.out.println("... This is Blinder value: "+mSensor.getValue());
                        System.out.println("... This is getSensorClass value: "+mSensor.getSensorClass());
                        System.out.println("... This is getIdentifier value: "+mSensor.getIdentifier());
                        break;

                    // door
                    case 2:
//                        ((Door) receivedDataSensor).setIsOpen(((Door)mSensor).isOpen());
                        System.out.println("... This is Door value: "+((Door)mSensor).isOpen());
                        System.out.println("... This is getSensorClass value: "+mSensor.getSensorClass());
                        System.out.println("... This is getIdentifier value: "+mSensor.getIdentifier());
                        break;

                    // Light
                    case 3:
//                        ((Light) receivedDataSensor).setIsOn(((Light)mSensor).isOn());
                        System.out.println("... This is Light value: "+((Light)mSensor).isOn());
                        System.out.println("... This is getSensorClass value: "+mSensor.getSensorClass());
                        System.out.println("... This is getIdentifier value: "+mSensor.getIdentifier());
                        break;

                    case 4:
                        receivedDataSensor.setValue(mSensor.getValue());
                        ((HVAC) receivedDataSensor).setIsOn(((HVAC)mSensor).isOn());
                        System.out.println("... This is HVAC value: "+((HVAC)mSensor).isOn());
                        System.out.println("... This is getSensorClass value: "+mSensor.getSensorClass());
                        System.out.println("... This is getIdentifier value: "+mSensor.getIdentifier());
                        break;
                }
    }

}

