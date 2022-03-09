package club.sentinc.bilibili.songhime.ui;

import club.sentinc.bilibili.songhime.config.SongHimeConfig;
import club.sentinc.bilibili.songhime.websocket.WebSocketInfoHandler;

public abstract class UIAbstract implements WebSocketInfoHandler {

    protected SongHimeConfig config;

    public abstract void onOpen(SongHimeConfig config);

}
