package com.shutuo.menuhttpserver;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UDPManager {

    private final String TAG = "UDPManager";
    private DatagramSocket rcvSocket = null;
    private DatagramSocket sendSocket = null;
    private final int BROADCAST_PORT_RCV = 38465;
    private final int BROADCAST_PORT_SEND = 38463;
    private static final String BROADCAST_ADDRESS = "255.255.255.255";
    private final String RCV_TOKEN = "tell me you ip";
    private int serverPort ;
    private Gson gson = new Gson();
    private UDPManager(){
    }
    private static UDPManager instance;

    public static UDPManager get(){
        if(instance!=null) return instance;
        synchronized (UDPManager.class){
            if (instance==null){
                instance = new UDPManager();
            }
        }
        return instance;
    }
    private volatile boolean oldRCVRunningBool = true;
    private Context context;
    public void onStart(int port , Context context1){
        serverPort = port;
        this.context =context1;
        Log.d(TAG,"init()");;
        oldRCVRunningBool = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                oldRCVMessage();
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendHttpServer();
            }
        }).start();

    }



    private void sendBroadcast(String msg) {
        try {
            msg = new JSONObject(msg).toString();
            Log.d(TAG, "[UDPManager] sendUdpMessage  accept msg:" + msg);
            sendSocket = new DatagramSocket(null);
            sendSocket.setReuseAddress(true);
            sendSocket.setBroadcast(true);
            sendSocket.bind(new InetSocketAddress(BROADCAST_PORT_SEND));
            byte[] allBytes = msg.getBytes(StandardCharsets.UTF_8);
            InetAddress address = InetAddress.getByName(BROADCAST_ADDRESS);
            DatagramPacket datagramPacket = new DatagramPacket(allBytes, allBytes.length, address, BROADCAST_PORT_SEND);
            sendSocket.send(datagramPacket);
        } catch (Exception e) {
            Log.e(TAG, "[SeedMessage] Exception:" + e);
        }
    }

    private void oldRCVMessage() {
        if(rcvSocket!=null) return;
        try {
            rcvSocket = new DatagramSocket(null);
            rcvSocket.setReuseAddress(true);
            rcvSocket.setBroadcast(true);
            rcvSocket.bind(new InetSocketAddress(BROADCAST_PORT_RCV));
            byte[] message = new byte[2048*8];
            DatagramPacket mDatagramPacket = new DatagramPacket(message, message.length);
            while (oldRCVRunningBool) {
                try {
                    if(rcvSocket==null) continue;
                    rcvSocket.receive(mDatagramPacket);
                    byte[] ipBuffer = mDatagramPacket.getData();
                    String data = new String(ipBuffer,0,FloatUtil.returnActualLength(ipBuffer));
                    Log.d(TAG, "subscribe() RCV = "+data);
                    JSONObject jsonObject = new JSONObject(data);
                    int udpType = jsonObject.optInt("udpType",0);
                    String msg = jsonObject.optString("msg","");
                    if(udpType == UDP_RCV_TYPE && TextUtils.equals(msg,RCV_TOKEN)){
                        sendHttpServer();
                    }else {
                        MsgDataV1 msgDataV1 = new MsgDataV1();
                        msgDataV1.msg = UDP_ERROR_MSG;
                        String json = gson.toJson(msgDataV1);
                        sendBroadcast(json);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "oldRCVMessage() Exception:" + e);
                }
            }
            if(!oldRCVRunningBool){
                rcvSocket.close();
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }


    private void sendHttpServer() {
        MsgDataV1 msgDataV1 = new MsgDataV1();
        msgDataV1.udpType = UDP_SEND_TYPE;
        msgDataV1.msg = getMsgIp();
        String json = gson.toJson(msgDataV1);
        sendBroadcast(json);
    }

    private boolean isIP(String str){
        if(str.length()<7 || str.length() >15) return false;
        String[] arr = str.split("\\.");
        if( arr.length != 4 )    return false;
        for(int i = 0 ; i <4 ; i++ ){
            if (!isNUM(arr[i]) || arr[i].length()==0 || Integer.parseInt(arr[i])>255 || Integer.parseInt(arr[i])<0){
                return false;
            }
        }
        return true;
    }

    private boolean isNUM(String str){
        Pattern p = Pattern.compile("[0-9]*");
        Matcher m = p.matcher(str);
        return m.matches();
    }



    public void onStop(){
        if (rcvSocket!=null){
            rcvSocket.close();
            rcvSocket = null;
        }
        if(sendSocket!=null){
            sendSocket.close();
            sendSocket = null;
        }
        oldRCVRunningBool = false;
    }


    private String getMsgIp(){
        HttpData httpData = new HttpData();
        httpData.version = "2.0";
        httpData.HttpServerIp = IpUtils.getIpAddress(context);
        httpData.HttpServerPort = serverPort;
        return gson.toJson(httpData);
    }
    public static class MsgDataV1{
        String version = "2.0";
        String msg;
        int udpType;
    }

    public class HttpData
    {
        public String HttpServerIp ;
        public int HttpServerPort ;
        public String version ;
    }

    private final int UDP_RCV_TYPE = 999;
    private final int UDP_SEND_TYPE = 888;
    private final String UDP_ERROR_MSG = "The parameter is invalid. Please send the message " +
            "in the format defined in the document";

}
