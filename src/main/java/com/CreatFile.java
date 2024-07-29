package com;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CreatFile {
    public static void main(String[] args) throws IOException {
        File dir = new File("C:/Education/Java4/Java4/com/com1");
        System.out.println(dir.getAbsolutePath());
        if (!dir.exists()) {
            Files.createDirectory(dir.toPath());
            System.out.println("Great com1");
            File f = new File(dir, "test1.txt");
            f.createNewFile();
            System.out.println("great file f");
        }
        System.out.println(dir.isDirectory());
    }
}
