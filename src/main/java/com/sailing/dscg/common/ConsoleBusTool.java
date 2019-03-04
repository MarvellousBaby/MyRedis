package com.sailing.dscg.common;

import com.caucho.hessian.client.HessianProxyFactory;
import com.caucho.hessian.client.HessianRuntimeException;
import com.sailing.dscg.entity.ApplicationProperties;
import com.sailing.dscg.entity.RespData;
import com.sailing.dscg.entity.monitoring.IpConnectMessage;
import com.sailing.dscg.entity.monitoring.NodeStatus;
import com.sailing.dscg.entity.serviceManager.ServiceManagerCoum;
import com.sailing.dscg.interfaces.ExecCommend;
import com.sailing.dscg.interfaces.NodeState;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.util.List;

/**
 * @Description: 消息通信工具类
 * @Auther:史俊华
 * @Date:2018/7/410
 */
@Component
public class ConsoleBusTool {
    //    private static Integer cbPort = 8000;

    /**
     * 获取集群节点状态
     *
     * @param ip
     * @return
     */
    public static RespData<NodeStatus> queryClusterNodeStatus(String ip) {
        RespData respData = null;
        try {
            NodeState nodeState = getNodeState(ip);
            respData = nodeState.queryClusterNodeStatus(ip);
        } catch (Exception e) {
            Boolean result = Tools.pingIp(ip);
            if (result) {
                respData = new RespData<>();
                respData.setRespCode(RespCodeEnum.SUCCESS);

                NodeStatus nodeStatus = new NodeStatus();
                nodeStatus.setIp(ip);
                nodeStatus.setRunning(true);
                respData.setData(nodeStatus);
            }
        }
        return respData;
    }

    /**
     * 部署服务
     *
     * @param object
     * @param ip
     * @return
     */
    public static RespData<String> deploy(Object object, String ip) throws Exception {
        RespData<String> respData = new RespData<>();
        try {
            ExecCommend execCommend = getExecCommend(ip);
            String result = execCommend.deploy((ServiceManagerCoum) object);
            if (result.equals("200")) {
                respData.setRespCode(RespCodeEnum.SUCCESS);
            }
        } catch (HessianRuntimeException hre) {
            throw new Exception("节点通信异常，请检查[" + ip + "]通信端口是否正常！");
        } catch (Exception e) {
            throw e;
        }
        return respData;
    }

    /**
     * 启动服务
     *
     * @param object
     * @param ip
     * @return
     */
    public static RespData<String> start(Object object, String ip) throws Exception {
        RespData<String> respData = new RespData<>();
        try {
            ExecCommend execCommend = getExecCommend(ip);
            String result = execCommend.start((ServiceManagerCoum) object);
            if (result.equals("200")) {
                respData.setRespCode(RespCodeEnum.SUCCESS);
            }
        } catch (HessianRuntimeException hre) {
            throw new Exception("节点通信异常，请检查[" + ip + "]通信端口是否正常！");
        } catch (Exception e) {
            throw e;
        }
        return respData;
    }

    /**
     * 停止服务
     *
     * @param object
     * @param ip
     * @return
     */
    public static RespData<String> stop(Object object, String ip) throws Exception {
        RespData<String> respData = new RespData<>();
        try {
            ExecCommend execCommend = getExecCommend(ip);
            String result = execCommend.stop((ServiceManagerCoum) object);
            if (result.equals("200")) {
                respData.setRespCode(RespCodeEnum.SUCCESS);
            }
        } catch (HessianRuntimeException hre) {
            throw new Exception("节点通信异常，请检查[" + ip + "]通信端口是否正常！");
        } catch (Exception e) {
            throw e;
        }
        return respData;
    }

    /**
     * 通用通信部署操作
     *
     * @param obj 服务对象（文件、消息、数据库）
     * @return
     */
    public static RespData<String> options(Object obj, String ip, String model) throws Exception {
        RespData respData = new RespData();
        if ("deploy".equals(model)) {
            respData = deploy(obj, ip);
        } else if ("start".equals(model)) {
            respData = start(obj, ip);
        } else if ("stop".equals(model)) {
            respData = stop(obj, ip);
        } else {
            respData.setRespCode(RespCodeEnum.FAIL);
        }
        return respData;
    }

    /**
     * 获取相应端的数据信息
     *
     * @param ip
     * @return com.sailing.dscg.entity.RespData<java.util.List   <   com.sailing.dscg.entity.monitoring.IpConnectMessage>>
     * @throws
     * @author Panyu
     * @date 2018/11/1 上午 10:44:35
     */
    public static RespData<List<IpConnectMessage>> getIpConnectMessage(String ip) throws Exception {

        RespData<List<IpConnectMessage>> respData = new RespData<>();
        try {
            ExecCommend execCommend = getExecCommend(ip);
            List<IpConnectMessage> ipConnectMessages = execCommend.getIpConnectMessage();
            if (ipConnectMessages != null && ipConnectMessages.size() != 0) {
                respData.setRespCode(RespCodeEnum.SUCCESS);
                respData.setData(ipConnectMessages);
            }
        } catch (HessianRuntimeException hre) {
            throw new Exception("节点通信异常，请检查[" + ip + "]通信端口是否正常！");
        } catch (Exception e) {
            throw e;
        }
        return respData;
    }

    /**
     * 获取节点状态通信接口对象
     *
     * @param ip
     * @return
     */
    private static NodeState getNodeState(String ip) throws Exception {
        ApplicationProperties applicationProperties = SpringContextUtil.getBean(ApplicationProperties.class);
        String url = "";
        if (ip.indexOf(":") > 0) {
            url = "http://" + ip + "/Status";
        } else {
            url = "http://" + ip + ":" + applicationProperties.getCbPort() +"/Status";
        }
        HessianProxyFactory factory = new HessianProxyFactory();
        return (NodeState) factory.create(NodeState.class, url);
    }

    /**
     * 获取执行命令通信接口对象
     *
     * @param ip
     * @return
     */
    private static ExecCommend getExecCommend(String ip) {
        ApplicationProperties applicationProperties = SpringContextUtil.getBean(ApplicationProperties.class);
        String url = "";
        if (ip.indexOf(":") > 0) {
            url = "http://" + ip + "/ExecCommend";
        } else {
            url = "http://" + ip + ":" + applicationProperties.getCbPort() + "/ExecCommend";
        }
        HessianProxyFactory factory = new HessianProxyFactory();
        try {
            return (ExecCommend) factory.create(ExecCommend.class, url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
