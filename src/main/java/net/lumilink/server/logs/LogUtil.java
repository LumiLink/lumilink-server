package net.lumilink.server.logs;

import jdk.jpackage.internal.Log;
import lombok.Getter;
import org.apache.log4j.*;
import org.apache.log4j.spi.LoggingEvent;

public class LogUtil extends PatternLayout {

    @Getter private static final LogUtil logger = new LogUtil("Lumilink server");
    private static final ANSIConsoleAppender colorAppender = new ANSIConsoleAppender();

    public static void start(){
        BasicConfigurator.configure();
    }

    @Override
    public String format(LoggingEvent event) {
        colorAppender.subAppend(event);
        System.out.println("test");
        return colorAppender.getColour(event.getLevel()) + super.format(event) + ANSIConsoleAppender.END_COLOUR;
    }

    private final Logger pLogger;

    public LogUtil(String name){
        pLogger = LogManager.getLogger(name);
    }

    public void log(String s){
        pLogger.info(s);
    }

    public void warn(String s){
        pLogger.warn(s);
    }

    public void debug(String s){
        pLogger.debug(s);
    }

    public void error(String s){
        pLogger.error(s);
    }
}
