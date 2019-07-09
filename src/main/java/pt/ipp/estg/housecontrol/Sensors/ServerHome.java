package pt.ipp.estg.housecontrol.Sensors;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class ServerHome implements Runnable {

	 ServerSocket serverSocket;
	private Socket myClientSocket = null;
	private DataInputStream myDataInputStream = null;
	private PrintWriter myOutputStream;
	private SimpleDateFormat formatter;
	private Date date = new Date();

	private boolean bIsClosed = false;
	private ArrayList<Sensor> mSensors;

//	public ServerHome(ArrayList<Sensor> mSensors) {
	public ServerHome() {

//		this.mSensors = mSensors;

		int portNumber = 3000;
		String host = "localhost";

		try {
			
			Log.message("Opening socket to server: " + host + " " + portNumber);

			this.serverSocket = new ServerSocket(3000);
//			System.out.println(formatter.format(date)+" - [HCServer started at Port "+serverSocket.getLocalPort()+"]");

			this.myClientSocket = this.serverSocket.accept();
			this.myOutputStream = new PrintWriter(new OutputStreamWriter(this.myClientSocket.getOutputStream()));
			this.myDataInputStream = new DataInputStream(myClientSocket.getInputStream());

//			System.out.println(formatter.format(date)+" - Client connected from IP " + myClientSocket.getInetAddress().
//					getHostAddress());


		} catch (UnknownHostException e) {
			Log.message("Unknown host " + host);
		} catch (IOException e) {
			Log.message("Couldn't get I/O for the connection to the host " + host);
		}

		if ((myClientSocket != null) && 
		    (myOutputStream != null) && 
		    (myDataInputStream != null)) {
			
			try {
				
				while (bIsClosed == false) {
					
					try {
						
						Log.message("Sleeping for 10 seconds");
						Thread.sleep(10000);
						
						// Get input data
						String szMessage = this.myDataInputStream.toString();
						Log.message("Writing data to client: " + szMessage);
						
						this.myOutputStream.write(szMessage + "\n");
						this.myOutputStream.flush();

					} catch (InterruptedException e) {

						System.out.println("Interrupted Exception");

					}
				}
        
				Log.message("Closing connection");
				
				myOutputStream.close();
				myDataInputStream.close();
				myClientSocket.close();
				
			} catch (IOException e) {
				System.err.println("IOException:  " + e);
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void run() {
    
		String szResponseLine = "";
		System.out.println("inside run");
		try {
			
			if (this.myDataInputStream != null) {
				while ((szResponseLine = myDataInputStream.readLine()) != null) {

					System.out.println("inside while");

					Log.message("Received data: " + szResponseLine);
	
					if (szResponseLine.contains("exit") == true) {
						break;
					}
					
					// Sensor data must be "CLASS" "ID" "VALUE" For example: "010120" 
					Sensor mSensor = parseData(szResponseLine);
					
					if (mSensor == null) {
						// do not do anything, we have received some invalid data
					} else {
						// Update current data
						this.updateSensorData(mSensor);
					}					
				}
			}
			this.bIsClosed = true;
			
		} catch (IOException e) {
			Log.message("Exception while reading data from server: " + e);
		}
	}
	
	
	private Sensor parseData(String szResponseLine) {
		
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
	
	
	private void updateSensorData(Sensor mSensor) {
		
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
