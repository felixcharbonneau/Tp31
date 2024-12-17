module com.example.tp2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens TP3 to javafx.fxml;
    exports TP3;
}