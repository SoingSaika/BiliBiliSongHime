package club.sentinc.bilibili.songhime.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;

import java.io.*;
import java.nio.charset.StandardCharsets;

public final class ConfigLoader {

    private final static String CONFIG_PATH = "./config/live_and_song_config.json";

    public static SongHimeConfig load() throws IOException, JSONException, ClassCastException {
        try (FileInputStream fis = new FileInputStream(CONFIG_PATH);
             InputStreamReader reader = new InputStreamReader(fis, StandardCharsets.UTF_8)) {
            BufferedReader br = new BufferedReader(reader);
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            return JSON.parseObject(builder.toString(), SongHimeConfig.class);
        }
    }

    public static void save(SongHimeConfig configEntity) throws IOException {
        File file = new File(CONFIG_PATH);
        try (FileWriter fw = new FileWriter(file.getAbsoluteFile());
             BufferedWriter bw = new BufferedWriter(fw);) {
            String configJsonStr = JSON.toJSONString(configEntity);
            if (!file.exists()) {
                file.createNewFile();
            }
            bw.write(configJsonStr);
            bw.flush();
        }
    }
}
