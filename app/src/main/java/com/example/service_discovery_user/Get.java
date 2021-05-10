package com.example.service_discovery_user;

import java.util.Scanner;

public class Get {
    public String id;
    public String pubKey;
    public int flag;
    public long currentTime;
    public long endTime;
    public String signature;
    public String PCAcert;
    public String PC;
    public String serviceID;
    public String serviceName;
    public String serviceInfo;
    public Get(String signedPC){
        Scanner scan = new Scanner(signedPC);
        scan.useDelimiter("//");
        this.serviceID = scan.next();
        this.serviceName = scan.next();
        this.serviceInfo = scan.next();
        this.pubKey = scan.next();
        scan.close();
    }
    public String getServiceID(){
        return this.serviceID;
    }
    public String getServiceName(){
        return this.serviceName;
    }
    public String getServiceInfo(){
        return this.serviceInfo;
    }
    public String getPubKey(){return this.pubKey;}
}
