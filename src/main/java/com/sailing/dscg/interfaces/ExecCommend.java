package com.sailing.dscg.interfaces;

import com.sailing.dscg.entity.monitoring.IpConn;
import com.sailing.dscg.entity.monitoring.IpConnectMessage;
import com.sailing.dscg.entity.serviceManager.ServiceManagerCoum;

import java.util.List;

public interface ExecCommend {
//    RespData<String> start(ServiceManagerCoum serviceManagerCoum);

     String start(ServiceManagerCoum ferryConfig);
     String deploy(ServiceManagerCoum ferryConfig);
     String stop(ServiceManagerCoum ferryConfig);

     List<IpConnectMessage> getIpConnectMessage();
}
