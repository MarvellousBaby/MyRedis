package com.sailing.dscg.entity.sysRunning;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Description:
 * <p>
 * Update by Panyu on 2018/10/19 上午 10:07:13
 */
@Data
public class SysRunning {

    private String id;
    //服务Id
    private String serviceID;
    //服务名称
    private String serviceName;
    //平台名称
    private String platName;

//    private Date openTime;
    //访问时长
    private Long visitTime;
    //流量大小（MB）
    private Double transportSize;
    //终端地址
    private String targetIp;
    //操作类型
    private String requestType;
    //传输路径
    private String transferPath;
    //开始时间
    private String transportStartTime;
    //结束时间
    private String transportStopTime;

    private Double transportBitStream;

    /*
     * 查询所用的查询条件
     */
    private String requestIPPort;
    private String callIds;
    private String serviceIds;
    private String transferPaths;
    private Integer pageSize;
    private Integer pageNum;


}
