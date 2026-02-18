module com.example.calculator {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.axiomdevv.calculator to javafx.fxml;
    exports com.axiomdevv.calculator;
}