package Controllers;

import Util.Client;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;


public class LoginController {

    @FXML
    TextField username;
    @FXML
    Button loginBtn;

    @FXML
    void clicked(){
        String userName = username.getText();
        try{

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Views/sample.fxml"));
            Parent root = fxmlLoader.load();
            Controller controller = fxmlLoader.getController();
            controller.setUsername(userName);
            Stage stage = (Stage) username.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);

            Client client = new Client(userName, controller);
            controller.setClientRef(client);
            System.out.println("New Client Created From Login Contr");

        }catch (IOException ie){
            System.out.println("While Clicking Login BTN");
            ie.printStackTrace();
        }

    }
}



