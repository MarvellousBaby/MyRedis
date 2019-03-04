package com.sailing.dscg.service.platformRegister.impl;

import com.sailing.dscg.common.CacheUtils;
import com.sailing.dscg.common.Constants;
import com.sailing.dscg.common.DateTool;
import com.sailing.dscg.common.Tools;
import com.sailing.dscg.common.page.PageHelper;
import com.sailing.dscg.entity.platformRegister.PlatformRegister;
import com.sailing.dscg.entity.serviceManager.ServiceManager;
import com.sailing.dscg.entity.system.ClusterConfig;
import com.sailing.dscg.entity.system.ClusterGatewayPassageway;
import com.sailing.dscg.service.platformRegister.IPlatformRegisterService;
import com.sailing.dscg.service.serviceManger.impl.ServiceManagerService;
import com.sailing.dscg.service.system.impl.ClusterConfigService;
import com.sailing.dscg.zookeeper.ZookeeperServer;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Description:
 * <p>
 * Update by Panyu on 2018/9/14 上午 11:21:22
 */
@Service
public class PlatformRegisterService implements IPlatformRegisterService {

    @Autowired
    private ZookeeperServer<PlatformRegister> zookeeperServer;

    @Autowired
    private ClusterConfigService clusterConfigService;

    @Autowired
    private ServiceManagerService serviceManagerService;

    @Override
    public PageHelper<PlatformRegister> queryAll() throws Exception {
        PageHelper pageHelper = new PageHelper();
        pageHelper.setObjects(zookeeperServer.queryAll(PlatformRegister.class));
        return pageHelper;
    }

    @Override
    public PageHelper<PlatformRegister> queryList(PageHelper<PlatformRegister> platformRegisterPageHelper) throws Exception {
        return null;
    }

    @Override
    public PageHelper<PlatformRegister> get(PageHelper<PlatformRegister> platformRegisterPageHelper) throws Exception {
        PlatformRegister platformRegister = platformRegisterPageHelper.getObject();
        platformRegister = zookeeperServer.get(platformRegister.getId(),PlatformRegister.class);
        platformRegisterPageHelper.setObject(platformRegister);
        return platformRegisterPageHelper;
    }

    @Override
    public Boolean save(PageHelper<PlatformRegister> platformRegisterPageHelper) throws Exception {
        List<PlatformRegister> platformRegisterList = queryAll().getObjects();
        PlatformRegister platformRegister = platformRegisterPageHelper.getObject();
        if (StringUtils.isBlank(platformRegister.getId())){
            platformRegister.setId(Tools.getUUID());
            platformRegister.setCreateTime(DateTool.getCurrDateString());
            platformRegister.setCreateUser(CacheUtils.getAdminLoginName());
            platformRegisterList = platformRegisterList.stream().filter(platformRegister1 ->platformRegister1.getRequestIp().equals(platformRegister.getRequestIp())&&
                                        platformRegister1.getRtpIp().equals(platformRegister.getRtpIp())).collect(Collectors.toList());
            if (platformRegisterList!=null&&platformRegisterList.size()!=0){
                return false;
            }
            Boolean result = zookeeperServer.create(platformRegister.getId(),platformRegister,PlatformRegister.class);
            return result;
        }else{
            PlatformRegister config = get(platformRegisterPageHelper).getObject();
            PlatformRegister platOld = config;
            config.setNetName(platformRegister.getNetName());
            config.setName(platformRegister.getName());
            config.setPlatNo(platformRegister.getPlatNo());
            config.setRequestIp(platformRegister.getRequestIp());
            config.setRequestPort(platformRegister.getRequestPort());
            config.setPlatVersion(platformRegister.getPlatVersion());
            config.setGetBySNMP(platformRegister.getGetBySNMP());
            config.setIp(platformRegister.getIp());
            config.setPort(platformRegister.getPort());
            config.setRtpIp(platformRegister.getRtpIp());
            config.setRtpPort(platformRegister.getRtpPort());

            existPs(config,platOld);

            Boolean result = zookeeperServer.update(config.getId(),config,PlatformRegister.class);
            return result;

        }
    }

    @Override
    public Boolean deleteSingle(PlatformRegister platformRegister) throws Exception{
        String ip = platformRegister.getRequestIp();
        if (clusterConfigService.existIp(ip) || serviceManagerService.existPlatIp(ip)){
            return false;
        }else {
            return zookeeperServer.delNode(platformRegister.getId(), PlatformRegister.class);
        }

    }

    @Override
    public Boolean delete(String[] ids) throws Exception {
        return null;
    }

    @Override
    public Boolean deleteAll() throws Exception {
        return null;
    }

    //如果修改的平台在未启动的服务里面有的话则同步修改服务管理
    private void existPs(PlatformRegister platformRegister,PlatformRegister platOld) throws Exception{
        List<ServiceManager> serviceManagerList = serviceManagerService.queryAll();
        serviceManagerList = serviceManagerList.stream().filter(serviceManager ->  serviceManager.getStatus().equals(Constants.UNDEPLOY)||serviceManager.getStatus().equals(Constants.DEPLOY)
                ||serviceManager.getStatus().equals(Constants.STOP)).collect(Collectors.toList());
        for (ServiceManager serviceManager:serviceManagerList){
            PlatformRegister reqPlatformRegister = serviceManager.getReqPlatformRegister();
            PlatformRegister resPlatformRegister = serviceManager.getResPlatformRegister();
            if (reqPlatformRegister.getId().equals(platformRegister.getId())){
                serviceManager.setReqPlatformRegister(platformRegister);
            }
            if (resPlatformRegister.getId().equals(platformRegister.getId())){
                serviceManager.setResPlatformRegister(platformRegister);
            }
            serviceManagerService.save(serviceManager);
        }
        List<ClusterConfig> clusterConfigs = clusterConfigService.queryAll();
        clusterConfigs = clusterConfigs.stream().filter(clusterConfig -> clusterConfig.getInOut().equals(Constants.CLUSTER_CONFIG_TYPE_GATEWAY)).collect(Collectors.toList());
        for (ClusterConfig clusterConfig:clusterConfigs){
            List<ClusterGatewayPassageway> clusterGatewayPassageways = clusterConfig.getVideoPassageways();
            for (ClusterGatewayPassageway clusterGatewayPassageway:clusterGatewayPassageways){
                if (clusterGatewayPassageway.getDestNode().equals(platOld.getRtpIp())){
                    clusterGatewayPassageway.setDestNode(platformRegister.getRtpIp());
                }
            }
            clusterConfig.setVideoPassageways(clusterGatewayPassageways);
            clusterConfigService.save(clusterConfig);
        }



    }
}
