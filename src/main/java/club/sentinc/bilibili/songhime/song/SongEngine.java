package club.sentinc.bilibili.songhime.song;

import club.sentinc.bilibili.songhime.config.SongHimeConfig;
import club.sentinc.bilibili.songhime.exception.NoSongSearchException;
import club.sentinc.bilibili.songhime.exception.NotLoginException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.goxr3plus.streamplayer.enums.Status;
import com.goxr3plus.streamplayer.stream.StreamPlayer;
import com.goxr3plus.streamplayer.stream.StreamPlayerEvent;
import com.goxr3plus.streamplayer.stream.StreamPlayerException;
import com.goxr3plus.streamplayer.stream.StreamPlayerListener;
import javazoom.jl.player.JavaSoundAudioDevice;
import org.apache.commons.io.FileUtils;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import work.sentinc.musiccaster.bean.FmResult;
import work.sentinc.musiccaster.bean.MusicResult;
import work.sentinc.musiccaster.bean.login.Account;
import work.sentinc.musiccaster.bean.music.Result;
import work.sentinc.musiccaster.bean.music.Song;

import javax.sound.sampled.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static club.sentinc.bilibili.songhime.SongHimeApplication.getConfig;

public class SongEngine extends NeteaseCloudMusic implements StreamPlayerListener {

    private Player player = new Player();

    private SongHimeConfig config;

    private List<Song> orderSongQueue;

    private List<Song> randomSongQueue;

    public Executor songMotionExecutor = Executors.newSingleThreadExecutor();

    public SongEngine() throws Exception {
        config = getConfig();
        HashMap<String, String> loginParams = new HashMap<>();
        loginParams.put("phone", config.getLoginPhone());
        loginParams.put("password", config.getLoginPassword());
        login(loginParams);
        orderSongQueue = new ArrayList<>();
        randomSongQueue = new ArrayList<>();
    }

    @Override
    protected void loadConfig(String configPath) {
    }

    @Override
    public void saveConfig(String configPath) {
    }

