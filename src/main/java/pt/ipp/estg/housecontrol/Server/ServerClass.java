package pt.ipp.estg.housecontrol.Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import pt.ipp.estg.housecontrol.FirebaseClasses.DataBaseFRDManaging;

import static pt.ipp.estg.housecontrol.FirebaseClasses.DataBaseFRDManaging.getAllUsers;

public class ServerClass {

    private boolean clientIsOff = false;

    private int porta;
    private List<PrintStream> clientsList;
//    private DataBaseFRDManaging dataBaseFRDManaging;

    public ServerClass(int porta) throws IOException {
        this.porta = porta;
        this.clientsList = new ArrayList<PrintStream>();
//        dataBaseFRDManaging = new DataBaseFRDManaging();
    }

    public void executa () throws IOException {
        ServerSocket serverSocket = new ServerSocket(this.porta);
        System.out.println("Conectado na porta: "+porta);


        while (!clientIsOff) {

//            dataBaseFRDManaging.getAllUsers();
//            dataBaseFRDManaging.getUserFRD();
//            dataBaseFRDManaging.writeUserFRD();

            // aceita um client
            Socket client = serverSocket.accept();
            System.out.println("Nova conexão com o client " +
                client.getInetAddress().getHostAddress()
            );


            // adiciona envio de msg ao client à lista
            PrintStream ps = new PrintStream(client.getOutputStream());
            this.clientsList.add(ps);

            InputStream clientInput = client.getInputStream();

            // cria tratamento de msgs recebidas numa nova thread
            TreatSensorsMsg tc = new TreatSensorsMsg(clientInput, this);
            new Thread(tc).start();
        }
    }

    public void setClientIsOff(boolean clientIsOff) {
        this.clientIsOff = clientIsOff;
    }

    public void sendMessage(String msg) {

        for (PrintStream client : this.clientsList) {
//            msg = "[Server sent] " + msg;
            client.println(msg);
            System.out.println("[Client Msg sent] "+msg);

        }
    }
}

