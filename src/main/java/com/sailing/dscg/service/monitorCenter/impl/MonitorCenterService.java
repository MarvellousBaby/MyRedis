package com.sailing.dscg.service.monitorCenter.impl;

import com.sailing.dscg.common.*;
import com.sailing.dscg.dao.IMonitorCenterDao;
import com.sailing.dscg.dao.IRefuseCataDao;
import com.sailing.dscg.dao.ISysRunningDao;
import com.sailing.dscg.entity.RespData;
import com.sailing.dscg.entity.monitoring.*;
import com.sailing.dscg.entity.monitoring.test.GatewayInfo;
import com.sailing.dscg.entity.monitoring.test.GraphicMonitor;
import com.sailing.dscg.entity.platformRegister.PlatformRegister;
import com.sailing.dscg.entity.serviceManager.ServiceManager;
import com.sailing.dscg.entity.sysRunning.RouteAudit;
import com.sailing.dscg.entity.system.ClusterConfig;
import com.sailing.dscg.entity.system.ClusterGatewayPassageway;
import com.sailing.dscg.entity.videoTransmission.VideoTransmission;
import com.sailing.dscg.service.monitorCenter.IMonitorCenterService;
import com.sailing.dscg.service.platformRegister.impl.PlatformRegisterService;
import com.sailing.dscg.service.serviceManger.impl.ServiceManagerService;
import com.sailing.dscg.service.system.impl.ClusterConfigService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Description:
 * <p>
 * Update by Panyu on 2018/9/26 上午 09:57:50
 */
@Service
public class MonitorCenterService implements IMonitorCenterService {

    @Autowired
    private IMonitorCenterDao iMonitorCenterDao;

    @Autowired
    private ISysRunningDao iSysRunningDao;

    @Autowired
    private IRefuseCataDao iRefuseCataDao;


    @Autowired
    private ServiceManagerService serviceManagerService;

    @Autowired
    private PlatformRegisterService platformRegisterService;

    @Autowired
    private ClusterConfigService clusterConfigService;


    @Override
    public MonitorCount queryMonitorCount() {
        MonitorCount monitorCount = new MonitorCount();

        Long transReqCount = iMonitorCenterDao.transReqCount();
        transReqCount = transReqCount == null ? 0 : transReqCount;
        Long devicePassCount = iMonitorCenterDao.devicePassCount();
        devicePassCount = devicePassCount == null ? 0 : devicePassCount;
        Long passFlowSize = iMonitorCenterDao.passFlowSizes();
        passFlowSize = passFlowSize == null ? 0 : passFlowSize;
        List<String> passFlowSizes = Tools.getByteSize(passFlowSize * Math.pow(1024, 2));

        long deviceFail = iMonitorCenterDao.deviceFail();

        monitorCount.setTransReqCount(transReqCount);
        monitorCount.setDevicePassCount(devicePassCount);
        monitorCount.setDeviceFail(deviceFail);
        monitorCount.setPassFlowSize(passFlowSizes.get(0) + passFlowSizes.get(1));

        return monitorCount;
    }

