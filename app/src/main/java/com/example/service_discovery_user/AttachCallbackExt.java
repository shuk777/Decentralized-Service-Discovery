package com.example.service_discovery_user;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.NetworkSpecifier;
import android.net.wifi.aware.AttachCallback;
import android.net.wifi.aware.DiscoverySessionCallback;
import android.net.wifi.aware.PeerHandle;
import android.net.wifi.aware.PublishConfig;
import android.net.wifi.aware.PublishDiscoverySession;
import android.net.wifi.aware.WifiAwareNetworkSpecifier;
import android.net.wifi.aware.WifiAwareSession;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.ECGenParameterSpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class AttachCallbackExt extends AttachCallback {
    private static PublishDiscoverySession discoverySession;
    final static String serviceName = "RestaurantInfo";
    final static String serviceID = "123456678";
    final static List<byte[]> list = Arrays.asList(serviceID.getBytes(), "Pref".getBytes());
    static PublicKey publicKey = null;
    static PrivateKey privateKey = null;
    public AttachCallbackExt() {
        System.out.println("AttachCallback object for Publisher created");
    }

    public static PublicKey getPublicKey(){
        return publicKey;
    }

    public static PrivateKey getPrivateKey(){
        return privateKey;
    }
    @Override
    public void onAttached(WifiAwareSession awareSession) {

        String sendsig = null;
        String sendpub = null;
        String msg = serviceID + "//" + serviceName+ "//" + "Information";
        String pubString = null;
        ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp224r1");
        KeyPairGenerator g = null;
        try {
            g = KeyPairGenerator.getInstance("EC");
            g.initialize(ecSpec, new SecureRandom());
            KeyPair keypair = g.generateKeyPair();
            publicKey = keypair.getPublic();
            privateKey = keypair.getPrivate();
            pubString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            msg = msg + "//" + pubString;
            sendsig = filereader.sign(filereader.getPrivateKeyFromFile("/sdcard/Download/prime256v1key.pem"),msg); //模拟CA签发简化证书
            System.out.println("sign: " + sendsig);

        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
        System.out.println("PUBLIC KEY :" + pubString);
        String msgString = msg;//第一次发消息：服务ID，服务名，公钥，
        System.out.println(msgString);
        byte[] encodedMsg = Base64.getEncoder().encode(msgString.getBytes());
        Log.d("Method called", "Attach operation completed and can now start discovery sessions");
            PublishConfig config = new PublishConfig.Builder()
                    .setServiceName(serviceName)
                    .setMatchFilter(list)
                    .setServiceSpecificInfo(encodedMsg)
                    .build();
        String finalSendsig = sendsig;
        awareSession.publish(config, new DiscoverySessionCallback() {
                @Override
                public void onPublishStarted(PublishDiscoverySession session) {
                    Log.d("Publish", "Successfully published");
                    System.out.println("Publish Start");
                    discoverySession = session;
                }

                @Override
                public void onServiceDiscovered(PeerHandle peerHandle,
                                                byte[] serviceSpecificInfo, List<byte[]> matchFilter) {
                    System.out.println("onServiceDiscovered");
                }

                @Override
                public void onMessageReceived (PeerHandle peerHandle,
                                               byte[] message){
                    discoverySession.sendMessage(peerHandle,1, Base64.getEncoder().encode(finalSendsig.getBytes()));
                    Log.d("Method called", "Connection built");
                    System.out.println("Message Received");
                    String messagestr = message.toString();
                    System.out.println(messagestr);
                    new Server();
                    System.out.println("Socket established");
                    NetworkSpecifier networkSpecifier = new WifiAwareNetworkSpecifier.Builder(discoverySession, peerHandle)
                            .setPskPassphrase("EP2800123456")
                            .setPort(8888)
                            .build();
                    NetworkRequest myNetworkRequest = new NetworkRequest.Builder()
                            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI_AWARE)
                            .setNetworkSpecifier(networkSpecifier)
                            .build();
                    System.out.println("Network request success");
                    ConnectivityManager connMgr = (ConnectivityManager) BaseApplication.getContext().getSystemService(CONNECTIVITY_SERVICE);
                    ConnectivityManager.NetworkCallback callback = new ConnectivityManager.NetworkCallback() {
                        @Override
                        public void onAvailable(Network network) {
                            Log.d("Publish", "Connection Available");
                            System.out.println("Connection Available");
//                            connectFlag = false;
                        }

                        @Override
                        public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
                            Log.d("Publish", "Status changed");
                            System.out.println("Status Changed");
//                            connectFlag = false;
                        }

                        @Override
                        public void onLost(Network network) {
                            Log.d("Publish", "Service Lost");
                        }
                    };
                    connMgr.requestNetwork(myNetworkRequest, callback);
                }
            }, null);
            //Attach end
        }
    /*
     * Called when Aware attach operation
     * {@link WifiAwareManager#attach(AttachCallback, android.os.Handler)} failed.
     */
    @Override
    public void onAttachFailed() {
        System.out.println("Attach operation failed");

    }
}


