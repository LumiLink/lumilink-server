package net.lumilink.server.devices;

import net.lumilink.api.devices.DeviceType;

import javax.sound.midi.SysexMessage;
import java.util.ArrayList;
import java.util.List;

public class DeviceHandler {
    private final List<DeviceType> deviceTypes;

    public DeviceHandler() {
        this.deviceTypes = new ArrayList<>();
    }

    public void addDeviceType(DeviceType type){
        deviceTypes.add(type);
        System.out.println("Added new device: " + type);
    }
}
