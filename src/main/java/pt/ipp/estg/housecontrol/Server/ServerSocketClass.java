package pt.ipp.estg.housecontrol.Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import pt.ipp.estg.housecontrol.FirebaseClasses.SensorsFRDManaging;
import pt.ipp.estg.housecontrol.Sensors.Log;

public class ServerSocketClass {

    private boolean clientIsOff = false;

    private int porta;
    private List<PrintStream> clientsList;
    private SensorsFRDManaging sensorsFRDManaging;


    public ServerSocketClass() {
    }

    public ServerSocketClass(int porta) throws IOException {
        this.porta = porta;
        this.clientsList = new ArrayList<PrintStream>();
    }

    public void executa () throws IOException {

        ServerSocket serverSocket = createServerConnection(this.porta);

        // creates an object to be used at a call of sendToHomeBus method
        sensorsFRDManaging = new SensorsFRDManaging(this);

        /**
         * loop to wait for data to sent and also call thread to wait for receiving data
         */
        while (!clientIsOff) {

            sensorsFRDManaging.checkConnection();
            sensorsFRDManaging.getSensorChildFRD();

            Socket client = createClientConnection(serverSocket);


            // add to a list data's to send to home bus
            PrintStream ps = new PrintStream(client.getOutputStream());
            this.clientsList.add(ps);

            InputStream clientInput = client.getInputStream();

            // creates thread to treat msgs that will receive from home bus
            ThreadSensorsData threadSensorMsg = new ThreadSensorsData(clientInput, this, sensorsFRDManaging);
            new Thread(threadSensorMsg).start();
        }
    }

    /**
     * stop while loope used above to wait for actions like send data and create thread
     */
    public void setClientIsOff(boolean clientIsOff) {
        this.clientIsOff = clientIsOff;
    }

    /**
     *  send data received from mobile to Home Bus to update sensor
     */
    public void sendToHomeBus(String msg) {

        for (PrintStream client : this.clientsList) {
            client.println(msg);
        }
    }

    /**
     * cria instância do servidor  -- criada para ser capturada pelo aspectJ e fazer println
     */
    public ServerSocket createServerConnection(int porta) throws IOException {

        return new ServerSocket(porta);
    }

    /**
     *  aceita um cliente  -- criada para ser capturada pelo aspectJ e fazer println
     */
    public Socket createClientConnection(ServerSocket serverSocket) throws IOException {
        Socket client = serverSocket.accept();
        return client;
    }
}

