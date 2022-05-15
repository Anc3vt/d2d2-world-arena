
package com.ancevt.net.connection;

import com.ancevt.net.CloseStatus;

public interface ConnectionListener {

    void connectionEstablished();

    void connectionBytesReceived(byte[] bytes);

    void connectionClosed(CloseStatus status);
}
