
package com.ancevt.net.server;

import com.ancevt.net.CloseStatus;
import com.ancevt.net.connection.IConnection;

public interface ServerListener {

    void serverStarted();

    void connectionAccepted(IConnection connectionWithClient);

    void connectionClosed(IConnection connectionWithClient, CloseStatus status);

    void connectionBytesReceived(IConnection connectionWithClient, byte[] bytes);

    void serverClosed(CloseStatus status);

    void connectionEstablished(IConnection connectionWithClient);
}
