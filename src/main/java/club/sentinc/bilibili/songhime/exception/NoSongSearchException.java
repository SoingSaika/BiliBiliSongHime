package club.sentinc.bilibili.songhime.exception;

public class NoSongSearchException extends RuntimeException{

    public NoSongSearchException() {
        super("歌曲没有找到");
    }

}
