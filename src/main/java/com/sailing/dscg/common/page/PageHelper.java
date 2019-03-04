package com.sailing.dscg.common.page;

import lombok.Data;

import java.util.List;

/**
 * Description:
 * <p>
 * Update by Panyu on 2018/8/6 上午 11:29:16
 */
@Data
public class PageHelper<T> {

    //增加查询时间区间
    String startDate;

    String endDate;

    int pageSize;   //每页显示的数量

    int pageNum;    //页码

    long pageCount; //总共条数

    T object; //对象
    List<T> objects; //对象集合



}
