package com.sailing.dscg.entity.warnMessage;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * Description:
 * <p>
 * Update by Panyu on 2018/9/25 下午 04:56:49
 */
@Data
public class WarnMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    //验证日期
    private Date verifyDate;
    //IP地址
    private String ipAddress;
    //MAC地址
    private String macAddress;
    //验证类型
    private String verifyType;
    //验证失败
    private Boolean status;

    private Integer pageSize;
    private Integer pageNum;
    private Date startDate;
    private Date endDate;

}
