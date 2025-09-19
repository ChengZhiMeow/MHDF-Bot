package cn.chengzhiya.mhdfbot.onebot;

import cn.chengzhiya.mhdfbot.api.MHDFBot;
import cn.chengzhiya.mhdfhttpframework.client.HttpClient;

public final class OneBotHttpClient extends HttpClient {
    private final String httpHost = MHDFBot.getBot().getBotConfig().getString("httpHost");

    public OneBotHttpClient() {
        this.getHeaderHashMap().put("Authorization", "Bearer " + MHDFBot.getBot().getBotConfig().getString("accessToken"));
    }

    @Override
    public String formatUrl(String urlString) {
        return this.httpHost + urlString;
    }
}
