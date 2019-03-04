package com.sailing.dscg.common.web;

import org.apache.poi.ss.formula.functions.T;

/**
 * Description:
 * <p>
 * Update by Panyu on 2018/9/13 下午 02:33:42
 */
public interface INewBaseService<T> {
    T queryAll() throws Exception;
    T queryList(T t) throws Exception;
    T get(T t) throws Exception;
    Boolean save(T t) throws Exception;
    Boolean delete(String[] ids) throws Exception;
    Boolean deleteAll() throws Exception;
}

