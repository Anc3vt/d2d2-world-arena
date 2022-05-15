
package com.ancevt.d2d2.exception;

public class AssetException extends RuntimeException {

    public AssetException(String message) {
        super(message);
    }

    public AssetException(String message, Throwable cause) {
        super(message, cause);
    }

    public AssetException(Throwable cause) {
        super(cause);
    }

    public AssetException() {
        super();
    }

}
