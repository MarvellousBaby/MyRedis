package com.sailing.dscg.entity.serviceManager;

import com.sailing.dscg.entity.BaseEntity;
import com.sailing.dscg.entity.platformRegister.PlatformRegister;
import com.sailing.dscg.entity.system.ClusterConfig;
import com.sailing.dscg.zookeeper.Node;
import lombok.Data;

/**
 * Description:
 * <p>
 * Update by Panyu on 2018/10/12 下午 03:17:26
 */
@Data
@Node(name = "sys_ServiceManager")
public class ServiceManager extends BaseEntity {

    //服务名称
    private String serviceName;
    //状态
    private String status;
    //请求平台
    private PlatformRegister reqPlatformRegister;
    //请求节点
    private ClusterConfig reqClusterConfig;
    //响应平台
    private PlatformRegister resPlatformRegister;
    //响应节点
    private ClusterConfig resClusterConfig;
    //传输方向
    private Boolean allowBidirection;
    //服务运行模式（0-全量模式，1-主备模式）
    private Integer runningModel;
    //信令分配原则()
    private String allocation;
    //本地视频网闸
    private String localVideoGateway;
    //本地信令网闸
    private String localSignalGateWay;
    //异地视频网闸
    private String alloVideoGateway;
    //异地信令网闸
    private String alloSignalGateWay;
    //SIP服务类型
    private String sipServiceType;
    //SIP操作类型(sipFile-文件，sipReview-回放，sipPreView-预览，sipTalkBack-语音对讲)
    private String sipOperateType;
    //最大允许带宽
    private Integer maxAllowBw;
    //优先级
    private Integer priority;
    //轮询
    private Integer polling;
    //服务状态(deploy-部署，start-启动，stop-停止)
    private String model;

}
