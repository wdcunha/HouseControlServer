package pt.ipp.estg.housecontrol;

import java.io.IOException;

import pt.ipp.estg.housecontrol.Server.ServerClass;

class MainStarter {
    public static void main(String[] args) throws IOException {

        new ServerClass(3000).executa();
    }
}
