package com.sailing.dscg.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ZooKeeperBase implements Watcher {

    private ZooKeeper zooKeeper = null;

    // CountDownLatch 用于停止（等待）主进程，直到客户端与ZooKeeper集合连接
    final static CountDownLatch connectedSignal = new CountDownLatch(1);

    /**
     * 获取Zookeeper连接实例
     * @return
     */
    public static ZooKeeperBase getInstance(){
        return new ZooKeeperBase();
    }

    /**
     * 获取zookeeper连接
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public void connect(String host,Integer timeout) throws IOException, InterruptedException {
        this.zooKeeper = new ZooKeeper(host, timeout,this);
        connectedSignal.await();
    }

    // 关闭zk
    public void close() throws InterruptedException {
        if(this.zooKeeper!=null){
            this.zooKeeper.close();
        }
    }

    /**
     * 判断节点是否存在
     * @param path
     * @return
     * @throws Exception
     */
    public Boolean nodeExists(String path) throws Exception {
        Stat stat = this.zooKeeper.exists(path, true);
        return stat == null ? false : true;
    }

    /**
     * 创建节点
     * @param path
     * @param data
     * @throws Exception
     */
    public Boolean createNode(String path, byte[] data) throws Exception {
        if(!this.nodeExists(path)){
            String listPath[] = path.split("/");
            String prePath = "";
            for(int i=1; i<listPath.length-1; i++){
                prePath = prePath + "/" + listPath[i];
                if(!this.nodeExists(prePath)){
                    this.zooKeeper.create(prePath,"".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
                }
            }
            this.zooKeeper.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            return true;
        }else{
            return false;
        }
    }

    /**
     * 删除节点
     * @param path
     * @throws Exception
     */
    public Boolean delNode(String path) throws Exception {
        if(this.nodeExists(path)){
            this.zooKeeper.delete(path, -1);
            return true;
        }
        return false;
    }

    // 获取某节点内数据
    public String getDate(String path) throws Exception{
        if (this.nodeExists(path)){
            return new String(this.zooKeeper.getData(path, null, null));
        }
        return null;
    }

    // 更新节点内数据
    public boolean setData(String path, byte[] data) throws Exception {
        if (this.nodeExists(path)) {
            this.zooKeeper.setData(path, data, -1);
            return true;
        }
        return false;
    }

    // 获取某节点的子节点,先判定该节点是否存在
    public List<String> getChildren(String path) throws Exception {
        if (this.nodeExists(path)){
            return this.zooKeeper.getChildren(path, false);
        }
        return null;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getState() == KeeperState.SyncConnected) {
            connectedSignal.countDown();
        }
    }
}