package cn.chengzhiya.mhdfbot.api.bot.data;

import com.alibaba.fastjson2.JSONObject;

public record BotStatus(boolean online, boolean good) {
    public static BotStatus fromJson(JSONObject data) {
        return new BotStatus(
                data.getBoolean("online"),
                data.getBoolean("good")
        );
    }
}
