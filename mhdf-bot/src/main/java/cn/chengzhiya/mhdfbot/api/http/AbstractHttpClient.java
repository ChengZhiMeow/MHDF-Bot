package cn.chengzhiya.mhdfbot.api.http;

import cn.chengzhiya.mhdfbot.api.MHDFBot;
import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

@Getter
@Setter
public abstract class AbstractHttpClient implements HttpClient {
    private String accessToken;

    @Override
    public String formatUrl(String urlString) {
        return urlString;
    }

    @Override
    public HttpURLConnection getConnection(String urlString) {
        try {
            URL url = new URL(formatUrl(urlString));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(7500);
            connection.setReadTimeout(7500);

            if (getAccessToken() != null) {
                connection.setRequestProperty("Authorization", getAccessToken());
            }

            return connection;
        } catch (IOException e) {
            MHDFBot.getLogger().error(e);
        }
        return null;
    }

    @Override
    public String getData(HttpURLConnection connection) {
        try {
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    return in.readLine();
                }
            } else {
                MHDFBot.getLogger().info(
                        "向{}发送{}请求失败 #{}({})",
                        connection.getURL(),
                        connection.getRequestMethod(),
                        connection.getResponseCode(),
                        connection.getResponseMessage()
                );
            }
        } catch (IOException e) {
            MHDFBot.getLogger().error(e);
        }
        connection.disconnect();
        return null;
    }

    @Override
    public String get(String urlString) {
        try {
            HttpURLConnection connection = getConnection(urlString);
            Objects.requireNonNull(connection).setRequestMethod("GET");

            return getData(connection);
        } catch (IOException e) {
            MHDFBot.getLogger().error(e);
        }
        return null;
    }

    @Override
    public String post(String urlString, String data) {
        try {
            HttpURLConnection connection = getConnection(urlString);
            Objects.requireNonNull(connection).setRequestMethod("POST");

            if (data != null && !data.isEmpty()) {
                connection.setDoOutput(true);
                try (OutputStream out = connection.getOutputStream()) {
                    out.write(data.getBytes());
                    out.flush();
                }
            }

            return getData(connection);
        } catch (IOException e) {
            MHDFBot.getLogger().error(e);
        }
        return null;
    }

    /**
     * 发送POST请求获取数据
     *
     * @param urlString 请求地址
     * @return 数据
     */
    public String post(String urlString) {
        return post(urlString, "");
    }

    /**
     * 发送POST请求获取数据
     *
     * @param urlString 请求地址
     * @return 数据
     */
    public String post(String urlString, JSONObject data) {
        if (data == null) {
            return post(urlString, "");
        }
        return post(urlString, data.toString());
    }
}