    public void doAction(String action) {
        config.containsAction(action).ifPresent(songAction -> {
            switch (SongActionType.valueOf(songAction.action.getAction())) {
                case NEXT:
                    player.stop();
                    try {
                        play();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    break;
                case PERVIOUS:
                    break;
                case PAUSE:
                    break;
                case SEARCH:
                    try {
                        Optional<Song> songOptional = firstSong(songAction.simplifyCommand);
                        if (songOptional.isPresent()) {
                            addSongToQueue(songOptional.get());
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    break;
            }
        });
    }

    public Optional<File> getDownloadHistorySong(String songName) {
        String downloadPathStr = getConfig().getDownloadPath();
        File downloadPath = new File(downloadPathStr);
        if (downloadPath.exists()) {
            File[] childFiles = downloadPath.listFiles();
            if (childFiles != null) {
                for (File childFile : childFiles) {
                    if (childFile.getName().equals(songName.concat(".mp3"))) {
                        return Optional.of(childFile);
                    }
                }
            }
        }
        return Optional.empty();
    }

    //TODO
    public void getPlayList(long uid, int limit, int offset) throws Exception {
        HashMap<String, String> params = new HashMap<>();
        params.put("uid", String.valueOf(uid));
        params.put("limit", String.valueOf(limit));
        params.put("offset", String.valueOf(offset));
        String json = this.request.getHttpResponseToString(playListUrl, null, this.crypto.weapi(params));
        System.out.println(json);
    }

    public List<Integer> getLikeMusicListIds(long uid) throws Exception {
        HashMap<String, String> params = new HashMap<>();
        params.put("uid", String.valueOf(uid));
        String json = this.request.getHttpResponseToString(likeListUrl, null, this.crypto.weapi(params));
        System.out.println(json);
        return new ArrayList<>();
    }

    public List<Integer> getLoginAccountLikeMusicListIds() throws Exception {
        if(loginResult != null) {
            Account account = loginResult.getAccount();
            if(account != null) {
                long uid = account.getId();
                return getLikeMusicListIds(uid);
            }
        }
        throw new NotLoginException();
    }

    public void play() throws Exception {
        play(getQueueNextSong());
    }

    public void play(Song song) throws Exception {
        player.downloadAndOpen(song.getPlayUrl(), song.getName());
        player.play();
    }

    public Optional<Song> firstSong(String songName) throws Exception {
        HashMap<String, String> params = new HashMap<>();
        params.put("s", songName);
        params.put("parse", "");
        MusicResult mresult = this.searchAndParse(params);
        Result result = mresult.getResult();
        if (result != null && result.getSongs() != null && result.getSongs().size() != 0) {
            Song song = result.getSongs().get(0);
            song.setPlayUrl(generateMusicUrl(song));
            return Optional.of(song);
        }
        return Optional.empty();
    }

    private String generateMusicUrl(Song song) {
        return "https://music.163.com/song/media/outer/url?id=" + song.getId() + ".mp3";
    }

    protected MusicResult searchAndParse(HashMap<String, String> params) throws Exception {
        if (params != null && params.containsKey("s")) {
            String url = "https://music.163.com/weapi/search/get";
            String expand = (String) params.remove("expand");
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

            String json = this.request.getHttpResponseToString(url, (HashMap) null, this.crypto.weapi(params));
            MusicResult mresult = this.parseMusicResult(json);
            mresult.setExpand(expand);
            mresult.getResult().setExpand(expand);
            return mresult;
        } else {
            throw new NullPointerException("歌曲名称不能为空");
        }
    }

    private Optional<File> downloadMusic(String url, String name) throws IOException {
        Optional<File> history = getDownloadHistorySong(name);
        if (history.isPresent()) {
            return history;
        }
        String downloadPathStr = getConfig().getDownloadPath();
        if (downloadPathStr != null) {
            File downloadPath = new File(downloadPathStr.concat("/").concat(name).concat(".mp3"));
            File parentPath = downloadPath.getParentFile();
            if (!parentPath.exists()) {
                parentPath.mkdirs();
            }
            if (!downloadPath.exists()) {
                downloadPath.createNewFile();
            }
            HttpPost httpPost = new HttpPost(url);
            try (FileOutputStream fos = new FileOutputStream(downloadPath);
                 CloseableHttpClient httpClient = HttpClients.createDefault();
                 CloseableHttpResponse response = httpClient.execute(httpPost);
                 InputStream is = response.getEntity().getContent()) {
                byte[] buffer = new byte[1024];
                int byteread = 0;
                while ((byteread = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, byteread);
                }
                fos.flush();
                return Optional.of(downloadPath);
            } catch (IOException e) {
                e.printStackTrace();
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    public void addSongToQueue(Song song) throws Exception {
        if (player.getStatus() == Status.STOPPED || player.getStatus() == Status.NOT_SPECIFIED) {
            play(song);
        } else {
            orderSongQueue.add(song);
            Status status = player.getStatus();
        }
    }

    private Song getQueueNextSong() throws Exception {
        if (orderSongQueue.size() == 0) {
            return getRandomQueueNextSong();
        } else {
            return orderSongQueue.remove(0);
        }
    }

    private Song getRandomQueueNextSong() throws Exception {
        if (randomSongQueue.size() == 0) {
            List<Song> songList = random();
            randomSongQueue.addAll(songList);
        }
        return randomSongQueue.remove(0);
    }

    public List<Song> random() throws Exception {
        String randomJson = random(new HashMap<>());
        JSONObject randomJsonObject = JSON.parseObject(randomJson);
        if (randomJsonObject.getInteger("code") == 200) {
            FmResult result = JSON.toJavaObject(randomJsonObject, FmResult.class);
            List<Song> randomSongs = result.getData();
            for (Song randomSong : randomSongs) {
                randomSong.setPlayUrl(generateMusicUrl(randomSong));
            }
            return randomSongs;
        }
        throw new NoSongSearchException();
    }

    @Override
    public void opened(Object o, Map<String, Object> map) {
    }

    @Override
    public void progress(int i, long l, byte[] bytes, Map<String, Object> map) {

    }

    @Override
    public void statusUpdated(StreamPlayerEvent streamPlayerEvent) {
        System.out.println(streamPlayerEvent.getPlayerStatus());
        switch (streamPlayerEvent.getPlayerStatus()) {
            case INIT:
                break;
            case NOT_SPECIFIED:
                break;
            case OPENING:
                break;
            case OPENED:
                break;
            case PLAYING:
                break;
            case EOM:
            case STOPPED:
                songMotionExecutor.execute(() -> {
                    try {
                        play();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                });
                break;
            case PAUSED:
                break;
            case RESUMED:
                break;
            case SEEKING:
                break;
            case BUFFERING:
                break;
            case SEEKED:
                break;
            case PAN:
                break;
            case GAIN:
                break;
        }
    }

    private class Player extends StreamPlayer {


        public Player() {
            addStreamPlayerListener(SongEngine.this);
        }

        public void downloadAndOpen(String url, String name) throws IOException {
            downloadMusic(url, name).ifPresent(
                    file -> {
                        try {
                            open(file);
                        } catch (StreamPlayerException e) {
                            e.printStackTrace();
                        }
                    }
            );
        }
    }

}
