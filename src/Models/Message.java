package Models;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import Constants.MessageType;
import DB.MySqlConnect;

public class Message implements Serializable {



    /**************** THIS CLASS REPRESENTS TABLE FOR THIS MODEL  *******************/
    public static class MetaData{

        public static final String TABLENAME = "messages";

        public static final String ID = "id";
        public static final String MESSAGE = "message";
        public static final String SENDER_USERNAME = "sender_username";
        public static final String GROUP_ID = "group_id";
        public static final String DATE_TIME = "dateTime";

    }



    // db variables

    private static Connection conn;
    private static PreparedStatement preparedStatement;
    private static ResultSet resultSet;

    private String message;
    private String senderUsername;
    private int groupId;
    private LocalDateTime dateTime;
    private MessageType type; //add enum

    static{
        conn = MySqlConnect.connectDB();
    }

    public Message(String senderUsername, int groupId, String message,MessageType type){
        //create localDate instance of now

        this.senderUsername = senderUsername;
        this.groupId = groupId;
        this.message = message;
        this.type = type;
    }
    public String getMessage(){ return this.message; }
    public int getGroupId(){ return this.groupId; }
    public String getSenderUsername(){ return this.senderUsername; }
    public MessageType getType() { return type; }


    // DATABASE METHODS
    public static ArrayList<Message> getMessagesByGroupId(int groupId){

        String raw = "SELECT * FROM %s WHERE %s = ?";
        String query = String.format(raw, MetaData.TABLENAME, MetaData.GROUP_ID);

        ArrayList<Message> messages = new ArrayList<>();


        try{
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, groupId);

            resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){

                String senderUsername = resultSet.getString(MetaData.SENDER_USERNAME);
                // String senderUsername = resultSet.getString("sender_username");

                String message = resultSet.getString(MetaData.MESSAGE);
                // String message = resultSet.getString("message");

                Message newMessage = new Message(senderUsername, groupId, message, MessageType.MESSAGE);
                messages.add(newMessage);
            }
        }catch(SQLException se){
            System.out.println("Exception while generating messages : ");
            se.printStackTrace();
        }
        return messages;
    }


    public static void addMessage(Message message){
        String raw = "INSERT INTO %s ( %s, %s, %s) VALUES (?, ?, ?);";
        String query = String.format(raw, MetaData.TABLENAME, MetaData.GROUP_ID, MetaData.SENDER_USERNAME, MetaData.MESSAGE);

        try {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, message.getGroupId());
            ps.setString(2, message.getSenderUsername());
            ps.setString(3, message.getMessage());

            ps.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    /************       CREATE 'messages' TABLE        ***************/

    public static void createTable(){
        try {

            String raw = "CREATE TABLE IF NOT EXISTS %s ();"; 			//incomplete query
            String query = String.format(raw, MetaData.TABLENAME);

            PreparedStatement ps = conn.prepareStatement(query);
            ps.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}
