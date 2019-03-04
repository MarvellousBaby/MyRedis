package com.sailing.dscg.entity.monitoring;

import lombok.Data;

/**
 * Description:
 * <p>
 * Update by Panyu on 2018/10/17 下午 02:38:39
 */
@Data
public class GatewayStatus {
    //网闸的信息
    private String gatewayIp;
    //网闸的实时流量
    private Long flowSize;

    private String size;
    /** signal 信令  video 媒体 **/
    private String type;

//    private Integer direct;

}
