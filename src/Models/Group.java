package Models;

import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import Models.Message;
import DB.MySqlConnect;

public class Group implements Serializable {


    /**************** THIS CLASS REPRESENTS TABLE FOR THIS MODEL  *******************/
    public static class MetaData{

        public static final String TABLENAME = "groups";

        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String IS_PRIVATE_CHAT = "is_private_chat";

    }

    private static HashMap<Integer, Group> groups;
    private static HashMap<String, HashSet<Integer>> clientGroups;

    //db variables:


    private static Connection conn;
    private static PreparedStatement preparedStatement;
    private static ResultSet resultSet;





    private int id;
    private String name;
    private boolean isPrivateChat;
    private ArrayList<String> members;
    private ArrayList<Message> messages;
    private static int lastCreatedGroupId = 0;



    static{
        conn = MySqlConnect.connectDB();

    }

    public Group(String name, boolean isPrivateChat, ArrayList<String> members) {


        this.id = ++lastCreatedGroupId;
        this.name = name;
        this.isPrivateChat = isPrivateChat;
        this.members = members;
        this.messages = new ArrayList<>();

        System.out.print("Group "+ this.id + "is alloted to : ");
        for(String member : members){
            System.out.println(member + " ");
        }

    }

    public synchronized void addMessageToDS(Message msg){
        this.messages.add(msg);
    }

    public ArrayList<Message> getMessages(){
        //returning messages read only
        return (ArrayList<Message>) this.messages.clone();
    }

    public void addMember(String username){
        if(!this.isPrivateChat)
            members.add(username);
    }
    public void setMessages(ArrayList<Message> messages){
        this.messages = messages;
    }

//    public String getOppositeMember(username user){
//        if(isPrivateChat){
//            if()
//        }
//        return "GROUP";
//    }

    // ******** Getters ********
    public int getId() { return id; }

    public String getName(String username) {
        if(isPrivateChat){
            return username.equals(members.get(0)) ? members.get(1) : members.get(0);
        }

        //else we will return group name;
        return this.name;
    }

    public boolean isPrivateChat() { return isPrivateChat; }

    public ArrayList<String> getMembers() {
        //returning members read only
        return this.members;
    }




    //************ Utility Methods **********/

    //check for user exist in any group
    public static Boolean isUserGroupHashsetExists(String username){
        return clientGroups.containsKey(username);

    }

    public static void initUserGroupHashset(String username){
        clientGroups.put(username,new HashSet<Integer>());
    }

    public static void createGroup(ArrayList<String> members){

        String usernameA = members.get(0);
        String usernameB = members.get(1);
        int groupId = getGroupIdByUsernames( usernameA , usernameB );
        Group group;

        if (groupId == -1) {
            group = new Group(usernameA + usernameB, true, members);
            addGroup(group.getId(), group);
        } else {
            System.out.println("Group already created for both of you : " + groupId);
        }
    }

    //return paticluar group id of two usernames
    public static int getGroupIdByUsernames(String client1,String client2){
        HashSet<Integer> client1GroupIds = getGroupIdsByUsername(client1);
        HashSet<Integer> client2GroupIds = getGroupIdsByUsername(client2);

        //creating new hashset for storing Intersection groups ids of both users:
        HashSet<Integer> intersectionGroupIds = (HashSet<Integer>)(client1GroupIds.clone());
        intersectionGroupIds.retainAll(client2GroupIds);
        if(intersectionGroupIds.size() != 1){
            return -1;
        }
        return intersectionGroupIds.iterator().next(); //will return the firstElement in Hashset
    }

    // return all the group ids of paticular username
    public static HashSet<Integer> getGroupIdsByUsername(String username){
        //check if username exists:
        if(clientGroups.containsKey(username))
            return clientGroups.get(username);
        return new HashSet<Integer>();
    }

    //adding groups in datastructure @groups
    public static void addGroup(Integer groupId, Group group) {
        groups.put(groupId, group);
        for(String member : group.getMembers())
            updateClientsGroup(member, groupId);
    }

