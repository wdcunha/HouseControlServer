package pt.ipp.estg.housecontrol;

public aspect SystemLog {

	pointcut my_pointcut_messaging(CloudMessaging sendMessage)
						: execution(* sendMessage(title, body))
						&& args(mClienteRecetorArg) && target(clientTargetIniciador);
	
	after( returning() : my_pointcut_messaging( ) {
		


		
		}


}
