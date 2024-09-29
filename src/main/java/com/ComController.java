package com;

import error.ErrorMessage;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import messageWorker.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wokerFiles.ComOutFiles;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ComController implements Initializable {
    private static final Logger log = LoggerFactory.getLogger(ComController.class);
    public Label labelClient;
    public Label labelServer;
    public ListView<String> clientMainTextField;
    public Button deleteFile;
    public Button addFile;
    public Button renameFile;
    public Button sendFile;
    private Path carentDir;
    private byte[] buffer;
    private Path com;
    public ListView<String> serverListFiles;
    public TextField textFieldInput;
    private ObjectDecoderInputStream dis;
    private ObjectEncoderOutputStream dos;
    private ComOutFiles comOutFiles;
    ErrorMessage errorMessage;
    File file;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            carentDir = Paths.get("C:\\Education\\Java4\\Java4\\com");
            Socket socket = new Socket("127.0.0.1", 8189);
            dos = new ObjectEncoderOutputStream(socket.getOutputStream());
            dis = new ObjectDecoderInputStream(socket.getInputStream());
            Thread read = new Thread(() -> {
                try {
                    while (true) {
                        AbstractMessage message = (AbstractMessage) dis.readObject();
                        processMessage(message);
                        listClientFiles();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            });
            read.setDaemon(true);
            read.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listClientFiles() {
        Platform.runLater(() -> {
                    try {
                        clientMainTextField.getItems().clear();
                        clientMainTextField.getItems().addAll(getFilesClientDir());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    private List<String> getFilesClientDir() throws IOException {
        List<String> files = Files.list(carentDir).map(p -> p.getFileName().toString()).collect(Collectors.toList());
        return files;
    }

    protected void processMessage(AbstractMessage message) throws IOException {
        switch (message.getCommand()) {
            case FILE_MESSAGE:
                FileMessage msg = (FileMessage) message;
                Path file = carentDir.resolve(msg.getName());
                if (msg.isFirstButch()) {
                    Files.deleteIfExists(file);
                }
                try (FileOutputStream fos = new FileOutputStream(file.toFile(), true)) {
                    fos.write(msg.getBytes(), 0, msg.getEndByteNum());
                }
                break;
            case LIST_MESSAGE:
                ListMessage listMessage = (ListMessage) message;
                Platform.runLater(() ->
                {
                    serverListFiles.getItems().clear();
                    serverListFiles.getItems().addAll(listMessage.getFiles());

                });
                break;
            case AUTENTIFCATION_MESSAGE:
                StringMessage stringMessage = new StringMessage(message.toString());
                System.out.println(stringMessage);
                autentification(String.valueOf(stringMessage));
                log.debug("mesage - *", message, " *");
                break;
        }
    }

    protected void autentification(String message) {
        List<String> list = Arrays.asList(message.toString().split("@"));
        Path path = Paths.get("com");
        File clientDir = Paths.get(String.valueOf(path.resolve(list.get(0)))).toFile();
        if (!clientDir.exists()) {
            clientDir.mkdir();
        }
    }

    public void sendMessage(ActionEvent actionEvent) throws IOException {
        String text = textFieldInput.getText();
        Platform.runLater(() -> {
            textFieldInput.clear();
        });
        dos.writeObject(new AbstractMessage(text));
        dos.flush();
    }

    public void addFileToClient(ActionEvent actionEvent) {
        String name = serverListFiles.getSelectionModel().getSelectedItem().toString();
        FileRequest fileRequest = new FileRequest(name);
    }

    public void sendFile(ActionEvent actionEvent) {
    }


    private void setDeleteFile() {
        if (clientMainTextField.getSelectionModel().getSelectedItem() != null) {
            file = Paths.get(String.valueOf(carentDir.resolve(clientMainTextField.getFocusModel().getFocusedItem().toString()))).toFile();
            try {
                file.delete();
                listClientFiles();
                textFieldInput.clear();
                textFieldInput.setText("был удажен " + file.getName());
            } catch (Exception e) {
                e.printStackTrace();
                }
        }
        else  {
            textFieldInput.clear();
            textFieldInput.setText("файл не существует или вы не имеете прав на удаление");
        }
    }

    public void deleteFile(ActionEvent actionEvent) {
        Platform.runLater(() -> setDeleteFile());
    }

    public void renameFile(ActionEvent actionEvent) throws IOException {
        if (file.exists()) {
//file.renameTo(System.in.read())
        }
    }
}

