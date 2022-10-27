package Start;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public Main(){
//        launch("HelloWorld");
    }

    @Override
    public void start(Stage primaryStage) throws Exception{


        Parent root = FXMLLoader.load(getClass().getResource("/Views/login.fxml"));

        primaryStage.setTitle("Chatting Application");
        primaryStage.setScene(new Scene(root, 700, 500));
        primaryStage.show();
    }
    public static void main(String[] args) {
//        System.out.println("ARGS : " + args[0]);
        launch(args);
    }
}