    @Override
    public List<MonitorGraphic> queryMonitorGraphic(int model) throws Exception {
        List<MonitorGraphic> monitorGraphicList = new ArrayList<>();
        List<IpConnectMessage> ipConnectMessageReq = iMonitorCenterDao.queryConnStatus();
        List<ServiceManager> serviceManagers = serviceManagerService.queryAll();
        List<PlatformRegister> platformRegisterList = platformRegisterService.queryAll().getObjects();
        ClusterConfig clusterConfig = new ClusterConfig();
        clusterConfig.setType(Constants.CLUSTER_CONFIG_TYPE_GATEWAY);
        List<ClusterConfig> gateways = clusterConfigService.queryList(clusterConfig);
        List<String> gatewayIps = new ArrayList<>();
        for (ClusterConfig clusterConfig1 : gateways) {
            if (!gatewayIps.contains(clusterConfig1.getIp())) {
                gatewayIps.add(clusterConfig1.getIp());
            }
        }

        serviceManagers = serviceManagers.stream().filter(serviceManager -> serviceManager.getStatus().equals(Constants.DEPLOY)
                || serviceManager.getStatus().equals(Constants.START)
                || serviceManager.getStatus().equals(Constants.STOP)).collect(Collectors.toList());
        for (ServiceManager serviceManager : serviceManagers) {
            List<IpConnectMessage> ipConnectMessageRes = new ArrayList<>();
            RespData<List<IpConnectMessage>> respData = ConsoleBusTool.getIpConnectMessage(serviceManager.getResClusterConfig().getIp());
            if (respData.getCode() == 200 && respData.getData() != null && respData.getData().size() != 0) {
                ipConnectMessageRes = respData.getData();
            }
            MonitorGraphic monitorGraphic = new MonitorGraphic();
            monitorGraphic.setReqName(serviceManager.getReqPlatformRegister().getName());
            monitorGraphic.setRespName(serviceManager.getResPlatformRegister().getName());

            List<ConnStatus> ipConnStatus = new ArrayList<>();
            List<NodeStatus> nodeStatues = new ArrayList<>();
            List<GatewayStatus> gatewayStatuse = new ArrayList<>();
            //服务配置的相关网闸iP
            List<String> serviceGatewayIps = new ArrayList<>();

            //信令流转链路 请求平台->请求节点->网闸->响应节点->响应平台 最后节点没有targetIp 构建图形和查询连接时需过滤
            //请求平台-->请求节点 sip信令通路
            String reqNodeIp = serviceManager.getReqClusterConfig().getIp();
            ConnStatus reqToNodeConn = new ConnStatus();
            reqToNodeConn.setSourIp(serviceManager.getReqPlatformRegister().getRequestIp());
            reqToNodeConn.setSourceName(serviceManager.getReqPlatformRegister().getName());
            reqToNodeConn.setTargIp(reqNodeIp);
            reqToNodeConn.setType(Constants.CONNSTATUS_TYPE_REQP);
            reqToNodeConn.setIsConn(false);
            ipConnStatus.add(reqToNodeConn);
            //本地网闸信令
            List<String> signalGatewayIps = new ArrayList<>();
            String localSignalGateWay = serviceManager.getLocalSignalGateWay();
            if (StringUtils.isNotBlank(localSignalGateWay)) {
                String[] localSignalGateWayArr = localSignalGateWay.split(";");
                signalGatewayIps.addAll(Arrays.asList(localSignalGateWayArr));
            }
            //异地网闸信令
            String alloSignalGateWay = serviceManager.getAlloSignalGateWay();
            if (StringUtils.isNotBlank(alloSignalGateWay)) {
                String[] alloSignalGateWayArr = alloSignalGateWay.split(";");
                signalGatewayIps.addAll(Arrays.asList(alloSignalGateWayArr));
            }

            for (String gatewayIpPort : signalGatewayIps) {
                GatewayStatus gatewayStatus = new GatewayStatus();
                String gatewayIp = gatewayIpPort.split(":")[0];
                String gatewayPort = gatewayIpPort.split(":")[1];
                gatewayStatus.setGatewayIp(gatewayIp);
                gatewayStatus.setType("signal");
                gatewayStatuse.add(gatewayStatus);
                serviceGatewayIps.add(gatewayIp);
                //请求节点到网闸 sip信令通路
                ConnStatus reqNodeToGatewayConn = new ConnStatus();
                reqNodeToGatewayConn.setSourIp(reqNodeIp);
                reqNodeToGatewayConn.setTargIp(gatewayIp);
                reqNodeToGatewayConn.setType(Constants.CONNSTATUS_TYPE_REQNODE);
                reqNodeToGatewayConn.setIsConn(false);
                ipConnStatus.add(reqNodeToGatewayConn);
                //网闸到响应节点 sip信令通路
                String respNode = clusterConfigService.getNodeIpByGatewayIpPort(gateways, gatewayIp, Integer.valueOf(gatewayPort));
                ConnStatus gatewayToRespNodeConn = new ConnStatus();
                gatewayToRespNodeConn.setSourIp(gatewayIp);
                gatewayToRespNodeConn.setTargIp(respNode);
                gatewayToRespNodeConn.setType(Constants.CONNSTATUS_TYPE_GATEWAY);
                gatewayToRespNodeConn.setIsConn(false);
                ipConnStatus.add(gatewayToRespNodeConn);
            }

            //响应节点-> 响应平台 sip信令通路
            ConnStatus respNodeTorespConn = new ConnStatus();
            respNodeTorespConn.setSourIp(serviceManager.getResClusterConfig().getIp());
            respNodeTorespConn.setTargIp(serviceManager.getResPlatformRegister().getRequestIp());
            respNodeTorespConn.setType(Constants.CONNSTATUS_TYPE_RESPNODE);
            respNodeTorespConn.setIsConn(false);
            ipConnStatus.add(respNodeTorespConn);

//            //响应平台
//            ConnStatus respConn = new ConnStatus();
//            respConn.setSourIp(serviceManager.getResPlatformRegister().getRequestIp());
//            respConn.setSourceName(serviceManager.getResPlatformRegister().getName());
//            respConn.setType(Constants.CONNSTATUS_TYPE_RESPP);
//            respConn.setIsConn(false);
//            ipConnStatus.add(respConn);

            //视频链路 响应节点->网闸->请求节点 最后节点没有targetIp 构建图形和查询连接时需过滤
            //视频网闸
            List<String> videoGatewayIps = new ArrayList<>();
            String localVideoGateway = serviceManager.getLocalVideoGateway();
            if (StringUtils.isNotBlank(localVideoGateway)) {
                String[] localVideoGatewayArr = localVideoGateway.split(";");
                videoGatewayIps.addAll(Arrays.asList(localVideoGatewayArr));
            }
            String alloVideoGateway = serviceManager.getAlloVideoGateway();
            if (StringUtils.isNotBlank(alloVideoGateway)) {
                String[] alloVideoGatewayArr = alloVideoGateway.split(";");
                videoGatewayIps.addAll(Arrays.asList(alloVideoGatewayArr));
            }

            //视频响应链路
            int index = 0;
            for (String gatewayIpPort : videoGatewayIps) {
                GatewayStatus gatewayStatus = new GatewayStatus();
                String gatewayIp = gatewayIpPort.split(":")[0];
                String gatewayPort = gatewayIpPort.split(":")[1];
                gatewayStatus.setGatewayIp(gatewayIp);
                gatewayStatus.setType("video");
                gatewayStatuse.add(gatewayStatus);
                serviceGatewayIps.add(gatewayIp);
                //响应节点到网闸 rtp流
                ConnStatus respNodeToGatewayConn = new ConnStatus();
                respNodeToGatewayConn.setSourIp(serviceManager.getResPlatformRegister().getRequestIp());
                respNodeToGatewayConn.setTargIp(gatewayIp);
                respNodeToGatewayConn.setType(Constants.CONNSTATUS_TYPE_RESPP);
                respNodeToGatewayConn.setIsConn(false);
                ipConnStatus.add(respNodeToGatewayConn);

                //网闸到请求节点 rtp流 请求节点在图形表现为只有一个图形 所以网闸对应的rtpIp直接换成requstIp
                ConnStatus gatewayToReqNodeConn = new ConnStatus();
                gatewayToReqNodeConn.setSourIp(gatewayIp);
                gatewayToReqNodeConn.setTargIp(serviceManager.getReqPlatformRegister().getRequestIp());
                gatewayToReqNodeConn.setType(Constants.CONNSTATUS_TYPE_GATEWAY);
                gatewayToReqNodeConn.setIsConn(false);
                ipConnStatus.add(gatewayToReqNodeConn);

//                if(index==0){
//                    //请求节点平台
//                    ConnStatus gatewayToRespNodeConn = new ConnStatus();
//                    gatewayToRespNodeConn.setSourIp(serviceManager.getReqPlatformRegister().getRequestIp());
//                    gatewayToRespNodeConn.setType(Constants.CONNSTATUS_TYPE_REQP);
//                    gatewayToRespNodeConn.setIsConn(false);
//                    ipConnStatus.add(gatewayToRespNodeConn);
//                }
//                index++;
            }

            List<IpConnectMessage> ipConnectMessageListReq = ipConnectMessageReq.stream().filter(ipConnectMessage -> ipConnectMessage.getServiceID().equals(serviceManager.getServiceId())).collect(Collectors.toList());
            List<IpConnectMessage> ipConnectMessageListRes = ipConnectMessageRes.stream().filter(ipConnectMessage -> ipConnectMessage.getServiceID().equals(serviceManager.getServiceId())).collect(Collectors.toList());

            //节点
            //请求
            NodeStatus nodeStatusReq = new NodeStatus();
            NodeStatus nodeStatusRes = new NodeStatus();
            nodeStatusReq = getNodeStatus(serviceManager.getReqClusterConfig().getIp());
            nodeStatusRes = getNodeStatus(serviceManager.getResClusterConfig().getIp());
            nodeStatues.add(nodeStatusReq);
            nodeStatues.add(nodeStatusRes);

            //网闸
//            for (String gatewayIp : serviceGatewayIps) {
//                Object obj = getStatus(gatewayIp, serviceManager.getServiceId(), gatewayIps);
//                GatewayStatus gatewayStatus = (GatewayStatus) obj;
//                gatewayStatuse.add(gatewayStatus);
//            }
            monitorGraphic.setFormat(0);
            monitorGraphic.setId(serviceManager.getId());
            monitorGraphic.setServiceName(serviceManager.getServiceName());
            monitorGraphic.setServiceId(serviceManager.getServiceId());
            monitorGraphic.setModifyTime(serviceManager.getModifyTime());
            monitorGraphic.setConnStatusList(ipConnStatus);
            monitorGraphic.setNodeStatus(nodeStatues);
            monitorGraphic.setGatewayStatuse(gatewayStatuse);
            monitorGraphic.setStatus(serviceManager.getStatus());
            monitorGraphicList.add(monitorGraphic);
        }

        return monitorGraphicList;
    }


