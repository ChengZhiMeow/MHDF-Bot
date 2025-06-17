package cn.chengzhiya.mhdfbot.api.http;

import java.net.HttpURLConnection;

public interface HttpClient {
    /**
     * 格式化请求地址
     *
     * @param urlString 请求地址
     * @return 格式化后的地址
     */
    String formatUrl(String urlString);

    /**
     * 获取连接实例
     *
     * @param urlString 请求地址
     * @return 连接地址
     */
    HttpURLConnection getConnection(String urlString);

    /**
     * 获取指定连接实例的数据
     *
     * @param connection 连接实例
     * @return 数据
     */
    String getData(HttpURLConnection connection);

    /**
     * 发送GET请求获取数据
     *
     * @param urlString 请求地址
     * @return 数据
     */
    String get(String urlString);

    /**
     * 发送POST请求获取数据
     *
     * @param urlString 请求地址
     * @param data      负载数据
     * @return 数据
     */
    String post(String urlString, String data);
}
