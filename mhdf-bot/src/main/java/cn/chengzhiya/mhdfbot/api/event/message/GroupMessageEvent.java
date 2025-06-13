package cn.chengzhiya.mhdfbot.api.event.message;

import cn.chengzhiya.mhdfbot.api.MHDFBot;
import cn.chengzhiya.mhdfbot.api.enums.bot.OpenIdType;
import cn.chengzhiya.mhdfbot.api.enums.user.RoleType;
import cn.chengzhiya.mhdfbot.api.util.OpenIdCacheUtil;
import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;

@Getter
public final class GroupMessageEvent extends AbstractMessageEvent {
    private Long groupId;
    private RoleType senderRole;

    public GroupMessageEvent(JSONObject data) {
        super(data);
        switch (MHDFBot.getBotType()) {
            case QQBOT -> {
                this.groupId = (long) OpenIdCacheUtil.addData(OpenIdType.GROUP, data.getString("group_id"));
            }
            case ONEBOT -> {
                this.groupId = data.getLong("group_id");
                this.senderRole = RoleType.get(data.getJSONObject("sender").getString("role"));
            }
        }
    }
}
