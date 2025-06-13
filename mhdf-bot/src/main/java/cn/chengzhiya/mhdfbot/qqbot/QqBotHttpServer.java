package cn.chengzhiya.mhdfbot.qqbot;

import cn.chengzhiya.mhdfbot.Main;
import cn.chengzhiya.mhdfbot.api.MHDFBot;
import cn.chengzhiya.mhdfbot.api.http.HttpAnnotationUtil;
import cn.chengzhiya.mhdfbot.api.http.HttpServer;
import cn.chengzhiya.mhdfbot.api.http.HttpUtil;
import cn.chengzhiya.mhdfbot.api.http.annotation.RequestPath;
import cn.chengzhiya.mhdfbot.api.http.annotation.RequestType;
import cn.chengzhiya.mhdfbot.api.http.entity.JsonHttpData;
import cn.chengzhiya.mhdfbot.api.http.entity.SSLConfig;
import cn.chengzhiya.mhdfbot.api.http.filter.CorsFilter;
import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.reflections.Reflections;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
public final class QqBotHttpServer extends HttpServlet implements HttpServer {
    private final int port;
    private final SSLConfig sslConfig;

    private final HashMap<String, Class<?>> controllerHashMap = new HashMap<>();
    private Tomcat server;

    public QqBotHttpServer(int port, SSLConfig sslConfig) {
        this.port = port;
        this.sslConfig = sslConfig;

        try {
            Reflections reflections = new Reflections(Main.class.getPackageName());

            for (Class<?> clazz : reflections.getTypesAnnotatedWith(RequestPath.class)) {
                String path = clazz.getAnnotation(RequestPath.class).value();
                getControllerHashMap().put(path, clazz);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Connector getConnector() {
        Connector connector = new Connector("HTTP/1.1");
        connector.setPort(getPort());
        if (getSslConfig().isEnable()) {
            connector.setSecure(true);
            connector.setScheme("https");
            connector.setAttribute("keyAlias", getSslConfig().getAlias());
            connector.setAttribute("keystorePass", getSslConfig().getKey());
            connector.setAttribute("keystoreFile", getSslConfig().getFile());
            connector.setAttribute("clientAuth", "false");
            connector.setAttribute("sslProtocol", "TLS");
            connector.setAttribute("SSLEnabled", "true");
        }
        return connector;
    }

    @Override
    public void start() {
        File fileFolder = new File("files");
//        if (imageFolder.exists()) {
//            FileUtil.removeFiles(imageFolder);
//        }
//        imageFolder.mkdirs();

        new Thread(() -> {
            try {
                Logger tomcatLogger = Logger.getLogger("org.apache");
                tomcatLogger.setLevel(Level.OFF);
                ConsoleHandler consoleHandler = new ConsoleHandler();
                consoleHandler.setLevel(Level.OFF);
                tomcatLogger.addHandler(consoleHandler);

                this.server = new Tomcat();

                Connector connector = getConnector();
                this.server.getService().addConnector(connector);

                Context context = this.server.addContext("", null);
                Tomcat.addServlet(context, "dispatcherServlet", this);
                context.addServletMappingDecoded("/", "dispatcherServlet");

                // 允许跨域
                {
                    FilterDef corsFilterDef = new FilterDef();
                    corsFilterDef.setFilterName("corsFilter");
                    corsFilterDef.setFilter(new CorsFilter());
                    context.addFilterDef(corsFilterDef);

                    FilterMap corsFilterMap = new FilterMap();
                    corsFilterMap.setFilterName("corsFilter");
                    corsFilterMap.addURLPattern("/*");
                    context.addFilterMap(corsFilterMap);
                }

                this.server.start();
                this.server.getServer().await();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 获取处理器实例列表
     *
     * @param path 接口路径
     * @return 处理器实例列表
     */
    private List<Class<?>> getControllerList(String path) {
        List<Class<?>> list = new ArrayList<>();
        for (String p : List.of(path, "*")) {
            Class<?> clazz = getControllerHashMap().get(p);
            if (clazz == null) {
                continue;
            }

            list.add(clazz);
        }

        return list;
    }

    /**
     * 转换文本为指定类型
     *
     * @param type 类型类实例
     * @param data 文本
     * @return 转换后的实例
     */
    private <T> T converter(Class<T> type, String data) {
        if (type == null || data == null) {
            return null;
        }

        if (type.equals(Integer.class)) {
            return type.cast(Integer.parseInt(data));
        }
        if (type.equals(Double.class)) {
            return type.cast(Double.parseDouble(data));
        }
        if (type.equals(Float.class)) {
            return type.cast(Float.parseFloat(data));
        }

        return type.cast(data);
    }

    /**
     * 处理请求
     *
     * @param request  请求实例
     * @param response 回应实例
     * @param type     请求类型实例
     */
    private void handleRequest(HttpServletRequest request, HttpServletResponse response, RequestType.Type type) {
        String uri = request.getRequestURI();
        String path = uri.substring(0, uri.lastIndexOf("/"));
        String methodPath = uri.substring(path.length());

        MHDFBot.getLogger().info("HTTP Server {}请求({}) - {}", type, request.getRemoteAddr(), uri);

        List<Class<?>> controllerList = getControllerList(path);
        for (Class<?> controller : controllerList) {
            for (Method method : controller.getMethods()) {
                if (HttpAnnotationUtil.getRequestType(method) != type) {
                    continue;
                }

                String requestPath = HttpAnnotationUtil.getRequestPath(method);
                if (!Objects.requireNonNull(requestPath).equals("*") && !Objects.equals(requestPath, methodPath)) {
                    continue;
                }

                JSONObject body = null;
                try {
                    InputStream in = request.getInputStream();
                    byte[] bytes = in.readAllBytes();
                    body = JSONObject.parseObject(new String(bytes));
                } catch (IOException ignored) {
                }

                List<Object> data = new ArrayList<>();
                for (Parameter parameter : method.getParameters()) {
                    if (parameter.getType().equals(HttpServletRequest.class)) {
                        data.add(request);
                        continue;
                    }
                    if (parameter.getType().equals(HttpServletResponse.class)) {
                        data.add(response);
                        continue;
                    }

                    String paramData = HttpAnnotationUtil.getDefaultValue(parameter);

                    // 获取cookie中的数据
                    {
                        String paramName = HttpAnnotationUtil.getCookieDataName(parameter);
                        if (paramName != null && request.getCookies() != null) {
                            for (Cookie cookie : request.getCookies()) {
                                if (!cookie.getName().equals(paramName)) {
                                    continue;
                                }

                                if (cookie.getValue() == null) {
                                    continue;
                                }

                                paramData = cookie.getValue();
                                break;
                            }

                            if (paramData == null || paramData.isEmpty()) {
                                HttpUtil.returnJsonHttpData(response, JsonHttpData.noCookie);
                                return;
                            }
                        }
                    }

                    // 获取请求参数中的数据
                    {
                        String paramName = HttpAnnotationUtil.getRequestParamName(parameter);
                        if (paramName != null) {
                            if (request.getParameter(paramName) != null) {
                                paramData = request.getParameter(paramName);
                            }

                            if (paramData == null || paramData.isEmpty()) {
                                HttpUtil.returnJsonHttpData(response, JsonHttpData.noParam);
                                return;
                            }
                        }
                    }

                    // 获取请求数据中的数据
                    {
                        String paramName = HttpAnnotationUtil.getBodyDataName(parameter);
                        if (paramName != null) {
                            if (body == null) {
                                data.add(null);
                                continue;
                            }

                            if (paramName.equals("body")) {
                                data.add(body);
                                continue;
                            }

                            Object bodyData = null;
                            if (body.getObject(paramName, parameter.getType()) != null) {
                                bodyData = body.getObject(paramName, parameter.getType());
                            }

                            if (bodyData == null) {
                                HttpUtil.returnJsonHttpData(response, JsonHttpData.noParam);
                                return;
                            }

                            data.add(bodyData);
                            continue;
                        }
                    }

                    data.add(converter(
                            parameter.getType(),
                            paramData
                    ));
                }

                try {
                    method.invoke(null, data.toArray());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                return;
            }
        }

        HttpUtil.returnJsonHttpData(response, JsonHttpData.noInterface);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        handleRequest(request, response, RequestType.Type.GET);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        handleRequest(request, response, RequestType.Type.POST);
    }
}
