package com.example.service_discovery_user;

import java.net.Socket;

class WriteHandlerThreadSub implements Runnable{
    private Socket client;

    public WriteHandlerThreadSub(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
//        DataOutputStream dos = null;
//        try{
//            String signedPC = null;
//            while (signedPC == null) {
//                try {
//                    signedPC = filereader.getstr("/sdcard/Download/signedSubPC.txt");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                dos = new DataOutputStream(client.getOutputStream());
//                dos.writeUTF(signedPC);
//            }

//            while(true){
//                if (Subscriber.getSendFlag()) {
//                    //向客户端回复信息
//                    System.out.print("请输入:\t");
//                    String send = Subscriber.getMessage();
//                    //发送数据
//                    dos.writeUTF(send);
//                    Subscriber.setSendFlag();
//                }
//            }
//        try {
//            while (true) {
//                dos = new DataOutputStream(client.getOutputStream());
//                //键盘录入
////                    br = new BufferedReader(new InputStreamReader(System.in));
////                    String send = br.readLine();
//                //发送数据
//                dos.writeUTF("From client");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally{
//            try{
//                if(dos != null){
//                    dos.close();
//                }
//                if(client != null){
//                    client = null;
//                }
//            }catch(Exception e){
//                e.printStackTrace();
//            }
//        }
    }
}
