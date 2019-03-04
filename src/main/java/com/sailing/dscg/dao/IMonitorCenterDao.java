package com.sailing.dscg.dao;

import com.sailing.dscg.entity.monitoring.IpConnectMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Description:
 * <p>
 * Update by Panyu on 2018/10/17 上午 09:59:55
 */
@Mapper
public interface IMonitorCenterDao {

    @Select({"select count(id) from log_videotransmission"})
    Long transReqCount();

    @Select({"select count(id) from log_deviceauthen where authenticated_state = true"})
    Long devicePassCount();

    @Select({"select sum(transportSize) as passFlowSize from log_videotransmission"})
    Long passFlowSizes();

    @Select({"select count(id) from log_deviceauthen where authenticated_state = false"})
    Long deviceFail();

    @Select({"select * from log_ipconnectmessage where time in (select MAX(time) from log_ipconnectmessage GROUP BY requestIpPort,selfIpPort,serviceID)"})
    List<IpConnectMessage> queryConnStatus();

    @Select({"select * from log_ipconnectmessage_copy1 where time in (select MAX(time) from log_ipconnectmessage GROUP BY requestIpPort,selfIpPort,serviceID)"})
    List<IpConnectMessage> queryConnStatus1();

    @Select({"select sum(transportSize) as transportSizes from log_videotransmission where requestIP = #{requestIP} and requestTime<=NOW() and requestTime>=DATE_SUB(NOW(),interval '00:00:30' HOUR_SECOND)"})
    Long queryTransportSizes(String requestIP);



}
