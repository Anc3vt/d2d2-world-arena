package ru.ancevt.d2d2world.sync;

public interface ISyncDataReceiver {

    void setEnabled(boolean enabled);

    boolean isEnabled();

    void bytesReceived(byte[] bytes);
}
