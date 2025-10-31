package cn.chengzhiya.mhdfbot.bot.qqbot;

import cn.chengzhiya.mhdfhttpframework.server.HttpServer;
import cn.chengzhiya.mhdfhttpframework.server.entity.SSLConfig;
import lombok.Getter;

@Getter
public final class MHDFQqBotHttpServer extends HttpServer {
    public MHDFQqBotHttpServer(int port, SSLConfig sslConfig) {
        super(port, sslConfig);
    }
}
