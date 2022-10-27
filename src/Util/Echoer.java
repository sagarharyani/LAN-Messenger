package Util;
import Constants.MessageType;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import Util.ClientManager;
import Models.Message;
import Models.Group;

public class Echoer extends Thread{

    private Socket clientSocket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Scanner sc;
    ClientManager clientManagerRef;

    public Echoer(Socket clientSocket){
        try {
            this.clientSocket = clientSocket;
            this.clientManagerRef = ClientManager.getInstance();
            this.input = new ObjectInputStream(clientSocket.getInputStream());
            this.output = new ObjectOutputStream(clientSocket.getOutputStream());
            this.sc = new Scanner(System.in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        try{
            while(true) {

                Message msg = (Message) input.readObject();
                if (msg.getType() == MessageType.MESSAGE) {
                    System.out.println("Server Got Message ");
                    // find user:
                    System.out.println(msg.getSenderUsername() + " -------->");
                    ArrayList<String> rcvrUsernames = Group.getRcvrUsernamesByGroupId(msg.getGroupId(), msg.getSenderUsername());
                    //send message to all the rcvrNames:
                    sendMessageToRcvr(msg, rcvrUsernames);
                }
                else{
                    decodeServerMessage(msg);
                }
            }
        }catch(Exception e){
            System.out.println("Issue In Echoer exception: ");
            e.printStackTrace();
        }finally{
            try{
                clientSocket.close();
            }catch(IOException e){
                System.out.println("Issue while close in echoer: " + e.getMessage());
            }
        }
    }

    private void decodeServerMessage(Message msg) {

        MessageType messageType = msg.getType();


        switch(messageType) {


            //sending  all the group objects of the client as a response message.
            //storing the ouput streams in the ds of clientmanager class.
            case REGISTER:
                System.out.println("registering "+ this.output);
                this.clientManagerRef.registerClient(msg.getSenderUsername(), this.output);
                ArrayList<Group> clientGroups = Group.getClientGroups(msg.getSenderUsername());
                try{
                    this.output.writeObject(clientGroups);
                }catch (Exception e){
                        System.out.println("Problem occurs while send groups to client");
                        e.printStackTrace();
                }


                break;
            case CREATE_GROUP:

                ArrayList<String> members = new ArrayList<>();
                // msg.getMesaage constains the username of other member
                members.add(msg.getMessage());
                members.add(msg.getSenderUsername());
                Group.createGroup(members);
                break;
        }

    }

    private void sendMessageToRcvr(Message msg, ArrayList<String> rcvrUsernames)throws IOException{

        for (String rcvrUsername : rcvrUsernames) {
            //getting output stream:S
            // create static method for below code
            System.out.println("forwarding message to rcvr " + rcvrUsername);
            ObjectOutputStream rcvrOutput = clientManagerRef.getClientOutputStream(rcvrUsername);
            if(rcvrOutput != null){
                rcvrOutput.writeObject(msg);
            }

            Group groupRef = Group.getGroup(msg.getGroupId());
            groupRef.addMessageToDS(msg);
            Message.addMessage(msg);
        }
    }

}

