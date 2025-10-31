package cn.chengzhiya.mhdfbotbukkithook.event;

import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
@SuppressWarnings("unused")
public final class WebSocketEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public @NotNull
    static HandlerList getHandlerList() {
        return WebSocketEvent.handlers;
    }

    private final JSONObject data;

    public WebSocketEvent(JSONObject data) {
        super(true);
        this.data = data;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return WebSocketEvent.handlers;
    }
}
