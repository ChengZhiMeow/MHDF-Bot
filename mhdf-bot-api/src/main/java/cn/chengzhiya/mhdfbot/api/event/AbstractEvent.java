package cn.chengzhiya.mhdfbot.api.event;

import cn.chengzhiya.mhdfbot.api.MHDFBot;
import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;

@Getter
public abstract class AbstractEvent implements Event {
    private final JSONObject data;
    private final Long selfId;

    public AbstractEvent(JSONObject data) {
        this.data = data;
        this.selfId = switch (MHDFBot.getBotType()) {
            case ONEBOT -> data.getLong("self_id");
            case QQBOT -> MHDFBot.getBot().getBotConfig().getLong("qq");
        };
    }
}
