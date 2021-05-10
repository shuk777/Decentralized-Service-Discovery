package com.example.service_discovery_user;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.Date;

class WriteHandlerThreadPub implements Runnable{
    private Socket client;
    private PrivateKey serverPrivateKey;
    public WriteHandlerThreadPub(Socket client) {
        this.client = client;
        this.serverPrivateKey = AttachCallbackExt.getPrivateKey();
    }

    @Override
    public void run() {
        DataOutputStream dos = null;
        BufferedReader br = null;
        String Info = null;
        String timeStamp = null;
        String signature = null;
        String send;
        try{
            dos = new DataOutputStream(client.getOutputStream());
            Info = "Restaurant Info Here";
            timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            signature = filereader.sign(serverPrivateKey,Info + "//" + timeStamp);
            send =Info + "//" + timeStamp + "//" + signature;
            dos.writeUTF(send);
//            String signedPC = null;
//            while (signedPC == null) {
//                try {
//                    signedPC = filereader.getstr("/sdcard/Download/signedPubPC.txt");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                dos = new DataOutputStream(client.getOutputStream());
//                dos.writeUTF(signedPC);
//            }
//            String send = null;
//            while(send == null){
//                if (Publisher.getSendFlag()) {
//                    //向客户端回复信息
//                    System.out.print("请输入:\t");
//                    String send = Publisher.getMessage();
//                    //发送数据
//                    dos.writeUTF(send);
//                    Publisher.setSendFlag();
//                }


//                String sendsig = filereader.sign(filereader.getPrivateKeyFromFile("/sdcard/Download/pubpriv.pem"),send);
//                message = sendsig + "//" + send;
//                dos = new DataOutputStream(client.getOutputStream());
//                dos.writeUTF(message);
//                System.out.println(sendsig);
//            }
        }catch(Exception e){
            e.printStackTrace();
        } finally{
            try {
                if(dos != null){
                    dos.close();
                }
                if(br != null){
                    br.close();
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
