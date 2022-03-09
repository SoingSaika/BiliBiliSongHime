package club.sentinc.bilibili.songhime.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.Optional;

public class Danmu {

    @JSONField(name = "cmd")
    private String cmd;
    @JSONField(name = "info")
    private JSONArray info;

    public static Optional<Danmu> generateDanmu(LivePacket packet) {
        Danmu danmu = null;
        if(packet.op == 5) {
            String danmuJson = packet.data.toString().trim().split("[\\x00-\\x1f]+")[0];
            try {
                danmu = JSON.parseObject(danmuJson, Danmu.class);
                if(DanmuCmdType.DANMU_MSG.toString().equals(danmu.cmd)) {
                    return Optional.of(danmu);
                }
            } catch (JSONException | NullPointerException |ClassCastException e) {
                e.printStackTrace();
            }
        }
        return Optional.ofNullable(danmu);
    }

    public String getDanmuContent() {
        return info.getString(1);
    }

    public String getDanmuSenderName() {
        return info.getJSONArray(2).getString(1);
    }

    public long getDanmuSenderId() {
        return info.getJSONArray(2).getLong(0);
    }

    public long getDanmuSendTimestamp() {
        return info.getJSONObject(9).getLong("ts");
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public JSONArray getInfo() {
        return info;
    }

    public void setInfo(JSONArray info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return "Danmu{\"uid\":"+getDanmuSenderId()+",\"name\":"+getDanmuSenderName()+",\"content:\""+getDanmuContent()+",\"time\":"+getDanmuSendTimestamp()+"}";
    }
}
