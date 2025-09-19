package cn.chengzhiya.mhdfbot.qqbot;

import cn.chengzhiya.mhdfhttpframework.server.HttpServer;
import cn.chengzhiya.mhdfhttpframework.server.entity.SSLConfig;
import lombok.Getter;

@Getter
public final class QqBotHttpServer extends HttpServer {
    public QqBotHttpServer(int port, SSLConfig sslConfig) {
        super(port, sslConfig);
    }
}
