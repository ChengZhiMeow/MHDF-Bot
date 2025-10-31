package cn.chengzhiya.mhdfbot.api.manager;

import cn.chengzhiya.mhdfbot.api.bot.type.OpenIdType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class OpenIdCacheManager {
    private static OpenIdCacheManager instance;

    public static OpenIdCacheManager getInstance() {
        if (OpenIdCacheManager.instance == null) OpenIdCacheManager.instance = new OpenIdCacheManager();
        return OpenIdCacheManager.instance;
    }

    private final Map<Integer, String> messageIdHashMap = new ConcurrentHashMap<>();
    private final Map<Integer, String> userIdHashMap = new ConcurrentHashMap<>();
    private final Map<Integer, String> groupIdHashMap = new ConcurrentHashMap<>();

    /**
     * 获取指定ID类型的数据表
     *
     * @param openIdType ID类型
     * @return 数据表实例
     */
    private Map<Integer, String> getMap(OpenIdType openIdType) {
        return switch (openIdType) {
            case MESSAGE -> this.messageIdHashMap;
            case USER -> this.userIdHashMap;
            case GROUP -> this.groupIdHashMap;
        };
    }

    /**
     * 增加数据
     *
     * @param openIdType ID类型
     * @param openId     数据ID
     * @return 数据ID编号
     */
    public int addData(OpenIdType openIdType, String openId) {
        Map<Integer, String> map = this.getMap(openIdType);

        Integer id = this.getId(openIdType, openId);
        if (id == null) {
            map.put(map.size(), openId);
            return map.size() - 1;
        }

        return id;
    }

    /**
     * 获取数据ID编号
     *
     * @param openIdType ID类型
     * @param openId     数据ID
     * @return 数据ID编号
     */
    public Integer getId(OpenIdType openIdType, String openId) {
        Map<Integer, String> map = this.getMap(openIdType);
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            if (!entry.getValue().equals(openId)) {
                continue;
            }

            return entry.getKey();
        }

        return null;
    }

    /**
     * 获取数据ID
     *
     * @param openIdType ID类型
     * @param id         数据ID编号
     * @return 数据ID
     */
    public String getData(OpenIdType openIdType, int id) {
        Map<Integer, String> map = this.getMap(openIdType);
        return map.get(id);
    }
}
