package cn.chengzhiya.mhdfbot.api.util;

import cn.chengzhiya.mhdfbot.api.enums.bot.OpenIdType;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class OpenIdCacheUtil {
    @Getter
    private static final Map<Integer, String> messageIdHashMap = new ConcurrentHashMap<>();
    @Getter
    private static final Map<Integer, String> userIdHashMap = new ConcurrentHashMap<>();
    @Getter
    private static final Map<Integer, String> groupIdHashMap = new ConcurrentHashMap<>();

    /**
     * 获取指定ID类型的数据表
     *
     * @param openIdType ID类型
     * @return 数据表实例
     */
    private static Map<Integer, String> getMap(OpenIdType openIdType) {
        return switch (openIdType) {
            case MESSAGE -> getMessageIdHashMap();
            case USER -> getUserIdHashMap();
            case GROUP -> getGroupIdHashMap();
        };
    }

    /**
     * 增加数据
     *
     * @param openIdType ID类型
     * @param openId     数据ID
     * @return 数据ID编号
     */
    public static int addData(OpenIdType openIdType, String openId) {
        Map<Integer, String> map = getMap(openIdType);

        map.put(map.size(), openId);
        return map.size() - 1;
    }

    /**
     * 获取数据
     *
     * @param openIdType ID类型
     * @param id         数据ID编号
     * @return 数据ID
     */
    public static String getData(OpenIdType openIdType, int id) {
        Map<Integer, String> map = getMap(openIdType);
        return map.get(id);
    }
}
