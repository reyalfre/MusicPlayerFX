module com.alfredo.musicplayerfx {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.alfredo.musicplayerfx to javafx.fxml;
    exports com.alfredo.musicplayerfx;
    exports com.alfredo.musicplayerfx.controller;
    opens com.alfredo.musicplayerfx.controller to javafx.fxml;
}