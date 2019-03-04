package com.sailing.dscg.entity.monitoring;

import lombok.Data;

import java.io.Serializable;

/**
 * 节点状态
 */
@Data
public class NodeStatus implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String ip; //节点Ip
    private Float cpu; //cpu使用值 0~100之间
//    private Float memory_total;//内存大小 单位：G
//    private Float memory_use;//使用内存 单位：G
    private Float memoryUsePercent; //内存使用百分比
//    private Float disk_total;//磁盘大小 单位：G
//    private Float disk_use;//已使用磁盘大小 单位：G
    private Float diskUsePercent; //磁盘使用百分比
//    private Float disk_read;//磁盘读速率 单位B/S
//    private Float disk_write;//磁盘写速率 单位B/S
//    private Float network_in;//网卡输入速率  单位MBit/S
//    private Float network_out;//网卡输出写速率 单位MBit/S
    private Boolean running; //是否运行

    private Integer direct;
}
