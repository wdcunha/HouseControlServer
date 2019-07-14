package pt.ipp.estg.housecontrol;

import java.text.SimpleDateFormat;
import java.util.Date;

import pt.ipp.estg.housecontrol.Sensors.Sensor;

public aspect SystemLog {

	/****************************************************************************
	 *
	 * my_pointcut_messaging
	 *
	 */

	pointcut my_pointcut_executaServerClass(int porta) : execution(* executa());

	before(int porta) : my_pointcut_executaServerClass() {


		System.out.println("Entering: " + thisJoinPoint);
		System.out.println(getCurrentDate() + "[ Aj ] Conectado na porta: "+porta);
	}


	/****************************************************************************
	 *
	 * my_pointcut_messaging
	 *
	 */

	pointcut my_pointcut_messaging() : execution(* sendFCMNewData());

	after() returning(Boolean cond) : my_pointcut_messaging() {

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

	pointcut my_pointcut_writeToFRD(Sensor mSensor) : execution(* prepareSensorDataToWriteFRD(Sensor))
			&& args(mSensor);

	before(Sensor mSensor) : my_pointcut_writeToFRD(mSensor) {

		System.out.println(getCurrentDate() + "[ Aj ] Preparing sensor data: " + mSensor);
	}

	/****************************************************************************
	 *
	 * my_pointcut_sendMsgHomeBus
	 *
	 */

	pointcut my_pointcut_sendMsgHomeBus(String msg) : execution(* sendMessage(String))
			&& args(msg);

	after(String msg) : my_pointcut_sendMsgHomeBus(msg) {

		System.out.println(getCurrentDate() + "[ Aj ] Client Msg sent: " + msg);
	}

	/****************************************************************************
	 *
	 * my_pointcut_parseData
	 *
	 */

	pointcut my_pointcut_parseData(String szResponseLine) : execution(* parseData(String))
			&& args(szResponseLine);

	around(String szResponseLine) : my_pointcut_parseData(szResponseLine) {

		System.out.println(getCurrentDate() + "[ Aj ] Parsing data: " + szResponseLine);
	}




	public static String getCurrentDate() {

		// SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy' 'HH:mm:ss");
		String timeStamp = "["+simpleDateFormat.format(new Date()) + "]";

		return timeStamp;
	}



}
