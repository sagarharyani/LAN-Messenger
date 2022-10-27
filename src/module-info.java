module chat.application.javafx {

    requires java.desktop;
    requires java.sql;
    requires javafx.fxml;
    requires javafx.controls;

    opens Start;
    opens Controllers;

}