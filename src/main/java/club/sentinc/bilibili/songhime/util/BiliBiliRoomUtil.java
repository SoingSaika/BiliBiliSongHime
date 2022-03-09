package club.sentinc.bilibili.songhime.util;

import club.sentinc.bilibili.songhime.exception.ExternalRequestException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.annotation.JSONField;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class BiliBiliRoomUtil {

    public static final String GET_ROOM_HOST = "https://api.live.bilibili.com/room/v1/Room/room_init?id=%d";

    public static final String GET_ROOM_WEBSOCKET = "https://api.live.bilibili.com/room/v1/Danmu/getConf?room_id=%d&platform=pc&player=web";

    public static int getRealRoomId(int roomId) throws IOException, JSONException, ClassCastException, ExternalRequestException {
        URL getRoomUrl = new URL(String.format(GET_ROOM_HOST, roomId));
        HttpURLConnection urlConnection = (HttpURLConnection) getRoomUrl.openConnection();
        urlConnection.connect();
        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line);
            }
            Room room = JSON.parseObject(builder.toString(), Room.class);
            if(room.getCode() == 0) {
                return room.getData().getRoomId();
            } else {
                throw new ExternalRequestException(getRoomUrl);
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static String getRoomWebSocketHost(int realRoomId) throws IOException, JSONException, ClassCastException, ExternalRequestException {
        URL getRoomWebSocketUrl = new URL(String.format(GET_ROOM_WEBSOCKET, realRoomId));
        HttpURLConnection urlConnection = (HttpURLConnection) getRoomWebSocketUrl.openConnection();
        urlConnection.connect();
        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line);
            }
            RoomWebSocket roomWebSocket = JSON.parseObject(builder.toString(), RoomWebSocket.class);
            if(roomWebSocket.getCode() == 0) {
                return "wss://".concat(roomWebSocket.getData().getHost()).concat("/sub");
            } else {
                throw new ExternalRequestException(getRoomWebSocketUrl);
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static class Room {

        @JSONField(name = "code")
        private Integer code;
        @JSONField(name = "msg")
        private String msg;
        @JSONField(name = "message")
        private String message;
        @JSONField(name = "data")
        private Data data;

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

        public static class Data {
            @JSONField(name = "room_id")
            private Integer roomId;
            @JSONField(name = "short_id")
            private Integer shortId;
            @JSONField(name = "uid")
            private Integer uid;
            @JSONField(name = "need_p2p")
            private Integer needP2p;
            @JSONField(name = "is_hidden")
            private Boolean isHidden;
            @JSONField(name = "is_locked")
            private Boolean isLocked;
            @JSONField(name = "is_portrait")
            private Boolean isPortrait;
            @JSONField(name = "live_status")
            private Integer liveStatus;
            @JSONField(name = "hidden_till")
            private Integer hiddenTill;
            @JSONField(name = "lock_till")
            private Integer lockTill;
            @JSONField(name = "encrypted")
            private Boolean encrypted;
            @JSONField(name = "pwd_verified")
            private Boolean pwdVerified;
            @JSONField(name = "live_time")
            private Long liveTime;
            @JSONField(name = "room_shield")
            private Integer roomShield;
            @JSONField(name = "is_sp")
            private Integer isSp;
            @JSONField(name = "special_type")
            private Integer specialType;

            public Integer getRoomId() {
                return roomId;
            }

            public void setRoomId(Integer roomId) {
                this.roomId = roomId;
            }

            public Integer getShortId() {
                return shortId;
            }

            public void setShortId(Integer shortId) {
                this.shortId = shortId;
            }

            public Integer getUid() {
                return uid;
            }

            public void setUid(Integer uid) {
                this.uid = uid;
            }

            public Integer getNeedP2p() {
                return needP2p;
            }

            public void setNeedP2p(Integer needP2p) {
                this.needP2p = needP2p;
            }

            public Boolean getIsHidden() {
                return isHidden;
            }

            public void setIsHidden(Boolean isHidden) {
                this.isHidden = isHidden;
            }

            public Boolean getIsLocked() {
                return isLocked;
            }

            public void setIsLocked(Boolean isLocked) {
                this.isLocked = isLocked;
            }

            public Boolean getIsPortrait() {
                return isPortrait;
            }

            public void setIsPortrait(Boolean isPortrait) {
                this.isPortrait = isPortrait;
            }

            public Integer getLiveStatus() {
                return liveStatus;
            }

            public void setLiveStatus(Integer liveStatus) {
                this.liveStatus = liveStatus;
            }

            public Integer getHiddenTill() {
                return hiddenTill;
            }

            public void setHiddenTill(Integer hiddenTill) {
                this.hiddenTill = hiddenTill;
            }

            public Integer getLockTill() {
                return lockTill;
            }

            public void setLockTill(Integer lockTill) {
                this.lockTill = lockTill;
            }

            public Boolean getEncrypted() {
                return encrypted;
            }

            public void setEncrypted(Boolean encrypted) {
                this.encrypted = encrypted;
            }

            public Boolean getPwdVerified() {
                return pwdVerified;
            }

            public void setPwdVerified(Boolean pwdVerified) {
                this.pwdVerified = pwdVerified;
            }

            public Long getLiveTime() {
                return liveTime;
            }

            public void setLiveTime(Long liveTime) {
                this.liveTime = liveTime;
            }

            public Integer getRoomShield() {
                return roomShield;
            }

            public void setRoomShield(Integer roomShield) {
                this.roomShield = roomShield;
            }

            public Integer getIsSp() {
                return isSp;
            }

            public void setIsSp(Integer isSp) {
                this.isSp = isSp;
            }

            public Integer getSpecialType() {
                return specialType;
            }

            public void setSpecialType(Integer specialType) {
                this.specialType = specialType;
            }
        }
    }

    public static class RoomWebSocket {

        @JSONField(name = "code")
        private Integer code;
        @JSONField(name = "msg")
        private String msg;
        @JSONField(name = "message")
        private String message;
        @JSONField(name = "data")
        private Data data;

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

        public static class Data {
            @JSONField(name = "refresh_row_factor")
            private Double refreshRowFactor;
            @JSONField(name = "refresh_rate")
            private Integer refreshRate;
            @JSONField(name = "max_delay")
            private Integer maxDelay;
            @JSONField(name = "port")
            private Integer port;
            @JSONField(name = "host")
            private String host;
            @JSONField(name = "host_server_list")
            private List<HostServerList> hostServerList;
            @JSONField(name = "server_list")
            private List<ServerList> serverList;
            @JSONField(name = "token")
            private String token;

            public Double getRefreshRowFactor() {
                return refreshRowFactor;
            }

            public void setRefreshRowFactor(Double refreshRowFactor) {
                this.refreshRowFactor = refreshRowFactor;
            }

            public Integer getRefreshRate() {
                return refreshRate;
            }

            public void setRefreshRate(Integer refreshRate) {
                this.refreshRate = refreshRate;
            }

            public Integer getMaxDelay() {
                return maxDelay;
            }

            public void setMaxDelay(Integer maxDelay) {
                this.maxDelay = maxDelay;
            }

            public Integer getPort() {
                return port;
            }

            public void setPort(Integer port) {
                this.port = port;
            }

            public String getHost() {
                return host;
            }

            public void setHost(String host) {
                this.host = host;
            }

            public List<HostServerList> getHostServerList() {
                return hostServerList;
            }

            public void setHostServerList(List<HostServerList> hostServerList) {
                this.hostServerList = hostServerList;
            }

            public List<ServerList> getServerList() {
                return serverList;
            }

            public void setServerList(List<ServerList> serverList) {
                this.serverList = serverList;
            }

            public String getToken() {
                return token;
            }

            public void setToken(String token) {
                this.token = token;
            }

            public static class HostServerList {
                @JSONField(name = "host")
                private String host;
                @JSONField(name = "port")
                private Integer port;
                @JSONField(name = "wss_port")
                private Integer wssPort;
                @JSONField(name = "ws_port")
                private Integer wsPort;

                public String getHost() {
                    return host;
                }

                public void setHost(String host) {
                    this.host = host;
                }

                public Integer getPort() {
                    return port;
                }

                public void setPort(Integer port) {
                    this.port = port;
                }

                public Integer getWssPort() {
                    return wssPort;
                }

                public void setWssPort(Integer wssPort) {
                    this.wssPort = wssPort;
                }

                public Integer getWsPort() {
                    return wsPort;
                }

                public void setWsPort(Integer wsPort) {
                    this.wsPort = wsPort;
                }
            }

            public static class ServerList {
                @JSONField(name = "host")
                private String host;
                @JSONField(name = "port")
                private Integer port;

                public String getHost() {
                    return host;
                }

                public void setHost(String host) {
                    this.host = host;
                }

                public Integer getPort() {
                    return port;
                }

                public void setPort(Integer port) {
                    this.port = port;
                }
            }
        }
    }

}
