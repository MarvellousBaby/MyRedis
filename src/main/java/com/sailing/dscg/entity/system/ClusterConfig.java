package com.sailing.dscg.entity.system;

import com.sailing.dscg.entity.BaseEntity;
import com.sailing.dscg.zookeeper.Node;
import lombok.Data;

import java.util.List;

/**
 * @Description: 集群配置实体类
 * @Auther:史俊华
 * @Date:2018/6/271919
 */
@Data
@Node(name = "sys_ClusterConfig")
public class ClusterConfig extends BaseEntity {

    /**序列号**/
    private String serialId;
    /**集群名称*/
    private String name;
    /**集群类型：节点，网闸*/
    private String type;
    /**IP地址*/
    private String ip;
    /** 端口 **/
    private Integer port;
    /**（本地，异地）||（公安信息网，视频专网）*/
    private String inOut;
    /**信令or视频(signal,video)**/
    private String gateType;
    /**网闸通道 信令*/
    private List<ClusterGatewayPassageway> signalPassageways;
    /**网闸通道 视频*/
    private List<ClusterGatewayPassageway> videoPassageways;
}
