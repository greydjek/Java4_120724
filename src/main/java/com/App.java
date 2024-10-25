package com;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    Autentification autentification;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent client = FXMLLoader.load(getClass().getResource("Client.fxml"));
        Parent aut = FXMLLoader.load(getClass().getResource("Autentification.fxml"));
        primaryStage.setScene(new Scene(aut));
        primaryStage.show();
     //   autentification.buttonAutentificftion.setOnAction(e -> primaryStage.setScene(new Scene(client)));

    }
}
