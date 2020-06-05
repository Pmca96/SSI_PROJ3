package main;

import client.Client;
import server.ServerController;

public class Main {
    public static void main(String[] args) {
        if (args[0].equals("client")) {
            new Client().init(args[1]);//Meter params
        }
        if (args[0].equals("server")) {
            new ServerController(args[1]).start();
        }
    }
}
