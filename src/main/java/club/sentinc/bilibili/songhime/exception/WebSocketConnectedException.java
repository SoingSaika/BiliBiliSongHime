package club.sentinc.bilibili.songhime.exception;

public class WebSocketConnectedException extends WebSocketException{

    public WebSocketConnectedException() {
        super("WebSocket已连接");
    }

    public WebSocketConnectedException(String message) {
        super(message);
    }
}
