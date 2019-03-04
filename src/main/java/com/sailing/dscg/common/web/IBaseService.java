package com.sailing.dscg.common.web;

import java.util.List;

/**
 * @Description: Service接口基类
 * @Auther:史俊华
 * @Date:2018/7/315
 */
public interface IBaseService<T> {
    List<T> queryAll() throws Exception;
    List<T> queryList(T t) throws Exception;
    T get(T t) throws Exception;
    Boolean save(T t) throws Exception;
    Boolean delete(T t) throws Exception;
    Boolean deleteAll() throws Exception;
}
