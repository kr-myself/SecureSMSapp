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
    /*
    1: Create New User
    2: Get Public Key
    3: Send Message
    4: Get Message
    5: Check If Username/Pass Correct
    6: Check If User Exists
    */
    private InetAddress ad;
    private Socket socket;
    DataOutputStream message_out;
    DataInputStream  message_in;

    public Connect() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public void get_ip(){
        try{
            System.out.println("Line 28");
            URL url = new URL("http://projecthost.hopto.org/");
            String host = url.getHost();
            System.out.println(host);
            this.ad = InetAddress.getByName(host);
            System.out.println("Line 30");
            this.socket = new Socket(ad.getHostAddress(), 5557);
            System.out.println("Line 32");
            this.message_out = new DataOutputStream(socket.getOutputStream());
            System.out.println("Line 34");
            this.message_in = new DataInputStream(socket.getInputStream());
            System.out.println("Line 36");
        }catch(Exception e){e.printStackTrace();}
    }

    public void close_sockets(){
        try{
            message_out.close();
            message_in.close();
            socket.close();
        }catch(Exception e){System.out.println(e);}
    }

    public Boolean user_exists(String name){
        String send = "6 " + name;
        String response = get_input(send);
        System.out.println(response);
        if(response.equals("1"))
            return true;
        else
            return false;
    }

    public Boolean login(String name, String pass){
        String send = "5 " + name + " " + pass;
        String response = get_input(send);
        if(response.equals("1"))
            return true;
        else
            return false;
    }

    public String get_messages(String name){
        String send = "4 " + name;
        String response = get_input(send);
        return response;
    }

    public void send_message(String sender, String receiver, String cipher){
        // TODO Server receives sender, reciever, and cipher
        // TODO String send = "3 " + sender + " " + receiver + "~" + cipher;
        String send = "3 " + receiver + " " + cipher;
        String response = get_input(send);
    }

    public String get_key(String name){
        String send = "2 " + name;
        String response = get_input(send);
        return response;
    }

    public void insert_user(String name, String pass, String public_key){
        String send = "1 " + name + " " + pass + " " + public_key;
        String response = get_input(send);
    }

    public String get_input(String sending){
        try{
            byte[] buff = new byte[1024];
            this.message_out.write(sending.getBytes());
            int length = message_in.read(buff);
            //Get Only Length Otherwise String Ends Up Being 1024 Bytes Long And != "1" If Returning 1
            String temp = new String(Arrays.copyOfRange(buff, 0, length), StandardCharsets.UTF_8);
            return temp;
        }catch(Exception e){System.out.println(e);}
        return "";
    }

    // TODO server needs to tell user the message was tampered
    public void alert_user(String sender, String receiver) {
        String send = "7 " + sender + " " + receiver;
        String response = get_input(send);
    }
}
