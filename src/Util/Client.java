package Util;

import java.io.*;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;

import Models.Message;
import Models.Group;
import Constants.MessageType;
import Controllers.Controller;
import java.util.Scanner;

public class Client {

    private String username;
    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Scanner sc;
    private Boolean flag = true;
    private Controller mainControllerRef;

    public Client(String username, Controller mainController){
        // accept the userName
        // accept ip address/name
        // accept the userName from UI
        this.username = username;
        this.mainControllerRef = mainController;
        try {
            this.socket = new Socket("127.0.0.1", 9000);
            this.output = new ObjectOutputStream(this.socket.getOutputStream());
            this.input = new ObjectInputStream(this.socket.getInputStream());
            this.sc = new Scanner(System.in);
            Message specialMessage = new Message(this.username, -1, "", MessageType.REGISTER);
            this.sendMessage(specialMessage);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUserName() {
        return username;
    }

    private void acceptMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        Message mssg = (Message)input.readObject();
                        System.out.println("I got some message");

                        Group groupRef = mainControllerRef.getGroup(mssg.getGroupId());
                        mainControllerRef.addReceivedText(mssg.getMessage());
                        groupRef.addMessageToDS(mssg);

                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }
            }
        }).start();
    }

    public void sendMessage(Message msg){
            // For first time we will accept the name of user on console
            if(flag) {
                writeMessage(msg);
                try {
                    ArrayList<Group> clientGroups = (ArrayList<Group>)input.readObject();
                    System.out.println("I got client groups");
                    mainControllerRef.setAllGroups(clientGroups);
                } catch (Exception e) {
                    System.out.println("In Regestering client");
                    e.printStackTrace();
                }

                flag = false;
                this.acceptMessage();
            }else{
                writeMessage(msg);
            }

//            Message msg;
//            if(groupId == -1) {
//                msg = new Message(username, -1, msgText, MessageType.CREATE_GROUP);
//            }
//            else
//                msg = new Message(username, groupId, msgText, MessageType.MESSAGE);
    }

    private void writeMessage(Message message){
        try {
            System.out.println("Runnning write");
            this.output.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}






