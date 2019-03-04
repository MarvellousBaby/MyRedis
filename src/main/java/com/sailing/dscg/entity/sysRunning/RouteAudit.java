package com.sailing.dscg.entity.sysRunning;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Description:
 * <p>
 * Update by Panyu on 2018/10/19 上午 11:32:11
 */
@Data
public class RouteAudit implements Serializable {

    private String serviceID;
    private String id;
    private Date requestTime;
    private String requestIPPort;
    private String selfIPPort;
    private String centerControl;
    private String callID;

}
