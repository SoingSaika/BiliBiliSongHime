//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package club.sentinc.bilibili.songhime.song;

import com.alibaba.fastjson.JSON;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import work.sentinc.musiccaster.bean.FmResult;
import work.sentinc.musiccaster.bean.LoginResult;
import work.sentinc.musiccaster.bean.LyricResult;
import work.sentinc.musiccaster.bean.MusicResult;
import work.sentinc.musiccaster.bean.music.Result;
import work.sentinc.musiccaster.bean.music.Song;
import work.sentinc.musiccaster.engine.MusicEngine;
import work.sentinc.musiccaster.engine.MusicFace;

public class NeteaseCloudMusic extends MusicEngine implements MusicFace {
    protected final String searchUrl = "https://music.163.com/weapi/search/get";
    protected final String cloudSearchUrl = "https://music.163.com/weapi/cloudsearch/pc";
    protected final String mp3Url = "https://music.163.com/song/media/outer/url?id=";
    protected final String mvUrl = "https://music.163.com/weapi/song/enhance/play/mv/url";
    protected final String loginUrl = "https://music.163.com/weapi/login/cellphone";
    protected final String randomUrl = "https://music.163.com/weapi/v1/radio/get";
    protected final String lyricUrl = "https://music.163.com/weapi/song/lyric";
    protected final String music_u = "5f0c0de61b91edba04d9068e1173a225fe7a54bbc0ce4909cfe212ddd001016033a649814e309366";
    protected final String nmtid = "00OR_FXEuGnOaiT90Osqs6oNHK19sMAAAF2EYm4tw";
    protected final String likeListUrl = "https://music.163.com/weapi/song/like/get";
    protected final String playListUrl = "https://music.163.com/weapi/user/playlist";
    protected final String configPath = "./config/Netease/app.properties";
    protected String csrf;
    protected String cookie;
    protected LoginResult loginResult;

    public NeteaseCloudMusic() {
        this.loadConfig("./config/Netease/app.properties");
        if (this.config.containsKey("autologin") && this.config.containsKey("phone") && this.config.containsKey("password")) {
            HashMap<String, String> params = new HashMap();
            params.put("phone", this.config.getProperty("phone"));
            params.put("password", this.config.getProperty("password"));

            try {
                this.login(params);
            } catch (Exception var3) {
                var3.printStackTrace();
            }
        }

    }

    public String search(HashMap<String, String> params) throws Exception {
        boolean parse = params.remove("parse") != null;
        MusicResult mresult = this.searchAndParse(params);
        Result result = mresult.getResult();
        if (result != null && result.getSongs() != null && result.getSongs().size() != 0) {
            result.setCode(mresult.getCode());
            result.setType((String)params.get("type"));
            if (parse) {
                this.parseUrl(result.getSongs(), result.getType());
            }

            return JSON.toJSONString(result);
        } else {
            return JSON.toJSONString(mresult);
        }
    }

    private MusicResult searchAndParse(HashMap<String, String> params) throws Exception {
        if (params != null && params.containsKey("s")) {
            String url = "https://music.163.com/weapi/search/get";
            String expand = (String)params.remove("expand");
            if (params.remove("cloudsearch") != null) {
                url = "https://music.163.com/weapi/cloudsearch/pc";
                params.put("total", "true");
            }

            if (!params.containsKey("type")) {
                params.put("type", "1");
            }

            if (!params.containsKey("limit")) {
                params.put("limit", "20");
            }

            if (!params.containsKey("offset")) {
                params.put("offset", "0");
            }

            String json = this.request.getHttpResponseToString(url, (HashMap)null, this.crypto.weapi(params));
            MusicResult mresult = this.parseMusicResult(json);
            mresult.setExpand(expand);
            mresult.getResult().setExpand(expand);
            return mresult;
        } else {
            throw new NullPointerException("歌曲名称不能为空");
        }
    }

    private void parseUrl(List<Song> songs, int type) throws Exception {
        switch(type) {
        case 1:
            Iterator var9 = songs.iterator();

            while(var9.hasNext()) {
                Song song = (Song)var9.next();
                song.setPlayUrl(this.lightSongUrl(song));
            }

            return;
        case 1004:
            HashMap<String, String> params = new HashMap();
            Iterator var5 = songs.iterator();

            while(var5.hasNext()) {
                Song song = (Song)var5.next();
                params.put("id", String.valueOf(song.getId()));
                params.put("r", "720");
                String json = this.request.getHttpResponseToString("https://music.163.com/weapi/song/enhance/play/mv/url", (HashMap)null, this.crypto.weapi(params));
                MusicResult mresult = this.parseMusicResult(json);
                if (mresult != null && mresult.getSong() != null) {
                    song.setPlayUrl(mresult.getSong().getPlayUrl());
                }
            }
        }

    }

    public String songUrl(HashMap<String, String> params) throws Exception {
        if (params != null && params.containsKey("ids")) {
            return "https://music.163.com/song/media/outer/url?id=" + (String)params.get("ids") + ".mp3";
        } else {
            throw new NullPointerException("歌曲ID不能为空");
        }
    }

