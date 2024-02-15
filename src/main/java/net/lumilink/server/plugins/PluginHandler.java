package net.lumilink.server.plugins;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class PluginHandler {
    private final List<Plugin> plugins = new LinkedList<>();
    private final List<Plugin> loadedPlugins = new ArrayList<>();
    private Map<String, Plugin> pluginMap = new HashMap<>();

    public void addPlugin(Plugin pl){
        plugins.add(pl);
    }

    /**
     * Loads the plugins added to instance of the handler
     */
    public void loadPlugins(){

        //Map all the plugin names to the right plugin
        for (Plugin plugin : plugins) {
            pluginMap.put(plugin.getName(), plugin);
        }

        loadedPlugins.clear(); //Keep track of which "nodes" (plugins) have been visited and thus been loaded

        // Treat loading plugins as the topological sorting of a DAG using Kahn's algorithm

        // We initialize a map indegree to keep track of the in-degree of each plugin.
        Map<Plugin, Integer> indegree = new HashMap<>();
        for (Plugin plugin : plugins) {
            indegree.put(plugin, 0);
        }

        // Then we iterate through the plugins to calculate the in-degree of each plugin.
        for (Plugin plugin : plugins) {
            for (String dependency : plugin.getDependencies()) {
                Plugin dependentPlugin = pluginMap.get(dependency);
                indegree.put(dependentPlugin, indegree.get(dependentPlugin) + 1);
            }
        }

        // We use a queue to maintain the set of plugins with an in-degree of 0, indicating they have no
        // dependencies and can be loaded.
        Queue<Plugin> queue = new LinkedList<>();
        for (Plugin plugin : plugins) {
            if (indegree.get(plugin) == 0) {
                queue.add(plugin);
            }
        }

        // Afterwards we iterate over the queue, decrementing the in-degree of dependent plugins, and adding them to
        // the queue if their in-degree becomes 0. We repeat this process until the queue is empty, ensuring all
        // plugins are processed in the correct order.
        while (!queue.isEmpty()) {
            Plugin plugin = queue.poll();

            //Plugin failed with loading, skip the rest so plugins that depend on it won't get loaded
            if(!load(plugin)){
                continue;
            }

            for (String dependency : plugin.getDependencies()) {
                Plugin dependentPlugin = pluginMap.get(dependency);
                indegree.put(dependentPlugin, indegree.get(dependentPlugin) - 1);
                if (indegree.get(dependentPlugin) == 0) {
                    queue.add(dependentPlugin);
                }
            }
        }
    }

    private boolean load(Plugin p){
        if(p.getName() == null || p.getMainClass() == null){
            //Plugin couldn't load
            //TODO: Error message
            return false;
        }

        try(URLClassLoader classLoader = new URLClassLoader(new URL[]{p.getJarFile().toURI().toURL()})){
            String className = p.getMainClass();

            Class<?> mainClass = classLoader.loadClass(className);
            Object instance = mainClass.getDeclaredConstructor().newInstance();

            mainClass.getMethod("onStart").invoke(instance);

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

    public boolean isLoaded(Plugin p){
        return loadedPlugins.contains(p);
    }

    public boolean isLoaded(String name){
        for(Plugin p : loadedPlugins){
            if(p.getName().equals(name)) return true;
        }

        return false;
    }

    public Plugin getPluginByName(String name){
        for(Plugin p : plugins){
            if(p.getName().equals(name)) return p;
        }

        return null;
    }
}
