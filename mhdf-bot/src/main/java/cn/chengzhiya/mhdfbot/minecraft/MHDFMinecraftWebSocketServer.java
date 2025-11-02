package cn.chengzhiya.mhdfbot.minecraft;

import cn.chengzhiya.mhdfbot.Main;
import cn.chengzhiya.mhdfbot.api.MHDFBot;
import cn.chengzhiya.mhdfbot.api.event.minecraft.MinecraftWebsocketMessageEvent;
import cn.chengzhiya.mhdfbot.lang.Languages;
import cn.chengzhiya.mhdfbot.thread.MHDFScheduledThread;
import com.alibaba.fastjson2.JSONObject;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

@ServerEndpoint("/ws")
@SuppressWarnings("unused")
public final class MHDFMinecraftWebSocketServer {
    private final int port = Main.getConfigManager().getData().getInt("minecraft_websocket_settings.port");
    private final String accessToken = Main.getConfigManager().getData().getString("minecraft_websocket_settings.access_token");
    private final MHDFScheduledThread thread = new MHDFScheduledThread("MHDF-Bot Minecraft-Websocket-HeartBeat Thread") {
    };
    private final Set<Session> sessions = new CopyOnWriteArraySet<>();

    /**
     * 启动服务器
     */
    public void startServer() {
        try {
            Logger tomcatLogger = Logger.getLogger("org.apache");
            tomcatLogger.setLevel(Level.OFF);
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.OFF);
            tomcatLogger.addHandler(consoleHandler);

            Tomcat tomcat = new Tomcat();
            tomcat.setPort(this.port);
            tomcat.getConnector();

            tomcat.start();
            MHDFBot.getLogger().info(Languages.MINECRAFT_WEBSOCKET_START_DONE, this.port);
            tomcat.getServer().await();
        } catch (LifecycleException e) {
            throw new RuntimeException(e);
        }

        this.thread.schedule(() -> this.send("heart_beat", new JSONObject()), 0, 1000);
    }

    /**
     * 向所有连接客户端发送消息
     *
     * @param action 操作
     * @param data   数据
     */
    public void send(String action, JSONObject data) {
        for (Session session : this.sessions) {
            this.send(session, action, data);
        }
    }

    /**
     * 向指定客户端发送消息
     *
     * @param session 客户端实例
     * @param action  操作
     * @param data    数据
     */
    public void send(Session session, String action, JSONObject data) {
        try {
            JSONObject sendData = new JSONObject();
            sendData.put("action", action);
            sendData.put("data", data);

            session.getBasicRemote().sendText(sendData.toJSONString());
        } catch (IOException e) {
            this.disableSession(session);
            throw new RuntimeException(e);
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        if (this.accessToken != null && !this.accessToken.isEmpty()) {
            String token = session.getUserProperties().get("Authorization").toString();
            if (token == null || !token.equals(this.accessToken)) {
                try {
                    session.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return;
            }
        }
        this.sessions.add(session);
        MHDFBot.getLogger().info(Languages.MINECRAFT_WEBSOCKET_CONNECT, session.getId());
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        JSONObject data = JSONObject.parseObject(message);
        MHDFBot.getListenerManager().callEvent(new MinecraftWebsocketMessageEvent(data));
    }

    @OnClose
    @OnError
    public void onClose(Session session) {
        this.disableSession(session);
        MHDFBot.getLogger().info(Languages.MINECRAFT_WEBSOCKET_DISCONNECT, session.getId());
    }

    private void disableSession(Session session) {
        if (session == null) return;

        for (MessageHandler handler : session.getMessageHandlers()) {
            try {
                session.removeMessageHandler(handler);
            } catch (Throwable ignored) {
            }
        }
        this.sessions.remove(session);
    }
}
