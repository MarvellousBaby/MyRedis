package com.sailing.dscg.entity.serviceManager;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * Description:
 * <p>
 * Update by Panyu on 2018/10/12 下午 06:37:20
 */
@Data
@ToString
@NoArgsConstructor
public class ServiceManagerCoum implements Serializable {

    private String serviceId;


    //Ip分配原则
    private String allocation;

    //是否打开视频传输审计
    private Boolean videoTransmission;
    //是否打开设备认证审计
    private Boolean deviceAuthen;
    //码流
    private String transportBitStream;
    //上级Ip和端口
    private String upIPAndPort;
    //下级ip和端口
    private String downIPAndPort;

    //备用上级ip和端口
    private String salverUpIPAndPort;
    //备份下级ip和端口（多个使用；隔开)
    private String salverDownIPAndPort;


    //rtp流转发IP
    private String rtpReDirectIP;
    //备份rtp流转发IP(配置网闸映射端口，多个使用；隔开)
    private String salverRtpReDirectIP;
    //服务自身IP和端口
    private String selfIPAndPort;
    //中央控制器（up/down/normal/null）
    private String centerControl;
    //轮询
    private Integer polling;




}
