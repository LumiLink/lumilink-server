package net.lumilink.server.plugins;

import lombok.Getter;
import net.lumilink.api.Plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class JavaPlugin extends net.lumilink.api.Plugin {
    @Getter private final File jarFile;
    @Getter private final String name;
    private String version;
    private String author;
    @Getter private String mainClass;
    @Getter private List<String> dependencies;

    private Plugin pluginObject;

    public JavaPlugin(File jarFile, String name, String version, String authors, String mainClass, String dependencies) {
        this.jarFile = jarFile;
        this.name = name;
        this.version = version;
        this.author = authors;
        this.mainClass = mainClass;
        this.dependencies = dependencies == null ? new ArrayList<>() : Arrays.stream(dependencies.replace("[", "")
                .replace("]", "")
                .split(", ")).map(String::trim).toList();
    }

    public static JavaPlugin fromConfigInputStream(File f, InputStream inputStream){
        Map<String, String> configMap = new HashMap<>();

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))){
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":", 2);
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();

                    configMap.put(key, value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return new JavaPlugin(f, configMap.get("name"), configMap.get("version"), configMap.get("author"), configMap.get("main-class"), configMap.get("dependencies"));
    }
}
