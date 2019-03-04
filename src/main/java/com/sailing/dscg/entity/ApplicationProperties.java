package com.sailing.dscg.entity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/***
 * application配置文件自定义属性文件
 */
@Component
@Configuration
@ConfigurationProperties(prefix="dscg") //接收application.yml中的dscg下面的属性
public class ApplicationProperties {
    /**zookeeper集群地址*/
    private String zookeeperIps;
    /**导出地址*/
    private String excel;
    @Value("${dscg.cbPort}")
    String cbPort;

    public String getZookeeperIps() {
        return zookeeperIps;
    }

    public void setZookeeperIps(String zookeeperIps) {
        this.zookeeperIps = zookeeperIps;
    }

    public String getExcel() {
        return excel;
    }

    public void setExcel(String excel) {
        this.excel = excel;
    }

    public String getCbPort() {
        return cbPort;
    }

    public void setCbPort(String cbPort) {
        this.cbPort = cbPort;
    }
}
