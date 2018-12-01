package project.csci6365.securesmsapp;

import android.os.StrictMode;

import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.lang.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Connect {
    private Socket socket;
    private DataOutputStream message_out;
    private DataInputStream  message_in;

    Connect() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    void get_ip(){
        try{
            URL url = new URL("http://projecthost.hopto.org/");
            String host = url.getHost();
            /*
            1: Create New User
            2: Get Public Key
            3: Send Message
            4: Get Message
            5: Check If Username/Pass Correct
            6: Check If User Exists
            */
            InetAddress ad = InetAddress.getByName(host);
            this.socket = new Socket(ad.getHostAddress(), 5557);
            this.message_out = new DataOutputStream(socket.getOutputStream());
            this.message_in = new DataInputStream(socket.getInputStream());
        }catch(Exception e){e.printStackTrace();}
    }

    void close_sockets(){
        try{
            message_out.close();
            message_in.close();
            socket.close();
        }catch(Exception e){System.out.println(e);}
    }

    Boolean user_exists(String name){
        String send = "6 " + name;
        String response = get_input(send);
        System.out.println(response);
        return response.equals("1");
    }

    public Boolean login(String name, String pass){
        String send = "5 " + name + " " + pass;
        String response = get_input(send);
        return response.equals("1");
    }

    public String get_messages(String name){
        String send = "4 " + name;
        return get_input(send);
    }

    void send_message(String sender, String receiver, String cipher){
        String send = "3 " + receiver + " " + sender + " " + cipher;
        String response = get_input(send);
    }

    String get_key(String name){
        String send = "2 " + name;
        return get_input(send);
    }

    void insert_user(String name, String pass, String public_key){
        String send = "1 " + name + " " + pass + " " + public_key;
        String response = get_input(send);
    }

    String get_input(String sending){
        try{
            byte[] buff = new byte[1024];
            this.message_out.write(sending.getBytes());
            int length = message_in.read(buff);
            //Get Only Length Otherwise String Ends Up Being 1024 Bytes Long And != "1" If Returning 1
            return new String(Arrays.copyOfRange(buff, 0, length), StandardCharsets.UTF_8);
        }catch(Exception e){e.printStackTrace();}
        return "";
    }
    void alert_user(String sender, String receiver) {
        String send = "7 " + sender + " " + receiver;
        String response = get_input(send);
    }
}
