package edu.rtcp.server.callback;

public interface AsyncCallback {
    void onSuccess();
    void onError(Exception e);
}
