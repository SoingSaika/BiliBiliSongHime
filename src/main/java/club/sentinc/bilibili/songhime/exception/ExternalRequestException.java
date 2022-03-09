package club.sentinc.bilibili.songhime.exception;

import java.net.URL;

public class ExternalRequestException extends RuntimeException {
    public ExternalRequestException(URL url) {
        super("请求外部链接错误：" + url.getHost());
    }
}
