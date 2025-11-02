package cn.chengzhiya.mhdfbotbungeehook.minecraft;

import cn.chengzhiya.mhdfbotbungeehook.Main;
import cn.chengzhiya.mhdfbotbungeehook.event.WebSocketEvent;
import com.alibaba.fastjson2.JSONObject;
import jakarta.websocket.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@ClientEndpoint
@SuppressWarnings({"unused", "CallToPrintStackTrace"})
public final class WebSocketClient {
    private final WebSocketContainer container = ContainerProvider.getWebSocketContainer();
    private final URI host;
    private final String accessToken;
    public Session session;

    public WebSocketClient() {
        try {
            this.host = new URI("ws://" + Main.instance.getConfig().getString("minecraft_websocket_settings.host"));
            this.accessToken = Main.instance.getConfig().getString("minecraft_websocket_settings.access_token");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 连接服务器
     */
    public void connectServer() {
        try {
            ClientEndpointConfig clientEndpointConfig = ClientEndpointConfig.Builder.create()
                    .configurator(new ClientEndpointConfig.Configurator() {
                        @Override
                        public void beforeRequest(Map<String, List<String>> headers) {
                            String token = WebSocketClient.this.accessToken;
                            if (token != null && !token.isEmpty())
                                headers.put("Authorization", List.of(token));
                        }
                    })
                    .build();

            this.container.connectToServer(this, this.host);
        } catch (DeploymentException | IOException e) {
            Main.instance.getLogger().info("无法正常连接至Minecraft-WebSocket服务端");
            e.printStackTrace();
            Main.instance.getProxy().getScheduler().schedule(Main.instance, this::connectServer, 5, TimeUnit.SECONDS);
        }
    }

    /**
     * 向服务器发送消息
     *
     * @param action 操作
     * @param data   数据
     */
    public void send(String action, JSONObject data) {
        try {
            if (this.session != null && this.session.isOpen()) {
                JSONObject message = new JSONObject();
                message.put("server_type", "bukkit");
                message.put("action", action);
                message.put("data", data);

                this.session.getAsyncRemote().sendText(message.toJSONString());
            }
            this.session = null;
        } catch (Exception ignored) {
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
    }

    @OnMessage
    public void onMessage(String message) {
        Main.instance.getProxy().getPluginManager().callEvent(new WebSocketEvent(JSONObject.parseObject(message)));
    }

    @OnClose
    public void onClose() {
        this.session = null;
        this.connectServer();
    }

    @OnError
    public void onError(Throwable e) {
        this.session = null;
        this.connectServer();
        e.printStackTrace();
    }
}
