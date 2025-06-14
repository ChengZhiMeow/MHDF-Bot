package cn.chengzhiya.mhdfbot.api.http;

import cn.chengzhiya.mhdfbot.api.MHDFBot;
import cn.chengzhiya.mhdfbot.api.exception.DownloadException;
import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
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
            }

            MHDFBot.getLogger().info(
                    "向{}发送{}请求失败 #{}({})",
                    connection.getURL(),
                    connection.getRequestMethod(),
                    connection.getResponseCode(),
                    connection.getResponseMessage()
            );
        } catch (IOException e) {
            MHDFBot.getLogger().error("发送网络请求的时候发生了错误: ");
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
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

    /**
     * 通过URL连接下载文件
     *
     * @param connection URL连接
     * @return 文件数据
     */
    public byte[] downloadFile(URLConnection connection) throws DownloadException {
        try {
            try (InputStream in = connection.getInputStream()) {
                byte[] bytes = in.readAllBytes();
                if (bytes.length == 0) {
                    throw new DownloadException("无可下载文件");
                }
                return bytes;
            }
        } catch (Exception e) {
            throw new DownloadException(e);
        }
    }

    /**
     * 通过URL地址下载文件
     *
     * @param url URL地址
     * @return 文件数据
     */
    public byte[] downloadFile(String url) throws DownloadException {
        return downloadFile(getConnection(url));
    }

    /**
     * 通过URL连接下载并保存文件
     *
     * @param connection URL连接
     * @param savePath   保存目录
     */
    public void downloadFile(URLConnection connection, Path savePath) throws DownloadException {
        try {
            Files.write(savePath, downloadFile(connection));
        } catch (IOException e) {
            throw new DownloadException(e);
        }
    }

    /**
     * 通过URL地址下载并保存文件
     *
     * @param url      URL地址
     * @param savePath 保存目录
     */
    public void downloadFile(String url, Path savePath) throws DownloadException {
        downloadFile(getConnection(url), savePath);
    }
}
