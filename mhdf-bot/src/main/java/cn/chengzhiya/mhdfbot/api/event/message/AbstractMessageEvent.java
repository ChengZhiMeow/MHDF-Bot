package cn.chengzhiya.mhdfbot.api.event.message;

import cn.chengzhiya.mhdfbot.api.MHDFBot;
import cn.chengzhiya.mhdfbot.api.entity.user.Sender;
import cn.chengzhiya.mhdfbot.api.enums.bot.OpenIdType;
import cn.chengzhiya.mhdfbot.api.enums.message.MessageSubType;
import cn.chengzhiya.mhdfbot.api.enums.message.MessageType;
import cn.chengzhiya.mhdfbot.api.event.AbstractEvent;
import cn.chengzhiya.mhdfbot.api.util.OpenIdCacheUtil;
import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;

@Getter
public abstract class AbstractMessageEvent extends AbstractEvent {
    private MessageType messageType;
    private MessageSubType subType;
    private String message;
    private Integer messageId;
    private Sender sender;

    public AbstractMessageEvent(JSONObject data) {
        super(data);
        switch (MHDFBot.getBotType()) {
            case QQBOT -> {
                this.messageType = MessageType.GROUP;
                this.subType = MessageSubType.GROUP;

                String content = data.getString("content");
                if (content.startsWith(" ")) {
                    content = content.substring(1);
                }
                if (content.endsWith(" ")) {
                    content = content.substring(0, content.length() - 1);
                }

                this.message = content;
                this.messageId = OpenIdCacheUtil.addData(OpenIdType.MESSAGE, data.getString("id"));
                this.sender = new Sender(data.getJSONObject("author"));
            }
            case ONEBOT -> {
                this.messageType = MessageType.get(data.getString("message_type"));
                this.subType = MessageSubType.get(data.getString("sub_type"));
                this.message = data.getString("raw_message");
                this.messageId = data.getIntValue("message_id");
                this.sender = new Sender(data.getJSONObject("sender"));
            }
        }
    }
}
