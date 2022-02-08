package com.shutuo.menuhttpserver;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {

    private SimpleHttpServer simpleHttpServer;
    private Gson gson = new Gson();
    private  UDPManager udpManager = UDPManager.get();
    private TextView messageText ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        messageText = findViewById(R.id.main_message_text);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(simpleHttpServer==null){
            simpleHttpServer = new SimpleHttpServer(41210);
            simpleHttpServer.addListener(serverListener);
            simpleHttpServer.onStart();
        }
        udpManager.onStart(41210,this);
        messageText.setText(R.string.main_text_msg_running);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(simpleHttpServer!=null){
            simpleHttpServer.removeListener(serverListener);
            simpleHttpServer.onStop();
        }
        udpManager.onStop();
    }

    private SimpleHttpServer.ServerListener serverListener = new SimpleHttpServer.ServerListener() {
        @Override
        public void onMenuData(MenuOrderData menuData) {
            String json = gson.toJson(menuData);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    messageText.setText("Received the following message: \n\n"+json);
                }
            });
        }
    };
}