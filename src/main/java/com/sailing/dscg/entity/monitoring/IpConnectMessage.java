package com.sailing.dscg.entity.monitoring;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Description:
 * <p>
 * Update by Panyu on 2018/10/17 下午 02:48:06
 */
@Data
public class IpConnectMessage implements Serializable {
    private String id;
    private Date time;
    private String requestIpPort;
    private String selfIpPort;
    private String serviceID;
    private Boolean connect;
}

