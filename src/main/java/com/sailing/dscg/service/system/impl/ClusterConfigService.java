package com.sailing.dscg.service.system.impl;

import com.sailing.dscg.common.CacheUtils;
import com.sailing.dscg.common.Constants;
import com.sailing.dscg.common.DateTool;
import com.sailing.dscg.common.Tools;
import com.sailing.dscg.entity.platformRegister.PlatformRegister;
import com.sailing.dscg.entity.serviceManager.ServiceManager;
import com.sailing.dscg.entity.system.ClusterConfig;
import com.sailing.dscg.entity.system.ClusterGatewayPassageway;
import com.sailing.dscg.service.serviceManger.impl.ServiceManagerService;
import com.sailing.dscg.service.system.IClusterConfigService;
import com.sailing.dscg.zookeeper.ZookeeperServer;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 集群配置Service类
 * @Auther:史俊华
 * @Date:2018/6/271919
 */
@Service
public class ClusterConfigService implements IClusterConfigService {
    @Autowired
    private ZookeeperServer<ClusterConfig> zookeeperServer;

    @Autowired
    private ServiceManagerService serviceManagerService;

    @Autowired
    private ZookeeperServer<PlatformRegister> zkp;

    @Override
    public List<ClusterConfig> queryAll() throws Exception {
        return zookeeperServer.queryAll(ClusterConfig.class);
    }

    @Override
    public List<ClusterConfig> queryList(ClusterConfig clusterConfig) throws Exception {
        List<ClusterConfig> list = queryAll();
        if (StringUtils.isNotBlank(clusterConfig.getType())) {
            list = list.stream().filter(clusterConfig1 -> clusterConfig1.getType().equals(clusterConfig.getType())).collect(Collectors.toList());
        }
        if (StringUtils.isNotBlank(clusterConfig.getInOut())) {
            list = list.stream().filter(clusterConfig1 -> clusterConfig1.getInOut().equals(clusterConfig.getInOut())).collect(Collectors.toList());
        }
        return list;
    }

    @Override
    public ClusterConfig get(ClusterConfig clusterConfig) throws Exception {
        return zookeeperServer.get(clusterConfig.getId(), ClusterConfig.class);
    }

