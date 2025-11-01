package cn.chengzhiya.mhdfbot.bot.onebot;

import cn.chengzhimeow.ccyaml.configuration.ConfigurationSection;
import cn.chengzhiya.mhdfhttpframework.client.HttpClient;

public final class MHDFOneBotHttpClient extends HttpClient {
    private final String httpHost;

    public MHDFOneBotHttpClient(ConfigurationSection botConfig) {
        super();

        this.httpHost = botConfig.getString("http_host");
        String token = botConfig.getString("access_token");
        if (token != null && !token.isEmpty())
            this.getHeaderHashMap().put("Authorization", "Bearer " + token);
    }

    @Override
    public String formatUrl(String urlString) {
        return this.httpHost + urlString;
    }
}
