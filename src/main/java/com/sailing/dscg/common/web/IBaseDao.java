package com.sailing.dscg.common.web;

import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description: dao接口基类
 * @Auther:史俊华
 * @Date:2018/7/314
 */
@Repository
public interface IBaseDao<T> {
    /**
     * 获取对象集合接口
     * @param t
     * @return
     */
    List<T> queryList(T t);

    /***
     * 获取单个对象
     * @param t
     * @return
     */
    T get(T t);

    /***
     * 获取单个对象
     * @param service_id
     * @return
     */
    T get(String service_id);

    /**
     * 新增对象
     * @param t
     */
    void insert(T t);

    /**
     * 更新对象
     * @param t
     */
    void update(T t);

    /**
     * 删除对象
     * @param t
     */
    void delete(T t);
}
