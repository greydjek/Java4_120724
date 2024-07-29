package com;

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
    private Path com;
    public ListView<String> mainListField;
    public TextField textFieldInput;
    private DataInputStream dis;
    private DataOutputStream dos;
    private ComOutFiles comOutFiles;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
                com = Paths.get("C:/Education/Java4/Java4/com");
                 Platform.runLater(()->{
            try {
                fuulFilesView();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        mainListField.setOnMouseClicked(e -> {
                    if (e.getClickCount() == 2) {
                        String fileName = mainListField.getSelectionModel().getSelectedItem();
                        if (!Files.isDirectory(com.resolve(fileName))) {
                            textFieldInput.setText(fileName);
                        } else {
                            textFieldInput.setText("File is not directory ");

                        }
                    }
                }
        );
        });

        try {
            Socket socket = new Socket("127.0.0.1", 8089);
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            Thread read = new Thread(() -> {
                try {
                    while (true) {
                        String message = dis.readUTF();
                        Platform.runLater(() -> mainListField.getItems().addAll(comOutFiles.outFile()));

                    }
                } catch (IOException e) {
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
        String message = textFieldInput.getText();
        dos.writeUTF(message);
        dos.flush();
        textFieldInput.clear();
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
