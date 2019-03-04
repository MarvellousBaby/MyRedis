package com.sailing.dscg.service.serviceManger;

import com.sailing.dscg.common.page.PageHelper;
import com.sailing.dscg.entity.RespData;
import com.sailing.dscg.entity.serviceManager.GetGateWay;
import com.sailing.dscg.entity.serviceManager.ServiceManager;
import com.sailing.dscg.entity.system.ClusterConfig;

import java.util.List;

/**
 * Description:
 * <p>
 * Update by Panyu on 2018/10/12 下午 03:10:54
 */

public interface IServiceMangerService {

    List<ServiceManager> queryAll() throws Exception;

    PageHelper queryList(PageHelper<ServiceManager> pageHelper) throws Exception;

    Boolean save(ServiceManager config) throws Exception;

    Boolean delete(ServiceManager serviceManager) throws Exception;

    ServiceManager get(ServiceManager serviceManager) throws Exception;

    RespData<String> deploy(ServiceManager serviceManager) throws Exception;

//    List<PlatformRegister> getPlats() throws Exception;

    List<ClusterConfig> getClusters() throws Exception;

    List<String> getGateWay(GetGateWay getGateWay) throws Exception;

}
