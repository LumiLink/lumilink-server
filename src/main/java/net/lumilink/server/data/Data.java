package net.lumilink.server.data;

import lombok.Getter;
import lombok.Setter;

public class Data<T> {

    @Getter private String key;
    @Getter @Setter private T value;


}
