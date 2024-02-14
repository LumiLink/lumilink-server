package net.lumilink.server;

import net.lumilink.server.logs.LogUtil;

public class Main {



    public static void main(String[] args){
        Server server = new Server();
        LogUtil.start();
        server.start();
    }
}
