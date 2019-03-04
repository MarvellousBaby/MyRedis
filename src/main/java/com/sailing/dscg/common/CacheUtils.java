package com.sailing.dscg.common;


import com.sailing.dscg.entity.safeManage.Admin;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 简单缓存工具类
 * @Auther:史俊华
 * @Date:2018/8/1021
 */
public class CacheUtils {
    private final static Map<String, Object> map = new HashMap<>();

    /**
     * 添加缓存
     *
     * @param key
     * @param obj
     */
    public static void put(String key, Object obj) {
        map.put(key, obj);
    }

    /**
     * 读取缓存
     *
     * @param key 键
     * @return
     */
    public static Object get(String key) {
        return map.get(key);
    }

    /**
     * 读取缓存
     *
     * @param key 键
     * @return
     */
    public static <T> T get(String key, Class<T> clazz) {
        return clazz.cast(CacheUtils.get(key));
    }

    /**
     * 清除缓存
     *
     * @param key
     * @return
     */
    public static void remove(String key) {
        map.remove(key);
    }

    /**
     * 获取当前用户
     *
     * @return
     */
    public static String getAdminLoginName() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) return "admin";
        HttpServletRequest request = attributes.getRequest();
        if (request == null) return "admin";
        Admin admin = (Admin) request.getSession().getAttribute("admin");
        if (admin == null) {
            return "admin";
        } else {
            return admin.getLoginName();
        }
    }
}
