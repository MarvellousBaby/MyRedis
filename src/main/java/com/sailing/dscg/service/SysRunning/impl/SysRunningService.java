package com.sailing.dscg.service.SysRunning.impl;

import com.sailing.dscg.common.Constants;
import com.sailing.dscg.common.DateTool;
import com.sailing.dscg.common.page.PageHelper;
import com.sailing.dscg.dao.ISysRunningDao;
import com.sailing.dscg.entity.platformRegister.PlatformRegister;
import com.sailing.dscg.entity.serviceManager.ServiceManager;
import com.sailing.dscg.entity.sysRunning.SysRunning;
import com.sailing.dscg.entity.system.ClusterConfig;
import com.sailing.dscg.entity.system.ClusterGatewayPassageway;
import com.sailing.dscg.service.SysRunning.ISysRunningService;
import com.sailing.dscg.service.serviceManger.impl.ServiceManagerService;
import com.sailing.dscg.service.system.impl.ClusterConfigService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Description:
 * <p>
 * Update by Panyu on 2018/9/14 下午 02:46:07
 */
@Service
public class SysRunningService implements ISysRunningService {

    @Autowired
    ISysRunningDao iSysRunningDao;

    @Autowired
    private ClusterConfigService clusterConfigService;

    @Autowired
    private ServiceManagerService serviceManagerService;


    @Override
    public PageHelper<SysRunning> queryList(PageHelper<SysRunning> sysRunningPageHelper) throws Exception {
        List<SysRunning> sysRunningList = new ArrayList<>();
        SysRunning sysRun = sysRunningPageHelper.getObject();

        int pageSize = sysRunningPageHelper.getPageSize();
        int pageNum = sysRunningPageHelper.getPageNum();
        pageNum = pageNum > 0 ? (pageNum - 1) * pageSize : pageSize;
        pageSize = pageSize == 0 ? 20 : pageSize;
        sysRun.setPageSize(pageSize * 2);
        sysRun.setPageNum(pageNum * 2);

        List<ServiceManager> serviceManagerList = serviceManagerService.queryAll();
        List<ClusterConfig> clusterConfigs = clusterConfigService.queryAll();
        String serviceIds = "";
        String transferPaths = "";
        String callIds = "";
        boolean serviceSearch = false;
        boolean clusterSearch = false;


        if (StringUtils.isNotBlank(sysRun.getServiceName())) {
            serviceSearch = true;
            serviceManagerList = serviceManagerList.stream().filter(serviceManager -> serviceManager.getServiceName().contains(sysRun.getServiceName())).collect(Collectors.toList());
        }
        if (StringUtils.isNotBlank(sysRun.getPlatName())) {
            serviceSearch = true;
            serviceManagerList = serviceManagerList.stream().filter(serviceManager -> serviceManager.getResPlatformRegister().getName().contains(sysRun.getPlatName())).collect(Collectors.toList());
        }
        if (StringUtils.isNotBlank(sysRun.getTransferPath())) {
            clusterSearch = true;
            clusterConfigs = clusterConfigs.stream().filter(clusterConfig -> clusterConfig.getType().equalsIgnoreCase(Constants.CLUSTER_CONFIG_TYPE_GATEWAY) &&
                    clusterConfig.getName().equalsIgnoreCase(sysRun.getTransferPath())
            ).collect(Collectors.toList());
        }
        if (StringUtils.isNotBlank(sysRun.getTransportStartTime())&&StringUtils.isNotBlank(sysRun.getTransportStopTime())) {
            sysRun.setTransportStartTime(sysRun.getTransportStartTime() + " 00:00:00");
            sysRun.setTransportStopTime(sysRun.getTransportStopTime() + " 00:00:00");

            List<String> callIDs = iSysRunningDao.getCallIdByDate(sysRun);
            for (String callId : callIDs) {
                callIds += "'" + callId + "'";
                callIds += ",";
            }
            sysRun.setCallIds(callIds.substring(0,callIds.length()-1));
        }

        if (StringUtils.isNotBlank(sysRun.getTransportStopTime())) {
            sysRun.setTransportStopTime(sysRun.getTransportStopTime() + " 00:00:00");
        }

        if (serviceSearch) {
            if (!serviceManagerList.isEmpty()) {
                for (ServiceManager serviceManager : serviceManagerList) {
                    serviceIds += "'" + serviceManager.getServiceId() + "'";
                    serviceIds += ",";
                }
                sysRun.setServiceIds(serviceIds.substring(0, serviceIds.length() - 1));
            }else{
                sysRun.setServiceIds("''");
            }
        }
        if (clusterSearch) {
            if (!clusterConfigs.isEmpty()) {
                for (ClusterConfig clusterConfig : clusterConfigs) {
                    for (ClusterGatewayPassageway clusterGatewayPassageway : clusterConfig.getSignalPassageways()) {
                        transferPaths += "'" + clusterGatewayPassageway.getDestNode() + ":" + clusterGatewayPassageway.getOutPort() + "'";
                        transferPaths += ",";
                    }
                }
                sysRun.setTransferPaths(transferPaths.substring(0, transferPaths.length() - 1));
            }else{
                sysRun.setTransferPaths("''");
            }
        }
        List<SysRunning> sysRunList = iSysRunningDao.getSysRunList(sysRun);


        if (sysRunList != null && sysRunList.size() != 0) {
            SysRunning sysRunningOne = new SysRunning();
            long count = 0;
            for (SysRunning sysRunning : sysRunList) {
                ++count;
                ServiceManager serviceManager = new ServiceManager();

                List<ServiceManager> serviceManagers = serviceManagerList.stream().filter(serviceManager1 -> serviceManager1.getServiceId().equals(sysRunning.getServiceID())).collect(Collectors.toList());
                if (serviceManagers != null && serviceManagers.size() != 0) {
                    serviceManager = serviceManagers.get(0);
                } else {
                    continue;
                }

                String transferPath = "";
                String startTime = "";
                String endTime = "";
                String requestType = "";
                Double transportSize = 0.00;
                Double transportBitStream = 0.00;

                startTime = sysRunning.getTransportStartTime() == null ? sysRunningOne.getTransportStartTime() : sysRunning.getTransportStartTime();

                endTime = sysRunning.getTransportStopTime() == null ? sysRunningOne.getTransportStopTime() : sysRunning.getTransportStopTime();

                if (sysRunning.getTransportBitStream() != null && sysRunning.getTransportBitStream() != 0) {
                    transportBitStream = sysRunning.getTransportBitStream();
                    sysRunningOne.setTransportBitStream(transportBitStream);
                }
                if (StringUtils.isNotBlank(sysRunning.getRequestType())) {
                    requestType = sysRunning.getRequestType();
                    sysRunningOne.setRequestType(requestType);
                }
                if (StringUtils.isNotBlank(sysRunning.getRequestIPPort())) {
                    transferPath = getTransferName(sysRunning.getRequestIPPort());
                    if (StringUtils.isNotBlank(transferPath)) {
                        sysRunningOne.setTransferPath(transferPath);
                    }

                }

                startTime = startTime == null ? "" : startTime;
                endTime = endTime == null ? "" : endTime;
                sysRunningOne.setTransportStartTime(startTime);
                sysRunningOne.setTransportStopTime(endTime);

                if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
                    sysRunningOne.setVisitTime((DateTool.StringToDateLong(endTime, "") - DateTool.StringToDateLong(startTime, "")) / 1000);
                    transportSize = sysRunningOne.getVisitTime() * sysRunningOne.getTransportBitStream();
                    sysRunningOne.setTransportSize(transportSize);

                }
                if (count % 2 == 0) {
                    sysRunningOne.setId(sysRunning.getId());
                    sysRunningOne.setServiceID(sysRunning.getServiceID());
                    sysRunningOne.setServiceName(serviceManager.getServiceName());
                    sysRunningOne.setPlatName(serviceManager.getResPlatformRegister().getName());
                    sysRunningOne.setTargetIp(serviceManager.getResPlatformRegister().getRequestIp());
                    sysRunningList.add(sysRunningOne);
                    sysRunningOne = new SysRunning();
                }
            }
        } else {
            sysRunningPageHelper.setObjects(sysRunningList);
            sysRunningPageHelper.setPageNum(sysRunningPageHelper.getPageNum());
            sysRunningPageHelper.setPageSize(sysRunningPageHelper.getPageSize());
            return sysRunningPageHelper;
        }

