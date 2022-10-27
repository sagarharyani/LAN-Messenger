package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SentMessageController {
    @FXML
    Label sentMessageText;
    void setText(String text){
        sentMessageText.setWrapText(true);
        sentMessageText.setMaxWidth(300);
        sentMessageText.setText(text);
    }
}
