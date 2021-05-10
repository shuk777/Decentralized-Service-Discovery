package com.example.service_discovery_user;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

class ReadHandlerThreadPub implements Runnable{
    private Socket client;
    private static String receiver;

    public ReadHandlerThreadPub(Socket client) {
        this.client = client;
    }


    @Override
    public void run() {
        DataInputStream dis = null;
        try{
//            while(true){
            //读取客户端数据
//                dis = new DataInputStream(client.getInputStream());
//                receiver = dis.readUTF();
//                System.out.println("From client:" + receiver);
//                Publisher.setTextView(receiver);
//            }
        }catch(Exception e){
            e.printStackTrace();
        } finally{
            try {
                if(dis != null){
                    dis.close();
                }
                if(client != null){
                    client = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
