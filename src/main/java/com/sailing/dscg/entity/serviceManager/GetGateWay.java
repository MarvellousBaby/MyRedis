package com.sailing.dscg.entity.serviceManager;

import lombok.Data;

import java.util.List;

/**
 * Description:
 * <p>
 * Update by Panyu on 2018/10/14 下午 05:14:04
 */
@Data
public class GetGateWay {
    //请求平台
    private List<String> reqIp;
    //类型（异地allo|本地local）
    private String type;
    //视频或者信令(video|signal)
    private String inOut;
    //状态0-新增，1-修改
    private Integer status;
    //网闸ip+port
    private List<String> ipAndPort;

}
