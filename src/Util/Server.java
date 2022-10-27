package Util;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import Models.Group;
import Models.Message;

public class Server {

    public static void main(String[] args)
    {
        try
        {
            ServerSocket server = new ServerSocket(9000);
            System.out.println("Server just started");

            Group.initGroups();
            Group.initClientGroups();

            while(true)
            {
                Socket socket = server.accept();
                new Echoer(socket).start();
                System.out.println("New Echoer instance Created!!");
            }
        }
        catch(IOException e)
        {
            System.out.println("Issue In Server class: "+ e.getMessage());
        }
    }
}
