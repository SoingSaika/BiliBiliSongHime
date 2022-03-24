package club.sentinc.bilibili.songhime.websocket;

import club.sentinc.bilibili.songhime.entity.Danmu;
import club.sentinc.bilibili.songhime.entity.LivePacket;
import club.sentinc.bilibili.songhime.util.BiliBiliRoomUtil;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class DanmuWebsocket extends WebSocketClient {

    private int realRoomId = -1;

    private final Timer heartbeatTimer = new Timer();

    private List<DanmuAction> danmuActions;

    public DanmuWebsocket(int realRoomId) throws IOException {
        super(URI.create(BiliBiliRoomUtil.getRoomWebSocketHost(realRoomId)), new DanmuDraft());
        this.realRoomId = realRoomId;
        danmuActions = new ArrayList<>();
    }

    public DanmuWebsocket(URI serverUri) {
        super(serverUri);
    }

    public DanmuWebsocket(URI serverUri, Draft protocolDraft) {
        super(serverUri, protocolDraft);
    }

    public DanmuWebsocket(URI serverUri, Map<String, String> httpHeaders) {
        super(serverUri, httpHeaders);
    }

    public DanmuWebsocket(URI serverUri, Draft protocolDraft, Map<String, String> httpHeaders) {
        super(serverUri, protocolDraft, httpHeaders);
    }

    public DanmuWebsocket(URI serverUri, Draft protocolDraft, Map<String, String> httpHeaders, int connectTimeout) {
        super(serverUri, protocolDraft, httpHeaders, connectTimeout);
    }

    @Override
    public void connect() {
        super.connect();
        heartbeatTimer.schedule(new HeartbeatTask(this), 10000, 30000);
    }

    @Override
    public void reconnect() {
        super.reconnect();
        heartbeatTimer.schedule(new HeartbeatTask(this), 10000, 30000);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        String auth = "{\"uid\":0,\"roomid\":" + realRoomId + ",\"protover\":2,\"platform\":\"web\",\"clientver\":\"1.6.3\",\"type\":2}";
        byte[] authBytes = auth.getBytes(StandardCharsets.UTF_8);
        ByteBuffer data = ByteBuffer.allocate(authBytes.length + 16);
        data.putInt(authBytes.length + 16)
                .putShort((short) 16)
                .putShort((short) 2)
                .putInt(7)
                .putInt(1);
        data.put(authBytes);
        send(data.array());
    }

    @Override
    public void onMessage(String s) {
    }

    public void onMessage(Danmu danmu) {
        System.out.println(danmu);
        danmuActions.forEach(danmuAction -> {
            danmuAction.doDanmuAction(danmu);
        });
    }

    public void onMessage(LivePacket packet) {
        Optional<Danmu> optionalDanmu = Danmu.generateDanmu(packet);
        optionalDanmu.ifPresent(this::onMessage);
    }

    public void addDanmuAction(DanmuAction action) {
        danmuActions.add(action);
    }

    public void removeDanmuAction(DanmuAction action) {
        danmuActions.remove(action);
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        onMessage(new LivePacket(bytes));
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        heartbeatTimer.cancel();
    }

    @Override
    public void onError(Exception e) {

    }

    public static class DanmuDraft extends Draft_6455 {

    }

    private static class HeartbeatTask extends TimerTask {

        private WebSocketClient websocket;

        public HeartbeatTask(WebSocketClient websocket) {
            this.websocket = websocket;
        }

        @Override
        public void run() {
            if (websocket.isOpen()) {
                ByteBuffer data = ByteBuffer.allocate(16);
                data.putInt(0)
                        .putShort((short) 16)
                        .putShort((short) 2)
                        .putInt(2)
                        .putInt(1);
                websocket.send(data.array());
            }
        }

        @Override
        public boolean cancel() {
            boolean cancel = super.cancel();
            websocket = null;
            return cancel;
        }
    }

}
