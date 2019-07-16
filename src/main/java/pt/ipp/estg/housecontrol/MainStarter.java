package pt.ipp.estg.housecontrol;

import java.io.IOException;

import pt.ipp.estg.housecontrol.Server.ServerSocketClass;

class MainStarter {
    public static void main(String[] args) throws IOException {

        new ServerSocketClass(3000).executa();

    }
}
