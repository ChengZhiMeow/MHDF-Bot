package cn.chengzhiya.mhdfbot.bot.onebot;

import cn.chengzhiya.mhdfbot.api.MHDFBot;
import cn.chengzhiya.mhdfhttpframework.client.HttpClient;

public final class MHDFOneBotHttpClient extends HttpClient {
    private final String httpHost = MHDFBot.getBot().getBotConfig().getString("httpHost");

    public MHDFOneBotHttpClient() {
        super();

        String token = MHDFBot.getBot().getBotConfig().getString("accessToken");
        if (token != null && !token.isEmpty())
            this.getHeaderHashMap().put("Authorization", "Bearer " + token);
    }

    @Override
    public String formatUrl(String urlString) {
        return this.httpHost + urlString;
    }
}
