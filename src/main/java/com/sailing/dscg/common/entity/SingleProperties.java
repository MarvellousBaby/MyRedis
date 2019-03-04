package com.sailing.dscg.common.entity;

import com.sailing.dscg.entity.BaseEntity;
import lombok.Data;

import java.util.List;

/**
 * Description:存放之前都是传递单个参数的改为JSON格式
 * <p>
 * Update by Panyu on 2019/2/13 下午 04:02:35
 */
@Data
public class SingleProperties extends BaseEntity {

    //验证码
    private String authCode;


    private List<Long> ids;

    private List<String> strIds;

    private String type;

    private Boolean model;


}
