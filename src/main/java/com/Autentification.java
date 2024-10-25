package com;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import messageWorker.AbstractMessage;
import messageWorker.AutentificationMessage;

import java.io.IOException;

@Getter
@Setter
public class Autentification {

    public TextField loginField;
    public PasswordField passwordField;
    public AnchorPane autentifControl;
    public Button buttonAutentificftion;
    private String login;
    private String pass;
    ComController comController = new ComController();

    public void sendMesageAutentification(ActionEvent actionEvent) throws IOException {
        Platform.runLater(() -> {
            try {
                autMessage();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void autMessage() throws IOException {
        login = loginField.getText();
        pass = passwordField.getText();
        if (login != null && pass != null) {
            AbstractMessage autentificationMessage = new AutentificationMessage(login + "@" + pass);
            comController.processMessage(autentificationMessage);
            switchClientScene();
        } else {
            System.err.println(" inter login and pass ");
        }
    }
    public Scene loadClientScene() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Client.fxml"));
        Parent client = loader.load();
        return new Scene(client);
    }
    public void switchClientScene() throws IOException {
        Scene newScene = loadClientScene();
        Stage primaryStage = (Stage) autentifControl.getScene().getWindow();
        primaryStage.setScene(newScene);
        primaryStage.show();
    }
}