    private String lightSongUrl(Song song) {
        StringBuilder builder = new StringBuilder();
        builder.append("https://music.163.com/song/media/outer/url?id=").append(song.getId()).append(".mp3");
        return builder.toString();
    }

    public String first(HashMap<String, String> params) throws Exception {
        boolean parse = params.remove("parse") != null;
        MusicResult mresult = this.searchAndParse(params);
        Result result = mresult.getResult();
        if (result != null && result.getSongs() != null && result.getSongs().size() != 0) {
            result.setCode(mresult.getCode());
            result.setType((String)params.get("type"));
            Iterator var6 = result.getSongs().iterator();

            while(var6.hasNext()) {
                Song song = (Song)var6.next();
                if (song.getFee() != 1) {
                    result.getSongs().clear();
                    result.getSongs().add(song);
                    break;
                }
            }

            if (parse) {
                this.parseUrl(result.getSongs(), result.getType());
            }

            return JSON.toJSONString(result);
        } else {
            return JSON.toJSONString(mresult);
        }
    }

    public String mvUrl(HashMap<String, String> params) throws Exception {
        if (params != null && params.containsKey("id")) {
            if (!params.containsKey("r")) {
                params.put("r", "1080");
            }

            String json = this.request.getHttpResponseToString("https://music.163.com/weapi/song/enhance/play/mv/url", (HashMap)null, this.crypto.weapi(params));
            MusicResult mresult = this.parseMusicResult(json);
            return mresult != null && mresult.getSong() != null ? JSON.toJSONString(mresult.getSong()) : JSON.toJSONString(mresult);
        } else {
            throw new NullPointerException("MV ID不能为空");
        }
    }

    public String checkSong(HashMap<String, String> params) {
        return "false";
    }

    public String random(HashMap<String, String> params) throws Exception {
        if (params == null) {
            params = new HashMap();
        } else if (!params.containsKey("csrf_token")) {
            params.put("csrf_token", this.csrf);
        }

        String expand = (String)params.remove("expand");
        this.clearParam();
        HashMap<String, String> header = this.getParamMap();
        header.put("Cookie", this.cookie);
        String json = this.request.getHttpResponseToString("https://music.163.com/weapi/v1/radio/get", header, this.crypto.weapi(params));
        FmResult result = this.parseFmResult(json);
        result.setExpand(expand);
        return result.getData() != null && result.getData().size() != 0 ? JSON.toJSONString(result) : JSON.toJSONString(result);
    }

    public String login(HashMap<String, String> params) throws Exception {
        if (params != null && params.containsKey("phone") && params.containsKey("password")) {
            this.clearParam();
            HashMap<String, String> header = this.getParamMap();
            long cookieTime = System.currentTimeMillis();
            header.put("Cookie", this.generateCookie(cookieTime));
            params.put("password", this.generateHash((String)params.get("password")));
            if (!params.containsKey("countrycode")) {
                params.put("countrycode", "86");
            }

            if (!params.containsKey("rememberLogin")) {
                params.put("rememberLogin", "true");
            }

            params.put("csrf_token", this.csrf);
            this.loginResult = this.parseLoginResult(this.request.getHttpResponseToString("https://music.163.com/weapi/login/cellphone", header, this.crypto.weapi(params)));
            this.loginResult.setCookieTime(cookieTime);
            return this.loginResult.getCode() == 200 ? "true" : "false";
        } else {
            throw new NullPointerException("登录账号或密码不能为空不能为空");
        }
    }

    public String lyric(HashMap<String, String> params) throws Exception {
        if (params != null && params.containsKey("id")) {
            params.put("lv", "-1");
            params.put("kv", "-1");
            params.put("tv", "-1");
            String expand = (String)params.remove("expand");
            LyricResult result = this.parseLyricResult(this.request.getHttpResponseToString("https://music.163.com/weapi/song/lyric", (HashMap)null, this.crypto.weapi(params)));
            result.setExpand(expand);
            result.setType(1006);
            return JSON.toJSONString(result);
        } else {
            throw new NullPointerException("音乐ID不能为空");
        }
    }

    private String generateHash(String input) {
        try {
            if (input == null) {
                return null;
            } else {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(input.getBytes());
                byte[] digest = md.digest();
                BigInteger bi = new BigInteger(1, digest);

                String hashText;
                for(hashText = bi.toString(16); hashText.length() < 32; hashText = "0" + hashText) {
                }

                return hashText;
            }
        } catch (Exception var6) {
            var6.printStackTrace();
            return "";
        }
    }

    private String generateCookie(long time) {
        this.csrf = this.generateHash(String.valueOf(time));
        this.cookie = "__remember_me=true; NMTID=00OR_FXEuGnOaiT90Osqs6oNHK19sMAAAF2EYm4tw; __csrf=" + this.csrf + " MUSIC_U=" + "5f0c0de61b91edba04d9068e1173a225fe7a54bbc0ce4909cfe212ddd001016033a649814e309366" + "; os=pc";
        return this.cookie;
    }
}
