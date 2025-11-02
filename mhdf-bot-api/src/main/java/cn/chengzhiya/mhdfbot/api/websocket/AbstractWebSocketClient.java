package cn.chengzhiya.mhdfbot.api.websocket;

import cn.chengzhiya.mhdfbot.api.MHDFBot;
import jakarta.websocket.*;
import lombok.Setter;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public abstract class AbstractWebSocketClient extends Endpoint implements WebSocketClient {
    private final String urlString;
    private final boolean closeReConnect;
    private final WebSocketContainer container = ContainerProvider.getWebSocketContainer();
    @Setter
    private String accessToken;
    private Session session;

    public AbstractWebSocketClient(String urlString, boolean closeReConnect) {
        this.urlString = urlString;
        this.closeReConnect = closeReConnect;
    }

    public AbstractWebSocketClient(String urlString) {
        this(urlString, false);
    }

    @Override
    public String formatUrl(String urlString) {
        return urlString;
    }

    @Override
    public void connectServer() {
        if (this.session != null && this.session.isOpen()) return;

        try {
            ClientEndpointConfig clientEndpointConfig = ClientEndpointConfig.Builder.create()
                    .configurator(new ClientEndpointConfig.Configurator() {
                        @Override
                        public void beforeRequest(Map<String, List<String>> headers) {
                            String token = AbstractWebSocketClient.this.accessToken;
                            if (token != null && !token.isEmpty())
                                headers.put("Authorization", List.of(token));
                        }
                    })
                    .build();

            this.container.connectToServer(this, clientEndpointConfig, new URI(this.urlString));
        } catch (DeploymentException | IOException | URISyntaxException e) {
            MHDFBot.getLogger().error("无法正常连接至WebSocket服务端,5秒后重试!", e);
            MHDFBot.getScheduler().runTaskLater(this::connectServer, 5000);
        }
    }

    @Override
    public void send(String message) {
        if (this.session != null) {
            if (this.session.isOpen()) {
                this.session.getAsyncRemote().sendText(message);
                return;
            }

            this.disableSession();
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        this.session = session;

        if (session.getMessageHandlers().isEmpty())
            session.addMessageHandler(String.class, this::handleMessage);
        MHDFBot.getLogger().info("WebSocket服务端连接成功({})!",
                this.urlString
        );

        this.open(config);
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        this.disableSession();
        MHDFBot.getLogger().info("WebSocket服务端已离线({})!",
                this.urlString
        );

        this.close(closeReason);
        if (this.closeReConnect) this.connectServer();
    }

    @OnError
    public void onError(Session session, Throwable e) {
        this.disableSession();
        MHDFBot.getLogger().error("WebSocket服务端遇到错误!", e);

        this.error(e);
        if (this.closeReConnect) this.connectServer();
    }

    @Override
    public void open(EndpointConfig config) {
    }

    @Override
    public void close(CloseReason closeReason) {
    }

    @Override
    public void error(Throwable throwable) {
    }

    @Override
    public void handleMessage(String message) {
    }

    private void disableSession() {
        if (this.session == null) return;

        for (MessageHandler handler : this.session.getMessageHandlers()) {
            try {
                this.session.removeMessageHandler(handler);
            } catch (Throwable ignored) {
            }
        }
        this.session = null;
    }
}
