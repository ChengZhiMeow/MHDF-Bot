package cn.chengzhiya.mhdfbot.api.event.request;

import cn.chengzhiya.mhdfbot.api.MHDFBot;
import cn.chengzhiya.mhdfbot.api.notice.type.request.RequestSubType;
import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;

@Getter
@SuppressWarnings("unused")
public final class GroupRequestEvent extends AbstractRequestEvent {
    private final RequestSubType subType;
    private final Long groupId;

    public GroupRequestEvent(JSONObject data) {
        super(data);
        this.subType = RequestSubType.get(data.getString("sub_type"));
        this.groupId = data.getLong("group_id");
    }

    public void handlingGroupRequest(boolean accept) {
        MHDFBot.handleGroupAddRequest(super.getFlag(), this.getSubType(), accept);
    }
}
