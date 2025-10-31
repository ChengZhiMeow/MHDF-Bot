package cn.chengzhiya.mhdfbot.api.bot.data;

import com.alibaba.fastjson2.JSONObject;

public record BotVersionInfo(String appName, String appVersion, String protocolVersion) {
    public static BotVersionInfo fromJson(JSONObject data) {
        return new BotVersionInfo(
                data.getString("app_name"),
                data.getString("app_version"),
                data.getString("protocol_version")
        );
    }
}
