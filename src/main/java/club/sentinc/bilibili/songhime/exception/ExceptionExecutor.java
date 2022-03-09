package club.sentinc.bilibili.songhime.exception;

public final class ExceptionExecutor {

    private static ExceptionShowHandler showHandler;

    public static void throwException(Throwable throwable) {
        if(throwable != null) {
            showHandler.show(throwable);
        }
    }

    public static void throwsException(Throwable... throwables) {
        if(throwables != null && throwables.length != 0) {
            for (Throwable throwable : throwables) {
                throwException(throwable);
            }
        }
    }

    public static void setExceptionShowHandler(ExceptionShowHandler showHandler) {
        ExceptionExecutor.showHandler = showHandler;
    }

}
