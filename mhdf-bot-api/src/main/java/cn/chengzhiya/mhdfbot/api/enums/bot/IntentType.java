package cn.chengzhiya.mhdfbot.api.enums.bot;

import lombok.Getter;

@Getter
public enum IntentType {
    GUILDS(0),
    GUILD_MEMBERS(1),
    GUILD_MESSAGES(9),
    GUILD_MESSAGE_REACTIONS(10),
    DIRECT_MESSAGE(12),
    GROUP_AND_C2C_EVENT(25),
    INTERACTION(26),
    MESSAGE_AUDIT(27),
    FORUMS_EVENT(28),
    AUDIO_ACTION(29),
    PUBLIC_GUILD_MESSAGES(30);

    private final int flag;

    IntentType(int flag) {
        this.flag = flag;
    }
}