        long total = iSysRunningDao.getSysRunListCount(sysRun);
        sysRunningPageHelper.setPageCount(total/2);
        sysRunningPageHelper.setObjects(sysRunningList);
        sysRunningPageHelper.setPageNum(sysRunningPageHelper.getPageNum());
        sysRunningPageHelper.setPageSize(sysRunningPageHelper.getPageSize());
        return sysRunningPageHelper;
    }

    @Override
    public List<String> queryPath() throws Exception {
        List<String> paths = new ArrayList<>();
        List<ClusterConfig> clusterConfigs = clusterConfigService.queryAll();
        clusterConfigs = clusterConfigs.stream().filter(clusterConfig -> clusterConfig.getType().equals(Constants.CLUSTER_CONFIG_TYPE_GATEWAY)).collect(Collectors.toList());
        clusterConfigs.forEach(clusterConfig -> paths.add(clusterConfig.getName()));
        return paths;
    }

    @Override
    public Boolean delete(List<String> ids) throws Exception {
        for (String id : ids) {
            iSysRunningDao.delete(id);
        }
        return true;
    }

    @Override
    public Boolean deleteAll() throws Exception {
        int result = iSysRunningDao.deleteAll();
        return result == 0 ? false : true;

    }

    public String getTransferName(String transferPath) throws Exception {
        String name = "";
        String ip = transferPath.split(":")[0];
        String port = transferPath.split(":")[1];
        List<ClusterConfig> clusterConfigs = clusterConfigService.queryAll();
        clusterConfigs = clusterConfigs.stream().filter(clusterConfig -> clusterConfig.getType().equalsIgnoreCase(Constants.CLUSTER_CONFIG_TYPE_GATEWAY)).collect(Collectors.toList());
        for (ClusterConfig clusterConfig : clusterConfigs) {
            for (ClusterGatewayPassageway clusterGatewayPassageway : clusterConfig.getSignalPassageways()) {
                if (ip.equals(clusterGatewayPassageway.getInIp()) && port.equals(clusterGatewayPassageway.getInPort())) {
                    name = clusterConfig.getName();
                }
            }
            for (ClusterGatewayPassageway clusterGatewayPassageway : clusterConfig.getVideoPassageways()) {
                if (ip.equals(clusterGatewayPassageway.getInIp()) && port.equals(clusterGatewayPassageway.getInPort())) {
                    name = clusterConfig.getName();
                }
            }
        }

        return name;
    }
}
