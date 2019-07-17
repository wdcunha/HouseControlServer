package pt.ipp.estg.housecontrol;

import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import pt.ipp.estg.housecontrol.Sensors.Sensor;
import pt.ipp.estg.housecontrol.Sensors.ServerHome;

public aspect SystemLog {


	/****************************************************************************
	 *
	 * my_pointcut_executaServerSocketClass
	 *
	 */

	pointcut my_pointcut_executaServerSocketClass(int porta) : execution(* createServerConnection(int))
			&& args(porta) ;

	before(int porta) : my_pointcut_executaServerSocketClass(porta) {


		System.out.println(getCurrentDate() + "[ Aj ] Conectado à porta: "+porta);
	}


	/****************************************************************************
	 *
	 * my_pointcut_executaCreateClientConnection
	 *
	 */

	pointcut my_pointcut_executaCreateClientConnection() : execution(* createClientConnection(..));

	after() returning (Socket client): my_pointcut_executaCreateClientConnection() {


		System.out.println(getCurrentDate() + "[ Aj ] Nova conexão com o client em: "+client);
	}

	/****************************************************************************
	 *
	 * my_pointcut_messaging
	 *
	 */

	pointcut my_pointcut_messaging() : execution(* sendFCMNewData(..));

	after() returning(boolean cond) : my_pointcut_messaging() {

		if (cond) {

			System.out.println(getCurrentDate() + "[ Aj ] Message sent succesfully!!!!");

		} else {
			System.out.println("[Aj] Message not sent or sth else is wrong!!!!");

		}
	}

	/****************************************************************************
	 *
	 * my_pointcut_writeToFRD
	 *
	 */

	pointcut my_pointcut_writeToFRD(Sensor mSensor) : execution(* classifyDataToWriteFRD(Sensor))
			&& args(mSensor);

	before(Sensor mSensor) : my_pointcut_writeToFRD(mSensor) {

		System.out.println(getCurrentDate() + "[ Aj ] Preparing sensor data: " + mSensor);
	}

	/****************************************************************************
	 *
	 * my_pointcut_sendMsgHomeBus
	 *
	 */

	pointcut my_pointcut_sendMsgHomeBus(String msg) : execution(* sendToHomeBus(String))
			&& args(msg);

	after(String msg) : my_pointcut_sendMsgHomeBus(msg) {

		System.out.println(getCurrentDate() + "[ Aj ] Sensor data sent to Home Bus: " + msg);
	}

	/****************************************************************************
	 *
	 * my_pointcut_parseData
	 *
	 */

	pointcut my_pointcut_parseData(String szResponseLine)
			: execution(* parseData(String))
			&& args(szResponseLine);

	after(String szResponseLine) returning (Sensor mSensor): my_pointcut_parseData(szResponseLine) {

		System.out.println(getCurrentDate() + "[ Aj ] Parsing data received: " + szResponseLine);


	}


	/****************************************************************************
	 *
	 * my_pointcut_checkConnection
	 *
	 */

	pointcut my_pointcut_checkConnection() : execution(* checkConnection());

	after() returning (boolean bConnected): my_pointcut_checkConnection() {

		System.out.println(getCurrentDate() + "[ Aj ] Conexão status: " + bConnected);


	}



	public static String getCurrentDate() {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy' 'HH:mm:ss");
		String timeStamp = "["+simpleDateFormat.format(new Date()) + "]";

		return timeStamp;
	}



}
