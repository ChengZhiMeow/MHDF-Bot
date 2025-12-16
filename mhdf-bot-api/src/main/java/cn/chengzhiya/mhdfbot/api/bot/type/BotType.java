package cn.chengzhiya.mhdfbot.api.bot.type;

public enum BotType {
    QQBOT,
    ONEBOT;

    @Override
    public String toString() {
        return this.name();
    }
}
