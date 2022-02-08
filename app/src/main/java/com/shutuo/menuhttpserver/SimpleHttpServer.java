package com.shutuo.menuhttpserver;


import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import fi.iki.elonen.NanoHTTPD;

public class SimpleHttpServer extends NanoHTTPD {

    private final String TAG = "SimpleHttpServer";
    private final String URI_CALL = "callnumber";
    private final String URI_MENU = "allfood";

    public SimpleHttpServer(int port) {
        super(port);
    }

    public void onStart(){
        try {
            if(!this.wasStarted()){
                this.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onStop(){
        this.onStop();
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        if(uri==null || !uri.contains(URI_CALL) || !uri.contains(URI_MENU)){
            return newFixedLengthResponse(Response.Status.NOT_FOUND,MIME_PLAINTEXT,"404 Not Found!");
        }
        String json = inputStream2String(session.getInputStream());
        Log.d(TAG, "serve() requestBody = "+json);
        if (json == null) return newFixedLengthResponse(Response.Status.BAD_REQUEST,MIME_PLAINTEXT,"body is null!");
        Gson gson = new Gson();
        if(uri.contains(URI_MENU)){
            MenuOrderData  menuData = gson.fromJson(json,MenuOrderData.class);
            onMenuListener(menuData);
        }else {
            return newFixedLengthResponse(Response.Status.OK,MIME_PLAINTEXT,"The URI address is invalid！");
        }
        return newFixedLengthResponse(Response.Status.OK,MIME_PLAINTEXT,json);
    }


    public String inputStream2String(InputStream inputStream){
        byte[] bytes = new byte[0];
        try {
            bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {

        }
        String str = new String(bytes);
        return str;
    }


    private List<ServerListener> list = new ArrayList<>();


    public void addListener(ServerListener serverListener){
        list.add(serverListener);
    }

    public void removeListener(ServerListener serverListener){
        list.add(serverListener);
    }


    public void onMenuListener(MenuOrderData data){
        for (ServerListener serverListener : list){
            serverListener.onMenuData(data);
        }
    }

    public interface ServerListener{
        void onMenuData(MenuOrderData menuData);
    }
}