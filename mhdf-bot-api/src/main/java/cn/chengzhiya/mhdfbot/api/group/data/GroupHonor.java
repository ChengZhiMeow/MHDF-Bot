package cn.chengzhiya.mhdfbot.api.group.data;

import com.alibaba.fastjson2.JSONObject;

public record GroupHonor(long userId, String avatar, String nickname, int dayCount, String description) {
    public static GroupHonor fromJson(JSONObject data) {
        return new GroupHonor(
                data.getLong("user_id"),
                data.getString("avatar"),
                data.getString("nickname"),
                data.getInteger("day_count"),
                data.getString("description")
        );
    }
}