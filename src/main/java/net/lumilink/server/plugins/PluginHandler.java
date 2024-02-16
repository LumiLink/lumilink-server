package net.lumilink.server.plugins;

import net.lumilink.api.Plugin;
import net.lumilink.api.devices.DeviceType;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class PluginHandler {
    private final List<JavaPlugin> plugins = new LinkedList<>();
    private final List<JavaPlugin> loadedPlugins = new ArrayList<>();
    private Map<String, JavaPlugin> pluginMap = new HashMap<>();

    private Map<String, Plugin.Method> pluginMethods = new HashMap<>();

    public void addPluginMethod(String methodName, Plugin.Method method) {
        pluginMethods.put(methodName, method);
    }


    public void addPlugin(JavaPlugin pl){
        plugins.add(pl);
    }

    /**
     * Loads the plugins added to instance of the handler
     */
    public void loadPlugins(){

        //Map all the plugin names to the right plugin
        for (JavaPlugin plugin : plugins) {
            pluginMap.put(plugin.getName(), plugin);
        }

        loadedPlugins.clear(); //Keep track of which "nodes" (plugins) have been visited and thus been loaded

        // Treat loading plugins as the topological sorting of a DAG using Kahn's algorithm

        // We initialize a map indegree to keep track of the in-degree of each plugin.
        Map<JavaPlugin, Integer> indegree = new HashMap<>();
        for (JavaPlugin plugin : plugins) {
            indegree.put(plugin, 0);
        }

        // Then we iterate through the plugins to calculate the in-degree  of each plugin.
        for (JavaPlugin plugin : plugins) {
            for (String dependency : plugin.getDependencies()) {
                JavaPlugin dependentPlugin = pluginMap.get(dependency);
                indegree.put(dependentPlugin, indegree.get(dependentPlugin) + 1);
            }
        }

        // We use a queue to maintain the set of plugins with an in-degree of 0, indicating they have no
        // dependencies and can be loaded.
        Queue<JavaPlugin> queue = new LinkedList<>();
        for (JavaPlugin plugin : plugins) {
            if (indegree.get(plugin) == 0) {
                queue.add(plugin);
            }
        }

        // Afterwards we iterate over the queue, decrementing the in-degree of dependent plugins, and adding them to
        // the queue if their in-degree becomes 0. We repeat this process until the queue is empty, ensuring all
        // plugins are processed in the correct order.
        while (!queue.isEmpty()) {
            JavaPlugin plugin = queue.poll();

            //Plugin failed with loading, skip the rest so plugins that depend on it won't get loaded
            if(!load(plugin)){
                continue;
            }

            for (String dependency : plugin.getDependencies()) {
                JavaPlugin dependentPlugin = pluginMap.get(dependency);
                indegree.put(dependentPlugin, indegree.get(dependentPlugin) - 1);
                if (indegree.get(dependentPlugin) == 0) {
                    queue.add(dependentPlugin);
                }
            }
        }
    }

    private boolean load(JavaPlugin p){
        if(p.getName() == null || p.getMainClass() == null){
            //Plugin couldn't load
            //TODO: Error message
            return false;
        }

        //Get the Main.class file from the plugin.
        try(URLClassLoader classLoader = new URLClassLoader(new URL[]{p.getJarFile().toURI().toURL()})){
            String className = p.getMainClass();

            //Load the class ad create an instance of the Plugin the Main class extends
            Class<?> mainClass = classLoader.loadClass(className);
            Plugin pluginInstance = (Plugin) mainClass.getDeclaredConstructor().newInstance();

            //We need to be able to interact with methods from the plugin.
            //In the plugin we store the methods that will be used for device type registration etc in a map.
            try {
                Field f = mainClass.getSuperclass().getDeclaredField("methodExecutions");
                f.setAccessible(true);

                HashMap<String, Plugin.Method> methodExecutions = (HashMap<String, Plugin.Method>) f.get(pluginInstance);

                for (String name : pluginMethods.keySet()) {
                    methodExecutions.put(name, pluginMethods.get(name));
                }

            } catch (SecurityException | NoSuchFieldException e) {
                throw new RuntimeException(e);
            }

            //Invoke the onStart method in the plugin
            pluginInstance.onStart();

        } catch (ClassNotFoundException e) {
            System.out.println("ERROR");
            return false;
            //TODO: Error message
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException |
                 IOException e) {
            throw new RuntimeException(e);
        }

        loadedPlugins.add(p);
        return true;
    }

    public boolean isLoaded(JavaPlugin p){
        return loadedPlugins.contains(p);
    }

    public boolean isLoaded(String name){
        for(JavaPlugin p : loadedPlugins){
            if(p.getName().equals(name)) return true;
        }

        return false;
    }

    public JavaPlugin getPluginByName(String name){
        for(JavaPlugin p : plugins){
            if(p.getName().equals(name)) return p;
        }

        return null;
    }
}
