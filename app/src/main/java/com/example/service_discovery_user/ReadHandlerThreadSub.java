package com.example.service_discovery_user;

import android.net.wifi.aware.AttachCallback;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Scanner;

class ReadHandlerThreadSub implements Runnable{
    private Socket client;
    private static String receiver;
    private static String message;
    public static PublicKey serverPublicKey = null ;
    private static String Info;
    private static String serverTimeStamp;
    private static String signature;
    private static boolean readFlag = true;
    private static boolean verifyFlag = false;


    public ReadHandlerThreadSub(Socket client) {

        this.client = client;
        this.serverPublicKey = AttachCallbackSub.getServerPubKey();
    }
    @Override
    public void run() {
        DataInputStream dis = null;
        try{
            while(true){
                dis = new DataInputStream(client.getInputStream());
                receiver = dis.readUTF();
                String userTimeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                System.out.println("Received Msg :" + receiver);
                Scanner scan = new Scanner(receiver);
                scan.useDelimiter("//");
                Info = scan.next();
                serverTimeStamp = scan.next();
                signature = scan.next();
                scan.close();
                if(filereader.verify(serverPublicKey,Info + "//" + serverTimeStamp, signature)){
                    System.out.println("Info verify Success");
                    long userTime = Long.parseLong(userTimeStamp);
                    System.out.println(userTime);
                    long serverTime = Long.parseLong(serverTimeStamp);
                    if(Math.abs(serverTime - userTime)<5){
                        System.out.println("Timestamp Check pass");
                    }
                }
//                String sig = scan.next();
//                message = scan.next();
//                verifyFlag = filereader.verify(filereader.getPublicKeyFromFile(pubPublic),message,sig);
//                if (verifyFlag){
//                    System.out.println("Verify Success");
//                }else {
//                    message = null;
//                    System.out.println("Verify failed");
//                }
//                System.out.println("From server:" + message);
////                    if (receiver != null) {
////                        readFlag = false;
////                    }
////                Subscriber.setTextView(receiver);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
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
