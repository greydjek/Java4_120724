package com;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import messageWorker.AbstractMessage;
import messageWorker.ListMessage;


import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ComController implements Initializable {
    private Path carentDir;
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
            carentDir = Paths.get("client");
            Socket socket = new Socket("127.0.0.1", 8189);
            dos = new ObjectEncoderOutputStream(socket.getOutputStream());
            dis = new ObjectDecoderInputStream(socket.getInputStream());
            Thread read = new Thread(() -> {
                try {
                    while (true) {
                        AbstractMessage message = (AbstractMessage) dis.readObject();
                        processMessage(message);
//                        Platform.runLater(() -> textFieldInput.setText(message.toString()));

                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            });
//            showFiles();

            read.setDaemon(true);
            read.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> getFilesCarentDir() throws IOException {
        return Files.list(carentDir).map(p -> p.getFileName().toString()).collect(Collectors.toList());
    }

    private void processMessage(AbstractMessage message) {
        switch (message.getCommand()) {
            case LIST_MESSAGE:

            case FILE_MESSAGE:
                ListMessage listMessage = (ListMessage) message;
                Platform.runLater(() ->
                {
                    mainListField.getItems().clear();
                    mainListField.getItems().addAll(listMessage.toString());

                });
                break;
        }
    }

    public void sendMessage(ActionEvent actionEvent) throws IOException {
        String text = textFieldInput.getText();
        textFieldInput.clear();
        dos.writeObject(new AbstractMessage(text));
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

    public void addFile(ActionEvent actionEvent) {
    }

    public void sendFile(ActionEvent actionEvent) {
    }

    public void deleteFile(ActionEvent actionEvent) {
    }

    public void renameFile(ActionEvent actionEvent) {
    }
}

