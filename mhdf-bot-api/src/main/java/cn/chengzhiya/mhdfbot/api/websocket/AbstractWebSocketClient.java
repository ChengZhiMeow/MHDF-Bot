package cn.chengzhiya.mhdfbot.api.websocket;

import cn.chengzhiya.mhdfbot.api.MHDFBot;
import jakarta.websocket.*;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
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
        try {
            ClientEndpointConfig clientEndpointConfig = ClientEndpointConfig.Builder.create()
                    .configurator(new ClientEndpointConfig.Configurator() {
                        @Override
                        public void beforeRequest(Map<String, List<String>> headers) {
                            if (this.getAccessToken() != null) {
                                headers.put("Authorization", Collections.singletonList(this.getAccessToken()));
                            }
                        }
                    })
                    .build();

            this.getContainer().connectToServer(this, clientEndpointConfig, new URI(this.getUrlString()));
        } catch (DeploymentException | IOException | URISyntaxException e) {
            MHDFBot.getLogger().info("无法正常连接至websocket服务端,正在重试!");
            MHDFBot.getScheduler().runTaskLater(this::connectServer, 5L);
        }
    }

    @Override
    public void send(String message) {
        try {
            if (this.getSession() != null && this.getSession().isOpen()) {
                this.getSession().getAsyncRemote().sendText(message);
                return;
            }

            this.session = null;
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        this.session = session;
        this.open(config);

        session.addMessageHandler(String.class, this::handleMessage);
        MHDFBot.getLogger().info("websocket服务端连接成功({})!",
                this.getUrlString()
        );
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        this.close(closeReason);

        this.session = null;
        MHDFBot.getLogger().info("websocket服务端已离线({})!",
                this.getUrlString()
        );

        if (this.isCloseReConnect()) {
            this.connectServer();
        }
    }

    @OnError
    public void onError(Session session, Throwable e) {
        this.error(e);

        this.session = null;
        MHDFBot.getLogger().error(e);

        if (this.isCloseReConnect()) {
            this.connectServer();
        }
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
}
