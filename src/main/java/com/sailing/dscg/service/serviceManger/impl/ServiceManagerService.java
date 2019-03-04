package com.sailing.dscg.service.serviceManger.impl;

import com.sailing.dscg.common.*;
import com.sailing.dscg.common.page.PageHelper;
import com.sailing.dscg.dao.IRefuseCataDao;
import com.sailing.dscg.entity.RespData;
import com.sailing.dscg.entity.serviceManager.GetGateWay;
import com.sailing.dscg.entity.serviceManager.ServiceManager;
import com.sailing.dscg.entity.serviceManager.ServiceManagerCoum;
import com.sailing.dscg.entity.system.ClusterConfig;
import com.sailing.dscg.entity.system.ClusterGatewayPassageway;
import com.sailing.dscg.service.serviceManger.IServiceMangerService;
import com.sailing.dscg.service.system.impl.ClusterConfigService;
import com.sailing.dscg.zookeeper.ZookeeperServer;
import lombok.extern.slf4j.Slf4j;
import net.hydromatic.linq4j.Linq4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Description:
 * <p>
 * Update by Panyu on 2018/10/12 下午 03:11:38
 */
@Service
@Slf4j
public class ServiceManagerService implements IServiceMangerService {

    @Autowired
    private ZookeeperServer<ServiceManager> zookeeperServer;

//    @Autowired
//    private ZookeeperServer<PlatformRegister> zk_plat;

    @Autowired
    private ClusterConfigService clusterConfigService;

    @Autowired
    private IRefuseCataDao iRefuseCataDao;

    @Override
    public List<ServiceManager> queryAll() throws Exception {
        return zookeeperServer.queryAll(ServiceManager.class);
    }

    @Override
    public PageHelper queryList(PageHelper<ServiceManager> pageHelper) throws Exception {
        List<ServiceManager> serviceManagers = zookeeperServer.queryAll(ServiceManager.class);
        pageHelper.setPageCount(serviceManagers.size());
        serviceManagers = Linq4j.asEnumerable(serviceManagers).skip((pageHelper.getPageNum() - 1) * pageHelper.getPageSize()).take(pageHelper.getPageSize()).toList();
        pageHelper.setObjects(serviceManagers);
        return pageHelper;
    }

    @Override
    public ServiceManager get(ServiceManager serviceManager) throws Exception {
        return zookeeperServer.get(serviceManager.getId(), ServiceManager.class);
    }

    @Override
    public Boolean save(ServiceManager config) throws Exception {
        if (StringUtils.isBlank(config.getId())) {
            config.setId(Tools.getUUID());
            config.setServiceId(Tools.generateServiceID("ServiceManager"));
            config.setStatus("undeploy");
            config.setCreateTime(DateTool.getCurrDateString());
            config.setCreateUser(CacheUtils.getAdminLoginName());
            return zookeeperServer.create(config.getId(), config, ServiceManager.class);
        } else {
            ServiceManager serviceManager = get(config);
            serviceManager.setServiceName(config.getServiceName());
            serviceManager.setModifyTime(config.getModifyTime());
            serviceManager.setStatus(config.getStatus());
//            serviceManager.setStatus("deploy");
            serviceManager.setReqPlatformRegister(config.getReqPlatformRegister());
            serviceManager.setReqClusterConfig(config.getReqClusterConfig());
            serviceManager.setResPlatformRegister(config.getResPlatformRegister());
            serviceManager.setResClusterConfig(config.getResClusterConfig());
            serviceManager.setAllowBidirection(config.getAllowBidirection());
            serviceManager.setRunningModel(config.getRunningModel());
            serviceManager.setAllocation(config.getAllocation());
            serviceManager.setLocalVideoGateway(config.getLocalVideoGateway());
//            serviceManager.setLocalVideoGateway("");
            serviceManager.setLocalSignalGateWay(config.getLocalSignalGateWay());
            serviceManager.setAlloVideoGateway(config.getAlloVideoGateway());
            serviceManager.setAlloSignalGateWay(config.getAlloSignalGateWay());
            serviceManager.setSipServiceType(config.getSipServiceType());
            serviceManager.setSipOperateType(config.getSipOperateType());
            serviceManager.setMaxAllowBw(config.getMaxAllowBw());
            serviceManager.setPriority(config.getPriority());
            serviceManager.setPolling(config.getPolling());
            return zookeeperServer.update(serviceManager.getId(), serviceManager, ServiceManager.class);

        }
    }

