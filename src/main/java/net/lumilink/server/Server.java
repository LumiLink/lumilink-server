package net.lumilink.server;

import lombok.Getter;
import net.lumilink.server.plugins.PluginHandler;
import net.lumilink.server.plugins.PluginReader;
import net.lumilink.server.logs.LogUtil;

public class Server {
    private final PluginReader reader;
    @Getter private final PluginHandler pluginHandler;

    public Server() {
        this.reader = new PluginReader();
        this.pluginHandler = new PluginHandler();
    }

    public void start(){
        LogUtil.getLogger().log("Starting server...");
        reader.readPlugins();

        pluginHandler.loadPlugins();
        LogUtil.getLogger().log("Server started successfully!");
    }
}
