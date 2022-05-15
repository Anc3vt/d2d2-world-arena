
package com.ancevt.net;

public class CloseStatus {

    private Throwable throwable;

    public CloseStatus(Throwable throwable) {
        this.throwable = throwable;
    }

    public CloseStatus() {

    }

    public boolean isError() {
        return throwable != null;
    }

    public String getErrorMessage() {
        return isError() ? throwable.getMessage() : "";
    }

    public Throwable getThrowable() {
        return throwable;
    }

    @Override
    public String toString() {
        return "CloseStatus{" +
                "isError=" + isError() +
                ", throwable=" + throwable +
                '}';
    }
}
