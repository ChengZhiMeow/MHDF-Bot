package cn.chengzhiya.mhdfbot.api.user.data;

import cn.chengzhiya.mhdfbot.api.user.type.SexType;
import com.alibaba.fastjson2.JSONObject;

public record Friend(long userId, String nickName, String remark, SexType sex, Integer level) {
    public static Friend fromJson(JSONObject data) {
        return new Friend(
                data.getLong("user_id"),
                data.getString("nickname"),
                data.getString("remark"),
                SexType.get(data.getString("sex")),
                data.getInteger("level")
        );
    }
}