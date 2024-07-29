package com;

import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ComOutFiles {
    public ArrayList<File> comFile = new ArrayList<>();
public List<String> name = new ArrayList<>();
     public static void main(String... args) throws IOException {
        if (!Files.exists(Paths.get("com1"))) {
            Files.createDirectory(Paths.get("com1"));
        }
        try {
            for (int i = 0; i < 10; i++) {
                File file = new File("com1/comFile" + i + ".txt");
                Files.createFile(file.toPath());
            }
        } catch (Exception e) {
            System.err.println("files Is Great !!!");
        }
        ;
//fileName();

    }

    private void fileAddArrayList() {
        File comFolder = new File("com/");
        for (File f : comFolder.listFiles()) {
            f.getName();
            comFile.add(f);
        }
    }

    public List outFile() {
        fileAddArrayList();
        {
            for (File f : comFile) {
                name.add(f.getName());
            }
        }
        return name;
    }
}