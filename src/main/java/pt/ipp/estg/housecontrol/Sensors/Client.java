package pt.ipp.estg.housecontrol.Sensors;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;

public class Client {

	private Socket myClientSocket = null;
	private DataInputStream myDataInputStream = null;
	private PrintWriter myOutputStream;

	private boolean bIsClosed = false;
	private ArrayList<Sensor> mSensors;

	@SuppressWarnings("deprecation")
	public Client(ArrayList<Sensor> mSensors) throws IOException {
		
		this.mSensors = mSensors;

		int portNumber = 3000;
		String host = "localhost";

		try {
			
			Log.message("Opening socket to server: " + host + " " + portNumber);
			
			this.myClientSocket = new Socket(host, portNumber);
			this.myOutputStream = new PrintWriter(new OutputStreamWriter(this.myClientSocket.getOutputStream()));
			this.myDataInputStream = new DataInputStream(myClientSocket.getInputStream());

		} catch (UnknownHostException e) {
			Log.message("Don't know about host " + host);
		} catch (IOException e) {
			Log.message("Couldn't get I/O for the connection to the host " + host);
		}

		String szResponseLine = null;

		if ((myClientSocket != null) &&
		    (myOutputStream != null)) {
//				&& (myClientSocket.getInputStream() != null)) {
//		    (myDataInputStream != null)) {

			try {
				
				while (bIsClosed == false) {
					
					try {

						Log.message("Sleeping for 10 seconds");
						Thread.sleep(10000);

						// Generate random event
						String szMessage = this.generateRandomData();
						Log.message("Writing data to server: " + szMessage);

						this.myOutputStream.write(szMessage + "\n");

						InputStream clientInput = myClientSocket.getInputStream();

						TreatMsgReceived tmr = new TreatMsgReceived(clientInput, mSensors);
						new Thread(tmr).start();

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

	public Client(Sensor mSensor) {
	}

	public void setbIsClosed(boolean bIsClosed) {
		this.bIsClosed = bIsClosed;
	}


	public String generateRandomData() {
		
		Log.message("Generating random data");
	
		Random m = new Random();
		int nNextSensorPos = m.nextInt(this.mSensors.size()-1);
		
		Sensor mSensor = this.mSensors.get(nNextSensorPos);
		switch(mSensor.getSensorClass()) {
		
		// Temperature sensor
		case 0:
			
			int nTemperature = mSensor.getValue();
			if (nTemperature == 0) {
				nTemperature = 20;
			}
			
			mSensor.setValue(nTemperature - 1);
			break;
			
		// Blinder
		case 1:
			int nLevel = mSensor.getValue();
			if (nLevel == 0) {
				nLevel = 100;
			}
			
			mSensor.setValue(nLevel - 1);
			break;
			
		// Door
		case 2:
			if (((Door)mSensor).isOpen() == true) {
				((Door)mSensor).setIsOpen(false);
			} else {
				((Door)mSensor).setIsOpen(true);
			}
			break;

		// Lights
		case 3:
			if (((Light)mSensor).isOn() == true) {
				((Light)mSensor).setIsOn(false);
			} else {
				((Light)mSensor).setIsOn(true);
			}
			break;

		// HVAC
		case 4:
			if (((HVAC)mSensor).isOn() == true) {
				
				if (mSensor.getValue() != 0) {
					mSensor.setValue(mSensor.getValue()-1);
				} else {
					((HVAC)mSensor).setIsOn(false);
					mSensor.setValue(20);
				}
			} else {
				((HVAC)mSensor).setIsOn(true);
			}
			break;
		}
		
		Log.message("Generated data: " + mSensor.toString());
		
		return mSensor.toString();
	}
}
