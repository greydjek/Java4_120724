package com;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CreatFile {
    private static Path dir;
    public static void main(String[] args) throws IOException {

              dir = Paths.get("C:\\Education\\Java4\\Java4\\com1");
        if (!Files.exists(dir)) {
            Files.createDirectory(dir);
            System.out.println("Great com1");
            File f = new File(dir.toFile(), "test1.txt");
            f.createNewFile();
            System.out.println("great file f");
        }
    }
}
