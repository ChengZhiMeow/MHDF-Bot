package cn.chengzhiya.mhdfbot.api.websocket;

import jakarta.websocket.CloseReason;
import jakarta.websocket.EndpointConfig;

public interface WebSocketClient {
    /**
     * 格式化请求地址
     *
     * @param urlString 请求地址
     * @return 格式化后的地址
     */
    String formatUrl(String urlString);

    /**
     * 连接服务器
     */
    void connectServer();

    /**
     * 向服务器发送消息
     *
     * @param message 消息
     */
    void send(String message);

    /**
     * 处理连接打开
     *
     * @param config 端点配置实例
     */
    void open(EndpointConfig config);


    /**
     * 处理连接关闭
     *
     * @param closeReason 关闭原因实例
     */
    void close(CloseReason closeReason);

    /**
     * 处理报错
     *
     * @param throwable 报错实例
     */
    void error(Throwable throwable);

    /**
     * 处理消息
     *
     * @param message 消息
     */
    void handleMessage(String message);
}
