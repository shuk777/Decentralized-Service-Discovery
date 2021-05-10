package com.example.service_discovery_user;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.NetworkSpecifier;
import android.net.wifi.aware.AttachCallback;
import android.net.wifi.aware.DiscoverySessionCallback;
import android.net.wifi.aware.PeerHandle;
import android.net.wifi.aware.SubscribeConfig;
import android.net.wifi.aware.SubscribeDiscoverySession;
import android.net.wifi.aware.WifiAwareNetworkInfo;
import android.net.wifi.aware.WifiAwareNetworkSpecifier;
import android.net.wifi.aware.WifiAwareSession;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import static android.content.Context.CONNECTIVITY_SERVICE;


public class AttachCallbackSub extends AttachCallback {
    private static SubscribeDiscoverySession subscribeDiscoverySession;
    static boolean subFlag = true;
    final static String serviceName = "RestaurantInfo";
    final static String serviceID = "123456678";
    final static List<byte[]> list = Arrays.asList(serviceID.getBytes(), "Pref".getBytes());
    private byte[] message = "Information type".getBytes();
    static String msg = null;
    private  static PublicKey serverPubKey = null;
    static String receivedMsg = null;
    static boolean networkFlag = true;

    public int peerPort;
    public Inet6Address peerIpv6;
    public Socket socket;
    public PeerHandle subPeerHandle;
    private Network subNet;
    Context context;

    public AttachCallbackSub() {
        System.out.println("AttachCallback object for subscriber created");
    }

    public static PublicKey getServerPubKey(){
        return serverPubKey;
    }
    /*
     * Called when Aware attach operation
     * {@link WifiAwareManager#attach(AttachCallback, android.os.Handler)}
     * is completed and that we can now start discovery sessions or connections.
     *
     * @param session The Aware object on which we can execute further Aware operations - e.g.
     *                discovery, connections.
     */
    @Override
    public void onAttached(WifiAwareSession awareSession) {
        Log.d("Method called", "Attach operation completed and can now start discovery sessions");
        System.out.println("Database created");
        final SubscribeConfig config = new SubscribeConfig.Builder()
                .setMatchFilter(list)
                .setServiceName(serviceName)
                .build();
            subFlag = false;
            awareSession.subscribe(config, new DiscoverySessionCallback() {
                @Override
                public void onSubscribeStarted(SubscribeDiscoverySession session) {
                    System.out.println("Subscribe Started");
                    subscribeDiscoverySession = session;
                }

                @RequiresApi(api = Build.VERSION_CODES.Q)
                @Override
                public void onServiceDiscovered(PeerHandle peerHandle, byte[] serviceSpecificInfo, List<byte[]> matchFilter) {
                    receivedMsg = new String(Base64.getDecoder().decode(serviceSpecificInfo));
                    System.out.println("Service Info:" + receivedMsg);
                    Get receiver = new Get(receivedMsg);
                    String serviceID = receiver.getServiceID();
                    String serviceName = receiver.getServiceName();
                    String serviceInfo = receiver.getServiceInfo();
                    byte[] encoded = Base64.getMimeDecoder().decode(receiver.getPubKey());
                    KeyFactory kf = null;
                    try {
                        kf = KeyFactory.getInstance("EC");
                        serverPubKey = (PublicKey) kf.generatePublic(new X509EncodedKeySpec(encoded));
                    } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    msg = serviceID + "//" + serviceName + "//" + serviceInfo;
                    byte[] msgToPublisher = "Messagehere".getBytes();
                    subscribeDiscoverySession.sendMessage(peerHandle,1,msgToPublisher);
                }
                @Override
                public void onMessageReceived (PeerHandle peerHandle,
                                               byte[] message){
                    System.out.println("Signature" + new String(Base64.getDecoder().decode(message)));
                    String signature = new String(Base64.getDecoder().decode(message));
                    boolean connectFlag = false;
                    try {
                        PublicKey caPubKey = filereader.getPublicKeyFromFile("/sdcard/Download/prime256v1pub.pem");
                        if(filereader.verify(caPubKey,receivedMsg,signature)){
                            System.out.println("Verify Success!");
                            connectFlag = true;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    }
                    if(connectFlag){
                        NetworkSpecifier networkSpecifier = new WifiAwareNetworkSpecifier.Builder(subscribeDiscoverySession, peerHandle)
                            .setPskPassphrase("EP2800123456")
                            .build();
                        NetworkRequest myNetworkRequest = new NetworkRequest.Builder()
                            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI_AWARE)
                            .setNetworkSpecifier(networkSpecifier)
                            .build();
                        System.out.println("Network request success");
                        ConnectivityManager connMgr = (ConnectivityManager) BaseApplication.getContext().getSystemService(CONNECTIVITY_SERVICE);
                        System.out.println("Manager on");
                        ConnectivityManager.NetworkCallback callback = new ConnectivityManager.NetworkCallback() {
                            @Override
                            public void onAvailable(Network network) {
                                Log.d("Subscribe", "Test Connection Available");
                                System.out.println("Test Subscribe Available");
                                subNet = network;
                            }

                            @Override
                            public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
                                Log.d("Subscribe", " Test Status changed");
                                System.out.println("Status Changed");
                                if(networkFlag) {
                                    WifiAwareNetworkInfo peerAwareInfo = (WifiAwareNetworkInfo) networkCapabilities.getTransportInfo();
                                    subNet = network;
                                    peerIpv6 = peerAwareInfo.getPeerIpv6Addr();
                                    peerPort = peerAwareInfo.getPort();
                                    System.out.println("peer ip:" + peerIpv6);
                                    System.out.println("peer port:" + peerPort);
                                    networkFlag = false;
                                    try {
                                        socket = subNet.getSocketFactory().createSocket(peerIpv6, peerPort);
                                        System.out.println("socket built");
                                        new Thread(new ReadHandlerThreadSub(socket)).start();
                                        new Thread(new WriteHandlerThreadSub(socket)).start();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onLost(Network network) {
                                Log.d("Subscribe", "Service Lost");
//                          subscribeDiscoverySession.sendMessage(subPeerHandle, 1, message);
                            }
                        };
                        connMgr.requestNetwork(myNetworkRequest, callback);
                    }
                }
            }, null);
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

