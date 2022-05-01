package com.ancevt.d2d2world.desktop;

public class MonitorDevice {
    private static long monitorDeviceId;

    public static void setMonitorDevice(long monitorDeviceId) {
        MonitorDevice.monitorDeviceId = monitorDeviceId;
    }

    public static long getMonitorDevice() {
        return monitorDeviceId;
    }
}
