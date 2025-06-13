package cn.chengzhiya.mhdfbot.api.entity.user;

import cn.chengzhiya.mhdfbot.api.MHDFBot;
import cn.chengzhiya.mhdfbot.api.enums.bot.OpenIdType;
import cn.chengzhiya.mhdfbot.api.enums.user.RoleType;
import cn.chengzhiya.mhdfbot.api.enums.user.SexType;
import cn.chengzhiya.mhdfbot.api.util.OpenIdCacheUtil;
import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;

@Getter
public final class Sender {
    private Long userId;
    private String nickName;
    private String card;
    private String longNick;
    private SexType sex;
    private Integer age;
    private String area;
    private String level;
    private RoleType role;
    private String title;

    public Sender(JSONObject data) {
        switch (MHDFBot.getBotType()) {
            case QQBOT -> {
                this.userId = (long) OpenIdCacheUtil.addData(OpenIdType.USER, data.getString("id"));
                this.nickName = String.valueOf(userId);
            }
            case ONEBOT -> {
                this.userId = data.getLong("user_id");
                this.nickName = data.getString("nickname");
                this.card = data.getString("card");
                this.longNick = data.getString("longNick");
                this.sex = SexType.get(data.getString("sex"));
                this.age = data.getInteger("age");
                this.area = data.getString("area");
                this.level = data.getString("level");
                this.role = RoleType.get(data.getString("role"));
                this.title = data.getString("title");
            }
        }
    }
}