    //updaing list of groups for client @clientgroups
    public static void updateClientsGroup(String username, Integer groupId) {
        clientGroups.get(username).add(groupId);
    }

    public static ArrayList<Group> getClientGroups(String clientUsername){
        ArrayList<Group> singleClientGroups = new ArrayList<>();

        for(int groupId : clientGroups.get(clientUsername)){
            Group group = groups.get(groupId);
            singleClientGroups.add(group);
        }
        return singleClientGroups;
    }

    // takes group id and sender  username returns all the other members of the group
    public static ArrayList<String> getRcvrUsernamesByGroupId(int groupId, String senderUsername) {

        ArrayList<String> members;
        ArrayList<String> recievers = new ArrayList<String>();
        boolean isSenderPresent = false;
        if(groups.containsKey(groupId)){
            Group group = groups.get(groupId);
            members = group.getMembers();
            for(String reciever : members){
                if(!(reciever.equals(senderUsername))){
                    recievers.add(reciever);
                }else
                    isSenderPresent = true;
            }
        }
        if(isSenderPresent)
            return recievers;

        System.out.println("New Array List Returned!!");
        return new ArrayList<String>();
    }

    public static Group getGroup(int groupId){
        return groups.get(groupId);
    }


    //************* DATABASE METHODS **************

    // fetching from db and creating objects of group and storing it in DS
    public static void initGroups(){
        groups = new HashMap<>();
        try {

            String raw = "SELECT %s, %s, %s, t1.member_username FROM %s INNER JOIN" +
                    " (SELECT group_id, GROUP_CONCAT(DISTINCT member_username) member_username " +
                    " FROM group_member " +
                    " GROUP BY group_id) as t1 WHERE t1.group_id = %s";

            String selectQuery = String.format(raw, MetaData.NAME,
                    MetaData.IS_PRIVATE_CHAT,
                    MetaData.ID,
                    MetaData.TABLENAME,
                    MetaData.ID);

            // String selectQuery = "SELECT name, is_private_chat, id, t1.member_username FROM groups INNER JOIN" +
            //         " (SELECT group_id, GROUP_CONCAT(DISTINCT member_username) member_username " +
            //         " FROM group_member " +
            //         " GROUP BY group_id) as t1 WHERE t1.group_id = id";

            preparedStatement = conn.prepareStatement(selectQuery);
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){

                int groupId = resultSet.getInt(MetaData.ID);
                // int groupId = resultSet.getInt("id");

                boolean isPrivateChat = resultSet.getInt(MetaData.IS_PRIVATE_CHAT) == 1 ? true : false;
                // boolean isPrivateChat = resultSet.getInt("is_private_chat") == 1 ? true : false;

                String groupName = resultSet.getString(MetaData.NAME);
                // String groupName = resultSet.getString("name");

                ArrayList<String> members = new ArrayList<>();

                String membersArr[] = resultSet.getString("member_username").split(",");
                for(String member : membersArr){
                    members.add(member);
                }

                ArrayList<Message> groupMessages = Message.getMessagesByGroupId(groupId);
                Group newGroup = new Group(groupName, isPrivateChat, members);
                newGroup.setMessages(groupMessages);
//                Message tempLastMessage = groupMessages.get(groupMessages.size()-1);
//                System.out.println(tempLastMessage.getMessage() + " Sender : " + tempLastMessage.getSenderUsername());
                groups.put(groupId, newGroup);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    //storing client group details
    public static void initClientGroups(){
        clientGroups = new HashMap<>();
        try {
            String selectQuery = "SELECT member_username, GROUP_CONCAT(DISTINCT group_id) group_ids" +
                    " FROM group_member" +
                    " GROUP BY member_username";
            preparedStatement = conn.prepareStatement(selectQuery);
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){

                String clientUsername = resultSet.getString("member_username");
                HashSet<Integer> groudIds = new HashSet<>();

                String clientGroupsIds[] = resultSet.getString("group_ids").split(",");
                for(String groupId : clientGroupsIds){
                    groudIds.add(Integer.parseInt(groupId));
                }
                clientGroups.put(clientUsername, groudIds);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }


    /************       CREATE 'group' TABLE        ***************/

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
