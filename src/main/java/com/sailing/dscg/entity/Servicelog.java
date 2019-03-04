package com.sailing.dscg.entity;

import lombok.Data;

import java.util.Date;


@Data
public class Servicelog {
    private String id;
    private String stateMsg;
    private String loginName;                //登录名
    private String userName;                //用户名
    private String roleName;                //角色
    private String accessIp;                //ip
    private String accessPath;              //访问路径
    private String accessMethod;            //访问方法
    private String operateType;             //操作类型
    private Date accessTime;              //访问时间
    private String accessParams;          //访问参数
    private String operateContent;          //操作内容
    private String createUser;
    private Date createTime;
    //增加查询时间区间
    private int flag;//标识 0 非登陆日志 1登陆日志
    private Date startDate;
    private Date endDate;

    private Integer pageSize;
    private Integer pageNum;

}
