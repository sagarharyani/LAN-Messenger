package Util;

import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import Models.Group;
import java.util.Map;
//Singleton class
public class ClientManager {

    private static ClientManager clientManager = null;

    private static HashMap<String, ObjectOutputStream> clients;

    private ClientManager(){
        this.clients = new HashMap<>(); // again setting on init
        System.out.println("ClientManager Instance Created");

    }
    public static ClientManager getInstance(){
        if(clientManager == null){
            clientManager = new ClientManager();
        }
        return  clientManager;
    }


    public void registerClient(String username, ObjectOutputStream output){
        clients.put(username, output);
        if(!(Group.isUserGroupHashsetExists(username))) {
            System.out.println("New HashSet Creating ::");
            Group.initUserGroupHashset(username);
        }
    }

    public void deregisterClient(String username){
        if(clients.containsKey(username))
            clients.remove(username);
        else
            System.out.println("User Not found : " + username);
    }

    public ObjectOutputStream getClientOutputStream(String username) {
        if(clients.containsKey(username))
            return clients.get(username);

        return null; //throw exception
    }

}
