package com.sailing.dscg.service.system;

import com.sailing.dscg.common.web.IBaseService;
import com.sailing.dscg.entity.system.ClusterConfig;

import java.util.List;

/**
 * @Description: 集群配置Service接口
 * @Auther:史俊华
 * @Date:2018/6/271919
 */
public interface IClusterConfigService extends IBaseService<ClusterConfig> {
    List<String> clusterNodeTree(String type) throws Exception;

    ClusterConfig getClusterConfigByIp(String ip) throws Exception;

//    String getPassagewayIpPortByNodeIp(String ip, Integer port) throws Exception;

    Boolean hasSameIp(ClusterConfig clusterConfig) throws Exception;

    Boolean hasSameInIp(ClusterConfig clusterConfig) throws Exception;

    //删除端口
//    Boolean deletePort(ClusterConfig clusterConfig) throws Exception;

    //删除dscg集群
    Boolean deleteGateWay(ClusterConfig clusterConfig) throws Exception;
    String getNodeIpByGatewayIpPort(List<ClusterConfig> clusterConfigs,String ip, Integer port) throws Exception;
}
