package com.example.calculator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


import java.io.IOException;
import java.util.Objects;

public class CalculatorApplication extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CalculatorApplication.class.getResource("calculator-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        CalculatorController controller = fxmlLoader.getController();
        controller.initKeyboard(scene);

        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/calculator/images/icon.png"))));


        stage.setTitle("Calculator");
        stage.setScene(scene);
        stage.show();
    }
}
