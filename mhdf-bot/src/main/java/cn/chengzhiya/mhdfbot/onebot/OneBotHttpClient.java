package cn.chengzhiya.mhdfbot.onebot;

import cn.chengzhiya.mhdfbot.api.MHDFBot;
import cn.chengzhiya.mhdfbot.api.http.AbstractHttpClient;

public final class OneBotHttpClient extends AbstractHttpClient {
    private final String httpHost = MHDFBot.getBot().getBotConfig().getString("httpHost");

    public OneBotHttpClient() {
        setAccessToken("Bearer " + MHDFBot.getBot().getBotConfig().getString("accessToken"));
    }

    @Override
    public String formatUrl(String urlString) {
        return httpHost + urlString;
    }
}
