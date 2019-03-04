package com.sailing.dscg.entity.platformRegister;

import com.sailing.dscg.entity.BaseEntity;
import com.sailing.dscg.zookeeper.Node;
import lombok.Data;
/**
 * Description:
 * <p>
 * Update by Panyu on 2018/9/14 上午 11:22:44
 */
@Data
@Node(name = "platformRegister")
public class PlatformRegister extends BaseEntity {

    //网域名称
    private String netName;
    //平台名称
    private  String name;
    //平台编号
    private String platNo;
    //平台地址
    private String requestIp;
    //平台端口
    private int requestPort;
    //流媒体服务IP
    private String rtpIp;
    //流媒体服务端口
    private String rtpPort;
    //平台版本
    private String platVersion;
    //通过SNMP获取
    private Boolean getBySNMP;
    //IP地址
    private String ip;
    //端口
    private String port;

}
