package cn.chengzhiya.mhdfbot.api.bot.data;

import com.alibaba.fastjson2.JSONObject;

public record BotLoginInfo(Long userId, String nickname) {
    public static BotLoginInfo fromJson(JSONObject data) {
        return new BotLoginInfo(
                data.getLong("user_id"),
                data.getString("nickname")
        );
    }
}
