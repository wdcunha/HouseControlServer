package pt.ipp.estg.housecontrol.Sensors;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class TreatMsgReceived implements Runnable {

    private InputStream inputStream = null;
    private pt.ipp.estg.housecontrol.Sensors.Client client;
    private ArrayList<pt.ipp.estg.housecontrol.Sensors.Sensor> mSensors;

    public TreatMsgReceived(InputStream myInputStream, ArrayList<pt.ipp.estg.housecontrol.Sensors.Sensor> mSensors) {
        this.inputStream = myInputStream;
        this.mSensors = mSensors;
    }
    @SuppressWarnings("deprecation")
    public void run() {

        String szResponseLine = "";

            if (this.inputStream == null || !this.inputStream.equals("")) {

                Scanner s = new Scanner(this.inputStream);

                while (s.hasNextLine()) {

                    szResponseLine = String.valueOf(s.nextLine());


                    if (szResponseLine.contains("exit") == true) {
                        break;
                    }

                    Log.message("Received data: " + szResponseLine);

                    Sensor mSensor = null;

                    // Sensor data must be "CLASS" "ID" "VALUE" For example: "010120"
                    if((mSensor = this.parseData(szResponseLine)) != null){
                        Log.message("Received data: " + szResponseLine);

                        this.updateSensorData(mSensor);
                    }
                }
            }
            client.setbIsClosed(true);
    }


    public static Sensor parseData(String szResponseLine) {

        Log.message("Parsing data: " + szResponseLine);

        if (szResponseLine == null || szResponseLine.length() < 5) {
            return null;
        }

        Sensor mSensor = null;

        // Parse sensor class
        String szSensorClass = szResponseLine.substring(0, 2);
        int nSensorClass = Integer.parseInt(szSensorClass);

        String szSensorId = szResponseLine.substring(2, 4);
        int nSensorId = Integer.parseInt(szSensorId);

        switch(nSensorClass) {
            case 0:
                String szTemperature = szResponseLine.substring(4, 6);
                int nSensorTemperature = Integer.parseInt(szTemperature);
                mSensor = new Temperature(nSensorClass, nSensorId, nSensorTemperature, "");
                break;

            case 1:
                String szBlinderPercentage = szResponseLine.substring(4, 7);
                int nBlinderPercentage = Integer.parseInt(szBlinderPercentage);
                mSensor = new Blinder(nSensorClass, nSensorId, nBlinderPercentage, "");
                break;

            case 2:
                String szDoorState = szResponseLine.substring(4, 6);
                int nDoorState = Integer.parseInt(szDoorState);

                boolean bIsDoorOpen = false;
                if (nDoorState == 1) {
                    bIsDoorOpen = true;
                }

                mSensor = new Door(nSensorClass, nSensorId, bIsDoorOpen, "");
                break;

            case 3:
                String szLightState = szResponseLine.substring(4, 6);
                int nLightState = Integer.parseInt(szLightState);

                boolean bIsLightOn = false;
                if (nLightState == 1) {
                    bIsLightOn = true;
                }

                mSensor = new Light(nSensorClass, nSensorId, bIsLightOn, "");
                break;

            case 4:
                String szHVACState = szResponseLine.substring(6, 8);
                String szHVACTemperature = szResponseLine.substring(4, 6);
                int nHVACState = Integer.parseInt(szHVACState);
                int nHVACTemperature = Integer.parseInt(szHVACTemperature);

                boolean bIsHVACOn = false;
                if (nHVACState == 1) {
                    bIsHVACOn = true;
                }

                mSensor = new HVAC(nSensorClass, nSensorId, bIsHVACOn, nHVACTemperature, "");
                break;
        }

        return mSensor;
    }


    public void updateSensorData(Sensor mSensor) {

        Log.message("Updating sensor data: " + mSensor.toString());

        for (int nIndex = 0; nIndex < mSensors.size(); nIndex++) {

            Sensor mListSensor = mSensors.get(nIndex);
            if ((mListSensor.getSensorClass() == mSensor.getSensorClass()) &&
                    (mListSensor.getIdentifier() == mSensor.getIdentifier())) {

                switch(mSensor.getSensorClass()) {
                    case 0:
                    case 1:
                        mListSensor.setValue(mSensor.getValue());
                        break;

                    // door
                    case 2:
                        ((Door)mListSensor).setIsOpen(((Door)mSensor).isOpen());
                        break;

                    // Light
                    case 3:
                        ((Light)mListSensor).setIsOn(((Light)mSensor).isOn());
                        break;

                    case 4:
                        mListSensor.setValue(mSensor.getValue());
                        ((HVAC)mListSensor).setIsOn(((HVAC)mSensor).isOn());
                        break;
                }
            }
        }

        Log.message("Sensor data updated: " + mSensors.toString());
    }


}
