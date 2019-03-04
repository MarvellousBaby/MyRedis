package com.sailing.dscg.interfaces;

import com.sailing.dscg.entity.RespData;
import com.sailing.dscg.entity.monitoring.NodeStatus;

/**
 * 集群节点状态
 */
public interface NodeState {
    /**
     * 获取集群节点状态
     *
     * @param ip 节点IP
     * @return json --> {code:,reason:,data:{...}}
     */
    RespData<NodeStatus> queryClusterNodeStatus(String ip);

}
