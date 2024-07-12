package com;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class ComController implements Initializable {

    public ListView<String> mainListField;
    public TextField textFieldInput;
    private DataInputStream dis;
    private DataOutputStream dos;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Socket socket = new Socket("127.0.0.1", 8089);
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            Thread read = new Thread(() -> {
                try {
                    while (true) {
                       String message =  dis.readUTF();
                        Platform.runLater(() ->   mainListField.getItems().add(message));

                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            read.setDaemon(true);
            read.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(ActionEvent actionEvent) throws IOException {
        String message = textFieldInput.getText();
        dos.writeUTF(message);
        dos.flush();
        textFieldInput.clear();
    }
}