    @Override
    public ClusterConfig getClusterConfigByIp(String ip) throws Exception {
        List<ClusterConfig> list = queryAll();
        if (list != null && !list.isEmpty()) {
            list = list.stream().filter(clusterConfig1 -> clusterConfig1.getIp().equals(ip)).collect(Collectors.toList());
        }
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    /**
     * 获取网闸ip端口，返回ip:port
     *
     * @param ip   集群节点ip
     * @param port 集群节点端口
     * @return
     */
//    @Override
//    public String getPassagewayIpPortByNodeIp(String ip, Integer port) throws Exception {
//        List<ClusterConfig> list = queryAll();
//        if (list != null && !list.isEmpty()) {
//            list = list.stream().filter(clusterConfig -> clusterConfig.getType().equals(Constants.CLUSTER_CONFIG_TYPE_GATEWAY))
//                    .filter(clusterConfig -> {
//                        List<ClusterGatewayPassageway> passageways = clusterConfig.getPassageways();
//                        passageways = passageways.stream().filter(passageway -> passageway.getDscgNode().equals(ip))
//                                .filter(passageway -> passageway.getOutPort().equals(port)).collect(Collectors.toList());
//                        clusterConfig.setPassageways(passageways);
//                        return !passageways.isEmpty();
//                    }).collect(Collectors.toList());
//        }
//
//        String ipPort = "";
//        if (list != null && !list.isEmpty()) {
//            ClusterConfig config = list.get(0);
//            List<ClusterGatewayPassageway> passageways = config.getPassageways();
//            ipPort = config.getIp() + ":" + passageways.get(0).getInPort();
//        }
//        return ipPort;
//    }

    /**
     * 获取节点IP，返回ip
     *
     * @param ip   网闸ip
     * @param port 网闸监听端口
     * @return
     */
    @Override
    public String getNodeIpByGatewayIpPort(List<ClusterConfig> clusterConfigs, String ip, Integer port) throws Exception {
        List<ClusterConfig> list = clusterConfigs.stream().filter(clusterConfig -> {
//            if (clusterConfig.getIp().equals(ip)) {
            List<ClusterGatewayPassageway> passageways = clusterConfig.getSignalPassageways();
            passageways = passageways.stream().filter(passageway -> passageway.getInIp().equals(ip) && Integer.parseInt(passageway.getInPort()) == port.intValue()).collect(Collectors.toList());
            clusterConfig.setSignalPassageways(passageways);
            return !passageways.isEmpty();
//            } else {
//                return false;
//            }
        }).collect(Collectors.toList());
        String nodeIp = "";
        if (list != null && !list.isEmpty()) {
            ClusterConfig config = list.get(0);
            List<ClusterGatewayPassageway> passageways = config.getSignalPassageways();
            nodeIp = passageways.get(0).getDestNode();
        }
        return nodeIp;
    }

    //检测节点是否有相同的ip
    @Override
    public Boolean hasSameIp(ClusterConfig clusterConfig) throws Exception {
        Boolean needCheck = true;
        if (StringUtils.isNotBlank(clusterConfig.getId())) {
            ClusterConfig oldConfig = get(clusterConfig);
            if (oldConfig != null && StringUtils.isNotBlank(oldConfig.getIp())
                    && oldConfig.getIp().equals(clusterConfig.getIp())
                    && oldConfig.getType().equals(clusterConfig.getType())
                    && oldConfig.getInOut().equals(clusterConfig.getInOut())) {
                needCheck = false;
            }
        }

        if (needCheck) {
            List<ClusterConfig> clusterConfigs = queryList(clusterConfig);
            clusterConfigs = clusterConfigs.stream().filter(clusterConfig1 -> clusterConfig1.getIp().equals(clusterConfig.getIp())
                    && clusterConfig1.getType().equals(clusterConfig.getType()) && clusterConfig1.getInOut().equals(clusterConfig.getInOut())).collect(Collectors.toList());
            return (clusterConfigs == null && clusterConfigs.isEmpty()) ? true : false;
        } else {
            return false;
        }
    }

    //校验网闸的唯一性
    @Override
    public Boolean hasSameInIp(ClusterConfig clusterConfig) throws Exception {
        List<ClusterGatewayPassageway> signalPassageways = clusterConfig.getSignalPassageways();
        List<ClusterGatewayPassageway> videoPassageways = clusterConfig.getVideoPassageways();
        List<ClusterConfig> clusterConfigList = queryAll();
        clusterConfigList = clusterConfigList.stream().filter(clusterConfig1 -> Constants.CLUSTER_CONFIG_TYPE_GATEWAY.equals(clusterConfig1.getType())).collect(Collectors.toList());
        if (StringUtils.isNotBlank(clusterConfig.getId())) {
            clusterConfigList = clusterConfigList.stream().filter(clusterConfig1 -> !clusterConfig1.getId().equals(clusterConfig.getId()) && clusterConfig1.getInOut().equals(clusterConfig.getInOut()))
                    .collect(Collectors.toList());
        }
        List<String> signalIp = new ArrayList<>();
        List<String> videoIp = new ArrayList<>();
        for (ClusterConfig clusterConfig1 : clusterConfigList) {
            List<ClusterGatewayPassageway> signalPassagewayList = clusterConfig1.getSignalPassageways();
            List<ClusterGatewayPassageway> videoPassagewayList = clusterConfig1.getVideoPassageways();
            if (signalPassagewayList.isEmpty()) {
                continue;
            }
            for (ClusterGatewayPassageway clusterGatewayPassageway : signalPassagewayList) {
                signalIp.add(clusterGatewayPassageway.getInIp());
            }
            if (videoPassagewayList.isEmpty()) {
                continue;
            }
            for (ClusterGatewayPassageway clusterGatewayPassageway : videoPassagewayList) {
                videoIp.add(clusterGatewayPassageway.getInIp());
            }
        }

        for (ClusterGatewayPassageway clusterGatewayPassageway : signalPassageways) {
            if (signalIp.contains(clusterGatewayPassageway.getInIp())) {
                return true;
            }
        }
        for (ClusterGatewayPassageway clusterGatewayPassageway : videoPassageways) {
            if (signalIp.contains(clusterGatewayPassageway.getInIp())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Boolean save(ClusterConfig clusterConfig) throws Exception {
        if (StringUtils.isEmpty(clusterConfig.getId())) {
            //创建时间
            clusterConfig.setId(Tools.getUUID());
            clusterConfig.setCreateTime(DateTool.getCurrDateString());
            clusterConfig.setCreateUser(CacheUtils.getAdminLoginName());
            return zookeeperServer.create(clusterConfig.getId(), clusterConfig, ClusterConfig.class);
        } else {
            ClusterConfig config = get(clusterConfig);
            String ipOld = config.getIp();
            String ipNew = clusterConfig.getIp();
            //变更时间
            config.setModifyTime(DateTool.getCurrDateString());
            config.setModifyUser(CacheUtils.getAdminLoginName());
            config.setIp(clusterConfig.getIp());
            config.setName(clusterConfig.getName());
            config.setInOut(clusterConfig.getInOut());
            config.setRemark(clusterConfig.getRemark());
            config.setType(clusterConfig.getType());
            config.setSignalPassageways(clusterConfig.getSignalPassageways());
            config.setVideoPassageways(clusterConfig.getVideoPassageways());
            config.setPort(clusterConfig.getPort());
            if (existCs(ipNew, ipOld)) {
                return zookeeperServer.update(config.getId(), config, ClusterConfig.class);
            } else {
                return false;
            }

        }
    }

    @Override
    public Boolean deleteGateWay(ClusterConfig clusterConfig) throws Exception {
        String ip = clusterConfig.getIp();
        if (!(serviceManagerService.exitsIp(ip))) {
            return zookeeperServer.delNode(clusterConfig.getId(), ClusterConfig.class);
        } else {
            return false;
        }


    }


    @Override
    public Boolean delete(ClusterConfig clusterConfig) throws Exception {
        String ip = clusterConfig.getIp();
        if (this.existIp(ip) || serviceManagerService.existNodeIp(ip)) {
            return false;
        } else {
            return zookeeperServer.delNode(clusterConfig.getId(), ClusterConfig.class);
        }
    }

    @Override
    public List<String> clusterNodeTree(String type) throws Exception {
        List<String> clusterNodes = new ArrayList<>();
        List<ClusterConfig> listAll = queryAll();
        List<ClusterConfig> listType = listAll.stream().filter(clusterConfig -> clusterConfig.getType().equals(type)).collect(Collectors.toList());
        if (listType != null && !listType.isEmpty()) {
            for (ClusterConfig clusterConfig : listType) {
                String ip = clusterConfig.getIp();
                clusterNodes.add(ip);
            }
        }

        List<PlatformRegister> platformRegisters = zkp.queryAll(PlatformRegister.class);
        platformRegisters = platformRegisters.stream().filter(platformRegister -> StringUtils.isNotBlank(platformRegister.getRtpIp())).collect(Collectors.toList());
        for (PlatformRegister platformRegister : platformRegisters) {
            clusterNodes.add(platformRegister.getRtpIp());
        }
        return clusterNodes;
    }

    @Override
    public Boolean deleteAll() throws Exception {
        return null;
    }

    public Boolean existIp(String ip) throws Exception {
        List<ClusterConfig> clusterConfigs = queryAll();
        List<String> ipAndPort = new ArrayList<>();
        clusterConfigs = clusterConfigs.stream().filter(clusterConfig -> clusterConfig.getType().equals(Constants.CLUSTER_CONFIG_TYPE_GATEWAY)).collect(Collectors.toList());
        for (ClusterConfig clusterConfig : clusterConfigs) {
            List<ClusterGatewayPassageway> clusterGatewayPassageways = clusterConfig.getSignalPassageways();
            for (ClusterGatewayPassageway clusterGatewayPassageway : clusterGatewayPassageways) {
                if (ipAndPort.contains(clusterGatewayPassageway.getDestNode())) {
                    continue;
                }
                ipAndPort.add(clusterGatewayPassageway.getDestNode());
            }
        }
        if (ipAndPort.contains(ip)) {
            return true;
        } else {
            return false;
        }
    }

    private Boolean existCs(String ipNew, String ipOld) throws Exception {
        List<ServiceManager> serviceManagers = serviceManagerService.queryAll();
        String localVideoGateway = "";
        String localSignalGateway = "";
        String alloVideoGateway = "";
        String alloSignalGateway = "";
        List<String> lvg = new ArrayList<>();
        List<String> lsg = new ArrayList<>();
        List<String> avg = new ArrayList<>();
        List<String> asg = new ArrayList<>();
        List<ServiceManager> serviceManagerList = serviceManagers.stream().filter(serviceManager -> serviceManager.getStatus().equals(Constants.UNDEPLOY) && serviceManager.getStatus().equals(Constants.DEPLOY)
                && serviceManager.getStatus().equals(Constants.STOP)).collect(Collectors.toList());
        List<ServiceManager> serviceManagerList1 = serviceManagers.stream().filter(serviceManager -> serviceManager.getStatus().equals(Constants.START)).collect(Collectors.toList());
        for (ServiceManager serviceManager : serviceManagerList1) {
            if (serviceManager.getReqClusterConfig().getIp().equals(ipOld)) {
                return false;
            }
            if (serviceManager.getResClusterConfig().getIp().equals(ipOld)) {
                return false;
            }

        }

        if (serviceManagerList != null && serviceManagerList.size() != 0) {
            for (ServiceManager serviceManager : serviceManagerList) {
                if (StringUtils.isNotBlank(serviceManager.getLocalVideoGateway())) {
                    localVideoGateway = serviceManager.getLocalVideoGateway();
                    lvg.addAll(Tools.getIpAndPort(localVideoGateway, 1));
                }
                if (StringUtils.isNotBlank(serviceManager.getLocalSignalGateWay())) {
                    localSignalGateway = serviceManager.getLocalSignalGateWay();
                    lsg.addAll(Tools.getIpAndPort(localSignalGateway, 1));
                }
                if (StringUtils.isNotBlank(serviceManager.getAlloVideoGateway())) {
                    alloVideoGateway = serviceManager.getAlloVideoGateway();
                    avg.addAll(Tools.getIpAndPort(alloVideoGateway, 1));
                }
                if (StringUtils.isNotBlank(serviceManager.getAlloSignalGateWay())) {
                    alloSignalGateway = serviceManager.getAlloSignalGateWay();
                    asg.addAll(Tools.getIpAndPort(alloSignalGateway, 1));
                }
                if (existGateWay(lvg, ipOld)) {
                    localVideoGateway.replace(ipOld, ipNew);
                    serviceManager.setLocalVideoGateway(localVideoGateway);
                }
                if (existGateWay(lsg, ipOld)) {
                    localSignalGateway.replace(ipOld, ipNew);
                    serviceManager.setLocalSignalGateWay(localVideoGateway);
                }
                if (existGateWay(avg, ipOld)) {
                    alloVideoGateway.replace(ipOld, ipNew);
                    serviceManager.setAlloVideoGateway(localVideoGateway);
                }
                if (existGateWay(asg, ipOld)) {
                    alloSignalGateway.replace(ipOld, ipNew);
                    serviceManager.setAlloSignalGateWay(localVideoGateway);
                }

                serviceManagerService.save(serviceManager);
            }
            return true;

        }
        return true;
    }

    private Boolean existGateWay(List<String> gateways, String ip) {
        for (String str : gateways) {
            if (str.contains(ip)) {
                return true;
            }
        }
        return false;
    }

}
