package com.sailing.dscg.service.monitorCenter;

import com.sailing.dscg.entity.monitoring.MonitorCount;
import com.sailing.dscg.entity.monitoring.MonitorGraphic;
import com.sailing.dscg.entity.monitoring.NodeStatus;
import com.sailing.dscg.entity.monitoring.RefuseCata;
import com.sailing.dscg.entity.monitoring.test.GraphicMonitor;

import java.util.List;

/**
 * Description:
 * <p>
 * Update by Panyu on 2018/9/26 上午 09:57:28
 */
public interface IMonitorCenterService {

    MonitorCount queryMonitorCount();

    List<MonitorGraphic> queryMonitorGraphic(int model) throws Exception;

    RefuseCata getRefuseCata(String serviceId) throws Exception;

    Boolean saveRefuseCata(RefuseCata refuseCata) throws Exception;

    List<GraphicMonitor> getMonitorGraphic(boolean model) throws Exception;



}
