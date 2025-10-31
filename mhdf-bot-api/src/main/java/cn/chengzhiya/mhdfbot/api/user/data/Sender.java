package cn.chengzhiya.mhdfbot.api.user.data;

import cn.chengzhiya.mhdfbot.api.MHDFBot;
import cn.chengzhiya.mhdfbot.api.bot.type.OpenIdType;
import cn.chengzhiya.mhdfbot.api.manager.OpenIdCacheManager;
import cn.chengzhiya.mhdfbot.api.user.type.RoleType;
import cn.chengzhiya.mhdfbot.api.user.type.SexType;
import com.alibaba.fastjson2.JSONObject;

public record Sender(
        long userId,
        String nickName,
        String card,
        String longNick,
        SexType sex,
        Integer age,
        String area,
        String level,
        RoleType role,
        String title
) {
    public static Sender fromJson(JSONObject data) {
        switch (MHDFBot.getBotType()) {
            case QQBOT -> {
                long userId = OpenIdCacheManager.getInstance().addData(OpenIdType.USER, data.getString("id"));
                String nickName = String.valueOf(userId);
                return new Sender(userId, nickName, null, null, null, null, null, null, null, null);
            }
            case ONEBOT -> {
                return new Sender(
                        data.getLong("user_id"),
                        data.getString("nickname"),
                        data.getString("card"),
                        data.getString("longNick"),
                        SexType.get(data.getString("sex")),
                        data.getInteger("age"),
                        data.getString("area"),
                        data.getString("level"),
                        RoleType.get(data.getString("role")),
                        data.getString("title")
                );
            }
            default -> throw new IllegalStateException("Unknown bot type: " + MHDFBot.getBotType());
        }
    }
}