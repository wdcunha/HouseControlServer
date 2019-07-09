package pt.ipp.estg.housecontrol.Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import pt.ipp.estg.housecontrol.*;

public class ServerClass {

    private boolean clientIsOff = false;

    private int porta;
    private List<PrintStream> clientsList;

    public ServerClass(int porta) {
        this.porta = porta;
        this.clientsList = new ArrayList<PrintStream>();
    }

    public void executa () throws IOException {
        ServerSocket serverSocket = new ServerSocket(this.porta);
        System.out.println("Porta 12345 aberta!");

        while (!clientIsOff) {
            // aceita um cliente
            Socket cliente = serverSocket.accept();
            System.out.println("Nova conexão com o cliente " +     
                cliente.getInetAddress().getHostAddress()
            );

            // adiciona envio de msg ao cliente à lista
            PrintStream ps = new PrintStream(cliente.getOutputStream());
            this.clientsList.add(ps);

            InputStream clientInput = cliente.getInputStream();

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

