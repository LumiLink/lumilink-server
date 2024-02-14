package net.lumilink.server.plugins;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AddonReader {

    public void readAddons(Path folderPath){
        boolean exists = Files.exists(folderPath);

        if(!exists){
            System.out.println("No addons folder found, creating one...");
            try {
                Files.createDirectory(folderPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                System.out.println("Addons folder successfully created!");
            }
        }

        //TODO: Logic after reading addons
    }

    public void readAddons(){
        readAddons(Paths.get("").resolve("addons"));
    }
}
