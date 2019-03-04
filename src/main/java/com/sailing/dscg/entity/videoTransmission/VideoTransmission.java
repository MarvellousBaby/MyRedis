package com.sailing.dscg.entity.videoTransmission;


import lombok.Data;

import java.util.Date;

/**
 * Description:
 * <p>
 * Update by Panyu on 2018/9/13 下午 02:02:38
 */
@Data
public class VideoTransmission {

    private String id;
    //请求时间
    private Date requestTime;
    //请求IP(网闸地址)
    private String requestIP;
    //请求端口
    private int requestPort;
    //传输开始时间
    private Date transportStartTime;
    //传输结束时间
    private Date transportStopTime;
    //传输视频大小
    private Double transportSize;
    //请求类型
    private String requestType;
    //请求码流
    private Double transportBitStream;
    //视频流标识
    private String callID;
    //流媒体服务器地址
    private String requestFromIP;
    //流媒体服务器地址端口
    private int requestFromPort;

}
