package cn.chengzhiya.mhdfbot.api.util;

public final class ErrorUtil {
    /**
     * 转换异常堆栈为字符串
     *
     * @param e 异常实例
     * @return 字符串
     */
    public static String getErrorMessage(Throwable e) {
        StringBuilder stringBuilder = new StringBuilder();

        StackTraceElement[] trace = e.getStackTrace();
        for (StackTraceElement element : trace) {
            stringBuilder.append(element.toString()).append("\n");
        }

        return stringBuilder.toString();
    }
}
