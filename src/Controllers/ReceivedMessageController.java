package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ReceivedMessageController {

    @FXML
    Label receivedMessageText;
    void setText(String text){
        receivedMessageText.setWrapText(true);
        receivedMessageText.setMaxWidth(300);
        receivedMessageText.setText(text);
    }
}
