package net.lumilink.server;

import lombok.Getter;
import net.lumilink.api.devices.DeviceType;
import net.lumilink.server.devices.DeviceHandler;
import net.lumilink.server.plugins.PluginHandler;
import net.lumilink.server.plugins.PluginReader;
import net.lumilink.server.logs.LogUtil;

public class Server {
    private final PluginReader reader;
    @Getter private final PluginHandler pluginHandler;
    @Getter private final DeviceHandler deviceHandler;

    public Server() {
        this.reader = new PluginReader();
        this.pluginHandler = new PluginHandler();
        this.deviceHandler = new DeviceHandler();

        pluginHandler.addPluginMethod("registerDeviceType", args -> {
            this.deviceHandler.addDeviceType((DeviceType) args[0]);
        });
    }

    public void start(){
        LogUtil.getLogger().log("Starting server...");
        reader.readPlugins();

        pluginHandler.loadPlugins();
        LogUtil.getLogger().log("Server started successfully!");
    }
}
