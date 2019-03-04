package com.sailing.dscg.entity.monitoring;

import lombok.Data;

/**
 * Description:
 * <p>
 * Update by Panyu on 2018/9/26 上午 09:43:45
 */
@Data
public class MonitorCount {

    //总传输请求
    private Long transReqCount;
    //设备验证通过
    private Long devicePassCount;
    //通行视频流量
    private String passFlowSize;
    //设备验证失败
    private Long deviceFail;

}