    //查询ip所属平台还是网闸
    private Object getStatus(String ip, String serviceId, List<String> ipAndPort) throws Exception {
        //判断平台
//        platformRegisters = platformRegisters.stream().filter(platformRegister -> ip.equals(platformRegister.getRequestIp() + ":" + platformRegister.getRequestPort())).collect(Collectors.toList());
//        if (platformRegisters.size() > 0) {
//            return platformRegisters.get(0);
//        }
        //判断网闸
        GatewayStatus gatewayStatus = new GatewayStatus();
        String ipx = ip;
        if (ipAndPort.contains(ipx)) {
            long flowSize = 0;
            String startTime = "";
            String endTime = "";
            Double transportBitStream = 0.00;
            long differTime = 0;
            String requestIp = "";
            List<String> destNodes = getDestNode(ipx);
            List<RouteAudit> routeAuditList = iSysRunningDao.getRoute();
            if (routeAuditList != null && routeAuditList.size() != 0) {
                for (RouteAudit routeAudit : routeAuditList) {
                    if (!routeAudit.getServiceID().equals(serviceId)) {
                        continue;
                    }
                    List<VideoTransmission> videoTransmissionList = iSysRunningDao.getVideo(routeAudit.getCallID());
                    for (VideoTransmission videoTransmission : videoTransmissionList) {
//                        if (StringUtils.isBlank(videoTransmission.getRequestIP())||!dscgNodes.contains(videoTransmission.getRequestIP())){
//                            continue;
//                        }
                        if (StringUtils.isNotBlank(videoTransmission.getRequestIP())) {
                            requestIp = videoTransmission.getRequestIP();
                        }
                        if (videoTransmission.getTransportStartTime() != null) {
                            startTime = DateTool.Date2String(videoTransmission.getTransportStartTime(), "");
                        }
                        if (videoTransmission.getTransportStopTime() != null) {
                            endTime = DateTool.Date2String(videoTransmission.getTransportStopTime(), "");
                        }
                        if (videoTransmission.getTransportBitStream() != null && videoTransmission.getTransportBitStream() != 0) {
                            transportBitStream = videoTransmission.getTransportBitStream();
                        }
                    }
                    if (!destNodes.contains(requestIp.trim())) {
                        continue;
                    }
//                    int count = 0;
//                    for (String dscgNode : dscgNodes) {
//                        if (dscgNode.trim().equals(requestIp.trim())) {
//                            ++count;
//                        }
//                    }
//                    if(count==0){
//                        continue;
//                    }
                    startTime = startTime == null ? "" : startTime;
                    endTime = endTime == null ? "" : endTime;
                    if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
                        differTime = (DateTool.StringToDateLong(endTime, "") - DateTool.StringToDateLong(startTime, "")) / 1000;
                        flowSize += differTime * transportBitStream;
                    }
                    startTime = "";
                    endTime = "";
                }

            }
            gatewayStatus.setGatewayIp(ipx);
            List<String> size = Tools.getByteSize(Math.pow(1024, 2) * flowSize);
            gatewayStatus.setSize(size.get(0) + size.get(1));
            return gatewayStatus;
        }
        return gatewayStatus;
    }


    //查询节点的状态
    public NodeStatus getNodeStatus(String ip) {
//        RespData<NodeStatus> respData = ConsoleBusTool.queryClusterNodeStatus(ip);
//        if (respData == null&&respData.getData()!=null) {
//            NodeStatus nodeStatus = new NodeStatus();
//            nodeStatus.setIp(ip);
//            return nodeStatus;
//        }
//        return respData.getData();
        //测试返回
        NodeStatus nodeStatus = new NodeStatus();
        nodeStatus.setCpu(1.23f);
        nodeStatus.setDiskUsePercent(2.44f);
        nodeStatus.setMemoryUsePercent(3.56f);
        return nodeStatus;
    }

    public List<String> getDestNode(String ipx) throws Exception {
        List<ClusterConfig> clusterConfigs = clusterConfigService.queryAll();

        List<String> destNodes = new ArrayList<>();
        clusterConfigs = clusterConfigs.stream().filter(clusterConfig -> Constants.CLUSTER_CONFIG_TYPE_GATEWAY.equals(clusterConfig.getType())
                && clusterConfig.getIp().equals(ipx)).collect(Collectors.toList());
        for (ClusterConfig clusterConfig : clusterConfigs) {
            for (ClusterGatewayPassageway clusterGatewayPassageway : clusterConfig.getSignalPassageways()) {
                if (!destNodes.contains(clusterGatewayPassageway.getDestNode())) {
                    destNodes.add(clusterGatewayPassageway.getDestNode());
                }
            }
        }
        return destNodes;
    }

    public List<String> getIp() throws Exception {
        List<ClusterConfig> clusterConfigs = clusterConfigService.queryAll();
        List<String> ipAndPort = new ArrayList<>();
        clusterConfigs = clusterConfigs.stream().filter(clusterConfig -> Constants.CLUSTER_CONFIG_TYPE_GATEWAY.equals(clusterConfig.getType())).collect(Collectors.toList());
        for (ClusterConfig clusterConfig : clusterConfigs) {
            if (!ipAndPort.contains(clusterConfig.getIp())) {
                ipAndPort.add(clusterConfig.getIp());
            }
        }
        return ipAndPort;
    }

    @Override
    public RefuseCata getRefuseCata(String serviceId) throws Exception {
        return iRefuseCataDao.getRefuseCata(serviceId);
    }

    @Override
    public Boolean saveRefuseCata(RefuseCata refuseCata) throws Exception {
        RefuseCata refuseCata1 = getRefuseCata(refuseCata.getServiceId());
        if (refuseCata1 == null) {
            int result = iRefuseCataDao.insert(refuseCata);
            return result == 1 ? true : false;
        } else {
            int result = iRefuseCataDao.update(refuseCata);
            return result == 1 ? true : false;
        }
    }

    @Override
    public List<GraphicMonitor> getMonitorGraphic(boolean model) throws Exception {
        List<GraphicMonitor> graphicMonitors = new ArrayList<>();
        List<ServiceManager> serviceManagers = serviceManagerService.queryAll();
        List<IpConnectMessage> ipConnectMessageReq = iMonitorCenterDao.queryConnStatus();


        serviceManagers = serviceManagers.stream().filter(serviceManager -> !serviceManager.getStatus().equalsIgnoreCase(Constants.UNDEPLOY)).collect(Collectors.toList());


        for (ServiceManager serviceManager : serviceManagers) {
            GraphicMonitor graphicMonitor = new GraphicMonitor();

            List<Plat> plats = new ArrayList<>();
            List<NodeStatus> nodes = new ArrayList<>();
            List<GatewayInfo> gatewayInfos = new ArrayList<>();
            List<ConnStatus> connStatuses = new ArrayList<>();

            /**平台**/
            Plat reqPlat = new Plat();
            reqPlat.setName(serviceManager.getReqPlatformRegister().getName());
            reqPlat.setIp(serviceManager.getReqPlatformRegister().getRequestIp());
            reqPlat.setDirect(0);

            Plat resPlat = new Plat();
            resPlat.setName(serviceManager.getResPlatformRegister().getName());
            resPlat.setIp(serviceManager.getResPlatformRegister().getRequestIp());
            resPlat.setDirect(1);

            //添加平台
            plats.add(reqPlat);
            plats.add(resPlat);

            /**节点**/
            NodeStatus reqNodeStatus = new NodeStatus();
            if (model) {
                reqNodeStatus = getNodeStatus(serviceManager.getReqClusterConfig().getIp());
                reqNodeStatus.setIp(serviceManager.getReqClusterConfig().getIp());
            } else {
                reqNodeStatus.setCpu(0.00f);
                reqNodeStatus.setDiskUsePercent(0.00f);
                reqNodeStatus.setMemoryUsePercent(0.00f);
                reqNodeStatus.setIp(serviceManager.getReqClusterConfig().getIp());
            }
            reqNodeStatus.setName("请求节点");
            reqNodeStatus.setDirect(0);

            NodeStatus resNodeStatus = new NodeStatus();
            String gatewayIp = serviceManagerService.getSignalIpByResIp(serviceManager.getResClusterConfig().getIp());
            if (StringUtils.isBlank(gatewayIp)){
                continue;
            }
            if (model) {
                resNodeStatus = getNodeStatus(gatewayIp);
                resNodeStatus.setIp(serviceManager.getResClusterConfig().getIp());
            } else {
                resNodeStatus.setCpu(0.00f);
                resNodeStatus.setDiskUsePercent(0.00f);
                resNodeStatus.setMemoryUsePercent(0.00f);
                resNodeStatus.setIp(serviceManager.getResClusterConfig().getIp());

            }
            resNodeStatus.setName("响应节点");
            resNodeStatus.setDirect(1);

            //添加节点
            nodes.add(reqNodeStatus);
            nodes.add(resNodeStatus);

            /**网闸**/
            //本地信令
            returnGateways(serviceManager, gatewayInfos, serviceManager.getLocalSignalGateWay(), Constants.SIGNAL, Constants.LOCAL);
            //本地视频
            returnGateways(serviceManager, gatewayInfos, serviceManager.getLocalVideoGateway(), Constants.VIDEO, Constants.LOCAL);
            //异地信令
            returnGateways(serviceManager, gatewayInfos, serviceManager.getAlloSignalGateWay(), Constants.SIGNAL, Constants.ALLO);
            //异地视频
            returnGateways(serviceManager, gatewayInfos, serviceManager.getAlloVideoGateway(), Constants.VIDEO, Constants.ALLO);

            /**连接信息**/
            List<IpConnectMessage> ipConnectMessageRes = new ArrayList<>();
            ipConnectMessageRes = iMonitorCenterDao.queryConnStatus1();

//            RespData<List<IpConnectMessage>> respData = ConsoleBusTool.getIpConnectMessage(gatewayIp);
//            if (respData != null && respData.getCode() == 200 && respData.getData() != null && respData.getData().size() != 0) {
//                ipConnectMessageRes = respData.getData();
//            }

            List<IpConnectMessage> ipConnectMessageListReq = ipConnectMessageReq.stream().filter(ipConnectMessage -> ipConnectMessage.getServiceID().equals(serviceManager.getServiceId())).collect(Collectors.toList());
            List<IpConnectMessage> ipConnectMessageListRes = ipConnectMessageRes.stream().filter(ipConnectMessage -> ipConnectMessage.getServiceID().equals(serviceManager.getServiceId())).collect(Collectors.toList());

            //当selfport为请求节点的时候
            //请求节点
            for (IpConnectMessage ipConnectMessage : ipConnectMessageListReq) {
                ConnStatus connStatusReq = new ConnStatus();
                connStatusReq.setSourceName("请求端");
                connStatusReq.setIsConn(ipConnectMessage.getConnect());
                String requestIp = ipConnectMessage.getRequestIpPort().split(":")[0];
                String selfIp = ipConnectMessage.getSelfIpPort().split(":")[0];
                if (requestIp.equals(serviceManager.getReqPlatformRegister().getRequestIp())) {
                    connStatusReq.setSourIp(requestIp);
                    connStatusReq.setTargIp(selfIp);
                    connStatusReq.setType(Constants.CONNSTATUS_TYPE_REQP);
                    connStatuses.add(connStatusReq);
                } else {
                    connStatusReq.setSourIp(ipConnectMessage.getSelfIpPort().split(":")[0]);
                    connStatusReq.setTargIp(requestIp);
                    connStatusReq.setType(Constants.CONNSTATUS_TYPE_REQNODE);
                    connStatuses.add(connStatusReq);
                }
            }

            //当selfport为响应节点的时候
            //响应节点
            for (IpConnectMessage ipConnectMessage : ipConnectMessageListRes) {
                ConnStatus connStatusRes = new ConnStatus();
                connStatusRes.setSourceName("响应端");
                connStatusRes.setIsConn(ipConnectMessage.getConnect());
                String requestIp = ipConnectMessage.getRequestIpPort().split(":")[0];
                String selfIp = ipConnectMessage.getSelfIpPort().split(":")[0];
                if (requestIp.equals(serviceManager.getResPlatformRegister().getRequestIp())) {
                    connStatusRes.setTargIp(requestIp);
                    connStatusRes.setSourIp(selfIp);
                    connStatusRes.setType(Constants.CONNSTATUS_TYPE_RESPP);
                    connStatuses.add(connStatusRes);
                } else {
                    connStatusRes.setTargIp(selfIp);
                    connStatusRes.setSourIp(requestIp);
                    connStatusRes.setType(Constants.CONNSTATUS_TYPE_RESPNODE);
                    connStatuses.add(connStatusRes);
                }
            }
            graphicMonitor.setPlats(plats);
            graphicMonitor.setNodes(nodes);
            graphicMonitor.setGatewayInfos(gatewayInfos);
            graphicMonitor.setConnStatuses(connStatuses);
            /**其他的小东西**/
            graphicMonitor.setServiceId(serviceManager.getServiceId());
            graphicMonitor.setServiceName(serviceManager.getServiceName());
            graphicMonitor.setModifyTime(serviceManager.getModifyTime());
            graphicMonitor.setStatus(serviceManager.getStatus());

            graphicMonitors.add(graphicMonitor);
        }

        return graphicMonitors;
    }

    private void returnGateways(ServiceManager serviceManager, List<GatewayInfo> gatewayInfos, String GateWay, String gateType, String inOut) throws Exception {
        GatewayInfo GatewayInfo;
        if (StringUtils.isNotBlank(GateWay)) {
            String[] sipLocateGateways = GateWay.split(";");
            for (String gateway1 : sipLocateGateways) {
                GatewayInfo = qryGatewayInfo(gateway1.split(":")[0], inOut, gateType, serviceManager);
                gatewayInfos.add(GatewayInfo);
            }
        }
    }

    private GatewayInfo qryGatewayInfo(String ip, String inOut, String gateType, ServiceManager serviceManager) throws Exception {
        GatewayInfo gatewayInfo = new GatewayInfo();

        /**网闸的相关信息**/
        gatewayInfo.setGatewayIp(ip);
        gatewayInfo.setGatewayType(gateType);
        gatewayInfo.setLocateOrAllo(inOut);
        /**集群的查询信息**/
        ClusterConfig clusterConfig = new ClusterConfig();
        clusterConfig.setType(Constants.CLUSTER_CONFIG_TYPE_GATEWAY);
        clusterConfig.setInOut(inOut);
        clusterConfig.setGateType(gateType);
        List<ClusterConfig> clusterConfigs = clusterConfigService.queryList(clusterConfig);
        //判断网闸的监听ip是进口还是出口
        cc:
        for (ClusterConfig config : clusterConfigs) {

            if (Constants.SIGNAL.equalsIgnoreCase(gateType)) {
                List<ClusterGatewayPassageway> clusterGatewayPassageways = config.getSignalPassageways();

                for (ClusterGatewayPassageway clusterGatewayPassageway : clusterGatewayPassageways) {
                    if (clusterGatewayPassageway.getInIp().equals(ip)) {
                        if (clusterGatewayPassageway.getDestNode().equals(serviceManager.getReqClusterConfig().getIp())) {
                            gatewayInfo.setInOut(Constants.INOUT_IN);
                            break cc;
                        } else if (clusterGatewayPassageway.getDestNode().equals(serviceManager.getResClusterConfig().getIp())) {
                            gatewayInfo.setInOut(Constants.INOUT_OUT);
                            break cc;
                        }
                    } else {
                        gatewayInfo.setInOut("None");
                    }
                }
            } else if (Constants.VIDEO.equalsIgnoreCase(gateType)) {
                List<ClusterGatewayPassageway> clusterGatewayPassageways = config.getVideoPassageways();

                for (ClusterGatewayPassageway clusterGatewayPassageway : clusterGatewayPassageways) {
                    if (clusterGatewayPassageway.getInIp().equals(ip)) {
                        if (clusterGatewayPassageway.getDestNode().equals(serviceManager.getReqPlatformRegister().getRtpIp())) {
                            gatewayInfo.setInOut(Constants.INOUT_IN);
                            break cc;
                        } else if (clusterGatewayPassageway.getDestNode().equals(serviceManager.getResPlatformRegister().getRtpIp())) {
                            gatewayInfo.setInOut(Constants.INOUT_OUT);
                            break cc;
                        }
                    } else {
                        gatewayInfo.setInOut("None");
                    }
                }
            }

        }
        return gatewayInfo;
    }

}
