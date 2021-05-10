package com.example.service_discovery_user;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public Server() {
        new ServerThread().start();
    }

    class ServerThread extends Thread {
        public void run() {
            try {
                ServerSocket ss = new ServerSocket(8888);
                while (true) {
                    Socket client = ss.accept();
                    new Thread(new ReadHandlerThreadPub(client)).start();
                    new Thread(new WriteHandlerThreadPub(client)).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
