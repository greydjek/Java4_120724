package com;

import javafx.event.ActionEvent;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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

    private String login = loginField.getText();
    private String pass = passwordField.getText();
    ComController comController = new ComController();
    AbstractMessage autentificationMessage = new AutentificationMessage(login + "@" + pass);

    public void sendMesageAutentification(ActionEvent actionEvent) throws IOException {
if (login!= null && pass!= null)
        comController.processMessage(autentificationMessage);
    }
}
