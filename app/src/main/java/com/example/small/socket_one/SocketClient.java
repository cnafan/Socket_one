package com.example.small.socket_one;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by small on 2016/5/27.
 */
public class SocketClient {
    static Socket client;

    public SocketClient(String site, int port){
        Log.d("qiang", site);
        try{
            client = new Socket(site,port);
            System.out.println("Client is created! site:"+site+" port:"+port);
            Log.d("qiang", site);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public String sendMsg(String msg){
        String txt = "";
        try{
            Log.d("qiang",msg);
            OutputStream outputStream;
            //Socket输出流
            outputStream = client.getOutputStream();
            outputStream.write(msg.getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    client.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                txt += line + "\n";
            }
            in.close();

        }catch(IOException e){
            e.printStackTrace();
        }
        return txt;
    }

    public void closeSocket(){
        try{
            client.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws Exception{

    }

}
