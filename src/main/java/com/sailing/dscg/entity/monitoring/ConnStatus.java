package com.sailing.dscg.entity.monitoring;

import lombok.Data;

import java.util.List;

/**
 * Description:
 * <p>
 * Update by Panyu on 2018/10/17 下午 02:29:12
 */
@Data
public class ConnStatus {

    //源名称
    private String sourceName;
    //源Ip
    private String sourIp;
    //目标Ip
    private String targIp;
    //源节点类型 请求平台：reqPlatform，请求节点：reqNode，网闸：gateway,响应节点respNode，响应平台：respPlatform
    private String type;
    //链路
    private Boolean isConn;
}
