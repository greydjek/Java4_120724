
package com;
import common.AbstractMessage;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ComController implements Initializable {
    private byte[] buffer;
    private Path com;
    public ListView<String> mainListField;
    public TextField textFieldInput;
    private ObjectDecoderInputStream dis;
    private ObjectEncoderOutputStream dos;
    private ComOutFiles comOutFiles;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Socket socket = new Socket("127.0.0.1", 8189);
            dos = new ObjectEncoderOutputStream(socket.getOutputStream());
            dis = new ObjectDecoderInputStream(socket.getInputStream());
            Thread read = new Thread(() -> {
                try {
                    while (true) {
                        AbstractMessage message = (AbstractMessage) dis.readObject();
                        Platform.runLater(() -> mainListField.getItems().add(message.toString()));

                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            });
            //showFiles();

            read.setDaemon(true);
            read.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(ActionEvent actionEvent) throws IOException {
        String string = textFieldInput.getText();
        textFieldInput.clear();
        dos.writeObject(new AbstractMessage(string) );
        dos.flush();
    }

    public void showFiles() {
        File folder = new File("/com");
        for (File file : folder.listFiles()) {
            String s = file.getName();
            mainListField.getItems().add(s);
        }
    }

    private void fuulFilesView() throws IOException {
        mainListField.getItems().clear();
        List<String> list = Files.list(com).map(p -> p.getFileName().
                toString()).collect(Collectors.toList());
        mainListField.getItems().addAll(list);
    }
}
