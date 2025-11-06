module com.doodlejump.doodlejump {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.doodlejump.doodlejump to javafx.fxml;
    exports com.doodlejump.doodlejump;
}