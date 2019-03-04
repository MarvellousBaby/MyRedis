package com.sailing.dscg.entity;

import lombok.Data;
import java.io.Serializable;

/*
 *  数据源配置基类实体类
 *  @author  李超群
 *  @version 2018-07-02
 *  @param service_id  服务ID,主键
 *  @param remark      备注/描述
 *  @param create_user 创建用户
 *  @param create_time 创建时间
 *  @param modify_user 变更用户
 *  @param modify_time 变更时间
 *  */
@Data
public class BaseEntity implements Serializable {

    private String id;
    private String serviceId;
    private String remark;
    private String createUser;
    private String createTime;
    private String modifyUser;
    private String modifyTime;

    private Integer pageSize;
    private Integer pageNum;

}
