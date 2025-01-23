package com;

import error.ErrorMessage;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import messageWorker.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wokerFiles.ComOutFiles;


import java.io.File;
import java.io.FileInputStream;
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
    public TextArea renameFileTextField;
    public Button renameFileYes;
    public Button renameFileNo;
    public Pane paneRenameFile;
    public AnchorPane mainPanelControl;
    private Path carentDir;
    private byte[] buffer;
    public ListView<String> serverListFiles;
    public TextField textFieldInput;
    private ObjectDecoderInputStream dis;
    private ObjectEncoderOutputStream dos;
    File file;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try { listClientFiles();
            buffer = new byte[8191];
            carentDir = Paths.get("com");

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
         Platform.runLater(() -> {
             StringMessage stringMessage = new StringMessage(message.toString());
             System.out.println(stringMessage);
             autentification(String.valueOf(stringMessage));
             log.debug("mesage - *", message, " *");
         });
                break;
        }
    }

    protected void autentification(String message) {
     //   autentification.autentifControl.setVisible(false);
       // mainPanelControl.setVisible(true);

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

    public void setAddFile() {
        if (serverListFiles.getSelectionModel().getSelectedItems() != null) {
            try {
                String name = serverListFiles.getSelectionModel().getSelectedItem().toString();
                FileRequest fileRequest = new FileRequest(name);
                dos.writeObject(fileRequest);
                dos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            textFieldInput.clear();
            textFieldInput.setText("Выберите файл на сервере");
        }
    }

    public void addFileToClient(ActionEvent actionEvent) throws IOException {
        Platform.runLater(this::setAddFile);

    }

    public void sendFile(ActionEvent actionEvent) throws Exception {
        boolean isFirstBath = true, isFinishBath;
        File file = new File(carentDir.resolve(clientMainTextField.getFocusModel().getFocusedItem().toString()).toFile().toURI());
        long size = Files.size(file.toPath());
        try (FileInputStream in = new FileInputStream(file)) {
            int read;
            while ((read = in.read(buffer)) != -1) {
                isFinishBath = (in.available() <= 0);
                FileMessage fileMessage = new FileMessage(file.toPath(), buffer, file.getName(), isFirstBath, isFinishBath, read);
                isFirstBath = false;
                dos.writeObject(fileMessage);
                dos.flush();
                dos.writeObject(new ListMessage());
            }
        }

    }


    public void setDeleteFile() {
        if (clientMainTextField.getSelectionModel().getSelectedItem() != null) {
            file = Paths.get(String.valueOf(carentDir.resolve(clientMainTextField.getFocusModel().getFocusedItem().toString()))).toFile();
            try {
                file.delete();
                listClientFiles();
                textFieldInput.clear();
                textFieldInput.setText("Удален файл -> " + file.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            textFieldInput.clear();
            textFieldInput.setText("файл не существует или вы не имеете прав на удаление");
        }
    }

    public void deleteFile(ActionEvent actionEvent) {
        Platform.runLater(() -> setDeleteFile());
    }

    public void renameFile(ActionEvent actionEvent) throws IOException {
        paneRenameFile.setVisible(true);
    }

    public void cancelRename(ActionEvent actionEvent) {
        paneRenameFile.setVisible(false);
    }

    private void renameF() throws IOException {
        if (renameFileTextField.getText() != null) {
            String newName = renameFileTextField.getText();
            File file1 = carentDir.resolve(clientMainTextField.getSelectionModel().getSelectedItems().toString()).toFile();
            File file2 = new File(String.valueOf(carentDir.resolve(newName)));
            Path s1 = Paths.get(carentDir.resolve(clientMainTextField.getFocusModel().getFocusedItem().toString()).toFile().toURI());
            try {
                Files.move(s1, s1.resolveSibling(newName));
                paneRenameFile.setVisible(false);
                listClientFiles();
                textFieldInput.clear();
                textFieldInput.setText("yes renamed file " + file1.getName() + " -->> " + file2.getName());

            } catch (IOException e) {
                e.printStackTrace();
                paneRenameFile.setVisible(false);
                textFieldInput.clear();
                textFieldInput.setText("NO renamed file ");

            }
        }
    }

    public void renameFileYes(ActionEvent actionEvent) {
        Platform.runLater(() -> {
            try {
                renameF();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}

