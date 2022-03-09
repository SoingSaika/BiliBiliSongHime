package club.sentinc.bilibili.songhime;

import club.sentinc.bilibili.songhime.config.ConfigLoader;
import club.sentinc.bilibili.songhime.config.SongHimeConfig;
import club.sentinc.bilibili.songhime.exception.WebSocketConnectedException;
import club.sentinc.bilibili.songhime.exception.WebSocketException;
import club.sentinc.bilibili.songhime.song.SongEngine;
import club.sentinc.bilibili.songhime.ui.UIAbstract;
import club.sentinc.bilibili.songhime.ui.UIType;
import club.sentinc.bilibili.songhime.ui.cmd.CmdUI;
import club.sentinc.bilibili.songhime.util.BiliBiliRoomUtil;
import club.sentinc.bilibili.songhime.websocket.DanmuWebsocket;
import club.sentinc.bilibili.songhime.websocket.WebSocketInfoHandler;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.ReadyState;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static club.sentinc.bilibili.songhime.exception.ExceptionExecutor.throwException;

public final class SongHimeApplication {

    private static SongHimeConfig config;
    private static WebSocketClient danmuWebSocket;
    private static UIAbstract ui;
    private static SongEngine songEngine;

    public static void run() {
        try {
            config = ConfigLoader.load();
            songEngine = new SongEngine();
            ui = createCurrentUIAbstract(UIType.valueOf(config.getUi()));
            connectDanmuWebSocket();
        } catch (IOException | InterruptedException e) {
            throwException(e);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static boolean connectDanmuWebSocket() throws InterruptedException, WebSocketException, IOException {
        if(danmuWebSocket == null) {
            danmuWebSocket = new DanmuWebsocket(BiliBiliRoomUtil.getRealRoomId(config.getRoom()));
        }
        if(danmuWebSocket.isOpen()) {
            throw new WebSocketConnectedException();
        }
        ui.connecting();
        boolean isOpen = danmuWebSocket.connectBlocking(config.getConnectTimeout() , TimeUnit.SECONDS);
        if(isOpen) {
            ui.connectSuccess();
        } else {
            ui.connectFailed();
        }
        return isOpen;
    }

    public static boolean reconnectDanmuWebSocket() throws InterruptedException, IOException {
        ui.restConnect();
        if(danmuWebSocket != null && danmuWebSocket.isOpen()) {
            danmuWebSocket.closeBlocking();
        }
        return connectDanmuWebSocket();
    }

    public static void shutdown() throws InterruptedException, IOException {
        if(danmuWebSocket != null && danmuWebSocket.isOpen()) {
            ui.closeConnect();
            danmuWebSocket.closeBlocking();
        }
        ConfigLoader.save(config);
    }

    public static void setUi(UIAbstract ui) {
        SongHimeApplication.ui = ui;
    }

    private  static UIAbstract createCurrentUIAbstract(UIType type) {
        UIAbstract uiAbstract = null;
        switch (type) {
            case CMD:
                uiAbstract = new CmdUI();
                break;
            case AWT:
                break;
            default:
        }
        return uiAbstract;
    }

    public static SongHimeConfig getConfig() {
        return config;
    }

    public static WebSocketClient getDanmuWebSocket() {
        return danmuWebSocket;
    }

    public static UIAbstract getUi() {
        return ui;
    }

    public static SongEngine getSongEngine() {
        return songEngine;
    }
}
