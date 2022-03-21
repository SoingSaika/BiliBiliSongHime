package club.sentinc.bilibili.songhime.exception;

public class NotLoginException extends RuntimeException{

    public NotLoginException() {
        super("尚未登录到音乐引擎");
    }

}
