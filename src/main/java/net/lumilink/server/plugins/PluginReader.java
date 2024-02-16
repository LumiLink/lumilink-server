package net.lumilink.server.plugins;

import net.lumilink.server.Main;
import net.lumilink.server.logs.LogUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class PluginReader {


    public void readPlugins(Path folderPath){
        if(!Files.exists(folderPath)){
            System.out.println("No plugin folder found, creating one...");
            try {
                Files.createDirectory(folderPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                System.out.println("Plugin folder successfully created!");
            }
        }

        File folder = new File(folderPath.toUri());
        File[] files = folder.listFiles();

        if(files == null) {
            LogUtil.getLogger().log("No plugins found in " + folderPath.toUri());
            return;
        }

        for(File f : files){
            if(f.isFile() && f.getName().toLowerCase().endsWith(".jar")){
                try (JarFile jar = new JarFile(f)){
                    initPlugin(f, jar);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void initPlugin(File f, JarFile jar){
        ZipEntry configEntry = jar.getEntry("config.yml");
        if(configEntry == null){
            LogUtil.getLogger().error(jar.getName() + " contains no configuration.");
            return;
        }

        try(InputStream inputStream = jar.getInputStream(configEntry)){
            Main.getServer().getPluginHandler().addPlugin(JavaPlugin.fromConfigInputStream(f, inputStream));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void readPlugins(){
        readPlugins(Paths.get("").resolve("plugins"));
    }
}
