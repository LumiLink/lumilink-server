package net.lumilink.server;

import net.lumilink.server.plugins.AddonReader;
import net.lumilink.server.logs.LogUtil;

public class Server {
    private final AddonReader reader;

    public Server() {
        this.reader = new AddonReader();
    }

    public void start(){
        LogUtil.getLogger().log("Starting server...");
        reader.readAddons();

        LogUtil.getLogger().log("Server started successfully!");
    }
}