    @Override
    public Boolean delete(ServiceManager serviceManager) throws Exception {
        if (serviceManager.getStatus().equals(Constants.START)) {
            return false;
        } else {
            iRefuseCataDao.delete(serviceManager.getServiceId());
            return zookeeperServer.delNode(serviceManager.getId(), ServiceManager.class);
        }
    }


    @Override
    public RespData<String> deploy(ServiceManager serviceManager1) throws Exception {
        RespData<String> result = new RespData<>();
        RespData<String> resultUp = new RespData<>();
        RespData<String> resultDown = new RespData<>();
        try {
            ServiceManager serviceManager = get(serviceManager1);
            serviceManager.setModel(serviceManager1.getModel());
            List<ServiceManagerCoum> serviceManagerCoums = setServiceManager(serviceManager);
//            System.out.println("serviceManagerCoums:" + serviceManagerCoums);

            /*
             * Up的部署
             */
            ServiceManagerCoum serviceManagerCoumUp = serviceManagerCoums.get(0);
            resultUp = ConsoleBusTool.options(serviceManagerCoumUp, serviceManager.getReqClusterConfig().getIp(), serviceManager.getModel());

            /*
             * Down的部署
             * 走网闸通道访问响应节点
             */
            ServiceManagerCoum serviceManagerCoumDown = serviceManagerCoums.get(1);
            String gatewayIp = getSignalIpByResIp(serviceManager.getResClusterConfig().getIp());
            if (StringUtils.isBlank(gatewayIp)){
                result.setRespCode(RespCodeEnum.FAIL);
                result.setReason("无相关信令网闸！");
                return result;
            }
            resultDown = ConsoleBusTool.options(serviceManagerCoumDown, gatewayIp, serviceManager.getModel());


            if (resultUp.getCode() == 200 && resultDown.getCode() == 200) {
                result.setRespCode(RespCodeEnum.SUCCESS);
                if ("deploy".equals(serviceManager.getModel())) {
                    serviceManager.setStatus("deploy");
                } else if ("start".equals(serviceManager.getModel())) {
                    serviceManager.setStatus("start");
                } else {
                    serviceManager.setStatus("stop");
                }
                serviceManager.setModifyTime(DateTool.getCurrDateString());
                save(serviceManager);
            }
        } catch (Exception e) {
            log.debug("服务部署异常", e);
            throw new Exception(e.getMessage());
        }
        return result;
    }


//    @Override
//    public List<PlatformRegister> getPlats() throws Exception {
//        return zk_plat.queryAll(PlatformRegister.class);
//    }

    @Override
    public List<ClusterConfig> getClusters() throws Exception {
        List<ClusterConfig> clusterConfigs = clusterConfigService.queryAll();
        clusterConfigs = clusterConfigs.stream().filter(clusterConfig -> clusterConfig.getType().equalsIgnoreCase(Constants.CLUSTER_CONFIG_TYPE_VSCG)).collect(Collectors.toList());
        return clusterConfigs;
    }

    @Override
    public List<String> getGateWay(GetGateWay getGateWay) throws Exception {
        List<ClusterConfig> clusterConfigs = clusterConfigService.queryAll();

        clusterConfigs = clusterConfigs.stream().filter(clusterConfig -> Constants.CLUSTER_CONFIG_TYPE_GATEWAY.equals(clusterConfig.getType()))
                .filter(clusterConfig -> clusterConfig.getInOut().equals(getGateWay.getType()))
                .collect(Collectors.toList());
        List<String> ipAndPort = new ArrayList<>();
        List<ClusterGatewayPassageway> clusterGatewayPassageways = new ArrayList<>();
        for (ClusterConfig clusterConfig : clusterConfigs) {
            if (Constants.SIGNAL.equals(getGateWay.getInOut())) {
                clusterGatewayPassageways = clusterConfig.getSignalPassageways();
            } else if (Constants.VIDEO.equals(getGateWay.getInOut())) {
                clusterGatewayPassageways = clusterConfig.getVideoPassageways();
            }
            for (ClusterGatewayPassageway clusterGatewayPassageway : clusterGatewayPassageways) {
                if (getGateWay.getReqIp().contains(clusterGatewayPassageway.getDestNode()) && !"8000".equals(clusterGatewayPassageway.getInPort())) {
                    ipAndPort.add(clusterGatewayPassageway.getInIp() + ":" + clusterGatewayPassageway.getInPort());
                }
            }
        }
        return ipAndPort;
    }

