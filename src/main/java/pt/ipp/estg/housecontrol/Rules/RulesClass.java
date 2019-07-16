package pt.ipp.estg.housecontrol.Rules;

import org.json.JSONException;

import java.io.IOException;

import pt.ipp.estg.housecontrol.Sensors.Door;
import pt.ipp.estg.housecontrol.Sensors.HVAC;
import pt.ipp.estg.housecontrol.Sensors.Sensor;

import static pt.ipp.estg.housecontrol.FirebaseClasses.CloudMessagingClass.sendFCMNewData;
import static pt.ipp.estg.housecontrol.FirebaseClasses.SensorsFRDManaging.getSensorSingleEvent;
import static pt.ipp.estg.housecontrol.FirebaseClasses.SensorsFRDManaging.writeSensorFRD;

public class RulesClass {

    public RulesClass() {
    }

    public void checkSensorsToSendFCM(Sensor sensor) throws IOException, JSONException {

        switch (sensor.getSensorClass()) {
            // Temperature
            case 0:
                int temp = sensor.getValue();
                if ((temp < 10) && (temp > 40)) {
                    String intensidade;
                    if (temp > 40){
                        intensidade = "alta";
                    } else if (temp > 50){
                        intensidade = "muito alta";
                    } else if (temp < 0){
                        intensidade = "muito baixa";
                    } else {
                        intensidade = "baixa";
                    }
                    sendFCMNewData("<<Alerta>> Temperatura "+intensidade+"!", "Atenção, valor de temperatura "+intensidade+ "atingida na sua residência: "
                            + temp+"º, marcada no sensor de nº " +sensor.getIdentifier());
                }
                break;
            // Blinder
            case 1:
                // Nenhum tipo de situação identificada para notificação, porém se surgir alguma, é só implementar aqui
                break;

            // door
            case 2:

                boolean doorOpen = ((Door) sensor).isOpen();
                if (doorOpen == true) {
                    sendFCMNewData("<<Alerta>> Uma porta foi destrancada!", "Atenção, foi aberta a porta de número " + sensor.getIdentifier());
                }
                break;

            // Light
            case 3:
                // Nenhum tipo de situação existente para notificação, porém se surgir alguma, é só implementar aqui
                break;
            // Hvac
            case 4:
                boolean hvacIsOn = ((HVAC) sensor).isOn();
                Sensor temperature = getSensorSingleEvent("temperature");


                if(temperature != null){
                    if (hvacIsOn == true && temperature.getValue() > 19 && temperature.getValue() < 26) {
                        sendFCMNewData("<<Alerta de gasto>> HVAC!", "Atenção, HVAC ligado com temperatura de " + sensor.getValue()+"º, gerando gasto excessivo!!!");
                    }
                }
                break;
        }
    }


    /**
     * classifica o dado do sensor recebido pela ThreadSensorsData e salva no nó correspondente
     */

    public void classifyDataToWriteFRD(Sensor mSensor) throws IOException {

        switch(mSensor.getSensorClass()) {
            case 0:
                writeSensorFRD("temperature", String.valueOf(mSensor));
                break;

            case 1:
                writeSensorFRD("blinder", String.valueOf(mSensor));
                break;

            // door
            case 2:
                writeSensorFRD("door", String.valueOf(mSensor));
                break;

            // Light
            case 3:
                writeSensorFRD("light", String.valueOf(mSensor));
                break;

            case 4:
                writeSensorFRD("hvac", String.valueOf(mSensor));
                break;
        }
    }
}
