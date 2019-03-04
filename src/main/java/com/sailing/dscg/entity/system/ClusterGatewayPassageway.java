package com.sailing.dscg.entity.system;

import com.sailing.dscg.entity.BaseEntity;
import lombok.Data;
/**
 * @Description: 网闸集群通道信息配置实体类
 * @Auther:史俊华
 * @Date:2018/6/271919
 */
@Data
public class ClusterGatewayPassageway extends BaseEntity {

    /**集群配置表ID*/
    private String clusterConfigId;
    /**编码*/
    private String code;
    /**监听Ip*/
    private String inIp;
    /**监听端口*/
    private String inPort;
    /**目标端口*/
    private String outPort;
    /**目标Ip*/
    private String destNode;
    /**外DSCG节点名称*/
    private String destNodeName;
}
