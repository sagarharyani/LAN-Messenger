package Controllers;

import Constants.MessageType;
import Models.Group;
import Models.Message;
import Util.Client;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class Controller {

    @FXML
    public TextArea typedMessageTF;
    @FXML
    ListView<Node> users;
    @FXML
    FlowPane flowPane;
    @FXML
    Label username;
    @FXML
    Label groupName;

    Client clientRef;
    String currUsername;
    private static HashMap<Integer, Group> groups;

    // for current chat
    Integer currGroupId = -1;

    private static final String DEFAULT_CONTROL_INNER_BACKGROUND = "derive(grey, 0%)";

    public void initialize(){

    }

    void setClientRef(Client ref){
        clientRef = ref;
    }

    void setUsername(String name){
        username.setText(name);
    }

    String getUsername(){
        return username.getText();
    }

    void setCurrUsername(String name){
        currUsername = name;
    }

    void setGroupName(String name){
        groupName.setText(name);
    }
    String getGroupName(){
        return groupName.getText();
    }

    void setCurrGroupId(Integer groupId){
        currGroupId = groupId;
    }
    Integer getCurrGroupId(){
        return currGroupId;
    }
    public Group getGroup(int groupId){
        return groups.get(groupId);
    }

    void setAllMessagesToChatFrame(int groupId){

        flowPane.getChildren().clear();

        ArrayList<Message> messages = groups.get(groupId).getMessages();

        for(Message message : messages){
            if(message.getSenderUsername().equals(getUsername()))
                addSentText(message.getMessage());
            else
                addReceivedText(message.getMessage());
        }
    }

    public void addReceivedText(String message){
            FXMLLoader fxmlLoaderForReceivedText = new FXMLLoader(getClass().getResource("/Views/received_message.fxml"));
            Pane n = new Pane();
            try {
                Node receivedTextNode = fxmlLoaderForReceivedText.load();
                ReceivedMessageController receivedMessageController  = fxmlLoaderForReceivedText.getController();

                receivedMessageController.setText(message);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        flowPane.getChildren().add(receivedTextNode);
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    private void addSentText(String message){
        FXMLLoader fxmlLoaderForSentText = new FXMLLoader(getClass().getResource("/Views/sent_message.fxml"));
        Pane n = new Pane();
        try {
            Node sentTextNode = null;
            sentTextNode = fxmlLoaderForSentText.load();
            SentMessageController sentMessageController  = fxmlLoaderForSentText.getController();

            sentMessageController.setText(message);
            flowPane.getChildren().add(sentTextNode);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addChatTiles(String username, Integer groupId){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Views/userTile.fxml"));

        try{
            Node node = null;
            node = fxmlLoader.load();

            UserTileController userTileController = fxmlLoader.getController();
            userTileController.setMainControllerRef(this);
            userTileController.setUsername(username);
            userTileController.setGroupId(groupId);

            users.getItems().add(node);
        }catch(IOException e){
            System.out.println("Exception occured while adding chat tiles!");
            e.printStackTrace();
        }
    }

    public void setAllGroups(ArrayList<Group> clientGroups) {
        groups = new HashMap<>();
        for(Group group : clientGroups){
            groups.put(group.getId(), group);
        }
        addAllChatTiles(clientGroups);
    }

    public void addAllChatTiles(ArrayList<Group> clientGroups){
        for(Group group : clientGroups){
            addChatTiles(group.getName(getUsername()), group.getId());
        }
    }

    public void sendBtnHandler(MouseEvent mouseEvent) {
        //code to add message to ui:
        addSentText(typedMessageTF.getText());

        Message msg = new Message(getUsername(), getCurrGroupId(), typedMessageTF.getText(), MessageType.MESSAGE);

        // Our Local Client
        getGroup(currGroupId).addMessageToDS(msg);

        // Send Message to Receiever
        clientRef.sendMessage(msg);

    }
}



















//    void addMessageUser(ListView<HBox> users, String userName, String recentMessage, String imagePath){
//        try {
//            // 1. Add Image in Circle
//            Circle cir2 = new Circle(20);
//            Image im = new Image(new FileInputStream(imagePath));
//            cir2.setFill(new ImagePattern(im));
//
//            // 2. Creating VBox for UserName & Last Message
//            VBox vbox = new VBox();
//
//            vbox.setAlignment(Pos.CENTER_LEFT);
//            vbox.setSpacing(2);
//
//            Label name = new Label(userName);
//            name.setFont(Font.font("Ebrima", FontWeight.BOLD, 13));
//            name.setTextFill(Paint.valueOf("#FFFFFF"));
//
//            Label msg = new Label(recentMessage);
//            msg.setTextFill(Paint.valueOf("#DADADA"));
//            msg.setFont(Font.font("Ebrima", 11));
//
//            vbox.setMaxWidth(150);
//            vbox.getChildren().addAll(name, msg);
//
//            // 3. Adding Image and VBox inside HBox
//            HBox myHbox = new HBox();
//            myHbox.setAlignment(Pos.CENTER_LEFT);
//            myHbox.getChildren().addAll(cir2, vbox);
//            myHbox.setMargin(vbox, new Insets(8, 0, 8, 10));
//            myHbox.setPadding(new Insets(5, 0, 5, 5));
//
//
//            // 4. Adding HBox inside the listView
//            users.getItems().add(myHbox);
//
//        }catch(Exception fe){
//            fe.printStackTrace();
//        }
//    }


















//        users.setCellFactory(new Callback<ListView<HBox>, ListCell<HBox>>() {
//            @Override
//            public ListCell<HBox> call(ListView<HBox> todoItemListView) {
//                ListCell<HBox> cell = new ListCell<HBox>() {
//                    @Override
//                    protected void updateItem(HBox item, boolean empty) {
//                        super.updateItem(item, empty);
//                        if(empty){
//                            setText(null);
//                        }else{
//                            setText("Hello");
////                            commitEdit(item);
////                            setItem();
//                            this.setItem(item);
//                        }
//                        setStyle("-fx-background-color: #636566");
//                    }
//                };
//                return cell;
//            }
//        });

