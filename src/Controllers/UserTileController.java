package Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class UserTileController {
    @FXML
    Label username;

    Integer groupId;

    Controller mainControllerRef;

    String getUsername(){
        return username.getText();
    }

    void setUsername(String name){
        username.setText(name);
    }

    void setMainControllerRef(Controller mainController){
        mainControllerRef = mainController;
    }

    void setGroupId(Integer id){
        groupId = id;
    }
    Integer getGroupId(){
        return groupId;
    }

    public void clickedHandler(MouseEvent mouseEvent)  {
        if(!getUsername().equals(mainControllerRef.getGroupName())){
            mainControllerRef.setCurrGroupId(getGroupId());
            mainControllerRef.setGroupName(getUsername());
            mainControllerRef.setAllMessagesToChatFrame(getGroupId());
        }

    }
}
