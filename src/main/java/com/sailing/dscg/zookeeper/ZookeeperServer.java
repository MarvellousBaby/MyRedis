package com.sailing.dscg.zookeeper;


import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ZookeeperServer<T> {

    @Value("${dscg.zookeeperTimeout}")
    private int timeout;     //超时时间
    @Value("${dscg.zookeeperIps}")
    private String host;     //IP地址
    private String basePath = "/vscg/config/"; //配置节点基础路径

    /**
     * 创建节点
     * @param id 节点ID
     * @param t 节点对象
     * @return 创建节点
     */
    public boolean create(String id,T t,Class<T> clazz) throws Exception {
        String nodeName = getNodeName(clazz);
        return create(nodeName,id,t);
    }

    /**
     * 创建节点
     * @param nodeName
     * @param id 节点ID
     * @param t 节点对象
     * @return 创建节点
     */
    public boolean create(String nodeName,String id,T t) throws Exception{
        ZooKeeperBase zooKeeperBase = ZooKeeperBase.getInstance();
        try{
            zooKeeperBase.connect(host,timeout);
            String confData = JSONObject.toJSONString(t);
            String path = basePath+nodeName+"/"+id;
            return zooKeeperBase.createNode(path, confData.getBytes());
        }catch (Exception e){
            throw e;
        }finally {
            zooKeeperBase.close();
        }
    }

    /**
     * 更新节点
     * @param id
     * @param t
     * @return 更新service配置
     */
    public boolean update(String id,T t,Class<T> clazz) throws Exception {
        String nodeName = getNodeName(clazz);
        return update(nodeName,id,t);
    }

    /**
     * 更新节点
     * @param nodeName 节点名称
     * @param id 节点ID
     * @param obj 数据对象
     * @return 更新service配置
     */
    public boolean update(String nodeName,String id,Object obj) throws Exception {
        ZooKeeperBase zooKeeperBase = ZooKeeperBase.getInstance();
        try{
            zooKeeperBase.connect(host,timeout);
            String confData = JSONObject.toJSONString(obj);
            String path = basePath+nodeName+"/"+id;
            return zooKeeperBase.setData(path, confData.getBytes());
        }catch (Exception e){
            throw e;
        }finally {
            zooKeeperBase.close();
        }
    }

    /**
     * @return 查询所有节点
     */
    public List<T> queryAll(Class<T> clazz) throws Exception{
        String nodeName = getNodeName(clazz);
        return queryAll(clazz,nodeName);
    }

    /**
     * @return 查询所有节点
     */
    public List<T> queryAll(Class<T> clazz,String nodeName) throws Exception{
        ZooKeeperBase zooKeeperBase = ZooKeeperBase.getInstance();
        try{
            zooKeeperBase.connect(host,timeout);
            String path = basePath+nodeName;
            List<String> list = zooKeeperBase.getChildren(path);
            List<T> nodes = new ArrayList<>();
            if(list!=null && !list.isEmpty()){
                for (String config:list) {
                    String nodeConfig = zooKeeperBase.getDate(path+"/"+config);
                    T t1 = JSONObject.parseObject(nodeConfig,clazz);
                    nodes.add(t1);
                }
            }
            return nodes;
        }catch (Exception e){
            throw e;
        }finally {
            zooKeeperBase.close();
        }
    }

    /**
     * 获取单个对象
     * @param id
     * @param clazz
     * @return 根据serviceID查询配置信息
     */
    public T get(String id,Class<T> clazz) throws Exception{
        String nodeName = getNodeName(clazz);
        return get(id,nodeName,clazz);
    }

    /**
     * 获取单个对象
     * @param id
     * @param clazz
     * @return 根据serviceID查询配置信息
     */
    public T get(String id,String nodeName,Class<T> clazz) throws Exception{
        ZooKeeperBase zooKeeperBase = ZooKeeperBase.getInstance();
        try{
            zooKeeperBase.connect(host,timeout);
            String path = basePath+nodeName+"/"+id;
            String config = zooKeeperBase.getDate(path);
            if(config!=null){
                T t = JSONObject.parseObject(config,clazz);
                return t;
            }
            return null;
        }catch (Exception e){
            throw e;
        }finally {
            zooKeeperBase.close();
        }
    }

    public boolean delNode(String id,Class<T> clazz) throws Exception{
        ZooKeeperBase zooKeeperBase = ZooKeeperBase.getInstance();
        try{
            String nodeName = getNodeName(clazz);
            zooKeeperBase.connect(host,timeout);
            String path = basePath+nodeName+"/"+id;
            return zooKeeperBase.delNode(path);
        }catch (Exception e){
            throw e;
        }finally {
            zooKeeperBase.close();
        }
    }

    /**
     * 获取类注解节点名称
     * @param clazz
     * @return
     * @throws Exception
     */
    private String getNodeName(Class<T> clazz) throws Exception {
        Node node = clazz.getAnnotation(Node.class);
        String nodeName = "";
        if(node!=null){
            nodeName = node.name();
        }
        if(StringUtils.isBlank(nodeName)){
            throw new Exception("未找到Node注解名称！");
        }
        return nodeName;
    }
}
