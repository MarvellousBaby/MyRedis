package com.sailing.dscg.entity.monitoring.test;

import com.sailing.dscg.entity.monitoring.ConnStatus;
import com.sailing.dscg.entity.monitoring.NodeStatus;
import com.sailing.dscg.entity.monitoring.Plat;
import com.sailing.dscg.entity.platformRegister.PlatformRegister;
import com.sailing.dscg.entity.system.ClusterConfig;
import lombok.Data;

import java.util.List;

/**
 * Description:
 * <p>
 * Update by Panyu on 2018/12/17 下午 04:19:16
 */
@Data
public class GraphicMonitor {

    /**服务id**/
    String serviceId;
    /**服务名称**/
    String serviceName;
    /**修改时间**/
    String modifyTime;
    /**状态**/
    String status;

    /**平台**/
    List<Plat> plats;

    /**节点**/
    List<NodeStatus> nodes;

    /**网闸**/
    List<GatewayInfo> gatewayInfos;

    /**连接信息**/
    List<ConnStatus> connStatuses;






}
