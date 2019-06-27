package pt.ipp.estg.housecontrol;

import java.text.SimpleDateFormat;
import java.util.Date;

public aspect SystemLog {

	pointcut my_pointcut_messaging()
						: execution(* sendFCMNewData());
	
	after() returning(Boolean cond) : my_pointcut_messaging() {

		if (cond){

			System.out.println(getCurrentDate()+"[ Aj ] Message sent succesfully!!!!");


		} else {
			System.out.println("[Aj] Message not sent or sth else is wrong!!!!");

		}
	}


	public static String getCurrentDate() {

		// SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy'T'HH:mm:ss'Z'");
		String timeStamp = "["+simpleDateFormat.format(new Date()) + "]";

		return timeStamp;
	}



}
