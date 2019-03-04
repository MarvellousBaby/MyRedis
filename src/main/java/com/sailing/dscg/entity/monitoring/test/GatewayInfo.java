package com.sailing.dscg.entity.monitoring.test;

import lombok.Data;

/**
 * Description:
 * <p>
 * Update by Panyu on 2018/12/17 下午 04:25:41
 */
@Data
public class GatewayInfo {

    //网闸ip
    String gatewayIp;

    //网闸类型   sip -信令  rtp -视频
    String gatewayType;

    //网闸进出  in - 监听  out - 输出
    String inOut;

    //网闸本地或者异地
    String locateOrAllo;



}
