package project.csci6365.securesmsapp;

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

    public void get_ip(){
        try{
            this.ad = InetAddress.getByName(new URL ("http://projecthost.hopto.org/").getHost());
            this.socket = new Socket(ad.getHostAddress(), 5557);
            this.message_out = new DataOutputStream(socket.getOutputStream());
            this.message_in = new DataInputStream(socket.getInputStream());
        }catch(Exception e){System.out.println(e);}
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

    public void send_message(String name, String cipher){
        String send = "3 " + name + " " + cipher;
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
}
