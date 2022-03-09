package club.sentinc.bilibili.songhime.websocket;

public interface WebSocketInfoHandler {

    void connecting();

    void connectFailed();

    void connectSuccess();

    void closeConnect();

    void restConnect();
}