    public Boolean exitsIp(String ip) throws Exception {
        String localVideoGateway = "";
        String localSignalGateway = "";
        String alloVideoGateway = "";
        String alloSignalGateway = "";
        List<String> lvg = new ArrayList<>();
        List<String> lsg = new ArrayList<>();
        List<String> avg = new ArrayList<>();
        List<String> asg = new ArrayList<>();
        List<ServiceManager> serviceManagers = zookeeperServer.queryAll(ServiceManager.class);
        for (ServiceManager serviceManager : serviceManagers) {
            if (StringUtils.isNotBlank(serviceManager.getLocalVideoGateway()))
                localVideoGateway = serviceManager.getLocalVideoGateway();
            if (StringUtils.isNotBlank(serviceManager.getLocalSignalGateWay()))
                localSignalGateway = serviceManager.getLocalSignalGateWay();
            if (StringUtils.isNotBlank(serviceManager.getAlloVideoGateway()))
                alloVideoGateway = serviceManager.getAlloVideoGateway();
            if (StringUtils.isNotBlank(serviceManager.getAlloVideoGateway()))
                alloSignalGateway = serviceManager.getAlloSignalGateWay();
            lvg.addAll(Tools.getIpAndPort(localVideoGateway, 1));
            lsg.addAll(Tools.getIpAndPort(localSignalGateway, 1));
            avg.addAll(Tools.getIpAndPort(alloVideoGateway, 1));
            asg.addAll(Tools.getIpAndPort(alloSignalGateway, 1));
        }
        if (lvg.contains(ip) || lsg.contains(ip) || avg.contains(ip) || asg.contains(ip)) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean existPlatIp(String ip) throws Exception {
        List<ServiceManager> serviceManagers = queryAll();
        for (ServiceManager serviceManager : serviceManagers) {
            if (ip.equals(serviceManager.getReqPlatformRegister().getRequestIp())) {
                return true;
            }
            if (ip.equals(serviceManager.getResPlatformRegister().getRequestIp())) {
                return true;
            }
        }
        return false;
    }

    public Boolean existNodeIp(String ip) throws Exception {
        List<ServiceManager> serviceManagers = queryAll();
        for (ServiceManager serviceManager : serviceManagers) {
            if (ip.equals(serviceManager.getReqClusterConfig().getIp())) {
                return true;
            }
            if (ip.equals(serviceManager.getResClusterConfig().getIp())) {
                return true;
            }
        }
        return false;
    }

    public List<ServiceManagerCoum> setServiceManager(ServiceManager serviceManager) throws Exception {
        List<ServiceManagerCoum> serviceManagerCoums = new ArrayList<>();
        //当前检测版本rtp地址移除端口号
        String localVideoGateway = serviceManager.getLocalVideoGateway();
        String localVideoGatewayIpPort = "";
//        if(StringUtils.isNotBlank(localVideoGateway)){
//            String[] videoGatewayArr = localVideoGateway.split(";");
//            String videoGateway = videoGatewayArr[0];
//            localVideoGatewayIpPort = videoGateway.substring(0,videoGateway.indexOf(":"));
//        }

        if (StringUtils.isNotBlank(localVideoGateway)) {
            String[] videoGatewayArrs = localVideoGateway.split(";");
            for (String s : videoGatewayArrs) {
                localVideoGatewayIpPort += s.split(":")[0] + ";";
            }
            localVideoGatewayIpPort = localVideoGatewayIpPort.substring(0, localVideoGatewayIpPort.length() - 1);
        }

        String alloVideoGateway = serviceManager.getAlloVideoGateway();
        String alloVideoGatewayIpPort = "";
        if (StringUtils.isNotBlank(alloVideoGateway)) {
            String[] videoGatewayArr = alloVideoGateway.split(";");
            for (String s : videoGatewayArr) {
                alloVideoGatewayIpPort += s.split(":")[0] + ";";
            }
            alloVideoGatewayIpPort = alloVideoGatewayIpPort.substring(0, alloVideoGatewayIpPort.length() - 1);
        }

        //信令网闸分内外向
        String upIp = "";
        String downIp = "";
        ClusterConfig clusterConfig = new ClusterConfig();
        clusterConfig.setType("gateway");

        if (StringUtils.isNotBlank(serviceManager.getLocalSignalGateWay())) {
            String localSignalGateway = serviceManager.getLocalSignalGateWay();
            String[] localSignalGateways = localSignalGateway.split(";");
            clusterConfig.setInOut("local");
            SetUpAndDown setLocal = new SetUpAndDown(serviceManager, upIp, downIp, clusterConfig, localSignalGateways).invoke();
            upIp += setLocal.getUpIp();
            downIp += setLocal.getDownIp();
        }
        //异地
        if (StringUtils.isNotBlank(serviceManager.getAlloSignalGateWay())) {
            String alloSignalGateway = serviceManager.getAlloSignalGateWay();
            String[] alloSignalGateways = alloSignalGateway.split(";");
            clusterConfig.setInOut("allo");
            SetUpAndDown setAllo = new SetUpAndDown(serviceManager, upIp, downIp, clusterConfig, alloSignalGateways).invoke();
            upIp += setAllo.getUpIp();
            downIp += setAllo.getDownIp();
        }

        /*
         * Up的部署
         */
        ServiceManagerCoum serviceManagerCoumUp = new ServiceManagerCoum();
        serviceManagerCoumUp.setServiceId(serviceManager.getServiceId());
        serviceManagerCoumUp.setAllocation(serviceManager.getAllocation());
//        serviceManagerCoumUp.setRtpReDirectIP(localVideoGatewayIpPort);
        //暂时默认为True
        serviceManagerCoumUp.setVideoTransmission(true);
        //暂时默认为true
        serviceManagerCoumUp.setDeviceAuthen(true);
        //暂时默认为8
        serviceManagerCoumUp.setTransportBitStream("8");
        serviceManagerCoumUp.setUpIPAndPort(serviceManager.getReqPlatformRegister().getRequestIp() + ":" + serviceManager.getReqPlatformRegister().getRequestPort());
//        serviceManagerCoumUp.setDownIPAndPort(serviceManager.getLocalSignalGateWay());
        if (StringUtils.isNotBlank(serviceManager.getLocalSignalGateWay())) {
//            serviceManagerCoumUp.setDownIPAndPort(serviceManager.getLocalSignalGateWay());
            serviceManagerCoumUp.setDownIPAndPort(upIp.substring(0, upIp.length() - 1));
        } else {
            serviceManagerCoumUp.setDownIPAndPort(serviceManager.getResClusterConfig().getIp() + ":" + serviceManager.getResClusterConfig().getPort());
        }
        serviceManagerCoumUp.setSalverUpIPAndPort(serviceManagerCoumUp.getUpIPAndPort());
        if (StringUtils.isNotBlank(serviceManager.getAlloSignalGateWay())) {
            serviceManagerCoumUp.setSalverDownIPAndPort(serviceManager.getAlloSignalGateWay());
        }
        if (StringUtils.isNotBlank(serviceManager.getAlloVideoGateway())) {
            serviceManagerCoumUp.setSalverRtpReDirectIP(alloVideoGatewayIpPort);
        }
        //默认的Up的部署，端口为页面灵活可配置
        serviceManagerCoumUp.setSelfIPAndPort(serviceManager.getReqClusterConfig().getIp() + ":" + serviceManager.getReqClusterConfig().getPort());
        serviceManagerCoumUp.setCenterControl("up");
        serviceManagerCoums.add(serviceManagerCoumUp);

        /*
         * Down的部署
         */
        ServiceManagerCoum serviceManagerCoumDown = new ServiceManagerCoum();
        serviceManagerCoumDown.setServiceId(serviceManager.getServiceId());
        serviceManagerCoumDown.setAllocation(serviceManager.getAllocation());
        serviceManagerCoumDown.setRtpReDirectIP(localVideoGatewayIpPort);
//        serviceManagerCoumDown.setRtpReDirectIP("");
        //暂时默认为True
        serviceManagerCoumDown.setVideoTransmission(false);
        //暂时默认为true
        serviceManagerCoumDown.setDeviceAuthen(false);
        //暂时默认为8
        serviceManagerCoumDown.setTransportBitStream("8");
//        serviceManagerCoumDown.setUpIPAndPort(serviceManager.getLocalSignalGateWay());
//        String upIpAndPort = existReqIp(serviceManager.getReqClusterConfig().getIp());
        if (StringUtils.isNotBlank(serviceManager.getLocalSignalGateWay())) {
//            serviceManagerCoumDown.setUpIPAndPort(serviceManager.getLocalSignalGateWay());
            serviceManagerCoumDown.setUpIPAndPort(downIp.substring(0, downIp.length() - 1));
        } else {
            serviceManagerCoumDown.setUpIPAndPort(serviceManager.getReqClusterConfig().getIp() + ":" + serviceManager.getReqClusterConfig().getPort());
        }
        serviceManagerCoumDown.setDownIPAndPort(serviceManager.getResPlatformRegister().getRequestIp() + ":" + serviceManager.getResPlatformRegister().getRequestPort());

        if (StringUtils.isNotBlank(serviceManager.getAlloSignalGateWay())) {
            serviceManagerCoumDown.setSalverUpIPAndPort(serviceManager.getAlloSignalGateWay());
        }
        serviceManagerCoumDown.setSalverDownIPAndPort(serviceManagerCoumDown.getDownIPAndPort());

        if (StringUtils.isNotBlank(serviceManager.getAlloVideoGateway())) {
            serviceManagerCoumDown.setSalverRtpReDirectIP(alloVideoGatewayIpPort);
        }
        //默认的Down的部署，响应界定啊的端口默认为5061
        serviceManagerCoumDown.setSelfIPAndPort(serviceManager.getResClusterConfig().getIp() + ":5061");
        serviceManagerCoumDown.setCenterControl("down");
        serviceManagerCoumDown.setPolling(serviceManager.getPolling());
        serviceManagerCoums.add(serviceManagerCoumDown);

        return serviceManagerCoums;
    }

    public String existReqIp(String ip) throws Exception {
        StringBuffer ips = new StringBuffer();
        List<ClusterConfig> clusterConfigs = clusterConfigService.queryAll();
        clusterConfigs = clusterConfigs.stream().filter(clusterConfig -> clusterConfig.getType().equals(Constants.CLUSTER_CONFIG_TYPE_GATEWAY)).collect(Collectors.toList());
        for (ClusterConfig clusterConfig : clusterConfigs) {
            for (ClusterGatewayPassageway clusterGatewayPassageway : clusterConfig.getSignalPassageways()) {
                if (clusterGatewayPassageway.getDestNode().equals(ip)) {
                    ips.append(clusterConfig.getIp() + ":" + clusterGatewayPassageway.getInPort());
                    ips.append(";");
                }
            }
        }
        if (ips.length() > 0) {
            return ips.substring(0, ips.length() - 1);
        } else {
            return null;
        }

    }

    public String getSignalIpByResIp(String ip) throws Exception {
        String gatewayIp = "";
        List<ClusterConfig> clusterConfigs = clusterConfigService.queryAll();
        clusterConfigs = clusterConfigs.stream().filter(clusterConfig -> clusterConfig.getType().equalsIgnoreCase(Constants.CLUSTER_CONFIG_TYPE_GATEWAY)).collect(Collectors.toList());
        for (ClusterConfig clusterConfig : clusterConfigs) {
            if (clusterConfig.getSignalPassageways() != null && clusterConfig.getSignalPassageways().size() != 0) {
                for (ClusterGatewayPassageway clusterGatewayPassageway : clusterConfig.getSignalPassageways()) {
                    if (clusterGatewayPassageway.getDestNode().equals(ip)) {
                        gatewayIp = clusterGatewayPassageway.getInIp();
                    }
                }
            }else{
                return null;
            }
        }
        return gatewayIp;
    }

    private class SetUpAndDown {
        private ServiceManager serviceManager;
        private String upIp;
        private String downIp;
        private ClusterConfig clusterConfig;
        private String[] SignalGateways;

        public SetUpAndDown(ServiceManager serviceManager, String upIp, String downIp, ClusterConfig clusterConfig, String... SignalGateways) {
            this.serviceManager = serviceManager;
            this.upIp = upIp;
            this.downIp = downIp;
            this.clusterConfig = clusterConfig;
            this.SignalGateways = SignalGateways;
        }

        public String getUpIp() {
            return upIp;
        }

        public String getDownIp() {
            return downIp;
        }

        public SetUpAndDown invoke() throws Exception {
            List<ClusterConfig> clusterConfigs;
            clusterConfigs = clusterConfigService.queryList(clusterConfig);
            for (ClusterConfig config : clusterConfigs) {
                List<ClusterGatewayPassageway> clusterGatewayPassageways = config.getSignalPassageways();
                for (ClusterGatewayPassageway clusterGatewayPassageway : clusterGatewayPassageways) {
                    for (String signalGateway : SignalGateways) {
                        if ((clusterGatewayPassageway.getInIp() + ":" + clusterGatewayPassageway.getInPort()).equals(signalGateway)) {
                            if (clusterGatewayPassageway.getDestNode().equals(serviceManager.getReqClusterConfig().getIp())) {
                                downIp += signalGateway + ";";
                            } else if (clusterGatewayPassageway.getDestNode().equals(serviceManager.getResClusterConfig().getIp())) {
                                upIp += signalGateway + ";";
                            }
                        }
                    }
                }
            }
            return this;
        }
    }
}
