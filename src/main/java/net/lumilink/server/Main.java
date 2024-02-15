package net.lumilink.server;

import lombok.Getter;
import net.lumilink.server.logs.LogUtil;

public class Main {

    @Getter private static Server server;


    public static void main(String[] args){
        server = new Server();
        LogUtil.start();
        server.start();
    }
}
