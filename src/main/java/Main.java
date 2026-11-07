import models.Server;

import java.io.*;
public class Main {
    public static void main(String[] args) {
        try {
            Server server = new Server("localhost", 9889);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}