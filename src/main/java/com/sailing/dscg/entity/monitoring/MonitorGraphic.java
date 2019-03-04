package com.sailing.dscg.entity.monitoring;

import com.sailing.dscg.entity.platformRegister.PlatformRegister;
import lombok.Data;

import java.util.List;

/**
 * Description:
 * <p>
 * Update by Panyu on 2018/9/28 下午 02:53:40
 */
@Data
public class MonitorGraphic {

    String id;
    String serviceId;
    String serviceName;
    String modifyTime;
    String reqName;
    String respName;
    //平台节点连接状态
    List<ConnStatus> connStatusList;
    //请求节点状态
    List<NodeStatus> nodeStatus;
    //网闸的状态
    List<GatewayStatus> gatewayStatuse;
    //状态
    String status;
    //格式(0-空，1-非空)
    Integer format;
}
